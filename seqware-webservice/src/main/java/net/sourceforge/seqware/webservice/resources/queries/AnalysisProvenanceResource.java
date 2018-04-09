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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.resource.Get;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import net.sourceforge.seqware.webservice.resources.CachedDocumentAutoUpdate;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceResource extends BasicResource {

	private static final Logger LOG = Logger.getLogger(AnalysisProvenanceResource.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final CachedDocumentAutoUpdate CACHE = new CachedDocumentAutoUpdate() {
		@Override
		public Document calculateDocument() {
			LOG.info("Updating analysis provenance");
			AnalysisProvenanceService service = BeanFactory.getAnalysisProvenanceServiceBean();
			JaxbObject<AnalysisProvenanceDtoList> jaxbTool = new JaxbObject<>();
			AnalysisProvenanceDtoList list = new AnalysisProvenanceDtoList();
			list.setAnalysisProvenanceDtos(
					service.list().stream().map(AnalysisProvenanceDto.class::cast).collect(Collectors.toList()));
			return XmlTools.marshalToDocument(jaxbTool, list, AnalysisProvenanceDtoList.class);
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
			JaxbObject<AnalysisProvenanceDtoList> jaxbTool = new JaxbObject<>();
			AnalysisProvenanceDtoList list = new AnalysisProvenanceDtoList();
			list.setAnalysisProvenanceDtos(
					service.list(filters).stream().map(AnalysisProvenanceDto.class::cast).collect(Collectors.toList()));
			Representation rep = XmlTools
					.getRepresentation(XmlTools.marshalToDocument(jaxbTool, list, AnalysisProvenanceDtoList.class));
			getResponse().setEntity(rep);
		}
	}

}
