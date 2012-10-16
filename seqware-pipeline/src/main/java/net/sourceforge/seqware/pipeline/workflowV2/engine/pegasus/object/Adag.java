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
package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.JavaJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.PerlJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.WorkflowExecutableUtils;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * @author yongliang
 */
public class Adag  {
    private Collection<WorkflowExecutable> executables;
    private List<PegasusJob> jobs;

    private String schemaLocation = "http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd";
    public static Namespace NAMESPACE = Namespace.getNamespace("http://pegasus.isi.edu/schema/DAX");
    public static Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    //FIXME should be passed in from maven 
    public static String PIPELINE = "seqware-pipeline-0.13.3-SNAPSHOT-full.jar";

    private String version = "3.2";
    private String count = "1";
    private String index = "0";
    
    private Workflow wf;
    private AbstractWorkflowDataModel wfdm;
    
    private Map<SqwFile, PegasusJob> fileJobMap;

    public Adag(AbstractWorkflowDataModel wfdm) {
    	this.wfdm = wfdm;
		this.jobs = new ArrayList<PegasusJob>();
		this.fileJobMap = new HashMap<SqwFile, PegasusJob>();
		//this.parseWorkflow(wf);
		this.parseWorkflow(wfdm);
		this.setDefaultExcutables();
    }
    


    private void setDefaultExcutables() {
		executables = new ArrayList<WorkflowExecutable>();
		executables.add(WorkflowExecutableUtils.getDefaultJavaExcutable(this.wfdm));
		executables.add(WorkflowExecutableUtils.getBashExcutable(this.wfdm));
		executables.add(WorkflowExecutableUtils.getDefaultPerlExcutable(this.wfdm));
		executables.add(WorkflowExecutableUtils.getDefaultDirManagerExcutable(this.wfdm));
		executables.add(WorkflowExecutableUtils.getDefaultSeqwareExecutable(this.wfdm));
    }


    public Element serializeXML() {
		Element adag = new Element("adag", NAMESPACE);
		adag.addNamespaceDeclaration(XSI);
		adag.setAttribute("schemaLocation", schemaLocation, XSI);
		adag.setAttribute("version", version);
		adag.setAttribute("count", count);
		adag.setAttribute("index", index);
		adag.setAttribute("name", this.wfdm.getName());
	
		for (WorkflowExecutable ex : executables) {
		    adag.addContent(ex.serializeXML());
		}
	
		for (PegasusJob pjob : this.jobs) {
		    adag.addContent(pjob.serializeXML());
		}
		// dependencies
		for (PegasusJob pjob : this.jobs) {
		    for (PegasusJob parent : pjob.getParents()) {	
		    	adag.addContent(pjob.getDependentElement(parent));	
		    }
		}
		return adag;
    }

	public Workflow getWorkflow() {
		return wf;
	}



	public void setWorkflow(Workflow wf) {
		this.wf = wf;
	}
	
	private void parseWorkflow(AbstractWorkflowDataModel wfdm) {
		boolean metadatawriteback = Boolean.parseBoolean(wfdm.getConfigs().get("metadata"));
		List<PegasusJob> parents = new ArrayList<PegasusJob>();
		//mkdir data job
		AbstractJob job0 = new BashJob("start");
		job0.getCommand().addArgument("mkdir provisionfiles");
		PegasusJob pjob0 = new PegasusJob(job0, wfdm.getConfigs().get("basedir"));
		pjob0.setId(this.jobs.size());
		pjob0.setMetadataWriteback(metadatawriteback);
		//if has parent-accessions, assign it to first job
		String parentAccession = wfdm.getConfigs().get("parent-accessions");
		if(parentAccession!=null && !parentAccession.isEmpty()) {
			pjob0.setParentAccession(parentAccession);
		}
		
		this.jobs.add(pjob0);
		parents.add(pjob0);
		//directories
		if(!wfdm.getDirectories().isEmpty()) {
			parents.clear();
			StringBuilder sb = new StringBuilder();
			for(String dir: wfdm.getDirectories()) {
				sb.append("mkdir "+dir+";");
			}
			AbstractJob dirJob = new BashJob("createdir");
			dirJob.getCommand().addArgument(sb.toString());
			PegasusJob pjobdir = new PegasusJob(dirJob, wfdm.getConfigs().get("basedir"));
			pjobdir.setId(this.jobs.size());
			pjobdir.setMetadataWriteback(metadatawriteback);
			this.jobs.add(pjobdir);
			pjobdir.addParent(pjob0);
			parents.add(pjobdir);
		}
		//sqwfiles
		if(!wfdm.getFiles().isEmpty()) {
			Collection<PegasusJob> newParents = new ArrayList<PegasusJob>();
			for(Map.Entry<String,SqwFile> entry: wfdm.getFiles().entrySet()) {
				AbstractJob job = new BashJob("provisionFile_"+entry.getKey());
				job.addFile(entry.getValue());
				PegasusJob pjob = new ProvisionFilesJob(job,wfdm.getConfigs().get("basedir"));
				pjob.setId(this.jobs.size());
				pjob.setMetadataWriteback(metadatawriteback);
				this.jobs.add(pjob);
				this.fileJobMap.put(entry.getValue(), pjob);
	
				//handle in 
				if(entry.getValue().isInput()) {
					newParents.add(pjob);
					for(PegasusJob parent: parents) {
						pjob.addParent(parent);
					}
				} 
			}
			//reset parents
			parents.clear();
			parents.addAll(newParents);
		}
		
		int idCount = this.jobs.size();
		//need to remember the provisionOut and reset the job's children to provisionout's children
		Map<PegasusJob, PegasusJob> hasProvisionOut = new HashMap<PegasusJob, PegasusJob>();
		for(AbstractJob job: wfdm.getWorkflow().getJobs()) {
			PegasusJob pjob = this.createPegasusJobObject(job, wfdm);
			pjob.setId(idCount);
			pjob.setMetadataWriteback(metadatawriteback);
			this.jobs.add(pjob);
			for(Job parent: job.getParents()) {
				pjob.addParent(this.getPegasusJobObject((AbstractJob)parent));
			}
			
			
			//has provisionfiles dependency?
			// this based on the assumption that the provisionFiles job is always in the beginning or the end.
			if(job.getFiles().isEmpty() == false) {
				for(SqwFile file: job.getFiles()) {
					//create a provisionfile job\
					if(file.isInput()) {					
							//create a provisionFileJob;
							AbstractJob pfjob = new BashJob("provisionFile_in");
							pfjob.addFile(file);
							PegasusJob parentPfjob = new ProvisionFilesJob(pfjob,wfdm.getConfigs().get("basedir"));
							parentPfjob.setId(this.jobs.size());
							parentPfjob.addParent(pjob0);
							parentPfjob.setMetadataWriteback(metadatawriteback);
							this.jobs.add(parentPfjob);
							pjob.addParent(parentPfjob);						
					} else {
							//create a provisionFileJob;
							AbstractJob pfjob = new BashJob("provisionFile_in");
							pfjob.addFile(file);
							PegasusJob parentPfjob = new ProvisionFilesJob(pfjob,wfdm.getConfigs().get("basedir"));
							parentPfjob.setId(this.jobs.size());
							parentPfjob.addParent(pjob);
							parentPfjob.setMetadataWriteback(metadatawriteback);
							this.jobs.add(parentPfjob);
							hasProvisionOut.put(pjob, parentPfjob);
					}
				}
			}

			if(!hasProvisionOut.isEmpty()) {
				for(Map.Entry<PegasusJob, PegasusJob> entry: hasProvisionOut.entrySet()) {
					//get all children
					Collection<PegasusJob> children = entry.getKey().getChildren();
					if(children.size()<=1)
						continue;
					// and set other's parent as the value
					for(PegasusJob child: children ) {
						if(child == entry.getValue())
							continue;
						child.addParent(entry.getValue());
					}
				}
			}
			//if no parent, set parents after provisionfiles
			if(pjob.getParents().isEmpty()) {
				for(PegasusJob parent: parents) {
					pjob.addParent(parent);
				}
			}
			
			//add all provision out job
			//get all the leaf job
			List<PegasusJob> leaves = new ArrayList<PegasusJob>();
			for(PegasusJob _job: this.jobs) {
				if(_job.getChildren().isEmpty()) {
					leaves.add(_job);
				}
			}
			for(Map.Entry<SqwFile, PegasusJob> entry: fileJobMap.entrySet()) {
				if(entry.getKey().isOutput()) {
					//set parents to all leaf jobs
					for(PegasusJob leaf: leaves) {
						if(leaf!=entry.getValue())
							entry.getValue().addParent(leaf);
					}
				}
			}
			this.jobs.add(pjob);
			idCount++;
		}
	}
	
	private PegasusJob getPegasusJobObject(AbstractJob job) {
		for(PegasusJob pjob: this.jobs) {
			if(job.equals(pjob.getJobObject()))
				return pjob;
		}
		return null;
	}
	
	private PegasusJob createPegasusJobObject(AbstractJob job, AbstractWorkflowDataModel wfdm) {
		PegasusJob ret = null;
		if(job instanceof JavaJob) {
			ret = new PegasusJavaJob(job,wfdm.getConfigs().get("basedir"));
		} else if(job instanceof PerlJob) {
			ret = new PegasusPerlJob(job, wfdm.getConfigs().get("basedir"));
		} else {
			ret = new PegasusJob(job, wfdm.getConfigs().get("basedir"));
		}
		return ret;
	}
}
