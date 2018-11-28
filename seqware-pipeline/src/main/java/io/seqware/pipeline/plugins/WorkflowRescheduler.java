package io.seqware.pipeline.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openide.util.lookup.ServiceProvider;

import io.seqware.Engines;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import joptsimple.ArgumentAcceptingOptionSpec;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;

import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Workflow Rescheduler can schedule a new workflow based on the configuration of a previously launched workflow.
 * 
 * This will typically be used to re-schedule failed workflow runs that should be re-run totally from scratch. A new workflow will be
 * re-scheduled using the same parameters that a specified workflow-run used.
 * 
 * @author dyuen
 * @version 1.1.0
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRescheduler extends Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRescheduler.class);

    public static final String INPUT_FILES = "input-files";
    private final ArgumentAcceptingOptionSpec<String> workflowEngineSpec;
    private final ArgumentAcceptingOptionSpec<String> hostSpec;
    private final ArgumentAcceptingOptionSpec<String> outFileSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowRunSpec;
  private final List<Integer> rescheduledWorkflowRunSwids = new ArrayList<>();

    public WorkflowRescheduler() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        this.hostSpec = parser.acceptsAll(Arrays.asList("host", "ho"), "Used to schedule onto a specific host").withRequiredArg()
                .ofType(String.class);
        this.workflowRunSpec = parser
                .acceptsAll(Arrays.asList("workflow-run", "wr"),
                        "Required: specify workflow-run(s) by swid, comma-delimited, to re-schedule").withRequiredArg().required()
                .withValuesSeparatedBy(",");
        this.workflowEngineSpec = WorkflowScheduler.createWorkflowEngineSpec(parser);
        this.outFileSpec = parser.acceptsAll(Arrays.asList("out"), "Optional: Will output a workflow-run by sw_accession")
                .withRequiredArg();
    }

  public List<Integer> getRescheduledWorkflowRunSwids() {
    return rescheduledWorkflowRunSwids;
  }

    private String getEngineParam() {
        String engine = options.valueOf(workflowEngineSpec);
        if (engine == null) {
            engine = config.get(SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
        }
        if (engine == null) {
            engine = Engines.DEFAULT_ENGINE;
        }

        return engine;
    }

    @Override
    public ReturnValue init() {
        if (options.has(workflowEngineSpec)) {
            return validateEngineString(options.valueOf(workflowEngineSpec));
        }
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    public static ReturnValue validateEngineString(String engine) {
        if (!Engines.ENGINES.contains(engine)) {
            LOGGER.error("Invalid workflow-engine value. Must be one of: " + Engines.ENGINES_LIST);
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
        }
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    /*
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public String get_description() {
        return "A plugin that lets you re-schedule previously launched workflow runs.";
    }

    @Override
    public ReturnValue do_run() {
        try {
            File outputFile = null;
            if (options.has(this.outFileSpec)) {
                outputFile = new File(options.valueOf(this.outFileSpec));
            }

            if (options.has(workflowRunSpec)) {

                List<String> workflowRunSWIDs = options.valuesOf(this.workflowRunSpec);
                for (String runSWID : workflowRunSWIDs) {

          WorkflowRun oldWorkflowRun = metadata.getWorkflowRunWithIuses(Integer.parseInt(runSWID));

          if (oldWorkflowRun == null) {
            LOGGER.error("Workflow run SWID = [" + runSWID + "] not found.");
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
          }

          // get IUSes that the old workflow run is linked to
          List<Integer> linkedIusSwids = new ArrayList<>();
          if (oldWorkflowRun.getIus() == null || oldWorkflowRun.getIus().isEmpty()) {
            LOGGER.warn("Workflow run [" + runSWID + "] does not have any linked IUSes.");
          } else {
            for (IUS ius : oldWorkflowRun.getIus()) {
              linkedIusSwids.add(ius.getSwAccession());
            }
          }
                    // create a new workflow run
                    int newWorkflowRunID = this.metadata.add_workflow_run(oldWorkflowRun.getWorkflowAccession());
                    // this translation here is ugly, do we still need to do this?
                    int workflowRunAccessionInt = this.metadata.get_workflow_run_accession(newWorkflowRunID);
                    WorkflowRun newWorkflowRun = metadata.getWorkflowRun(workflowRunAccessionInt);
                    System.out.println("Created workflow run with SWID: " + workflowRunAccessionInt);

                    // have the old workflow run mimic the new one and upload for re-launching
                    oldWorkflowRun.setWorkflowRunId(newWorkflowRunID);
                    oldWorkflowRun.setSwAccession(workflowRunAccessionInt);
                    oldWorkflowRun.setCreateTimestamp(newWorkflowRun.getCreateTimestamp());
                    oldWorkflowRun.setStatus(WorkflowRunStatus.submitted);
                    // null out stuff that doesn't make sense to copy over
                    oldWorkflowRun.setStatusCmd(null);
                    oldWorkflowRun.setCurrentWorkingDir(null);
                    oldWorkflowRun.setDax(null);
                    oldWorkflowRun.setStdOut(null);
                    oldWorkflowRun.setStdErr(null);
          oldWorkflowRun.setIus(null);
          oldWorkflowRun.setLanes(null);

                    // override host and engine if needed
                    if (options.has(hostSpec)) {
                        String host = options.valueOf(hostSpec);
                        oldWorkflowRun.setHost(host);
                    }
                    if (options.has(workflowEngineSpec)) {
                        String engine = getEngineParam();
                        oldWorkflowRun.setWorkflowEngine(engine);
                    }

                    LOGGER.info("You are re-scheduling workflow-run " + runSWID + " to " + workflowRunAccessionInt);
                    this.metadata.updateWorkflowRun(oldWorkflowRun);
          rescheduledWorkflowRunSwids.add(workflowRunAccessionInt);

          // link new workflow run to the old workflow run's IUSes
          for (Integer iusSwid : linkedIusSwids) {
            try {
              this.metadata.linkWorkflowRunAndParent(newWorkflowRunID, iusSwid);
            } catch (Exception e) {
              LOGGER.error("Could not link workflow run SWID = [" + runSWID + "] to its parents: " + linkedIusSwids.toString());
              throw Rethrow.rethrow(e);
            }
          }

                    if (options.has(outFileSpec)) {
                        FileUtils.write(outputFile, String.valueOf(newWorkflowRun) + "\n", StandardCharsets.UTF_8,true);
                    }
                }

            } else {
                LOGGER.error("I don't understand the combination of arguments you gave!");
                LOGGER.info(this.get_syntax());
                return new ReturnValue(ExitStatus.INVALIDARGUMENT);
            }
        } catch (IOException ex) {
            return new ReturnValue(ExitStatus.FILENOTWRITABLE);
        }
        return new ReturnValue();
    }
}
