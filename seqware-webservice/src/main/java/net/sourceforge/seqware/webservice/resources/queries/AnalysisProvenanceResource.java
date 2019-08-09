/*
 * Copyright (C) 2015 SeqWare
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

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.resource.Get;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import net.sourceforge.seqware.webservice.resources.CachedCollectionAutoUpdate;
import org.apache.log4j.Logger;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResource extends BasicResource {

    private static final Logger LOG = Logger.getLogger(AnalysisProvenanceResource.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final CachedCollectionAutoUpdate<AnalysisProvenanceDto, AnalysisProvenanceDtoList> CACHE = new CachedCollectionAutoUpdate() {

        @Override
        public List<AnalysisProvenanceDto> calculateCollection() {
            LOG.info("Updating analysis provenance");
            AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();
            return service.list().stream().map(AnalysisProvenanceDto.class::cast).collect(Collectors.toList());
        }

        @Override
        public void writeResponse(Response response, List cachedCollection, Instant modificationDate) {
            streamAnalysisProvenanceXml(response, (List<AnalysisProvenanceDto>) cachedCollection, modificationDate);
        }

    };

    @Get("xml")
    public void getXml() {
        Map<FileProvenanceFilter, Set<String>> filters = new HashMap<>();
        for (Entry<String, String[]> e : BasicRestlet.queryMap(getRequest()).entrySet()) {
            filters.put(FileProvenanceFilter.fromString(e.getKey()), Sets.newHashSet(e.getValue()));
        }
        buildResponse(filters);
    }

    @Post("json,xml")
    public void postAndReturnXml(Representation entity) throws ResourceException {
        try {
            Map<FileProvenanceFilter, Set<String>> filters = MAPPER.readValue(entity.getText(),
                    new TypeReference<Map<FileProvenanceFilter, Set<String>>>() {
            });
            buildResponse(filters);
        } catch (IOException ex) {
            LOG.error("Failed to get analyis provenance", ex);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        }
    }

    private void buildResponse(Map<FileProvenanceFilter, Set<String>> filters) {
        AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();
        if (Sets.intersection(filters.keySet(), service.getSupportedFilters()).isEmpty()) {
            // no supported filters provided, return all records
            CACHE.processRequest(getResponse());
        } else {
            Instant modificationDate = Instant.now();
            List<AnalysisProvenanceDto> data = service.list(filters).stream().map(AnalysisProvenanceDto.class::cast).collect(Collectors.toList());
            streamAnalysisProvenanceXml(getResponse(), data, modificationDate);
        }
    }

    private static void streamAnalysisProvenanceXml(Response response, List<AnalysisProvenanceDto> data, Instant modificationDate) {
        WriterRepresentation writerRepresentation = new WriterRepresentation(MediaType.APPLICATION_XML) {
            @Override
            public void write(Writer writer) throws IOException {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
                writer.write("<AnalysisProvenanceDtoList>");
                try {
                    Marshaller m = JAXBContext.newInstance(AnalysisProvenanceDto.class).createMarshaller();
                    m.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    for (AnalysisProvenanceDto dto : data) {
                        m.marshal(dto, writer);
                    }
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
                writer.write("</AnalysisProvenanceDtoList>");
            }
        };
        writerRepresentation.setModificationDate(Date.from(modificationDate));
        response.setEntity(writerRepresentation);
    }

}
