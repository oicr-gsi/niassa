package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ProcessingSequencerRunsDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ProcessingSequencerRunsDAOHibernate extends HibernateDaoSupport implements ProcessingSequencerRunsDAO {
    private final Logger logger = LoggerFactory.getLogger(ProcessingSequencerRunsDAOHibernate.class);

    /** {@inheritDoc} */
    @Override
    public void insert(ProcessingSequencerRuns processingSequencerRuns) {
        this.getHibernateTemplate().save(processingSequencerRuns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingSequencerRunsDAO #update (
     * net.sourceforge.seqware.common.model.ProcessingSequencerRuns)
     */
    /** {@inheritDoc} */
    @Override
    public void update(ProcessingSequencerRuns processingSequencerRuns) {
        this.getHibernateTemplate().update(processingSequencerRuns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingSequencerRunsDAO #delete (
     * net.sourceforge.seqware.common.model.ProcessingSequencerRuns)
     */
    /** {@inheritDoc} */
    @Override
    public void delete(ProcessingSequencerRuns processingSequencerRuns) {
        this.getHibernateTemplate().delete(processingSequencerRuns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingSequencerRunsDAO #findByProcessingSequencerRun
     * (net.sourceforge.seqware.common.model.Processing, net.sourceforge.seqware.common.model.SequencerRun)
     */
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun) {
        String query = "from ProcessingSequencerRuns as ps where ps.processing.processingId = ? and ps.sequencerRun.sequencerRunId = ?";
        ProcessingSequencerRuns obj = null;
        Object[] parameters = { processing.getProcessingId(), sequencerRun.getSequencerRunId() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (ProcessingSequencerRuns) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns) {
        ProcessingSequencerRuns dbObject = findByProcessingSequencerRun(processingSequencerRuns.getProcessing(),
                processingSequencerRuns.getSequencerRun());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, processingSequencerRuns);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ProcessingSequencerRunsDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingSequencerRuns> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
