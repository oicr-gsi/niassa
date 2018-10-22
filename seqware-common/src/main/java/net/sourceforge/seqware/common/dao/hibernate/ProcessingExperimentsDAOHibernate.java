package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingExperimentsDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingExperiments;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ProcessingExperimentsDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ProcessingExperimentsDAOHibernate extends HibernateDaoSupport implements ProcessingExperimentsDAO {
    private final Logger logger = LoggerFactory.getLogger(ProcessingExperimentsDAOHibernate.class);

    /** {@inheritDoc} */
    @Override
    public void insert(ProcessingExperiments processingExperiments) {
        this.getHibernateTemplate().save(processingExperiments);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ProcessingExperiments processingExperiments) {
        this.getHibernateTemplate().update(processingExperiments);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(ProcessingExperiments processingExperiments) {
        this.getHibernateTemplate().delete(processingExperiments);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public ProcessingExperiments findByProcessingExperiment(Processing processing, Experiment experiment) {
        String query = "from ProcessingExperiments as pe where pe.processing.processingId = ? and pe.experiment.experimentId = ?";
        ProcessingExperiments obj = null;
        Object[] parameters = { processing.getProcessingId(), experiment.getExperimentId() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (ProcessingExperiments) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ProcessingExperiments updateDetached(ProcessingExperiments processingExperiments) {
        ProcessingExperiments dbObject = findByProcessingExperiment(processingExperiments.getProcessing(),
                processingExperiments.getExperiment());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, processingExperiments);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ProcessingExperimentsDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingExperiments> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
