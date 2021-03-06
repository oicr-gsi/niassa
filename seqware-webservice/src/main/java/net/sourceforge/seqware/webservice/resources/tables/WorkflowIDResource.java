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
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.queryengine.webservice.controller.SeqWareWebServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * WorkflowIDResource class.
 * </p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowIDResource extends DatabaseIDResource {
    private final Logger logger = LoggerFactory.getLogger(WorkflowIDResource.class);

	/**
	 * <p>
	 * Constructor for WorkflowIDResource.
	 * </p>
	 */
	public WorkflowIDResource() {
		super("workflowId");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Override
	public SeqWareWebServiceApplication getApplication() {
		return (SeqWareWebServiceApplication) super.getApplication();
	}

	/**
	 * <p>
	 * getXml.
	 * </p>
	 */
	@Get
	public void getXml() {
		authenticate();
		WorkflowService ss = BeanFactory.getWorkflowServiceBean();
		Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
		JaxbObject<Workflow> jaxbTool = new JaxbObject<>();
		Workflow workflow = testIfNull(ss.findBySWAccession(getId()));
		CollectionPropertyName<Workflow>[] createCollectionPropertyNames = CollectionPropertyName
				.createCollectionPropertyNames(Workflow.class,
						new String[] { "workflowAttributes", "parameterDefaults" });
		Workflow dto = copier.hibernate2dto(Workflow.class, workflow, ArrayUtils.EMPTY_CLASS_ARRAY,
				createCollectionPropertyNames);
		// TODO: Remove this when all deciders are upgraded to use getParameterDefaults
		if (fields.contains("params")) {
			int i = 0;
			final SortedSet<WorkflowParam> params = new TreeSet<>();
			for (Entry<String, String> defaultValue : workflow.getParameterDefaults().entrySet()) {
				WorkflowParam parameter = new WorkflowParam();
				parameter.setDefaultValue(defaultValue.getValue());
				parameter.setKey(defaultValue.getKey());
				parameter.setWorkflowParamId(i++);
				params.add(parameter);
			}
			dto.setWorkflowParams(params);
		}
		Document line = XmlTools.marshalToDocument(jaxbTool, dto, Workflow.class);
		getResponse().setEntity(XmlTools.getRepresentation(line));
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
		Workflow newWorkflow = null;
		JaxbObject<Workflow> jo = new JaxbObject<>();
		try {
			String text = entity.getText();
			newWorkflow = (Workflow) XmlTools.unMarshal(jo, Workflow.class, text);
                } catch (SAXException | IOException ex) {
                        logger.error("WorkflowIDResource.put SAX/IO exception:",ex);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
		}
		try {
			WorkflowService fs = BeanFactory.getWorkflowServiceBean();
			Workflow workflow = testIfNull(fs.findByID(newWorkflow.getWorkflowId()));
			workflow.givesPermission(registration);
			// simple types
			String name = newWorkflow.getName();
			String desc = newWorkflow.getDescription();
			String baseIniFile = newWorkflow.getBaseIniFile();
			String command = newWorkflow.getCommand();
			String cwd = newWorkflow.getCwd();
			String host = newWorkflow.getHost();
			String inputAlgorithm = newWorkflow.getInputAlgorithm();
			String permanentBundleLocation = newWorkflow.getPermanentBundleLocation();
			String seqwareVersion = newWorkflow.getSeqwareVersion();
			String template = newWorkflow.getTemplate();
			String username = newWorkflow.getUsername();
			String version = newWorkflow.getVersion();
			// foreign keys
			Registration owner = newWorkflow.getOwner();

			workflow.setName(name);
			workflow.setDescription(desc);
			workflow.setBaseIniFile(baseIniFile);
			workflow.setCommand(command);
			workflow.setCwd(cwd);
			workflow.setHost(host);
			workflow.setInputAlgorithm(inputAlgorithm);
			workflow.setPermanentBundleLocation(permanentBundleLocation);
			workflow.setSeqwareVersion(seqwareVersion);
			workflow.setTemplate(template);
			workflow.setUsername(username);
			workflow.setVersion(version);
			workflow.setWorkflowClass(newWorkflow.getWorkflowClass());
			workflow.setWorkflowType(newWorkflow.getWorkflowType());
			workflow.setWorkflowEngine(newWorkflow.getWorkflowEngine());

			if (owner != null) {
				RegistrationService rs = BeanFactory.getRegistrationServiceBean();
				Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
				if (newReg != null) {
					workflow.setOwner(newReg);
				} else {
					logger.info("Could not be found: " + owner);
				}
			} else if (workflow.getOwner() == null) {
				workflow.setOwner(registration);
			}

			if (newWorkflow.getWorkflowAttributes() != null) {
				// SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
				WorkflowIDResource.mergeAttributes(workflow.getWorkflowAttributes(),
						newWorkflow.getWorkflowAttributes(), workflow);
			}
			fs.update(registration, workflow);
			Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
			Workflow detachedWorkflow = copier.hibernate2dto(Workflow.class, workflow, ArrayUtils.EMPTY_CLASS_ARRAY,
					CollectionPropertyName.createCollectionPropertyNames(Workflow.class,
							new String[] { "parameterDefaults" }));

			Document line = XmlTools.marshalToDocument(jo, detachedWorkflow, Workflow.class);
			representation = XmlTools.getRepresentation(line);
			getResponse().setEntity(representation);
			getResponse().setLocationRef(getRequest().getRootRef() + "/workflows/" + detachedWorkflow.getSwAccession());
			getResponse().setStatus(Status.SUCCESS_CREATED);
		} catch (SecurityException e) {
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
		} catch (Exception e) {
			logger.error("WorkflowIDResource.put exception:",e);
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}

		return representation;
	}
}
