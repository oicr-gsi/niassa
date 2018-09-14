/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.pipeline.runner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.ExitException;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ExtendedPluginTest;
import net.sourceforge.seqware.pipeline.plugins.ModuleRunner;

/**
 * Runs tests for the ModuleRunner class.
 *
 * @author dyuen
 */

public class ModuleRunnerTest extends ExtendedPluginTest {

	@BeforeClass
	public static void beforeClass() {
		new BasicTestDatabaseCreator().resetDatabaseWithUsers();
	}

	@Before
	@Override
	public void setUp() {
		instance = new ModuleRunner();
		super.setUp();
	}

	public ModuleRunnerTest() {
	}

	@Test
	public void normalParentAccessionRunTest() throws IOException {
		System.out.println("normalParentAccessionRunTest");
		try {
			Path parentAccession = Files.createTempFile("accession", "test");
			FileUtils.write(parentAccession.toFile(), "836", StandardCharsets.UTF_8);
			launchPlugin("--metadata", "--metadata-parent-accession-file", parentAccession.toAbsolutePath().toString(),
					"--module", "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner", "--",
					"--gcr-algorithm", "bash_cp", "--gcr-command", "echo");
		} catch (ExitException e) {
			assertEquals(e.getExitCode(), ReturnValue.SUCCESS);
		}
	}

	@Test
	public void normalAccessionFileRunTest() throws IOException {
		System.out.println("normalAccessionFileRunTest");
		try {
			Path resultAccession = Files.createTempFile("accession", "test");
			launchPlugin("--metadata", "--metadata-processing-accession-file",
					resultAccession.toAbsolutePath().toString(), "--module",
					"net.sourceforge.seqware.pipeline.modules.GenericCommandRunner", "--", "--gcr-algorithm", "bash_cp",
					"--gcr-command", "echo");
		} catch (ExitException e) {
			assertEquals(e.getExitCode(), ReturnValue.SUCCESS);
		}
	}

	@Test
	public void testBlankParentAccessionFileFail() throws IOException {
		System.out.println("testBlankParentAccessionFile");
		try {
			Path parentAccession = Files.createTempFile("accession", "test");
			launchPlugin("--metadata", "--metadata-parent-accession-file", parentAccession.toAbsolutePath().toString(),
					"--module", "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner", "--",
					"--gcr-algorithm", "bash_cp", "--gcr-command", "echo");
		} catch (ExitException e) {
			assertEquals(e.getExitCode(), ReturnValue.METADATAINVALIDIDCHAIN);
		}

	}

	@Test
	public void testInvalidAccessionFileFail() throws IOException {
		System.out.println("testInvalidAccessionFile");
		try {
			Path parentAccession = Files.createTempFile("accession", "test");
			FileUtils.write(parentAccession.toFile(), "-1", StandardCharsets.UTF_8);
			launchPlugin("--metadata", "--metadata-parent-accession-file", parentAccession.toAbsolutePath().toString(),
					"--module", "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner", "--",
					"--gcr-algorithm", "bash_cp", "--gcr-command", "echo");
		} catch (ExitException e) {
			assertEquals(e.getExitCode(), ReturnValue.SQLQUERYFAILED);
		}
	}

}
