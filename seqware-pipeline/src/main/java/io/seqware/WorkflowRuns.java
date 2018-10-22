package io.seqware;

import com.google.common.base.Objects;
import io.seqware.common.model.WorkflowRunStatus;
import static io.seqware.common.model.WorkflowRunStatus.pending;
import static io.seqware.common.model.WorkflowRunStatus.running;
import static io.seqware.common.model.WorkflowRunStatus.submitted;
import io.seqware.pipeline.SqwKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRuns {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRuns.class);

    public static void submitCancel(int... workflowRunAccessions) {
        multithreadTransition(Transition.CANCEL, workflowRunAccessions);
    }

    private static void cancelWorkflowRun(int workflowRunAccession) throws UnsupportedOperationException {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
        if (Engines.supportsCancel(wr.getWorkflowEngine())) {
            switch (wr.getStatus()) {
            case submitted:
            case pending:
            case running:
                wr.setStatus(WorkflowRunStatus.submitted_cancel);
                md.updateWorkflowRun(wr);
            default: // do nothing
            }
        } else {
            throw new UnsupportedOperationException("Workflow run cancellation not supported for engine: " + wr.getWorkflowEngine());
        }
    }

    public static void failWorkflowRuns(int... workflowRunAccessions) {
        multithreadTransition(Transition.FAIL, workflowRunAccessions);
    }

    private static void failWorkflowRun(int workflowRunAccession) {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
        wr.setStatus(WorkflowRunStatus.failed);
        md.updateWorkflowRun(wr);
    }

    public static void submitRetry(int... workflowRunAccessions) {
        multithreadTransition(Transition.RETRY, workflowRunAccessions);
    }

    private enum Transition {
        FAIL, CANCEL, RETRY
    }

    private static void multithreadTransition(final Transition transition, int[] workflowRunAccessions) {
        ExecutorService pool = Executors.newFixedThreadPool(Math.min(10, workflowRunAccessions.length));
        List<Future<?>> futures = new ArrayList<>(workflowRunAccessions.length);
        for (int workflowRunAccession : workflowRunAccessions) {
            final int workflowRunAccessionCopy = workflowRunAccession;
            futures.add(pool.submit(new Runnable() {
                @Override
                public void run() {
                    if (transition == Transition.RETRY) {
                        retryWorkflowRun(workflowRunAccessionCopy);
                    } else if (transition == Transition.CANCEL) {
                        cancelWorkflowRun(workflowRunAccessionCopy);
                    } else if (transition == Transition.FAIL) {
                        failWorkflowRun(workflowRunAccessionCopy);
                    }
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error("WorkflowRuns.multithreadTransition InterruptedException", ex);
            }
        }
        pool.shutdown();
    }

    private static void retryWorkflowRun(int workflowRunAccession) throws UnsupportedOperationException {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
        if (Engines.supportsRetry(wr.getWorkflowEngine())) {
            switch (wr.getStatus()) {
            case failed:
            case cancelled:
                wr.setStatus(WorkflowRunStatus.submitted_retry);
                md.updateWorkflowRun(wr);
            default: // do nothing
            }
        } else {
            throw new UnsupportedOperationException("Workflow run retrying not supported for engine: " + wr.getWorkflowEngine());
        }
    }

    public static String workflowRunIni(int workflowRunAccession) {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun workflowRun = md.getWorkflowRun(workflowRunAccession);
        return workflowRun.getIniFile();
    }

    /**
     * Convert from status command to workflow run
     *
     * @param statusCmd
     * @return
     */
    public static WorkflowRun getWorkflowRunByStatusCmd(String statusCmd) {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        List<WorkflowRun> workflowRuns = md.getWorkflowRunsByStatusCmd(statusCmd);
        // assume that this is unique for now (should be a safe assumption for Oozie)
        if (!workflowRuns.isEmpty()) {
            return workflowRuns.get(0);
        }
        return null;
    }

    /**
     * Extremely extremely expensive! Converts between sge id and determines the workflow run accession that it corresponds to.
     *
     * This is expensive because the Oozie-sge engine doesn't expose the external ID, instead we need to do a really expensive search.
     *
     * @param sgeid
     * @return
     */
    public static Integer getAccessionByActionExternalID(String sgeid) {
        Map<String, String> config = ConfigTools.getSettings();
        OozieClient oc = new OozieClient(config.get(SqwKeys.OOZIE_URL.getSettingKey()));
        try {
            for (int i = 0;; i++) {
                int rangeStart = i * 10;
                int rangeEnd = i * 10 + 10;
                LOGGER.debug("Requesting " + rangeStart + " " + rangeEnd);
                List<WorkflowJob> jobsInfo = oc.getJobsInfo("", rangeStart, rangeEnd);
                if (jobsInfo.isEmpty()) {
                    break;
                }
                for (WorkflowJob job : jobsInfo) {
                    job = oc.getJobInfo(job.getId());
                    for (WorkflowAction action : job.getActions()) {
                        if (Objects.equal(action.getExternalId(), sgeid)) {
                            return getWorkflowRunByStatusCmd(job.getId()).getSwAccession();
                        }
                    }
                }
            }
        } catch (OozieClientException ex) {
            LOGGER.error("WorkflowRuns.getAccessionByActionExternalID OozieClientException", ex);
            throw new RuntimeException(ex);
        }
        return null;
    }
}
