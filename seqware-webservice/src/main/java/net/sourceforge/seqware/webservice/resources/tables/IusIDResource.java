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
package net.sourceforge.seqware.webservice.resources.tables;

import java.io.IOException;
import java.util.Set;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.LimsKeyService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.err.DataIntegrityException;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;

/**
 * <p>
 * IusIDResource class.
 * </p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class IusIDResource extends DatabaseIDResource {

    /**
     * <p>
     * Constructor for IusIDResource.
     * </p>
     */
    public IusIDResource() {
        super("iusId");
    }

    /**
     * <p>
     * getXml.
     * </p>
     */
    @Get
    public void getXml() {
        authenticate();
        IUSService ss = BeanFactory.getIUSServiceBean();
        IUS ius = testIfNull(ss.findBySWAccession(getId()));
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        if (getRequestAttributes().containsKey("object")) {
            if (getRequestAttributes().get("object").equals("limskey")) {
                LimsKey limsKey = ius.getLimsKey().asDetached();
                JaxbObject<LimsKey> jaxbTool = new JaxbObject<>();
                Document line = XmlTools.marshalToDocument(jaxbTool, limsKey, LimsKey.class);
                getResponse().setEntity(XmlTools.getRepresentation(line));
            } else {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Object type not supported");
            }
        } else {
            JaxbObject<IUS> jaxbTool = new JaxbObject<>();

            CollectionPropertyName<IUS>[] createCollectionPropertyNames = CollectionPropertyName.createCollectionPropertyNames(IUS.class,
                    new String[]{"iusAttributes"});
            IUS dto = copier.hibernate2dto(IUS.class, ius, new Class<?>[]{}, createCollectionPropertyNames);

            Document line = XmlTools.marshalToDocument(jaxbTool, dto, IUS.class);
            getResponse().setEntity(XmlTools.getRepresentation(line));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        IUS newIUS = null;
        JaxbObject<IUS> jo = new JaxbObject<>();
        try {
            String text = entity.getText();
            newIUS = (IUS) XmlTools.unMarshal(jo, IUS.class, text);
        } catch (SAXException | IOException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex);
        }
        try {
            IUSService fs = BeanFactory.getIUSServiceBean();
            IUS ius = testIfNull(fs.findByID(newIUS.getIusId()));
            ius.givesPermission(registration);
            // simple types
            String name = newIUS.getName();
            String desc = newIUS.getDescription();
            String tags = newIUS.getTag();
            Boolean skip = newIUS.getSkip();

            // foreign keys
            Sample sample = newIUS.getSample();
            Lane lane = newIUS.getLane();
            LimsKey limsKey = newIUS.getLimsKey();
            Set<WorkflowRun> workflowRuns = newIUS.getWorkflowRuns();
            Set<Processing> processings = newIUS.getProcessings();
            Registration owner = newIUS.getOwner();

            Set<IUSAttribute> newAttributes = newIUS.getIusAttributes();

            ius.setName(name);
            ius.setDescription(desc);
            ius.setSkip(skip);
            ius.setTag(tags);

            if (sample == null) {
                //do not modify IUS samples
            } else if (sample.getSwAccession() == null && sample.getSampleId() == null) {
                ius.setSample(null);
            } else {
                SampleService ss = BeanFactory.getSampleServiceBean();
                Sample newSample = ss.findByID(sample.getSampleId());
                if (newSample != null && newSample.givesPermission(registration)) {
                    ius.setSample(newSample);
                } else if (newSample == null) {
                    Log.info("Could not be found " + sample);
                }
            }

            if (lane == null) {
                //do not modify IUS lanes
            } else if (lane.getSwAccession() == null && lane.getLaneId() == null) {
                ius.setLane(null);
            } else {
                LaneService laneService = BeanFactory.getLaneServiceBean();
                Lane newLane = laneService.findByID(lane.getLaneId());
                if (newLane != null) {
                    ius.setLane(newLane);
                } else if (newLane == null) {
                    Log.info("Could not be found " + lane);
                }
            }

            if (limsKey == null) {
                //do not modify IUS lims keys
            } else if (limsKey.getSwAccession() == null && limsKey.getLimsKeyId() == null) {
                ius.setLimsKey(null);
            } else {
                LimsKeyService limsKeyService = BeanFactory.getLimsKeyServiceBean();
                LimsKey newLimsKey = limsKeyService.findByID(limsKey.getLimsKeyId());
                if (newLimsKey != null) {
                    ius.setLimsKey(newLimsKey);
                } else if (newLimsKey == null) {
                    Log.info("Could not be found " + limsKey);
                }
            }

            if (owner != null) {
                RegistrationService rs = BeanFactory.getRegistrationServiceBean();
                Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
                if (newReg != null) {
                    ius.setOwner(newReg);
                } else {
                    Log.info("newReg cannot be found: " + owner.getEmailAddress());
                }
            } else if (ius.getOwner() == null) {
                ius.setOwner(registration);
            }
            if (newAttributes != null) {
                // SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                IusIDResource.mergeAttributes(ius.getIusAttributes(), newAttributes, ius);
            }

            fs.update(registration, ius);

            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            IUS detachedIUS = copier.hibernate2dto(IUS.class, ius);

            Document line = XmlTools.marshalToDocument(jo, detachedIUS, IUS.class);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/ius/" + detachedIUS.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }

        return representation;
    }

    @Override
    public Representation delete() {
        authenticate();
        Representation rep = super.delete();

        IUSService service = BeanFactory.getIUSServiceBean();
        IUS ius = (IUS) testIfNull(service.findBySWAccession(getId()));
        try {
            service.delete(ius);
            getResponse().setStatus(Status.SUCCESS_OK);
        } catch (DataIntegrityException ex) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, ex);
        }
        return rep;
    }

}
