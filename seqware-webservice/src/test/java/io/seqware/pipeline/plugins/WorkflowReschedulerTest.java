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

import com.google.common.collect.Iterables;
import io.seqware.common.model.WorkflowRunStatus;
import java.util.List;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ExtendedPluginTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * WorkflowReschedule (from seqware-pipeline) WS test
 *
 * @author mlaszloffy
 */
public class WorkflowReschedulerTest extends ExtendedPluginTest {

    private BasicTestDatabaseCreator dbCreator;
    private WorkflowRescheduler workflowRescheduler;

    @Before
    @Override
    public void setUp() {
        dbCreator = BasicTestDatabaseCreator.getFromSystemProperties();
        dbCreator.resetDatabaseWithUsers();

        workflowRescheduler = new WorkflowRescheduler();
        instance = workflowRescheduler;
        super.setUp();
    }

    @Test
    public void testRechedule1() {
        runRescheduleTest(872); //Novalign WR with duplicate IUS links (two links to IUS ID = 5)
    }

    @Test
    public void testRechedule2() {
        runRescheduleTest(6819); //GATK WR with a lot of missing info
    }

    // The following two tests are skipped due to the following reason:
    // WRs in submitted status can not be rescheduled - the webservice errors out when parsing the ini of submitted WRs
    // Rescheduling submitted workflow runs doesn't really make sense though
    @Ignore
    @Test
    public void testRechedule3() {
        runRescheduleTest(6683); //WR in submitted status
    }

    @Ignore
    @Test
    public void testRechedule4() {
        runRescheduleTest(6684); //WR in submitted status
    }

    @Test
    public void testRechedule5() {
        runRescheduleTest(863); //WR with no IUS links
    }

    @Test(expected = AssertionError.class)
    public void testRescheduleNotValidWorkflowRun() {
        launchPlugin("--workflow-run", "0");
    }

    private void runRescheduleTest(Integer targetWorkflowRunSwid) {
        WorkflowRun oldWr = metadata.getWorkflowRunWithIuses(targetWorkflowRunSwid);

        //reschedule the workflow run
        launchPlugin("--workflow-run", targetWorkflowRunSwid.toString());

        List<Integer> rescheduledWorkflowRunSwids = workflowRescheduler.getRescheduledWorkflowRunSwids();
        WorkflowRun newWr = metadata.getWorkflowRunWithIuses(Iterables.getOnlyElement(rescheduledWorkflowRunSwids));

        assertEquals(newWr.getStatus(), WorkflowRunStatus.submitted);
        assertEquals(oldWr.getInputFileAccessions(), newWr.getInputFileAccessions());
        assertEquals(oldWr.getIus(), newWr.getIus());
        assertEquals(oldWr.getIniFile(), newWr.getIniFile());
        assertEquals(oldWr.getWorkflow(), newWr.getWorkflow());
        assertNotEquals(oldWr.getCreateTimestamp(), newWr.getCreateTimestamp());
        assertNotEquals(oldWr.getSwAccession(), newWr.getSwAccession());
    }
}
