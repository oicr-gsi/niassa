/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.openide.util.lookup.ServiceProvider;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.ReturnValue;

import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a shell script to re-run an existing workflow run
 * 
 * @author apmasell, mtaschuk, boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRunReRunner extends Plugin {
    private final Logger logger = LoggerFactory.getLogger(WorkflowRunReRunner.class);

	private ReturnValue ret = new ReturnValue();

	public WorkflowRunReRunner() {
		super();
		parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"), "The SWID of the workflow run")
				.withRequiredArg();
		ret.setExitStatus(ReturnValue.SUCCESS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public ReturnValue init() {
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public ReturnValue do_test() {
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public ReturnValue do_run() {
		String workflowRunAccession = (String) options.valueOf("workflow-run-accession");
		WorkflowRun wr;
		try {
			wr = metadata.getWorkflowRunWithIuses(Integer.parseInt(workflowRunAccession));
			if (wr == null) {
				ret = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
				return ret;
			}
		} catch (RuntimeException e) {
			println("Workflow run not found");
			e.printStackTrace();
			ret = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
			return ret;
		}
		String iniFileName = String.format("wr-%s.ini", workflowRunAccession);
		String shFileName = String.format("wr-%s.sh", workflowRunAccession);
		try (PrintWriter iniFile = new PrintWriter(iniFileName); PrintWriter shFile = new PrintWriter(shFileName)) {
			iniFile.print(wr.getIniFile());

			shFile.println("#!/bin/sh");
			shFile.print(
					"WRA=$(seqware --plugin io.seqware.pipeline.plugins.WorkflowScheduler -- --workflow-accession ");
			shFile.print(wr.getWorkflowAccession());
			shFile.print(" --ini-files $(dirname $0)/");
			shFile.print(iniFileName);
			printAccessions(shFile, "input-files", wr.getInputFileAccessions());
			if (wr.getIus() != null)
				printAccessions(shFile, "link-workflow-run-to-parents",
						wr.getIus().stream().map(IUS::getSwAccession).collect(Collectors.toSet()));
			shFile.print(" --host ");
			shFile.print(wr.getHost());
			shFile.println(" | grep 'Created workflow run with SWID:' | cut -f 2 -d:)");
			for (WorkflowRunAttribute attribute : wr.getAnnotations()) {
				shFile.print(
						"seqware --plugin net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator -- --workflow-run-accession $WRA --key ");
				shFile.print(attribute.getTag());
				shFile.print(" --value '");
				shFile.print(attribute.getValue());
				shFile.println("'");
			}
			shFile.println("echo Workflow run SWID: $WRA");
			Files.setPosixFilePermissions(Paths.get(shFileName), PosixFilePermissions.fromString("rwxr-xr-x"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			ret.setExitStatus(ReturnValue.FILENOTREADABLE);
			ret.setDescription(e.getMessage());
		}
		return ret;
	}

	private void printAccessions(PrintWriter writer, String key, Set<Integer> accessions) {
		if (!accessions.isEmpty()) {
			writer.print(" --");
			writer.print(key);
			writer.print(" ");
			boolean first = true;
			for (int accession : accessions) {
				if (first) {
					first = false;
				} else {
					writer.print(",");
				}
				writer.print(accession);
			}
			writer.print(" ");
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public ReturnValue clean_up() {
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public String get_description() {
		return "This plugin recreates an WorkflowScheduler command to re-run an existing workflow run.";
	}
}
