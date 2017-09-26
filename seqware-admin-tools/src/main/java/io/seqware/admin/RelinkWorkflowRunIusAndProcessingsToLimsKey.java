/*
 * Copyright (C) 2016 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.seqware.admin;

import ca.on.oicr.gsi.provenance.model.LaneProvenance;
import ca.on.oicr.gsi.provenance.model.SampleProvenance;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import net.sourceforge.seqware.common.business.SampleProvenanceService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.LimsKey;
import org.hibernate.Criteria;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import java.io.IOException;
import java.util.ArrayList;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import javax.naming.NamingException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import org.hibernate.SessionFactory;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import java.nio.charset.StandardCharsets;
import net.sourceforge.seqware.common.business.LaneProvenanceService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Collections;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This tool iterates over all WorkflowRuns that are linked to IUS and relinks the WorkflowRuns to a new IUS that is linked to a LimsKey.
 *
 * The tool does the following:
 * - get all Sample and Lane Provenance from SeqWare
 * - get all WorkflowRuns and the IUSes and Lanes that are linked to the WorkflowRun or to the Processings
 * - verify that the IUSes have SampleProvenance
 * - verify that the Lanes have LaneProvenance
 * - verify that either all or none of the IUSes in the Processing-IUS link set are in the WorkflowRun-IUS link set
 * - verify that either all or none of the Lanes in the Processing-Lane link set are in the WorkflowRun-Lane link set
 * - relink the WorkflowRun by:
 * 1) create a new IUS for each existing IUS or Lane link
 * 2) create a new LimsKey with the information from the existing IUS's SampleProvenance record or Lane's LaneProvenance record
 * 3) link the WorkflowRun to the new IUS
 * 4) if necessary, link the Processing to the new IUS
 *
 * @author mlaszloffy
 */
@Component
public class RelinkWorkflowRunIusAndProcessingsToLimsKey {

    private Date timestamp; //All new LimsKeys and IUS createTstmp and updateTstmp will be set to this
    private Map<String, SampleProvenance> sps;
    private Map<String, LaneProvenance> lps;
    private Map<Integer, RelinkingTask> relinkingTasksByWorkflowRunId;
    private List<Integer> workflowRunIdsThatCannotBeRelinked;
    private List<RelinkingTask> validRelinkingTasks;
    private StatelessSession session;
    private Summary summary;
    private boolean doRelinking = false;

    @Autowired
    @Qualifier("sessionFactory")
    SessionFactory sessionFactory;

    @Autowired
    SampleProvenanceService sampleProvenanceService;

    @Autowired
    LaneProvenanceService laneProvenanceService;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setDoRelinking(boolean doRelinking) {
        this.doRelinking = doRelinking;
    }

    private void setup() {
        workflowRunIdsThatCannotBeRelinked = new ArrayList<>();
        validRelinkingTasks = new ArrayList<>();
        summary = new Summary();
        session = sessionFactory.openStatelessSession();
    }

    //preload all required data for relinking
    private void preload() throws IOException {
        System.out.println("Preloading started");
        Stopwatch timer = Stopwatch.createStarted();
        Transaction preloadTx = null;
        try {
            preloadTx = session.beginTransaction();

            //get all sample provenance records
            sps = new HashMap<>();
            for (SampleProvenance sp : sampleProvenanceService.list()) {
                if (sps.put(sp.getSampleProvenanceId(), sp) != null) {
                    throw new RuntimeException("Duplicate sample provenance, id = [" + sp.getSampleProvenanceId() + "]");
                }
            }

            //get all lane provenance records
            lps = new HashMap<>();
            for (LaneProvenance lp : laneProvenanceService.list()) {
                if (lps.put(lp.getLaneProvenanceId(), lp) != null) {
                    throw new RuntimeException("Duplicate lane provenance, id = [" + lp.getLaneProvenanceId() + "]");
                }
            }

            //get all WorkflowRun-IUS links
            List workflowRunIusRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("workflow_run_iuses.sql"), "UTF-8"))
                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                    .list();
            relinkingTasksByWorkflowRunId = new HashMap<>();
            for (Object workflowRunIusRecord : workflowRunIusRecordList) {
                Map workflowRunIusRecordAsMap = (Map) workflowRunIusRecord;
                Integer workflowRunId = (Integer) workflowRunIusRecordAsMap.get("workflow_run_id");
                checkNotNull(workflowRunId);

                RelinkingTask re;
                if (relinkingTasksByWorkflowRunId.containsKey(workflowRunId)) {
                    re = relinkingTasksByWorkflowRunId.get(workflowRunId);
                } else {
                    re = new RelinkingTask(workflowRunId);
                    relinkingTasksByWorkflowRunId.put(workflowRunId, re);
                }

                Integer iusId = (Integer) workflowRunIusRecordAsMap.get("ius_id");
                Integer iusSwid = (Integer) workflowRunIusRecordAsMap.get("ius_swid");
                Integer limsKeyId = workflowRunIusRecordAsMap.get("lims_key_id") == null ? null : Integer.parseInt((String) workflowRunIusRecordAsMap.get("lims_key_id"));
                checkNotNull(iusId);
                checkNotNull(iusSwid);

                WorkflowRunIusLink wre = new WorkflowRunIusLink();
                wre.setWorkflowRunId(workflowRunId);
                wre.setIusId(iusId);
                wre.setIusSwid(iusSwid);
                wre.setLimsKeyId(limsKeyId);

                re.addWorkflowRunIus(wre);
            }

            //get all Processing-IUS links
            List processingIusRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("processing_iuses.sql"), "UTF-8"))
                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                    .list();
            for (Object processingIusRecord : processingIusRecordList) {
                Map processingIusRecordAsMap = (Map) processingIusRecord;
                Integer workflowRunId = (Integer) processingIusRecordAsMap.get("workflow_run_id");
                checkNotNull(workflowRunId);

                RelinkingTask relinkingTask = relinkingTasksByWorkflowRunId.get(workflowRunId);
                checkNotNull(relinkingTask);

                Integer iusId = (Integer) processingIusRecordAsMap.get("ius_id");
                Integer iusSwid = (Integer) processingIusRecordAsMap.get("ius_swid");
                Integer processingId = (Integer) processingIusRecordAsMap.get("processing_id");
                checkNotNull(iusId);
                checkNotNull(iusSwid);
                checkNotNull(processingId);

                ProcessingIusLink wrp = new ProcessingIusLink();
                wrp.setWorkflowRunId(workflowRunId);
                wrp.setIusId(iusId);
                wrp.setIusSwid(iusSwid);
                wrp.setProcessingId(processingId);

                relinkingTask.addProcessingIusLink(wrp);
            }

            //get all WorkflowRun-Lane links
            List workflowRunLaneRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("workflow_run_lanes.sql"), "UTF-8"))
                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                    .list();
            for (Object workflowRunLaneRecord : workflowRunLaneRecordList) {
                Map workflowRunLaneAsMap = (Map) workflowRunLaneRecord;
                Integer workflowRunId = (Integer) workflowRunLaneAsMap.get("workflow_run_id");
                checkNotNull(workflowRunId);

                RelinkingTask re;
                if (relinkingTasksByWorkflowRunId.containsKey(workflowRunId)) {
                    re = relinkingTasksByWorkflowRunId.get(workflowRunId);
                } else {
                    re = new RelinkingTask(workflowRunId);
                    relinkingTasksByWorkflowRunId.put(workflowRunId, re);
                }

                Integer laneId = (Integer) workflowRunLaneAsMap.get("lane_id");
                Integer laneSwid = (Integer) workflowRunLaneAsMap.get("lane_swid");
                checkNotNull(laneId);
                checkNotNull(laneSwid);

                WorkflowRunLaneLink wrl = new WorkflowRunLaneLink();
                wrl.setWorkflowRunId(workflowRunId);
                wrl.setLaneId(laneId);
                wrl.setLaneSwid(laneSwid);

                re.addWorkflowRunLane(wrl);
            }

            //get all Processing-Lane links
            List processingLaneRecordList = session.createSQLQuery(IOUtils.toString(this.getClass().getResourceAsStream("processing_lanes.sql"), "UTF-8"))
                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                    .list();
            for (Object processingLaneRecord : processingLaneRecordList) {
                Map processingLaneRecordAsMap = (Map) processingLaneRecord;
                Integer workflowRunId = (Integer) processingLaneRecordAsMap.get("workflow_run_id");
                checkNotNull(workflowRunId);

                RelinkingTask relinkingTask = relinkingTasksByWorkflowRunId.get(workflowRunId);
                checkNotNull(relinkingTask);

                Integer laneId = (Integer) processingLaneRecordAsMap.get("lane_id");
                Integer laneSwid = (Integer) processingLaneRecordAsMap.get("lane_swid");
                Integer processingId = (Integer) processingLaneRecordAsMap.get("processing_id");
                checkNotNull(laneId);
                checkNotNull(laneSwid);
                checkNotNull(processingId);

                ProcessingLaneLink pll = new ProcessingLaneLink();
                pll.setWorkflowRunId(workflowRunId);
                pll.setLaneId(laneId);
                pll.setLaneSwid(laneSwid);
                pll.setProcessingId(processingId);

                relinkingTask.addProcessingLaneLink(pll);
            }
        } finally {
            if (preloadTx != null) {
                preloadTx.rollback(); //rollback, the above "transaction" was read only
            }
        }

        System.out.println("Preloading completed in " + timer.stop());
    }

    private void validate() {
        System.out.println("Validation started");
        Stopwatch timer = Stopwatch.createStarted();
        int workflowRunsAlreadyLinked = 0;
        for (RelinkingTask relinkingTask : relinkingTasksByWorkflowRunId.values()) {
            //don't process workflow runs that have IUS-LimsKey links already
            if (relinkingTask.hasIusLimsKeyLinks()) {
                Log.debug("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] already has IUS-LimsKey links");
                workflowRunsAlreadyLinked++;
                continue;
            }

            //workflow run is a candidate for relinking - the following checks verify if the following required assumptions hold:
            // 1) all IUS/Lanes associated with the workflow run have an associated SampleProvenance/LaneProvenance
            // 2) all Processing-IUS/Processing-Lane links are a subset of WorkflowRun-IUS/WorkflowRun-Lane links
            boolean okayToRelinkWorkflowRun = true;

            // 1) all IUS/Lanes associated with the workflow run have an associated SampleProvenance/LaneProvenance
            for (Integer iusSwid : relinkingTask.getWorkflowRunIusSwids()) {
                if (sps.get(iusSwid.toString()) == null) {
                    okayToRelinkWorkflowRun = false;
                    Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Not able to map IUS to SP");
                }
            }
            for (Integer laneSwid : relinkingTask.getWorkflowRunLaneSwids()) {
                if (lps.get(laneSwid.toString()) == null) {
                    okayToRelinkWorkflowRun = false;
                    Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Not able to map Lane to LP");
                }
            }

            // 2) all Processing-IUS/Processing-Lane links are a subset of WorkflowRun-IUS/WorkflowRun-Lane links
            if (!relinkingTask.getWorkflowRunIusSwids().containsAll(relinkingTask.getProcessingIusLinkSwids())) {
                okayToRelinkWorkflowRun = false;
                Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Processing-IUS links set contains records that are not in WorkflowRun-IUS links set");
            }
            if (!relinkingTask.getWorkflowRunLaneSwids().containsAll(relinkingTask.getProcessingLaneLinkSwids())) {
                okayToRelinkWorkflowRun = false;
                Log.warn("WorkflowRun[id=" + relinkingTask.getWorkflowRunId() + "] NOT RELINKED: Processing-Lane links set contains records that are not in WorkflowRun-Lane links set");
            }

            //add workflow run id to exception set and do not relink workflow run if assumptions do not hold
            if (!okayToRelinkWorkflowRun) {
                workflowRunIdsThatCannotBeRelinked.add(relinkingTask.workflowRunId);
            } else {
                validRelinkingTasks.add(relinkingTask);
            }
        }

        summary.setWorkflowRunsAlreadyLinked(workflowRunsAlreadyLinked);
        summary.setWorkflowRunsAnalyzed(relinkingTasksByWorkflowRunId.size());
        summary.setWorkflowRunsToBeRelinked(validRelinkingTasks.size());
        summary.setWorkflowRunsErrorState(workflowRunIdsThatCannotBeRelinked.size());

        System.out.println("Validation completed in " + timer.stop());
    }

    //relink WorkflowRun and Processings to the appropriate IUS-LimsKey
    private void relink() {
        System.out.println("Relinking started");
        Stopwatch timer = Stopwatch.createStarted();
        Stopwatch timerBatch = Stopwatch.createStarted();

        int workflowRunsRelinked = 0;
        int iter = 0;
        int totalSize = validRelinkingTasks.size();

        for (RelinkingTask relinkingTask : validRelinkingTasks) {
            //progress reporting
            if (++iter % 5000 == 0) {
                long batchDuration = timerBatch.stop().elapsed(TimeUnit.MILLISECONDS) + 1;
                System.out.println("Relinking progress: " + iter + "/" + totalSize + " (" + 1000 * 5000 / batchDuration + "recs/sec)");
                timerBatch = Stopwatch.createStarted();
            }

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Map<Integer, Integer> swidToIusLimsKey = new HashMap<>();

                //create all required IUS-LimsKeys
                for (Integer swid : relinkingTask.getWorkflowRunLinkSwids()) {
                    String swidAsString = Integer.toString(swid);

                    LimsKey lk = new LimsKey();
                    lk.setProvider("seqware");
                    lk.setCreateTimestamp(timestamp);
                    lk.setUpdateTimestamp(timestamp);
                    if (sps.containsKey(swidAsString)) {
                        SampleProvenance sp = sps.get(swidAsString);
                        lk.setLastModified(sp.getLastModified());
                        lk.setVersion(sp.getVersion());
                        lk.setId(sp.getSampleProvenanceId());
                    } else if (lps.containsKey(swidAsString)) {
                        LaneProvenance lp = lps.get(swidAsString);
                        lk.setLastModified(lp.getLastModified());
                        lk.setVersion(lp.getVersion());
                        lk.setId(lp.getLaneProvenanceId());
                    } else {
                        throw new RuntimeException("WorkflowRun link with SWID = [" + swid + "] can not be mapped to SampleProvenance or LaneProvenance");
                    }

                    IUS ius = new IUS();
                    ius.setLimsKey(lk);
                    ius.setCreateTimestamp(timestamp);
                    ius.setUpdateTimestamp(timestamp);

                    session.insert(lk);
                    session.insert(ius);

                    //link workflow run to the new IUS-LimsKey
                    session.createSQLQuery("insert into ius_workflow_runs(ius_id,workflow_run_id) values (:iusId,:workflowRunId)")
                            .setParameter("iusId", ius.getIusId())
                            .setParameter("workflowRunId", relinkingTask.getWorkflowRunId())
                            .executeUpdate();

                    //mapping of SWID to IusLimsKey to be used when linking Processing to IUS-LimsKey
                    if (swidToIusLimsKey.put(swid, ius.getIusId()) != null) {
                        throw new RuntimeException("Duplicate input WorkflowRun links found");
                    }
                }

                //link processing to IUS-LimsKey(s)
                for (Integer processingId : relinkingTask.getProcessingIds()) {

                    Set<? extends ProcessingLink> processingLinks;
                    if (!relinkingTask.getProcessingIusLinksFromProcessingId(processingId).isEmpty()) {
                        //if the existing Processing is linked to IUS and Lane, only relink to IUS-LimsKey(s) for SampleProvenance
                        processingLinks = relinkingTask.getProcessingIusLinksFromProcessingId(processingId);
                    } else if (!relinkingTask.getProcessingLaneLinksFromProcessingId(processingId).isEmpty()) {
                        //no IUS links, link IUS-LimsKey(s) for LaneProvenance
                        processingLinks = relinkingTask.getProcessingLaneLinksFromProcessingId(processingId);
                    } else {
                        //no processing links, do not do any Processing linking
                        processingLinks = Collections.EMPTY_SET;
                    }

                    //link processing to the new IUS-LimsKey
                    for (ProcessingLink pll : processingLinks) {
                        Integer iusId = swidToIusLimsKey.get(pll.getSwid());
                        session.createSQLQuery("insert into processing_ius(ius_id,processing_id) values (:iusId,:processingId)")
                                .setParameter("iusId", iusId)
                                .setParameter("processingId", pll.getProcessingId())
                                .executeUpdate();
                    }
                }
                tx.commit();
                workflowRunsRelinked++;
            } catch (RuntimeException re) {
                if (tx != null) {
                    tx.rollback();
                }
                throw re;
            }
        }

        summary.setWorkflowRunsRelinked(workflowRunsRelinked);

        System.out.println("Relinking completed in " + timer.stop());
    }

    private void printSummary() {
        if (!workflowRunIdsThatCannotBeRelinked.isEmpty()) {
            Log.warn(workflowRunIdsThatCannotBeRelinked.size() + " workflow runs were not relinked:\n" + Joiner.on(",").join(workflowRunIdsThatCannotBeRelinked));
        }
    }

    public Summary run() throws IOException {
        try {
            setup();
            preload();
            validate();
            if (validRelinkingTasks.isEmpty()) {
                System.out.println("Relinking skipped - there are no relinking tasks");
            } else if (!doRelinking) {
                System.out.println("Relinking skipped - dry run mode enabled");
            } else {
                relink();
            }
            printSummary();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return summary;
    }

    private class RelinkingTask {

        private Integer workflowRunId;
        private final SetMultimap<Integer, WorkflowRunIusLink> workflowRunIusLinks = HashMultimap.create();
        private final SetMultimap<Integer, WorkflowRunLaneLink> workflowRunLaneLinks = HashMultimap.create();
        private final SetMultimap<Integer, ProcessingIusLink> processingIusLinks = HashMultimap.create();
        private final SetMultimap<Integer, ProcessingLaneLink> processingLaneLinks = HashMultimap.create();

        public RelinkingTask(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public void addWorkflowRunIus(WorkflowRunIusLink wri) {
            this.workflowRunIusLinks.put(wri.getIusSwid(), wri);
        }

        public void addWorkflowRunLane(WorkflowRunLaneLink wrl) {
            this.workflowRunLaneLinks.put(wrl.getLaneSwid(), wrl);
        }

        public void addProcessingIusLink(ProcessingIusLink pil) {
            this.processingIusLinks.put(pil.getProcessingId(), pil);
        }

        public void addProcessingLaneLink(ProcessingLaneLink pll) {
            this.processingLaneLinks.put(pll.getProcessingId(), pll);
        }

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public Set<Integer> getWorkflowRunLinkSwids() {
            Set<Integer> swids = new HashSet<>();
            for (WorkflowRunIusLink wril : workflowRunIusLinks.values()) {
                swids.add(wril.getIusSwid());
            }
            for (WorkflowRunLaneLink wrll : workflowRunLaneLinks.values()) {
                swids.add(wrll.getLaneSwid());
            }
            return swids;
        }

        public Set<Integer> getProcessingIds() {
            Set<Integer> processingIds = new HashSet<>();
            for (ProcessingIusLink pil : processingIusLinks.values()) {
                processingIds.add(pil.getProcessingId());
            }
            for (ProcessingLaneLink pll : processingLaneLinks.values()) {
                processingIds.add(pll.getProcessingId());
            }
            return processingIds;
        }

        public Set<Integer> getWorkflowRunIusSwids() {
            Set<Integer> swids = new HashSet<>();
            for (WorkflowRunIusLink wril : workflowRunIusLinks.values()) {
                swids.add(wril.getIusSwid());
            }
            return swids;
        }

        public Set<Integer> getWorkflowRunLaneSwids() {
            Set<Integer> swids = new HashSet<>();
            for (WorkflowRunLaneLink wrll : workflowRunLaneLinks.values()) {
                swids.add(wrll.getLaneSwid());
            }
            return swids;
        }

        public Set<Integer> getProcessingIusLinkSwids() {
            Set<Integer> swids = new HashSet<>();
            for (ProcessingIusLink pil : processingIusLinks.values()) {
                swids.add(pil.getIusSwid());
            }
            return swids;
        }

        public Set<Integer> getProcessingLaneLinkSwids() {
            Set<Integer> swids = new HashSet<>();
            for (ProcessingLaneLink pll : processingLaneLinks.values()) {
                swids.add(pll.getLaneSwid());
            }
            return swids;
        }

        public Set<ProcessingIusLink> getProcessingIusLinks() {
            return new HashSet<>(processingIusLinks.values());
        }

        public Set<ProcessingLaneLink> getProcessingLaneLinks() {
            return new HashSet<>(processingLaneLinks.values());
        }

        public Set<ProcessingIusLink> getProcessingIusLinksFromProcessingId(Integer processingId) {
            return processingIusLinks.get(processingId);
        }

        public Set<ProcessingLaneLink> getProcessingLaneLinksFromProcessingId(Integer processingId) {
            return processingLaneLinks.get(processingId);
        }

        public boolean hasIusLimsKeyLinks() {
            for (WorkflowRunIusLink workflowRunIusLink : workflowRunIusLinks.values()) {
                if (workflowRunIusLink.getLimsKeyId() != null) {
                    return true;
                }
            }
            return false;
        }

    }

    private class WorkflowRunLink {

        private Integer workflowRunId;
        private Integer swid;

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public void setWorkflowRunId(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public Integer getSwid() {
            return swid;
        }

        public void setSwid(Integer swid) {
            this.swid = swid;
        }

    }

    private class WorkflowRunIusLink extends WorkflowRunLink {

        private Integer iusId;
        private Integer iusSwid;
        private Integer limsKeyId;

        public Integer getIusId() {
            return iusId;
        }

        public void setIusId(Integer iusId) {
            this.iusId = iusId;
        }

        public Integer getIusSwid() {
            return iusSwid;
        }

        public void setIusSwid(Integer iusSwid) {
            this.setSwid(iusSwid);
            this.iusSwid = iusSwid;
        }

        public Integer getLimsKeyId() {
            return limsKeyId;
        }

        public void setLimsKeyId(Integer limsKeyId) {
            this.limsKeyId = limsKeyId;
        }

    }

    private class WorkflowRunLaneLink extends WorkflowRunLink {

        private Integer laneId;
        private Integer laneSwid;

        public Integer getLaneId() {
            return laneId;
        }

        public void setLaneId(Integer laneId) {
            this.laneId = laneId;
        }

        public Integer getLaneSwid() {
            return laneSwid;
        }

        public void setLaneSwid(Integer laneSwid) {
            this.setSwid(laneSwid);
            this.laneSwid = laneSwid;
        }

    }

    private class ProcessingLink {

        private Integer workflowRunId;
        private Integer processingId;
        private Integer swid;

        public Integer getWorkflowRunId() {
            return workflowRunId;
        }

        public void setWorkflowRunId(Integer workflowRunId) {
            this.workflowRunId = workflowRunId;
        }

        public Integer getProcessingId() {
            return processingId;
        }

        public void setProcessingId(Integer processingId) {
            this.processingId = processingId;
        }

        public Integer getSwid() {
            return swid;
        }

        public void setSwid(Integer swid) {
            this.swid = swid;
        }

    }

    private class ProcessingIusLink extends ProcessingLink {

        private Integer iusId;
        private Integer iusSwid;

        public Integer getIusId() {
            return iusId;
        }

        public void setIusId(Integer iusId) {
            this.iusId = iusId;
        }

        public Integer getIusSwid() {
            return iusSwid;
        }

        public void setIusSwid(Integer iusSwid) {
            this.setSwid(iusSwid);
            this.iusSwid = iusSwid;
        }

    }

    private class ProcessingLaneLink extends ProcessingLink {

        private Integer laneId;
        private Integer laneSwid;

        public Integer getLaneId() {
            return laneId;
        }

        public void setLaneId(Integer laneId) {
            this.laneId = laneId;
        }

        public Integer getLaneSwid() {
            return laneSwid;
        }

        public void setLaneSwid(Integer laneSwid) {
            this.setSwid(laneSwid);
            this.laneSwid = laneSwid;
        }

    }

    public static class Summary {

        private int workflowRunsAnalyzed;
        private int workflowRunsAlreadyLinked;
        private int workflowRunsToBeRelinked;
        private int workflowRunsRelinked;
        private int workflowRunsErrorState;

        public int getWorkflowRunsAnalyzed() {
            return workflowRunsAnalyzed;
        }

        public void setWorkflowRunsAnalyzed(int workflowRunsAnalyzed) {
            this.workflowRunsAnalyzed = workflowRunsAnalyzed;
        }

        public int getWorkflowRunsAlreadyLinked() {
            return workflowRunsAlreadyLinked;
        }

        public void setWorkflowRunsAlreadyLinked(int workflowRunsAlreadyLinked) {
            this.workflowRunsAlreadyLinked = workflowRunsAlreadyLinked;
        }

        public int getWorkflowRunsToBeRelinked() {
            return workflowRunsToBeRelinked;
        }

        public void setWorkflowRunsToBeRelinked(int workflowRunsToBeRelinked) {
            this.workflowRunsToBeRelinked = workflowRunsToBeRelinked;
        }

        public int getWorkflowRunsRelinked() {
            return workflowRunsRelinked;
        }

        public void setWorkflowRunsRelinked(int workflowRunsRelinked) {
            this.workflowRunsRelinked = workflowRunsRelinked;
        }

        public int getWorkflowRunsErrorState() {
            return workflowRunsErrorState;
        }

        public void setWorkflowRunsErrorState(int workflowRunsErrorState) {
            this.workflowRunsErrorState = workflowRunsErrorState;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

    }

    public static void main(String args[]) throws IOException, IllegalStateException, NamingException {
        OptionParser parser = new OptionParser();
        parser.accepts("help").forHelp();
        parser.accepts("do-relinking", "Execute the relinking process (dryrun is default)");
        parser.accepts("host", "Postgres DB host name/ip").withRequiredArg().required();
        parser.accepts("port", "Portgres DB port").withRequiredArg().required();
        parser.accepts("user", "Postgres DB user").withRequiredArg().required();
        parser.accepts("password", "Postgres DB password").withRequiredArg();
        parser.accepts("db-name", "Postgres DB name").withRequiredArg().required();
        parser.accepts("timestamp", "The timestamp that new IUS and LimsKey \"createdTstmp\" and \"updatedTstmp\" should be set to (eg. 2016-01-01T00:00:00) (Default: \"now\")").withRequiredArg();

        OptionSet options = parser.parse(args);

        if (options.has("help")) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        boolean doRelinking = false;
        if (options.has("do-relinking")) {
            doRelinking = true;
        }

        String host = options.valueOf("host").toString();
        String port = options.valueOf("port").toString();
        String user = options.valueOf("user").toString();
        String dbName = options.valueOf("db-name").toString();

        DateTime timestamp;
        if (options.has("timestamp")) {
            timestamp = DateTime.parse(options.valueOf("timestamp").toString());
        } else {
            timestamp = DateTime.now(DateTimeZone.UTC);
        }

        String password;
        if (options.has("password")) {
            password = options.valueOf("password").toString();
        } else {
            Console console = System.console();
            if (console != null) {
                password = new String(console.readPassword("Please enter Postgres DB password:\n"));
            } else {
                //support for running from within an ide
                InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                System.out.println("Please enter Postgres DB password:");
                password = reader.readLine();
            }
        }
        checkNotNull(password);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName);
        ds.setValidationQuery("SELECT 1");

        try {
            ds.getConnection().createStatement().execute("SELECT 1");
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage());
            System.exit(1);
        }

        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/jdbc/SeqWareMetaDB", ds);
        builder.activate();

        GenericApplicationContext ctx = new GenericXmlApplicationContext("applicationContext.xml");

        AbstractBeanDefinition someClassDefinition = BeanDefinitionBuilder.rootBeanDefinition(RelinkWorkflowRunIusAndProcessingsToLimsKey.class).getBeanDefinition();
        ctx.registerBeanDefinition("relinker", someClassDefinition);

        RelinkWorkflowRunIusAndProcessingsToLimsKey relinker = ctx.getBean(RelinkWorkflowRunIusAndProcessingsToLimsKey.class);
        relinker.setTimestamp(timestamp.toDate());
        relinker.setDoRelinking(doRelinking);
        Summary summary = relinker.run();
        System.out.println(summary.toString());
    }

}
