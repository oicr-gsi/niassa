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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResourceTest extends DatabaseResourceTest {

	public AnalysisProvenanceResourceTest() {
		super("/reports/analysis-provenance");
	}

	@Ignore
	@Override
	public void testDelete() {
		// super.testDelete();
	}

	@Ignore
	@Override
	public void testPost() {
		// super.testPost();
	}

	@Ignore
	@Override
	public void testPut() {
		// super.testPut();
	}

	@Test
	public void testFilterGet() throws IOException, SAXException {
		List<AnalysisProvenanceDto> dtos;
		ClientResource cr = null;
		Representation rep = null;
		try {
			cr = ClientResourceInstance.getChild("/reports/analysis-provenance?workflow=4767");
			rep = cr.get();
			AnalysisProvenanceDtoList dtoList = (AnalysisProvenanceDtoList) XmlTools.unMarshal(new JaxbObject<>(),
					AnalysisProvenanceDtoList.class, rep.getText());
			dtos = dtoList.getAnalysisProvenanceDtos();

		} finally {
			if (rep != null) {
				rep.exhaust();
				rep.release();
			}
			if (cr != null) {
				cr.release();
			}
		}
		assertEquals(3, dtos.size());
		for (AnalysisProvenanceDto dto : dtos) {
			assertEquals("GenomicAlignmentNovoalign", dto.getWorkflowName());
		}
	}

	@Test
	public void testFilterPost() throws IOException, SAXException {
		List<AnalysisProvenanceDto> dtos;
		ClientResource cr = null;
		Representation rep = null;

		Map<FileProvenanceFilter, Set<String>> filters = new HashMap<>();
		filters.put(FileProvenanceFilter.workflow, ImmutableSet.of("4767"));

		ObjectMapper mapper = new ObjectMapper();
		try {
			cr = ClientResourceInstance.getChild("/reports/analysis-provenance");
			rep = cr.post(mapper.writeValueAsString(filters));
			AnalysisProvenanceDtoList dtoList = (AnalysisProvenanceDtoList) XmlTools.unMarshal(new JaxbObject<>(),
					AnalysisProvenanceDtoList.class, rep.getText());
			dtos = dtoList.getAnalysisProvenanceDtos();
		} finally {
			if (rep != null) {
				rep.exhaust();
				rep.release();
			}
			if (cr != null) {
				cr.release();
			}
		}
		assertEquals(3, dtos.size());
		for (AnalysisProvenanceDto dto : dtos) {
			assertEquals("GenomicAlignmentNovoalign", dto.getWorkflowName());
		}
	}

	@Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");

        List<Future<GetResult<AnalysisProvenanceDtoList>>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CompletionService<GetResult<AnalysisProvenanceDtoList>> completionService = new ExecutorCompletionService<>(executorService);

        List<Callable<GetResult<AnalysisProvenanceDtoList>>> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            callables.add(new Get<>("/reports/analysis-provenance",  AnalysisProvenanceDtoList.class));
        }

        Collections.shuffle(callables);
        for (Callable<GetResult<AnalysisProvenanceDtoList>> c : callables) {
            futures.add(completionService.submit(c));
        }

        while (futures.size() > 0) {

            Future<GetResult<AnalysisProvenanceDtoList>> completedTask = null;
            try {
                completedTask = completionService.take();
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }
            try {
                if (completedTask == null) {
                    fail("Null completed task");
                } else {
                    futures.remove(completedTask);
                    GetResult<AnalysisProvenanceDtoList> r = completedTask.get();

                    assertNotNull(r.getStatus());

                    assertNotNull(r.getRequestDate());
                    assertNotNull(r.getResponseDate());
                    assertTrue(r.getRequestDate().isEqual(r.getResponseDate())
                            || r.getRequestDate().isBefore(r.getResponseDate()));

                    AnalysisProvenanceDtoList dtos = r.getData();
                        assertNotNull(r.getDataLastModificationDate());
                        assertTrue(r.getResponseDate().isEqual(r.getDataLastModificationDate())
                                || r.getResponseDate().isAfter(r.getDataLastModificationDate()));

                        assertNotNull(dtos);
                        //+ 20 IUS without workflow runs
                        //+ 3 files attached to workflow run
                        //+ 3 workflow runs without files
                        //= 26 expected records
                        Assert.assertEquals(26, dtos.getAnalysisProvenanceDtos().size());
                }
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
        }

        executorService.shutdown();
    }

}
