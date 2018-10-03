/*
 * Copyright (C) 2017 SeqWare
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
package io.seqware.pipeline.plugins;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Iterables;
import com.google.common.collect.Table;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataInMemory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;

/**
 *
 * @author mlaszloffy
 */
public class WorkflowReschedulerTest {

    private Map<String, String> config;
    private Metadata metadata;
    private Integer workflowRunSwid;
    private WorkflowRescheduler workflowRescheduler;

    @Before
    public void init() throws Exception {
        workflowRescheduler = new WorkflowRescheduler();
        metadata = new MetadataInMemory();
        config = new HashMap<>();
        config.put("SW_METADATA_METHOD", "inmemory");

        ReturnValue workflowRv = metadata.addWorkflow("WorkflowName", "1.0", "", null, null, "", "/tmp", true, null, false, "/tmp", "java", "Oozie", "1.x");
        Integer workflowSwid = Integer.parseInt(workflowRv.getAttribute("sw_accession"));

        Integer workflowRunId = metadata.add_workflow_run(workflowSwid);
        workflowRunSwid = metadata.get_workflow_run_accession(workflowRunId);

        Integer limsKeySwid = metadata.addLimsKey("provider", "id", "version", ZonedDateTime.now());
        Integer iusSwid1 = metadata.addIUS(limsKeySwid, false);
        Integer iusSwid2 = metadata.addIUS(limsKeySwid, false);

        metadata.linkWorkflowRunAndParent(workflowRunSwid, iusSwid1);
        metadata.linkWorkflowRunAndParent(workflowRunSwid, iusSwid2);
    }

  @After
    public void reset() {
        Whitebox.<Table>getInternalState(MetadataInMemory.class, "STORE").clear();
    }

    @Test
    public void rescheduleWorkflowRun() {
        List<String> params = Arrays.asList("--host", "host", "--workflow-run", workflowRunSwid.toString());

        ReturnValue rv = runPlugin(workflowRescheduler, params);
        assertEquals(ReturnValue.SUCCESS, rv.getExitStatus());

        assertEquals(1, workflowRescheduler.getRescheduledWorkflowRunSwids().size());

        Integer newWorkflowRunSwid = Iterables.getOnlyElement(workflowRescheduler.getRescheduledWorkflowRunSwids());
        WorkflowRun newWr = metadata.getWorkflowRunWithIuses(newWorkflowRunSwid);

        assertEquals(newWr.getIus().size(), 2);
    }

    private ReturnValue runPlugin(Plugin instance, List<String> params) {
        instance.setParams(params);
        instance.setConfig(config);
        instance.setMetadata(metadata);

        ReturnValue parseRv = instance.parse_parameters();
        if (ReturnValue.SUCCESS != parseRv.getExitStatus()) {
            return parseRv;
        }
        ReturnValue initRv = instance.init();
        if (ReturnValue.SUCCESS != initRv.getExitStatus()) {
            return initRv;
        }
        ReturnValue testRv = instance.do_test();
        if (ReturnValue.SUCCESS != testRv.getExitStatus()) {
            return testRv;
        }
        ReturnValue runRv = instance.do_run();
        if (ReturnValue.SUCCESS != runRv.getExitStatus()) {
            return runRv;
        }
        ReturnValue cleanRv = instance.clean_up();
        if (ReturnValue.SUCCESS != cleanRv.getExitStatus()) {
            return cleanRv;
        }

        return runRv;
    }

}
