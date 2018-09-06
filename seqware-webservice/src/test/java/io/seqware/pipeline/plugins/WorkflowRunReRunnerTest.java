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

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ExtendedPluginTest;
import net.sourceforge.seqware.pipeline.plugins.WorkflowRunReRunner;

/**
 * WorkflowRunReRunner (from seqware-pipeline) WS test
 *
 * @author amasella, mlaszloffy
 */
public class WorkflowRunReRunnerTest extends ExtendedPluginTest {

	private BasicTestDatabaseCreator dbCreator;
	private WorkflowRunReRunner workflowRescheduler;

	@Before
	@Override
	public void setUp() {
		dbCreator = BasicTestDatabaseCreator.getFromSystemProperties();
		dbCreator.resetDatabaseWithUsers();

		workflowRescheduler = new WorkflowRunReRunner();
		instance = workflowRescheduler;
		super.setUp();
	}

	@Test
	public void testRerun1() throws FileNotFoundException, IOException {
		runRerunTest(872); // Novalign WR with duplicate IUS links (two links to IUS ID = 5)
	}

	@Test
	public void testRerun2() throws FileNotFoundException, IOException {
		runRerunTest(6819); // GATK WR with a lot of missing info
	}

	@Test
	public void testRecrun3() throws FileNotFoundException, IOException {
		runRerunTest(863); // WR with no IUS links
	}

	@Test(expected = AssertionError.class)
	public void testRescheduleNotValidWorkflowRun() {
		launchPlugin("--workflow-run-accession", "0");
	}

	private void runRerunTest(int targetWorkflowRunSwid) throws FileNotFoundException, IOException {
		String iniFileName = String.format("wr-%d.ini", targetWorkflowRunSwid);
		String shFileName = String.format("wr-%d.sh", targetWorkflowRunSwid);
		
		launchPlugin("--workflow-run-accession", Integer.toString(targetWorkflowRunSwid));
		assertTrue("The INI files differ!", IOUtils.contentEquals(new FileInputStream(iniFileName),
				WorkflowRunReRunnerTest.class.getResourceAsStream(iniFileName)));
		assertTrue("The shell scripts differ!", IOUtils.contentEquals(new FileInputStream(shFileName),
				WorkflowRunReRunnerTest.class.getResourceAsStream(shFileName)));
	}
}
