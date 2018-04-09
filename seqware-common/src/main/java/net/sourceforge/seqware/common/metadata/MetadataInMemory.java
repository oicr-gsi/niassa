/*
 * Copyright (C) 2014 SeqWare
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

package net.sourceforge.seqware.common.metadata;

import java.io.Writer;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.SequencerRunStatus;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.business.impl.AnalysisProvenanceServiceImpl;
import net.sourceforge.seqware.common.business.impl.LaneProvenanceServiceImpl;
import net.sourceforge.seqware.common.business.impl.SampleProvenanceServiceImpl;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.model.FirstTierModel;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.ParentAccessionModel;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * This stores some metadata in memory as an exploration of running workflows without a running database or web service.
 *
 * Data will only be stored while this VM is still active and cannot be accessed by other clients.
 *
 * @author dyuen
 */
public class MetadataInMemory implements Metadata {

    /**
     * Stores SWID/id -> Model object. Unlike the postgres database, we re-use the sw accession as the id
     */
    private static final Table<Integer, Class<?>, Object> STORE = HashBasedTable.create();

    /**
     * Not really thread-safe, why does Guava not have a synchronized wrapper?
     *
     * @return the store
     */
    private static synchronized Table<Integer, Class<?>, Object> getStore() {
        return STORE;
    }

    @Override
    public ReturnValue clean_up() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int mapProcessingIdToAccession(int processingId) {
        //In this implementation, SWIDs == ID
        return processingId;
    }

    @Override
    public ReturnValue addStudy(String title, String description, String centerName, String centerProjectName, Integer studyTypeId) {

        StudyType st = new StudyType();
        st.setStudyTypeId(studyTypeId);

        Study study = new Study();
        study.setTitle(title);
        study.setAlias(title);
        study.setDescription(description);
        study.setExistingType(st);
        study.setCenterName(centerName);
        study.setCenterProjectName(centerProjectName);
        study.setCreateTimestamp(new Date());
        study.setSwAccession(getNextSwAccession());
        study.setStudyId(study.getSwAccession());

        MetadataInMemory.getStore().put(study.getSwAccession(), Study.class, study);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", study.getSwAccession().toString());
        return rv;
    }

    @Override
    public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title,
            Integer experimentLibraryDesignId, Integer experimentSpotDesignId) {

        Study study = (Study) MetadataInMemory.getStore().get(studySwAccession, Study.class);

        Platform p = new Platform();
        p.setPlatformId(platformId);

        Experiment e = new Experiment();
        e.setStudy(study);
        e.setPlatform(p);
        e.setDescription(description);
        e.setTitle(title);
        e.setName(title);
        e.setSwAccession(getNextSwAccession());
        e.setExperimentId(e.getSwAccession());

        MetadataInMemory.getStore().put(e.getSwAccession(), Experiment.class, e);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", e.getSwAccession().toString());
        return rv;
    }

    @Override
    public ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession, Integer organismId, String description,
            String title) {

        Experiment experiment = (Experiment) MetadataInMemory.getStore().get(experimentAccession, Experiment.class);

        Set<Sample> parents = new HashSet<>();
        if (parentSampleAccession != 0) {
            parents.add((Sample) MetadataInMemory.getStore().get(parentSampleAccession, Sample.class));
        }

        Organism organism = new Organism();
        organism.setOrganismId(organismId);

        Sample s = new Sample();
        s.setExperiment(experiment);
        s.setParents(parents);
        s.setOrganism(organism);
        s.setTitle(title);
        s.setName(title);
        s.setDescription(description);
        s.setCreateTimestamp(new Date());
        s.setSwAccession(getNextSwAccession());
        s.setSampleId(s.getSwAccession());

        MetadataInMemory.getStore().put(s.getSwAccession(), Sample.class, s);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", s.getSwAccession().toString());
        return rv;
    }

    @Override
    public ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip,
            String filePath, SequencerRunStatus status) {

        Platform p = new Platform();
        p.setPlatformId(platformAccession);

        SequencerRun sr = new SequencerRun();
        sr.setName(name);
        sr.setDescription(description);
        sr.setPairedEnd(pairdEnd);
        sr.setSkip(skip);
        sr.setPlatform(p);
        sr.setFilePath(filePath);
        sr.setStatus(status);
        sr.setSwAccession(getNextSwAccession());
        sr.setSequencerRunId(sr.getSwAccession());

        MetadataInMemory.getStore().put(sr.getSwAccession(), SequencerRun.class, sr);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", sr.getSwAccession().toString());
        return rv;
    }

    @Override
    public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId,
            Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip, Integer laneNumber) {

        SequencerRun sr = (SequencerRun) MetadataInMemory.getStore().get(sequencerRunAccession, SequencerRun.class);

        StudyType st = new StudyType();
        st.setStudyTypeId(studyTypeId);

        LibraryStrategy ls = new LibraryStrategy();
        ls.setLibraryStrategyId(libraryStrategyId);

        LibrarySelection lsel = new LibrarySelection();
        lsel.setLibrarySelectionId(librarySelectionId);

        LibrarySource lsource = new LibrarySource();
        lsource.setLibrarySourceId(librarySourceId);

        Lane l = new Lane();
        l.setStudyType(st);
        l.setLibraryStrategy(ls);
        l.setLibrarySelection(lsel);
        l.setLibrarySource(lsource);
        l.setSequencerRun(sr);
        l.setName(name);
        l.setDescription(description);
        l.setCycleDescriptor(cycleDescriptor);
        l.setSkip(skip);
        l.setLaneIndex(laneNumber - 1);
        l.setSwAccession(getNextSwAccession());
        l.setLaneId(l.getSwAccession());

        MetadataInMemory.getStore().put(l.getSwAccession(), Lane.class, l);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", l.getSwAccession().toString());
        return rv;
    }

    @Override
    public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip) {

        Lane lane = (Lane) MetadataInMemory.getStore().get(laneAccession, Lane.class);
        Sample sample = (Sample) MetadataInMemory.getStore().get(sampleAccession, Sample.class);

        IUS i = new IUS();
        if (lane != null) {
            i.setLane(lane);
        }
        if (sample != null) {
            i.setSample(sample);
        }
        i.setName(name);
        i.setDescription(description);
        i.setTag(barcode);
        i.setSkip(skip);
        i.setSwAccession(getNextSwAccession());
        i.setIusId(i.getSwAccession());

        MetadataInMemory.getStore().put(i.getSwAccession(), IUS.class, i);

        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        rv.setAttribute("sw_accession", i.getSwAccession().toString());
        return rv;
    }

    @Override
    public Integer addIUS(Integer limsKeyAccession, boolean skip) {
        LimsKey limsKey = (LimsKey) MetadataInMemory.getStore().get(limsKeyAccession, LimsKey.class);

        IUS i = new IUS();
        i.setSkip(skip);
        i.setSwAccession(getNextSwAccession());
        i.setIusId(i.getSwAccession());
        if (limsKey != null) {
            i.setLimsKey(limsKey);
        }

        MetadataInMemory.getStore().put(i.getSwAccession(), IUS.class, i);

        return i.getSwAccession();
    }

    @Override
    public Integer addLimsKey(String provider, String id, String version, ZonedDateTime lastModified) {
        LimsKey key = new LimsKey();
        key.setProvider(provider);
        key.setId(id);
        key.setVersion(version);
        key.setLastModified(lastModified);
        key.setSwAccession(getNextSwAccession());
        key.setLimsKeyId(key.getSwAccession());

        MetadataInMemory.getStore().put(key.getLimsKeyId(), LimsKey.class, key);

        return key.getSwAccession();
    }

    @Override
    public void updateIUS(IUS ius) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateLimsKey(LimsKey limsKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteIUS(Integer iusAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteLimsKey(Integer limsKeyAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Platform> getPlatforms() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Experiment getExperiment(int swAccession) {
        return (Experiment) MetadataInMemory.getStore().get(swAccession, Experiment.class);
    }

    @Override
    public List<ExperimentLibraryDesign> getExperimentLibraryDesigns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ExperimentSpotDesign> getExperimentSpotDesigns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Organism> getOrganisms() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<StudyType> getStudyTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibraryStrategy> getLibraryStrategies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibrarySelection> getLibrarySelections() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibrarySource> getLibrarySource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue add_empty_processing_event(int[] parentIDs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
        Processing processing = new Processing();
        processing.setStatus(ProcessingStatus.pending);
        processing.setCreateTimestamp(new Date());
        processing.setSwAccession(getNextSwAccession());
        processing.setProcessingId(processing.getSwAccession()); //use swid for id

        for (int i : parentAccessions) {
            ParentAccessionModel resolveParentAccession = resolveParentAccession(i);
            if (resolveParentAccession == null) {
                throw new RuntimeException("This parent ID is invalid: " + i);
            } else if (resolveParentAccession instanceof Processing) {
                Processing p = (Processing) resolveParentAccession;
                processing.getParents().add(p);
                p.getChildren().add(processing);
            } else if (resolveParentAccession instanceof IUS) {
                IUS ius = (IUS) resolveParentAccession;
                processing.getIUS().add(ius);
                ius.getProcessings().add(processing);
            } else {
                throw new RuntimeException("Model unaccounted for, we cannot attach this");
            }
        }

        MetadataInMemory.getStore().put(processing.getSwAccession(), Processing.class, processing);

        ReturnValue rv = new ReturnValue();
        rv.setReturnValue(processing.getProcessingId());
        return rv;
    }

    @Override
    public ReturnValue add_task_group(int[] parentIDs, int[] childIDs, String algorithm, String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue processing_event_to_task_group(int processingID, int[] parentIDs, int[] childIDs, String algorithm,
            String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
        Integer processingSwid = processingID;
        Processing processing = (Processing) MetadataInMemory.getStore().get(processingSwid, Processing.class);

        //copied logic from net.sourceforge.seqware.common.metadata.MetadataWS.update_processing_event(int, ReturnValue)
        processing.setExitStatus(retval.getExitStatus());
        processing.setProcessExitStatus(retval.getProcessExitStatus());
        processing.setAlgorithm(retval.getAlgorithm());
        processing.setDescription(retval.getDescription());
        processing.setParameters(retval.getParameters());
        processing.setVersion(retval.getVersion());
        processing.setUrl(retval.getUrl());
        processing.setUrlLabel(retval.getUrlLabel());
        processing.setStdout(retval.getStdout());
        processing.setStderr(retval.getStderr());
        processing.setRunStartTimestamp(retval.getRunStartTstmp());
        processing.setRunStopTimestamp(retval.getRunStopTstmp());
        processing.setUpdateTimestamp(new Date());

        Set<File> modelFiles = new HashSet<>();
        // Add and associate files for each item
        if (retval.getFiles() != null) {
            for (FileMetadata file : retval.getFiles()) {
                // If the file path is empty, warn and skip
                if (file.getFilePath().compareTo("") == 0) {
                    Log.error("WARNING: Skipping empty FilePath for ProcessingID entry: " + processingID);
                    continue;
                }
                // If the meta type is empty, warn and skip
                if (file.getMetaType().compareTo("") == 0) {
                    Log.error("WARNING: Skipping empty MetaType for ProcessingID entry: " + processingID);
                    continue;
                }
                File modelFile = new File();
                modelFile.setFilePath(file.getFilePath());
                modelFile.setMetaType(file.getMetaType());
                modelFile.setType(file.getType());
                modelFile.setDescription(file.getDescription());
                modelFile.setUrl(file.getUrl());
                modelFile.setUrlLabel(file.getUrlLabel());
                modelFile.setMd5sum(file.getMd5sum());
                modelFile.setSize(file.getSize());
                modelFile.setFileAttributes(file.getAnnotations());
                modelFile.setSkip(false);
                modelFile.setSwAccession(getNextSwAccession());
                modelFile.setFileId(modelFile.getSwAccession());

                MetadataInMemory.getStore().put(modelFile.getSwAccession(), File.class, modelFile);

                modelFiles.add(modelFile);
            }
        }
        processing.getFiles().addAll(modelFiles);

        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    @Override
    public ReturnValue update_processing_status(int processingID, ProcessingStatus status) {
        Integer processingSwid = processingID;
        Processing p = (Processing) MetadataInMemory.getStore().get(processingSwid, Processing.class);
        p.setStatus(status);
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    @Override
    public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int add_workflow_run(int workflowAccession) {
        WorkflowRun wr = new WorkflowRun();
        wr.setSwAccession(this.getNextSwAccession());
        wr.setWorkflowRunId(wr.getSwAccession());
        wr.setCreateTimestamp(new Date());
        wr.setUpdateTimestamp(new Date());
        wr.setOwnerUserName(ConfigTools.getSettings().get(SqwKeys.SW_REST_USER.getSettingKey()));
        Workflow workflow = (Workflow) MetadataInMemory.getStore().get(workflowAccession, Workflow.class);
        wr.setWorkflow(workflow);
        MetadataInMemory.getStore().put(wr.getSwAccession(), WorkflowRun.class, wr);
        return wr.getSwAccession();
    }

    @Override
    public ReturnValue update_processing_workflow_run(int processingID, int workflowRunID) {
        Integer processingSwid = processingID;
        Integer workflowRunSwid = workflowRunID;
        Processing p = (Processing) MetadataInMemory.getStore().get(processingSwid, Processing.class);
        WorkflowRun wr = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSwid, WorkflowRun.class);
        p.setWorkflowRun(wr);
        SortedSet<Processing> processings = wr.getProcessings();
        if (processings == null) {
            processings = new TreeSet<>();
        }
        processings.add(p);
        wr.setProcessings(processings);
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    @Override
    public void add_workflow_run_ancestor(int workflowRunAccession, int processingID) {
        Integer processingSwid = processingID;
        Integer workflowRunSwid = workflowRunAccession;
        Processing p = (Processing) MetadataInMemory.getStore().get(processingSwid, Processing.class);
        WorkflowRun wr = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSwid, WorkflowRun.class);
        p.setWorkflowRunByAncestorWorkflowRunId(wr);
        SortedSet<Processing> processings = wr.getProcessings();
        if (processings == null) {
            processings = new TreeSet<>();
        }
        processings.add(p);
        wr.setProcessings(processings);
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
    }

    @Override
    public int get_workflow_run_accession(int workflowRunId) {
        return workflowRunId;
    }

    @Override
    public int get_workflow_run_id(int workflowRunAccession) {
        return workflowRunAccession;
    }

    @Override
    public WorkflowRun getWorkflowRun(int workflowRunAccession) {
        return (WorkflowRun) MetadataInMemory.getStore().get(workflowRunAccession, WorkflowRun.class);
    }

    @Override
    public WorkflowRun getWorkflowRunWithIuses(int workflowRunAccession) {
        return (WorkflowRun) MetadataInMemory.getStore().get(workflowRunAccession, WorkflowRun.class);
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions, List<Integer> workflowAccessions) {
        List<WorkflowRun> wrs = new ArrayList<>();
        for (Entry<Integer, Object> e : MetadataInMemory.getStore().column(WorkflowRun.class).entrySet()) {
            WorkflowRun wr = (WorkflowRun) e.getValue();
            if (!workflowAccessions.contains(wr.getWorkflowAccession())) {
                continue;
            }
            if (wr.getInputFileAccessions() == null) {
                continue;
            }
            if (Sets.intersection(Sets.newHashSet(fileAccessions), wr.getInputFileAccessions()).isEmpty()) {
                continue;
            }
            wrs.add(wr);
        }
        return wrs;
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> get_workflow_info(int workflowAccession) {
        Workflow workflow = (Workflow) getStore().get(workflowAccession, Workflow.class);
        Map<String, String> convertWorkflowToMap = MetadataWS.convertWorkflowToMap(workflow);
        return convertWorkflowToMap;
    }

    @Override
  public boolean linkWorkflowRunAndParent(int workflowRunId, int... parentAccessions) throws SQLException {
        Integer workflowRunSwid = workflowRunId;
        WorkflowRun workflowRun = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSwid, WorkflowRun.class);

    for (int parentAccession : parentAccessions) {
        IUS ius = (IUS) MetadataInMemory.getStore().get(parentAccession, IUS.class);
        Lane lane = (Lane) MetadataInMemory.getStore().get(parentAccession, Lane.class);
        if (ius != null) {
            SortedSet<IUS> iuses = workflowRun.getIus();
            if (iuses == null) {
                iuses = new TreeSet<>();
            }
            iuses.add(ius);
            workflowRun.setIus(iuses);

            Set<WorkflowRun> wrs = ius.getWorkflowRuns();
            if (wrs == null) {
                wrs = new TreeSet<>();
            }
            wrs.add(workflowRun);
            ius.setWorkflowRuns(wrs);
        } else if (lane != null) {
            SortedSet<Lane> lanes = workflowRun.getLanes();
            if (lanes == null) {
                lanes = new TreeSet<>();
            }
            lanes.add(lane);
            workflowRun.setLanes(lanes);

            Set<WorkflowRun> wrs = lane.getWorkflowRuns();
            if (wrs == null) {
                wrs = new TreeSet<>();
            }
            wrs.add(workflowRun);
            lane.setWorkflowRuns(wrs);
        } else {
            Log.error("ERROR: SW Accession is neither a lane nor an IUS: " + parentAccession);
            return false;
        }
    }
        return true;
    }

    @Override
    public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, WorkflowRunStatus status,
            String statusCmd, String workingDirectory, String dax, String ini, String host, String stdErr, String stdOut,
            String workflowEngine, Set<Integer> inputFiles) {
        WorkflowRun workflowRun = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunId, WorkflowRun.class);
        MetadataWS.convertParamsToWorkflowRun(workflowRun, pegasusCmd, workflowTemplate, status, statusCmd, workingDirectory, dax, ini,
                host, stdErr, stdOut, workflowEngine, inputFiles);
        ReturnValue returnValue = new ReturnValue();
        returnValue.setReturnValue(workflowRun.getSwAccession());
        return returnValue;
    }

    @Override
    public void updateWorkflowRun(WorkflowRun wr) {
        MetadataInMemory.getStore().put(wr.getSwAccession(), WorkflowRun.class, wr);
    }

    @Override
    public ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile,
            String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip,
            String workflowClass, String workflowType, String workflowEngine, String seqwareVersion) {
        int nextKey = getNextSwAccession();
        Workflow workflow = MetadataWS.convertParamsToWorkflow(baseCommand, name, description, version, configFile, storeProvisionDir,
                provisionDir, templateFile, storeArchiveZip, archiveZip, workflowClass, workflowType, workflowEngine, seqwareVersion);
        workflow.setCreateTimestamp(new Date());
        workflow.setUpdateTimestamp(new Date());
        workflow.setSwAccession(nextKey);
        MetadataInMemory.getStore().put(nextKey, Workflow.class, workflow);
        ReturnValue returnValue = new ReturnValue();
        Log.stdout("Added '" + workflow.getName() + "' (SWID: " + workflow.getSwAccession() + ")");
        returnValue.setAttribute("sw_accession", String.valueOf(workflow.getSwAccession()));
        returnValue.setReturnValue(workflow.getSwAccession());

        HashMap<String, Map<String, String>> hm = MetadataWS.convertIniToMap(configFile, provisionDir);
        TreeSet<WorkflowParam> setOfDefaultParams = new TreeSet<>();
        for (Entry<String, Map<String, String>> e : hm.entrySet()) {
            WorkflowParam workflowParam = MetadataWS.convertMapToWorkflowParam(e.getValue(), workflow);
            int nextSwAccession = getNextSwAccession();
            workflowParam.setWorkflowParamId(nextSwAccession);
            setOfDefaultParams.add(workflowParam);
            MetadataInMemory.getStore().put(nextSwAccession, WorkflowParam.class, workflowParam);
        }
        workflow.setWorkflowParams(setOfDefaultParams);
        return returnValue;
    }

    private synchronized int getCurrentSwAccession() {
        int currKey = MetadataInMemory.getStore().rowKeySet().size();
        return currKey;
    }

    private synchronized int getNextSwAccession() {
        int nextKey = MetadataInMemory.getStore().rowKeySet().size() + 1;
        return nextKey;
    }

    public void loadEntity(FirstTierModel model) {
        // populate store up to the desiredkey
        int currSWID = getCurrentSwAccession();
        while (currSWID < model.getSwAccession()) {
            int swid = getNextSwAccession();
            MetadataInMemory.getStore().put(swid, Integer.class, swid);
            currSWID = getCurrentSwAccession();
        }
        MetadataInMemory.getStore().put(model.getSwAccession(), model.getClass(), model);
    }

    @Override
    public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String listInstalledWorkflows() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String listInstalledWorkflowParams(String workflowAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWorkflowAccession(String name, String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fileProvenanceReportTrigger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fileProvenanceReport(Map<FileProvenanceParam, List<String>> params, Writer out) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Map<String, String>> fileProvenanceReport(Map<FileProvenanceParam, List<String>> params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isDuplicateFile(String filepath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsByStatus(WorkflowRunStatus status) {
        Map<Integer, Object> column = MetadataInMemory.getStore().column(WorkflowRun.class);
        List<WorkflowRun> returnList = new ArrayList<>();
        for (Entry<Integer, Object> e : column.entrySet()) {
            WorkflowRun r = (WorkflowRun) e.getValue();
            if (r.getStatus() == status) {
                returnList.add(r);
            }
        }
        return returnList;
    }

    @Override
    public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
        return (WorkflowRun) MetadataInMemory.getStore().get(Integer.valueOf(workflowRunAccession), WorkflowRun.class);
    }

    @Override
    public List<Study> getAllStudies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSequencerRunReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateFile(int fileSWID, FileAttribute fileAtt, Boolean skip) {
        File f = (File) MetadataInMemory.getStore().get(fileSWID, File.class);
        if (skip != null) {
            f.setSkip(skip);
        }
        if (fileAtt != null) {
            fileAtt.setFileAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(fileAtt.getFileAttributeId(), FileAttribute.class, fileAtt);
            f.getFileAttributes().add(fileAtt);
        }
    }

    @Override
    public void annotateFile(int fileSWID, Set<FileAttribute> iusAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateIUS(int iusSWID, IUSAttribute iusAtt, Boolean skip) {
        IUS i = (IUS) MetadataInMemory.getStore().get(iusSWID, IUS.class);
        if (skip != null) {
            i.setSkip(skip);
        }
        if (iusAtt != null) {
            iusAtt.setIusAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(iusAtt.getIusAttributeId(), IUSAttribute.class, iusAtt);
            i.getIusAttributes().add(iusAtt);
        }
    }

    @Override
    public void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
        Lane l = (Lane) MetadataInMemory.getStore().get(laneSWID, Lane.class);
        if (skip != null) {
            l.setSkip(skip);
        }
        if (laneAtt != null) {
            laneAtt.setLaneAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(laneAtt.getLaneAttributeId(), LaneAttribute.class, laneAtt);
            l.getLaneAttributes().add(laneAtt);
        }
    }

    @Override
    public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
        SequencerRun sr = (SequencerRun) MetadataInMemory.getStore().get(sequencerRunSWID, SequencerRun.class);
        if (skip != null) {
            sr.setSkip(skip);
        }
        if (sequencerRunAtt != null) {
            sequencerRunAtt.setSequencerRunAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(sequencerRunAtt.getSequencerRunAttributeId(), SequencerRunAttribute.class, sequencerRunAtt);
            sr.getSequencerRunAttributes().add(sequencerRunAtt);
        }
    }

    @Override
    public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateExperiment(int experimentSWID, ExperimentAttribute experimentAtt, Boolean skip) {
        Experiment e = (Experiment) MetadataInMemory.getStore().get(experimentSWID, Experiment.class);
        if (skip != null) {
            throw new UnsupportedOperationException();
            //e.setSkip(skip);
        }
        if (experimentAtt != null) {
            experimentAtt.setExperimentAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(experimentAtt.getExperimentAttributeId(), ExperimentAttribute.class, experimentAtt);
            e.getExperimentAttributes().add(experimentAtt);
        }
    }

    @Override
    public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateProcessing(int processingSWID, ProcessingAttribute processingAtt, Boolean skip) {
        Processing p = (Processing) MetadataInMemory.getStore().get(processingSWID, Processing.class);
        if (skip != null) {
            throw new UnsupportedOperationException();
            //p.setSkip(skip);
        }
        if (processingAtt != null) {
            processingAtt.setProcessingAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(processingAtt.getProcessingAttributeId(), ProcessingAttribute.class, processingAtt);
            p.getProcessingAttributes().add(processingAtt);
        }
    }

    @Override
    public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
        Processing p = (Processing) MetadataInMemory.getStore().get(processingSWID, Processing.class);
        for (ProcessingAttribute attr : atts) {
            attr.setProcessingAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(attr.getProcessingAttributeId(), ProcessingAttribute.class, attr);
            p.getProcessingAttributes().add(attr);
        }

    }

    @Override
    public void annotateSample(int sampleSWID, SampleAttribute sampleAtt, Boolean skip) {
        Sample s = (Sample) MetadataInMemory.getStore().get(sampleSWID, Sample.class);
        if (skip != null) {
            s.setSkip(skip);
        }
        if (sampleAtt != null) {
            sampleAtt.setSampleAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(sampleAtt.getSampleAttributeId(), SampleAttribute.class, sampleAtt);
            s.getSampleAttributes().add(sampleAtt);
        }
    }

    @Override
    public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateStudy(int studySWID, StudyAttribute studyAtt, Boolean skip) {
        Study s = (Study) MetadataInMemory.getStore().get(studySWID, Study.class);
        if (skip != null) {
            throw new UnsupportedOperationException();
            //s.setSkip(skip);
        }
        if (studyAtt != null) {
            studyAtt.setStudyAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(studyAtt.getStudyAttributeId(), StudyAttribute.class, studyAtt);
            s.getStudyAttributes().add(studyAtt);
        }
    }

    @Override
    public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflow(int workflowSWID, WorkflowAttribute workflowAtt, Boolean skip) {
        Workflow w = (Workflow) MetadataInMemory.getStore().get(workflowSWID, Workflow.class);
        if (skip != null) {
            throw new UnsupportedOperationException();
            //w.setSkip(skip);
        }
        if (workflowAtt != null) {
            //not supported... workflowAtt.setWorkflowAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(workflowAtt.getWorkflowAttributeId(), WorkflowAttribute.class, workflowAtt);
            w.getWorkflowAttributes().add(workflowAtt);
        }
    }

    @Override
    public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflowRun(int workflowRunSWID, WorkflowRunAttribute workflowRunAtt, Boolean skip) {
        WorkflowRun wr = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSWID, WorkflowRun.class);
        if (skip != null) {
            throw new UnsupportedOperationException();
            //wr.setSkip(skip);
        }
        if (workflowRunAtt != null) {
            //not supported... workflowRunAtt.setWorkflowRunAttributeId(getNextSwAccession());
            MetadataInMemory.getStore().put(workflowRunAtt.getWorkflowRunAttributeId(), WorkflowRunAttribute.class, workflowRunAtt);
            wr.getWorkflowRunAttributes().add(workflowRunAtt);
        }
    }

    @Override
    public void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(int workflowRunSWID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReportStdErr(int workflowRunSWID) {
        WorkflowRun wr = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSWID, WorkflowRun.class);
        return wr.getStdErr() == null ? "" : wr.getStdErr();
    }

    @Override
    public String getWorkflowRunReportStdOut(int workflowRunSWID) {
        WorkflowRun wr = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunSWID, WorkflowRun.class);
        return wr.getStdOut() == null ? "" : wr.getStdOut();
    }

    @Override
    public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getFile(int swAccession) {
        return (File) MetadataInMemory.getStore().get(swAccession, File.class);
    }

    @Override
    public SortedSet<WorkflowParam> getWorkflowParams(String swAccession) {
        Workflow workflow = (Workflow) MetadataInMemory.getStore().get(Integer.valueOf(swAccession), Workflow.class);
        return workflow.getWorkflowParams();
    }

    @Override
    public String getProcessingRelations(String swAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Workflow getWorkflow(int workflowAccession) {
        return (Workflow) MetadataInMemory.getStore().get(workflowAccession, Workflow.class);
    }

    @Override
    public List<SequencerRun> getAllSequencerRuns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Lane getLane(int laneAccession) {
        return (Lane) MetadataInMemory.getStore().get(laneAccession, Lane.class);
    }

    @Override
    public LimsKey getLimsKey(int limsKeyAccession) {
        return (LimsKey) MetadataInMemory.getStore().get(limsKeyAccession, LimsKey.class);
    }

    @Override
    public Processing getProcessing(int processingAccession) {
        return (Processing) MetadataInMemory.getStore().get(processingAccession, Processing.class);
    }

    @Override
    public SequencerRun getSequencerRun(int sequencerRunAccession) {
        return (SequencerRun) MetadataInMemory.getStore().get(sequencerRunAccession, SequencerRun.class);
    }

    @Override
    public List<Lane> getLanesFrom(int sequencerRunAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<IUS> getIUSFrom(int laneOrSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LimsKey getLimsKeyFrom(Integer iusAccession) {
        return ((IUS) MetadataInMemory.getStore().get(iusAccession, IUS.class)).getLimsKey();
    }

    @Override
    public List<Experiment> getExperimentsFrom(int studyAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getSamplesFrom(int experimentAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getChildSamplesFrom(int parentSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getParentSamplesFrom(int childSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ParentAccessionModel> getViaParentAccessions(int[] potentialParentAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Object> getViaAccessions(int[] potentialAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Study> getStudyByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getSampleByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SequencerRun getSequencerRunByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(WorkflowRunStatus status, Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(Integer workflowSWID, WorkflowRunStatus status, Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsByStatusCmd(String statusCmd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getEnvironmentReport() {
        SortedMap<String, String> environment = new TreeMap<>();
        environment.put("metadata", "in-memory");
        environment.put("version", this.getClass().getPackage().getImplementationVersion());
        environment.put("java.version", System.getProperty("java.version"));
        for (Entry<Object, Object> property : System.getProperties().entrySet()) {
            environment.put("java.property." + property.getKey().toString(), property.getValue().toString());
        }
        return environment;
    }

    @Override
    public boolean checkClientServerMatchingVersion() {
        return true;
    }

    @Override
    public IUS getIUS(int swAccession) {
        return (IUS) MetadataInMemory.getStore().get(swAccession, IUS.class);
    }

    @Override
    public Sample getSample(int swAccession) {
        return (Sample) MetadataInMemory.getStore().get(swAccession, Sample.class);
    }

    @Override
    public Study getStudy(int swAccession) {
        return (Study) MetadataInMemory.getStore().get(swAccession, Study.class);
    }

    @Override
    public List<AnalysisProvenance> getAnalysisProvenance() {
        return AnalysisProvenanceServiceImpl.buildList((Collection<IUS>) (Collection<?>) MetadataInMemory.getStore().column(IUS.class).values(), Collections.emptyMap());
    }

    @Override
    public List<AnalysisProvenance> getAnalysisProvenance(Map<FileProvenanceFilter, Set<String>> filters) {
        return AnalysisProvenanceServiceImpl.buildList((Collection<IUS>) (Collection<?>) MetadataInMemory.getStore().column(IUS.class).values(), filters);
    }

    @Override
    public List<SampleProvenanceDto> getSampleProvenance() {
        return SampleProvenanceServiceImpl.buildList((Collection<IUS>) (Collection<?>) MetadataInMemory.getStore().column(IUS.class).values());
    }

    @Override
    public void refreshSampleProvenance() {
    }

    @Override
    public List<LaneProvenanceDto> getLaneProvenance() {
        return LaneProvenanceServiceImpl.buildList((Collection<Lane>) (Collection<?>) MetadataInMemory.getStore().column(Lane.class).values());
    }

    @Override
    public void refreshLaneProvenance() {
    }

    private ParentAccessionModel resolveParentAccession(Integer searchString) {
        Map m = MetadataInMemory.getStore().row(searchString);
        return (ParentAccessionModel) Iterables.getOnlyElement(m.values());
    }

}
