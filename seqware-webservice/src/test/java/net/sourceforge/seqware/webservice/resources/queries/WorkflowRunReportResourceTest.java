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
package net.sourceforge.seqware.webservice.resources.queries;

import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 *
 * @author mlaszloffy
 */
public class WorkflowRunReportResourceTest extends DatabaseResourceTest {

	public WorkflowRunReportResourceTest() {
		super("/reports/workflowruns");
	}

	@Ignore
	@Override
	public void testDelete() {
	}

	@Ignore
	@Override
	public void testPost() {
	}

	@Ignore
	@Override
	public void testPut() {
	}

	@Override
	public void testGet() {
		ClientResource cr = null;
		Representation rep = null;
		try {
			cr = ClientResourceInstance.getChild("/reports/workflowruns/6819");
			rep = cr.get();
			String result = rep.getText();
			System.out.println(result);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			if (rep != null) {
				try {
					rep.exhaust();
				} catch (IOException e) {
					Assert.fail(e.getMessage());
				}
				rep.release();
			}
			if (cr != null) {
				cr.release();
			}
		}
	}

	@Test
	public void testGetFromWorkflowSwid() {
		ClientResource cr = null;
		Representation rep = null;
		try {
			cr = ClientResourceInstance.getChild("/reports/workflow/6595/runs");
			rep = cr.get();
			String result = rep.getText();
			System.out.println(result);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			if (rep != null) {
				try {
					rep.exhaust();
				} catch (IOException e) {
					Assert.fail(e.getMessage());
				}
				rep.release();
			}
			if (cr != null) {
				cr.release();
			}
		}
	}

}
