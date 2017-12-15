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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.SampleProvenanceDtoList;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author mlaszloffy
 */
public class SampleProvenanceResourceTest extends DatabaseResourceTest {

    public SampleProvenanceResourceTest() {
        super("/reports/sample-provenance");
    }

    @Test
    public void testSomeMethod() {
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

    @Override
    public void testGet() {
        System.out.println(getRelativeURI() + " GET");

        try {
            //ensure sample provenance is cached incase "invalidate" is called first (below)
            new Get<>("/reports/sample-provenance", SampleProvenanceDtoList.class).call();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        List<Future<GetResult<SampleProvenanceDtoList>>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CompletionService<GetResult<SampleProvenanceDtoList>> completionService = new ExecutorCompletionService<>(executorService);

        List<Callable<GetResult<SampleProvenanceDtoList>>> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            callables.add(new Get<>("/reports/sample-provenance", SampleProvenanceDtoList.class));
            callables.add(new Get<>("/reports/sample-provenance/refresh", null));
            callables.add(new Get<>("/reports/sample-provenance/invalidate", null));
        }

        Collections.shuffle(callables);
        for (Callable<GetResult<SampleProvenanceDtoList>> c : callables) {
            futures.add(completionService.submit(c));
        }

        while (futures.size() > 0) {

            Future<GetResult<SampleProvenanceDtoList>> completedTask = null;
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
                    GetResult<SampleProvenanceDtoList> r = completedTask.get();

                    assertNotNull(r.getStatus());

                    assertNotNull(r.getRequestDate());
                    assertNotNull(r.getResponseDate());
                    assertTrue(r.getRequestDate().isEqual(r.getResponseDate())
                            || r.getRequestDate().isBefore(r.getResponseDate()));

                    Object data = r.getData();
                    if (data instanceof LaneProvenanceDtoList) {
                        List<LaneProvenanceDto> dtos = ((LaneProvenanceDtoList) data).getLaneProvenanceDtos();

                        assertNotNull(r.getDataLastModificationDate());
                        assertTrue(r.getResponseDate().isEqual(r.getDataLastModificationDate())
                                || r.getResponseDate().isAfter(r.getDataLastModificationDate()));

                        assertNotNull(dtos);
                        assertEquals(22, dtos.size());

                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                fail(ex.getMessage());
            }
        }

        executorService.shutdown();
    }
}
