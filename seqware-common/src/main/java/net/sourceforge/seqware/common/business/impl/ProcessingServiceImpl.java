package net.sourceforge.seqware.common.business.impl;

import io.seqware.common.model.ProcessingStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * ProcessingServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ProcessingServiceImpl implements ProcessingService {

    private ProcessingDAO processingDAO = null;
    private FileDAO fileDAO = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingServiceImpl.class);


    /**
     * <p>
     * Constructor for ProcessingServiceImpl.
     * </p>
     */
    public ProcessingServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Sets a private member variable with an instance of an implementation of ProcessingDAO. This method is called by the Spring framework
     * at run time.
     * 
     * @see ProcessingDAO
     */
    @Override
    public void setProcessingDAO(ProcessingDAO processingDAO) {
        this.processingDAO = processingDAO;
    }

    /**
     * Sets a private member variable with an instance of an implementation of FileDAO. This method is called by the Spring framework at run
     * time.
     * 
     * @param fileDAO
     *            implementation of FileDAO
     * @see FileDAO
     */
    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of Processing into the database.
     */
    @Override
    public void insert(SequencerRun sequencerRun, Processing processing) {

        if (processing.getStatus() == null) {
            processing.setStatus(ProcessingStatus.pending);
        }
        // processing.setExperimentId(experiment.getExperimentId());
        processing.setCreateTimestamp(new Date());
        processingDAO.insert(processing);

    }

    /** {@inheritDoc} */
    @Override
    public void insert(Registration registration, SequencerRun sequencerRun, Processing processing) {
        if (processing.getStatus() == null) {
            processing.setStatus(ProcessingStatus.pending);
        }
        // processing.setExperimentId(experiment.getExperimentId());
        processing.setCreateTimestamp(new Date());
        processingDAO.insert(registration, processing);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Processing processing) {

        if (processing.getStatus() == null) {
            processing.setStatus(ProcessingStatus.pending);
        }
        // processing.setExperimentId(experiment.getExperimentId());
        processing.setCreateTimestamp(new Date());
        return processingDAO.insert(processing);

    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of Processing in the database.
     */
    @Override
    public void update(Processing processing) {
        processingDAO.update(processing);
    }

    /**
     * {@inheritDoc}
     * 
     * @param processing
     * @param deleteRealFiles
     */
    @Override
    public void delete(Processing processing, boolean deleteRealFiles) {
        List<File> deleteFiles = null;
        if (deleteRealFiles) {
            deleteFiles = processingDAO.getFiles(processing.getProcessingId());
        }

        Set<Study> studies = processing.getStudies();
        for (Study study : studies) {
            study.getProcessings().remove(processing);
        }

        Set<Experiment> experiments = processing.getExperiments();
        for (Experiment experiment : experiments) {
            experiment.getProcessings().remove(processing);
        }

        Set<Sample> samples = processing.getSamples();
        for (Sample sample : samples) {
            sample.getProcessings().remove(processing);
        }

        Set<SequencerRun> sequencerRuns = processing.getSequencerRuns();
        for (SequencerRun sequencerRun : sequencerRuns) {
            sequencerRun.getProcessings().remove(processing);
        }

        Set<Lane> lanes = processing.getLanes();
        for (Lane lane : lanes) {
            lane.getProcessings().remove(processing);
        }

        Set<IUS> iuses = processing.getIUS();
        for (IUS ius : iuses) {
            ius.getProcessings().remove(processing);
        }

        Set<Processing> parents = processing.getParents();
        for (Processing parent : parents) {
            parent.getChildren().remove(processing);
        }

        Set<Processing> children = processing.getChildren();
        for (Processing child : children) {
            child.getParents().remove(processing);
        }

        processingDAO.delete(processing);

        if (deleteRealFiles) {
            fileDAO.deleteAllWithFolderStore(deleteFiles);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer processingId) {
        return processingDAO.getFiles(processingId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer processingId) {
        return processingDAO.isHasFile(processingId);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Processing> setWithHasFile(Set<Processing> list) {
        for (Processing processing : list) {
            boolean isHasFile = isHasFile(processing.getProcessingId());
            processing.setIsHasFile(isHasFile);
            if (processing.getWorkflowRun() != null) {
                processing.getWorkflowRun().setIsHasFile(isHasFile);
            }
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer processingId, String metaType) {
        return processingDAO.getFiles(processingId, metaType);
    }

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param processingId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer processingId, String metaType) {
        return processingDAO.isHasFile(processingId, metaType);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Processing> setWithHasFile(Set<Processing> list, String metaType) {
        Set<Processing> result = new TreeSet<>();
        for (Processing processing : list) {
            boolean isHasFile = isHasFile(processing.getProcessingId(), metaType);
            if (isHasFile) {
                processing.setIsHasFile(isHasFile);
                if (processing.getWorkflowRun() != null) {
                    processing.getWorkflowRun().setIsHasFile(isHasFile);
                }
                result.add(processing);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @param id
     */
    @Override
    public Processing findByID(Integer id) {

        Processing processing = null;
        if (id != null) {
            try {
                processing = processingDAO.findByID(id);
            } catch (Exception exception) {
                LOGGER.error("ProcessingServiceImpl.findByID: Cannot find Processing by ID " + id,exception.getMessage(),exception);
            }
        }
        return processing;

    }

    /** {@inheritDoc} */
    @Override
    public Processing findByIDOnlyWithRunningWR(Integer processingID) {
        Processing processing = findByID(processingID);
        /*
         * Set<Processing> all = processing.getChildren(); Set<Processing> res = new TreeSet<Processing>();
         * 
         * // get processing with workflow run has not status equal completed for (Processing pr : all) { WorkflowRun workflowRun =
         * pr.getWorkflowRun(); if(workflowRun == null || !workflowRun.getStatus().equals("completed")){ res.add(pr); } }
         * 
         * processing.setChildren(res);
         */
        return processing;
    }

    /** {@inheritDoc} */
    @Override
    public Processing findBySWAccession(Integer swAccession) {
        Processing processing = null;
        if (swAccession != null) {
            try {
                processing = processingDAO.findBySWAccession(swAccession);
            } catch (Exception exception) {
                LOGGER.error("ProcessingServiceImpl.findBySWAccession: Cannot find Processing by swAccession " + swAccession, exception.getMessage(), exception);
            }
        }
        return processing;
    }

    /** {@inheritDoc} */
    @Override
    public List<Processing> findByOwnerID(Integer registrationId) {
        List<Processing> processings = null;
        if (registrationId != null) {
            try {
                processings = processingDAO.findByOwnerID(registrationId);
            } catch (Exception exception) {
                LOGGER.error("ProcessingServiceImpl.findByOwnerID: Cannot find Processings by registrationId " + registrationId,exception.getMessage(),exception);
            }
        }
        return processings;
    }

    /** {@inheritDoc} */
    @Override
    public List<Processing> findByCriteria(String criteria, boolean isCaseSens) {
        return processingDAO.findByCriteria(criteria, isCaseSens);
    }

    /** {@inheritDoc} */
    @Override
    public Processing updateDetached(Processing processing) {
        return processingDAO.updateDetached(processing);
    }

    /** {@inheritDoc} */
    @Override
    public List<Processing> list() {
        return processingDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Processing processing) {
        processingDAO.update(registration, processing);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Processing processing) {
        if (processing.getStatus() == null) {
            processing.setStatus(ProcessingStatus.pending);
        }
        processing.setCreateTimestamp(new Date());
        return processingDAO.insert(registration, processing);
    }

    /** {@inheritDoc} */
    @Override
    public Processing updateDetached(Registration registration, Processing processing) {
        return processingDAO.updateDetached(registration, processing);
    }

    /**
     * {@inheritDoc}
     * 
     * Returns the Set of the Processings belongs to the specific Sample, WorkflowRun.
     */
    @Override
    public Set<Processing> findFor(Sample sample, WorkflowRun workflowRun) {
        Set<Processing> processings = new HashSet<>();
        if (sample.getProcessings() != null) {
            processings.addAll(sample.getProcessings());
            for (Processing proc : sample.getProcessings()) {
                addNestedProcessings(processings, proc);
            }
        }
        for (IUS ius : sample.getIUS()) {
            processings.addAll(ius.getProcessings());
            for (Processing proc : ius.getProcessings()) {
                addNestedProcessings(processings, proc);
            }
        }

        // Remove processings doesn't belong to the workflow run.
        if (workflowRun != null) {
            Iterator<Processing> processingIter = processings.iterator();
            while (processingIter.hasNext()) {
                Processing currProcessing = processingIter.next();
                if (!workflowRun.equals(currProcessing.getWorkflowRun())) {
                    processingIter.remove();
                }
            }
        }
        return processings;
    }

    private void addNestedProcessings(Set<Processing> processings, Processing processing) {
        if (processing.getChildren() != null) {
            Set<Processing> childrenProcessings = processing.getChildren();
            for (Processing child : childrenProcessings) {
                addNestedProcessings(processings, child);
            }
            processings.addAll(childrenProcessings);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<Processing> findFor(Sample sample) {
        return findFor(sample, null);
    }
}

// ex:sw=4:ts=4:
