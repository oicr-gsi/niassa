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

import net.sourceforge.seqware.webservice.resources.CachedDocumentManualUpdate;
import net.sourceforge.seqware.common.business.LaneProvenanceService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.apache.log4j.Logger;
import org.restlet.resource.Get;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceResource extends BasicResource {

    private static final Logger LOG = Logger.getLogger(LaneProvenanceResource.class);

    private static final CachedDocumentManualUpdate CACHE = new CachedDocumentManualUpdate() {
        @Override
        public Document calculateDocument() {
            LOG.info("Updating lane provenance");
            LaneProvenanceService service = BeanFactory.getLaneProvenanceServiceBean();
            JaxbObject<LaneProvenanceDtoList> jaxbTool = new JaxbObject<>();
            LaneProvenanceDtoList list = new LaneProvenanceDtoList();
            list.setLaneProvenanceDtos(service.list());
            return XmlTools.marshalToDocument(jaxbTool, list, LaneProvenanceDtoList.class);
        }
    };

    @Get
    public void getXml() {
        CachedDocumentManualUpdate.Operation op;
        if (getRequestAttributes().containsKey("operation")) {
            op = CachedDocumentManualUpdate.Operation.valueOf(getRequestAttributes().get("operation").toString().toUpperCase());
        } else {
            op = CachedDocumentManualUpdate.Operation.GET;
        }

        CACHE.processRequest(getResponse(), op);
    }
}
