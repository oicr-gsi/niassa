package net.sourceforge.seqware.common.metadata;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.SequencerRunStatus;
import io.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.FileAttribute;
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
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface Metadata {

    /**
     * <p>
     * clean_up.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue clean_up(); // Close out connection to Meta store

    /**
     * <p>
     * mapProcessingIdToAccession.
     * </p>
     *
     * @param processingId
     *            a int.
     * @return a int.
     */
    int mapProcessingIdToAccession(int processingId);

    /**
     * <p>
     * addStudy.
     * </p>
     *
     * @param title
     *            a {@link java.lang.String} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @param centerName
     *            a {@link java.lang.String} object.
     * @param centerProjectName
     *            a {@link java.lang.String} object.
     * @param studyTypeId
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */

    ReturnValue addStudy(String title, String description, String centerName, String centerProjectName, Integer studyTypeId);

    /**
     * <p>
     * addExperiment.
     * </p>
     *
     * @param studySwAccession
     *            a {@link java.lang.Integer} object.
     * @param platformId
     *            a {@link java.lang.Integer} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @param title
     *            a {@link java.lang.String} object.
     * @param experimentLibraryDesignId
     *            the value of experimentLibraryDesignId
     * @param experimentSpotDesignId
     *            the value of experimentSpotDesignId
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title,
            Integer experimentLibraryDesignId, Integer experimentSpotDesignId);

    /**
     * <p>
     * addSample.
     * </p>
     *
     * @param experimentAccession
     *            a {@link java.lang.Integer} object.
     * @param parentSampleAccession
     * @param organismId
     *            a {@link java.lang.Integer} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @param title
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession, Integer organismId, String description, String title);

    /**
     * <p>
     * addSample.
     * </p>
     *
     * @param platformAccession
     * @param name
     * @param description
     *            a {@link java.lang.String} object.
     * @param status
     *            the value of status
     * @param pairdEnd
     * @param filePath
     * @param skip
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip, String filePath,
            SequencerRunStatus status);

    /**
     * <p>
     * addSample.
     * </p>
     *
     * @param sequencerRunAccession
     * @param studyTypeId
     * @param libraryStrategyId
     * @param librarySelectionId
     * @param description
     *            a {@link java.lang.String} object.
     * @param laneNumber
     * @param librarySourceId
     * @param name
     * @param skip
     * @param cycleDescriptor
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId,
            Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip, Integer laneNumber);

    /**
     * <p>
     * addIUS.
     * </p>
     *
     * @param laneAccession
     * @param sampleAccession
     * @param skip
     * @param name
     * @param barcode
     * @param description
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */

    public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip);

    /**
     * Creates an IUS object that is linked to the specified LimsKey.
     *
     * @param limsKeyAccession
     * @param skip
     *
     * @return the IUS SWID/accession.
     */
    public Integer addIUS(Integer limsKeyAccession, boolean skip);
    
    /**
     * Creates a LimsKey with the specified Lims provider, ID, version, and lastModified date.
     * 
     * @param provider
     * @param id
     * @param version
     * @param lastModified
     * @return the LimsKey SWID/accession.
     */
    public Integer addLimsKey(String provider, String id, String version, ZonedDateTime lastModified);
    
    /**
     * Get the LimsKey from SWID/accession.
     *
     * @param limsKeyAccession
     * @return {@link net.sourceforge.seqware.common.model.LimsKey}
     */
    public LimsKey getLimsKey(int limsKeyAccession);

    /**
     * Replaces the persisted IUS with the provided IUS.
     *
     * Warning: This includes Empty or null fields.
     *
     * @param ius
     */
    public void updateIUS(IUS ius);

    /**
     * Replaces the persisted LimsKey with the provided LimsKey.
     *
     * Warning: This includes Empty or null fields.
     *
     * @param limsKey
     */
    public void updateLimsKey(LimsKey limsKey);

    /**
     * Deletes the orphaned IUS.
     *
     * Deletion of IUSes with references will result in a run time exception.
     *
     * @param iusAccession
     */
    public void deleteIUS(Integer iusAccession);

    /**
     * Deletes the orphaned IUS.
     *
     * Deletion of IUSes with references will result in a run time exception.
     *
     * @param limsKeyAcccession
     */
    public void deleteLimsKey(Integer limsKeyAcccession);

    /**
     * <p>
     * getPlatforms.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
     */
    List<Platform> getPlatforms();

    /**
     * <p>
     * getExperiment.
     * </p>
     *
     * @param swAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment getExperiment(int swAccession);

    /**
     * <p>
     * getExperimentLibraryDesigns.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    List<ExperimentLibraryDesign> getExperimentLibraryDesigns();

    /**
     * <p>
     * getPlatforms.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec} object.
     */
    List<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecs();

    /**
     * <p>
     * getExperimentSpotDesigns.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     */
    List<ExperimentSpotDesign> getExperimentSpotDesigns();

    /**
     * <p>
     * getOrganisms.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Organism} object.
     */
    List<Organism> getOrganisms();

    /**
     * <p>
     * getStudyTypes.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
     */
    List<StudyType> getStudyTypes();

    /**
     * <p>
     * getLibraryStrategies.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
     */
    List<LibraryStrategy> getLibraryStrategies();

    /**
     * <p>
     * getLibrarySelections.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
     */
    List<LibrarySelection> getLibrarySelections();

    /**
     * <p>
     * getLibrarySource.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
     */
    List<LibrarySource> getLibrarySource();

    /**
     * <p>
     * add_empty_processing_event.
     * </p>
     *
     * @param parentIDs
     *            an array of int.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue add_empty_processing_event(int parentIDs[]); // Return Processing
                                                             // ID of record just
                                                             // created,
                                                             // deprecated use
                                                             // add_empty_processing_event_by_parent_accession

    /**
     * <p>
     * add_empty_processing_event_by_parent_accession.
     * </p>
     *
     * @param parentAccessions
     *            an array of int.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue add_empty_processing_event_by_parent_accession(int parentAccessions[]);

    /**
     * <p>
     * add_task_group.
     * </p>
     *
     * @param parentIDs
     *            an array of int.
     * @param childIDs
     *            an array of int.
     * @param algorithm
     *            a {@link java.lang.String} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description); // Return
                                                                                                       // Processing
                                                                                                       // ID
                                                                                                       // of
                                                                                                       // record
                                                                                                       // just
                                                                                                       // created

    /**
     * <p>
     * processing_event_to_task_group.
     * </p>
     *
     * @param processingID
     *            a int.
     * @param parentIDs
     *            an array of int.
     * @param childIDs
     *            an array of int.
     * @param algorithm
     *            a {@link java.lang.String} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs, String algorithm, String description); // Return
                                                                                                                                         // Processing
                                                                                                                                         // ID
                                                                                                                                         // of
                                                                                                                                         // record
                                                                                                                                         // just
                                                                                                                                         // created

    /**
     * <p>
     * update_processing_event.
     * </p>
     *
     * @param processingID
     *            a int.
     * @param retval
     *            a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue update_processing_event(int processingID, ReturnValue retval);

    /**
     * <p>
     * update_processing_status.
     * </p>
     *
     * @param processingID
     *            a int.
     * @param status
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue update_processing_status(int processingID, ProcessingStatus status);

    /**
     * <p>
     * associate_processing_event_with_parents_and_child.
     * </p>
     *
     * @param processingID
     *            a int.
     * @param parentIDs
     *            an array of int.
     * @param childIDs
     *            an array of int.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs);

    /**
     * <p>
     * add_workflow_run.
     * </p>
     *
     * @param workflowAccession
     *            a int.
     * @return a int.
     */
    int add_workflow_run(int workflowAccession);

    /**
     * <p>
     * update_processing_workflow_run.
     * </p>
     *
     * @param processingID
     *            a int.
     * @param workflowRunID
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue update_processing_workflow_run(int processingID, int workflowRunID);

    /**
     * <p>
     * add_workflow_run_ancestor.
     * </p>
     *
     * @param workflowRunAccession
     *            a int.
     * @param processingId
     *            a int.
     */
    void add_workflow_run_ancestor(int workflowRunAccession, int processingId);

    /**
     * <p>
     * get_workflow_run_accession.
     * </p>
     *
     * @param workflowRunId
     *            a int.
     * @return a int.
     */
    int get_workflow_run_accession(int workflowRunId);

    /**
     * <p>
     * get_workflow_run_id.
     * </p>
     *
     * @param workflowRunAccession
     *            a int.
     * @return a int.
     */
    int get_workflow_run_id(int workflowRunAccession);

    /**
     * <p>
     * getWorkflowRun.
     * </p>
     *
     * @param workflowRunAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun getWorkflowRun(int workflowRunAccession);

    /**
     * <p>
     * getWorkflowRun and linked IUSes.
     * </p>
     *
     * @param workflowRunAccession
     *                             a int.
     *
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun getWorkflowRunWithIuses(int workflowRunAccession);

    /**
     * Get workflow run by status cmd (oozie job id in oozie).
     *
     * @param statusCmd
     * @return
     */
    List<WorkflowRun> getWorkflowRunsByStatusCmd(String statusCmd);

    /**
     * Returns the workflow_runs associated with a group of input files.
     *
     * Specifically, this resource will return all workflow runs that use at least one of the input file accessions
     *
     * @param fileAccessions
     * @return
     */
    List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions);

    /**
     * Returns the workflow_runs associated with a group of input files.
     *
     * Specifically, this resource will return all workflow runs that use at least one of the input file accessions constrained to the
     * provided list of workflow accessions
     *
     * @param fileAccessions
     * @param workflowAccessions
     * @return
     */
    List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions, List<Integer> workflowAccessions);

    /**
     * Returns the workflow_runs associated with a group of files. Search types are defined as:
     *
     * @param fileAccessions
     * @param search_type
     * @return
     */
    List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type);

    /**
     * <p>
     * get_workflow_info.
     * </p>
     *
     * @param workflowAccession
     *            a int.
     * @return a {@link java.util.Map} object.
     */
    Map<String, String> get_workflow_info(int workflowAccession);

    /**
     * <p>
     * linkWorkflowRunAndParent.
     * </p>
     *
     * @param workflowRunId
     *            a int.
     * @param parentAccession
     *            a int.
     * @return a boolean.
     * @throws java.sql.SQLException
     *             if any.
     */
    boolean linkWorkflowRunAndParent(int workflowRunId, int... parentAccession) throws SQLException;

    /**
     * <p>
     * update_workflow_run.
     * </p>
     *
     * @param workflowRunId
     *            a int.
     * @param pegasusCmd
     *            a {@link java.lang.String} object.
     * @param workflowTemplate
     *            a {@link java.lang.String} object.
     * @param status
     *            a {@link java.lang.String} object.
     * @param statusCmd
     *            a {@link java.lang.String} object.
     * @param workingDirectory
     *            a {@link java.lang.String} object.
     * @param dax
     *            a {@link java.lang.String} object.
     * @param ini
     *            a {@link java.lang.String} object.
     * @param host
     *            a {@link java.lang.String} object.
     * @param stdErr
     *            a {@link java.lang.String} object.
     * @param stdOut
     *            a {@link java.lang.String} object.
     * @param workflowEngine
     *            the value of workflowEngine
     * @param inputFiles
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, WorkflowRunStatus status,
            String statusCmd, String workingDirectory, String dax, String ini, String host, String stdErr, String stdOut,
            String workflowEngine, Set<Integer> inputFiles);

    void updateWorkflowRun(WorkflowRun wr);

    /**
     * <p>
     * addWorkflow.
     * </p>
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @param version
     *            a {@link java.lang.String} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @param baseCommand
     *            a {@link java.lang.String} object.
     * @param configFile
     *            a {@link java.lang.String} object.
     * @param templateFile
     *            a {@link java.lang.String} object.
     * @param provisionDir
     *            a {@link java.lang.String} object.
     * @param storeProvisionDir
     *            a boolean.
     * @param archiveZip
     *            a {@link java.lang.String} object.
     * @param storeArchiveZip
     *            a boolean.
     * @param workflowClass
     * @param workflowEngine
     * @param workflowType
     * @param seqwareVersion
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile, String templateFile,
            String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip, String workflowClass,
            String workflowType, String workflowEngine, String seqwareVersion);

    /**
     * <p>
     * updateWorkflow.
     * </p>
     *
     * @param workflowId
     *            a int.
     * @param permanentBundleLocation
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation);

    /**
     * <p>
     * listInstalledWorkflows.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    String listInstalledWorkflows();

    /**
     * <p>
     * getWorkflowAccession.
     * </p>
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @param version
     *            a {@link java.lang.String} object.
     * @return a int.
     */
    int getWorkflowAccession(String name, String version);

    /**
     * Retrieves all environmental information from the web-service
     *
     * @return
     */
    Map<String, String> getEnvironmentReport();

    /**
     * Check to see if client seqware version matches server version
     *
     * @return
     */
    boolean checkClientServerMatchingVersion();

    /**
     * <p>
     * isDuplicateFile.
     * </p>
     *
     * @param filepath
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    Boolean isDuplicateFile(String filepath);

    /**
     * <p>
     * getWorkflowRunsByStatus.
     * </p>
     *
     * @param status
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRun> getWorkflowRunsByStatus(WorkflowRunStatus status);

    /**
     * <p>
     * getWorkflowRunWithWorkflow.
     * </p>
     *
     * @param workflowRunAccession
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession);

    /**
     * <p>
     * getAllStudies.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Study> getAllStudies();

    /**
     * <p>
     * getSequencerRunReport.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSequencerRunReport();

    /**
     * <p>
     * annotateFile.
     * </p>
     *
     * @param laneSWID
     * @param iusAtt
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateFile(int laneSWID, FileAttribute iusAtt, Boolean skip);

    /**
     * <p>
     * annotateFile.
     * </p>
     *
     * @param fileSWID
     *            a int.
     * @param iusAtts
     */
    void annotateFile(int fileSWID, Set<FileAttribute> iusAtts);

    /**
     * <p>
     * annotateIUS.
     * </p>
     *
     * @param laneSWID
     *            a int.
     * @param iusAtt
     *            a {@link net.sourceforge.seqware.common.model.IUSAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateIUS(int laneSWID, IUSAttribute iusAtt, Boolean skip);

    /**
     * <p>
     * annotateIUS.
     * </p>
     *
     * @param laneSWID
     *            a int.
     * @param iusAtts
     *            a {@link java.util.Set} object.
     */
    void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts);

    /**
     * <p>
     * annotateLane.
     * </p>
     *
     * @param laneSWID
     *            a int.
     * @param laneAtt
     *            a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip);

    /**
     * <p>
     * annotateLane.
     * </p>
     *
     * @param laneSWID
     *            a int.
     * @param laneAtts
     *            a {@link java.util.Set} object.
     */
    void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts);

    /**
     * <p>
     * annotateSequencerRun.
     * </p>
     *
     * @param sequencerRunSWID
     *            a int.
     * @param sequencerRunAtt
     *            a {@link net.sourceforge.seqware.common.model.SequencerRunAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip);

    /**
     * <p>
     * annotateSequencerRun.
     * </p>
     *
     * @param sequencerRunSWID
     *            a int.
     * @param sequencerRunAtts
     *            a {@link java.util.Set} object.
     */
    void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts);

    /**
     * <p>
     * annotateExperiment.
     * </p>
     *
     * @param experimentSWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip);

    /**
     * <p>
     * annotateExperiment.
     * </p>
     *
     * @param experimentSWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts);

    /**
     * <p>
     * annotateProcessing.
     * </p>
     *
     * @param processingSWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip);

    /**
     * <p>
     * annotateProcessing.
     * </p>
     *
     * @param processingSWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts);

    /**
     * <p>
     * annotateSample.
     * </p>
     *
     * @param sampleSWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip);

    /**
     * <p>
     * annotateSample.
     * </p>
     *
     * @param sampleSWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateSample(int sampleSWID, Set<SampleAttribute> atts);

    /**
     * <p>
     * annotateStudy.
     * </p>
     *
     * @param studySWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.StudyAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateStudy(int studySWID, StudyAttribute att, Boolean skip);

    /**
     * <p>
     * annotateStudy.
     * </p>
     *
     * @param studySWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateStudy(int studySWID, Set<StudyAttribute> atts);

    /**
     * <p>
     * annotateWorkflow.
     * </p>
     *
     * @param workflowSWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip);

    /**
     * <p>
     * annotateWorkflow.
     * </p>
     *
     * @param workflowSWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts);

    /**
     * <p>
     * annotateWorkflowRun.
     * </p>
     *
     * @param workflowrunSWID
     *            a int.
     * @param att
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
     * @param skip
     *            a {@link java.lang.Boolean} object.
     */
    void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip);

    /**
     * <p>
     * annotateWorkflowRun.
     * </p>
     *
     * @param workflowSWID
     *            a int.
     * @param atts
     *            a {@link java.util.Set} object.
     */
    void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts);

    // void annotateFile(int fileSWID, FileAttribute att, Boolean skip);

    /**
     * <p>
     * getWorkflowRunReport.
     * </p>
     *
     * @param workflowRunSWID
     *            a int.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReport(int workflowRunSWID);

    /**
     * <p>
     * getWorkflowRunReportStdErr.
     * </p>
     *
     * @param workflowRunSWID
     *            a int.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReportStdErr(int workflowRunSWID);

    /**
     * <p>
     * getWorkflowRunReportStdOut.
     * </p>
     *
     * @param workflowRunSWID
     *            a int.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReportStdOut(int workflowRunSWID);

    /**
     * <p>
     * getWorkflowRunReport.
     * </p>
     *
     * @param workflowSWID
     *            a int.
     * @param earliestDate
     *            a {@link java.util.Date} object.
     * @param latestDate
     *            a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate);

    /**
     * <p>
     * getWorkflowRunReport.
     * </p>
     *
     * @param status
     *            , can be null
     * @param earliestDate
     *            a {@link java.util.Date} object.
     * @param latestDate
     *            a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReport(WorkflowRunStatus status, Date earliestDate, Date latestDate);

    String getWorkflowRunReport(Integer workflowSWID, WorkflowRunStatus status, Date earliestDate, Date latestDate);

    /**
     * <p>
     * getWorkflowRunReport.
     * </p>
     *
     * @param earliestDate
     *            a {@link java.util.Date} object.
     * @param latestDate
     *            a {@link java.util.Date} object.
     * @return a {@link java.lang.String} object.
     */
    String getWorkflowRunReport(Date earliestDate, Date latestDate);

    /**
     * <p>
     * getFile.
     * </p>
     *
     * @param swAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    net.sourceforge.seqware.common.model.File getFile(int swAccession);

    /**
     * <p>
     * getProcessingRelations.
     * </p>
     *
     * @param swAccession
     * @return a Dot format string
     */
    String getProcessingRelations(String swAccession);

    /**
     * Get a workflow
     *
     * @param workflowAccession
     * @return
     */
    Workflow getWorkflow(int workflowAccession);

    /**
     * Get all of the sequencer runs in the DB.
     *
     * @return a list of Sequencer runs
     */
    List<SequencerRun> getAllSequencerRuns();

    /**
     * <p>
     * getLane.
     * </p>
     *
     * @param laneAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane getLane(int laneAccession);

    /**
     * <p>
     * getProcessing.
     * </p>
     *
     * @param processingAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    Processing getProcessing(int processingAccession);

    /**
     * <p>
     * getSequencerRun.
     * </p>
     *
     * @param sequencerRunAccession
     *            a int.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    SequencerRun getSequencerRun(int sequencerRunAccession);

    /**
     * Get Lanes from a sequencer run.
     *
     * @param sequencerRunAccession
     *            the sw_accession of the sequencer run
     * @return the lanes from the run
     */
    List<Lane> getLanesFrom(int sequencerRunAccession);

    /**
     * Get IUSes (barcodes) from a lane or sample.
     *
     * @param laneOrSampleAccession
     *            the sw_accession of the lane or sample
     * @return a list of IUSes (barcodes)
     */
    List<IUS> getIUSFrom(int laneOrSampleAccession);

    /**
     * Get LimsKey that is associated with an IUS.
     *
     * @param iusAccession the sw_accession/swid of the IUS
     *
     * @return the LimsKey or null if there is no LimsKey associated with the IUS
     */
    public LimsKey getLimsKeyFrom(Integer iusAccession);

    /**
     * Get experiments from a study.
     *
     * @param studyAccession
     *            the sw_accession of the study
     * @return a list of Experiments
     */
    List<Experiment> getExperimentsFrom(int studyAccession);

    /**
     * Get the samples from an experiment.
     *
     * @param experimentAccession
     *            the sw_accession of the experiment
     * @return a list of samples
     */
    List<Sample> getSamplesFrom(int experimentAccession);

    /**
     * Get the child samples of a parent sample. These samples are further down the sample hierarchy (more specific). For example, if the
     * parent sample or donor is ABC_001, the child sample would be ABC_001_Ref for the reference (blood) sample from the donor.
     *
     * @param parentSampleAccession
     *            the sw_accession of the parent sample / donor
     * @return a list of child samples.
     */
    List<Sample> getChildSamplesFrom(int parentSampleAccession);

    /**
     * Get the parent samples of a sample. These samples are further up the sample hierarchy (less specific). For example, if the parent
     * sample or donor is ABC_001, the child sample would be ABC_001_Ref for the reference (blood) sample from the donor.
     *
     * @param childSampleAccession
     *            the sw_accession of the child sample
     * @return a list of parent samples / donors
     */
    List<Sample> getParentSamplesFrom(int childSampleAccession);

    /**
     * Get the models corresponding to potential parent accessions
     *
     * @param potentialParentAccessions
     * @return a list, with null when parent accessions are invalid
     */
    List<ParentAccessionModel> getViaParentAccessions(int[] potentialParentAccessions);

    /**
     * Get the models corresponding to potential accessions
     *
     * @param potentialAccessions
     * @return a list, with null when accessions are invalid
     */
    List<Object> getViaAccessions(int[] potentialAccessions);

    List<Study> getStudyByName(String name);

    List<Sample> getSampleByName(String name);

    SequencerRun getSequencerRunByName(String name);

    /**
     * Get ius by sw accession
     * 
     * @param swAccession
     * @return
     */
    IUS getIUS(int swAccession);

    /**
     * Get sample by swa
     * 
     * @param swAccession
     * @return
     */
    Sample getSample(int swAccession);

    /**
     * Get study by swa
     * 
     * @param swAccession
     * @return
     */
    Study getStudy(int swAccession);
    /**
     * Get all AnalysisProvenance objects from the SeqWare MetaDB.
     * 
     * @return List of {@link net.sourceforge.seqware.common.dto.AnalysisProvenanceDto} objects
     */
    public List<AnalysisProvenance> getAnalysisProvenance();
    
    /**
     * Get AnalysisProvenance objects from the SeqWare MetaDB that pass the user provided filters.
     *
     * @param filters
     *
     * @return List of {@link net.sourceforge.seqware.common.dto.AnalysisProvenanceDto} objects
     */
    public List<AnalysisProvenance> getAnalysisProvenance(Map<FileProvenanceFilter, Set<String>> filters);

    /**
     * Get all SampleProvenance objects from the SeqWare MetaDB.
     *
     * @return List of {@link net.sourceforge.seqware.common.dto.SampleProvenanceDto} objects
     */
    public List<SampleProvenanceDto> getSampleProvenance();

    /**
     * Refresh the SampleProvenance object cache.
     *
     */
    public void refreshSampleProvenance();

    /**
     * Get all LaneProvenance objects from the SeqWare MetaDB.
     *
     * @return List of {@link net.sourceforge.seqware.common.dto.LaneProvenanceDto} objects
     */
    public List<LaneProvenanceDto> getLaneProvenance();

    /**
     * Refresh the LaneProvenance object cache.
     *
     */
    public void refreshLaneProvenance();}
