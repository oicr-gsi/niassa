package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingStudiesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingStudies;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ProcessingStudiesDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ProcessingStudiesDAOHibernate extends HibernateDaoSupport implements ProcessingStudiesDAO {
    private final Logger logger = LoggerFactory.getLogger(ProcessingStudiesDAOHibernate.class);

    /** {@inheritDoc} */
    @Override
    public void insert(ProcessingStudies processingStudies) {
        this.getHibernateTemplate().save(processingStudies);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ProcessingStudies processingStudies) {
        this.getHibernateTemplate().update(processingStudies);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(ProcessingStudies processingStudies) {
        this.getHibernateTemplate().delete(processingStudies);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public ProcessingStudies findByProcessingStudy(Processing processing, Study study) {
        String query = "from ProcessingStudies as ps where ps.processing.processingId = ? and ps.study.studyId = ?";
        ProcessingStudies obj = null;
        Object[] parameters = { processing.getProcessingId(), study.getStudyId() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (ProcessingStudies) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ProcessingStudies updateDetached(ProcessingStudies processingStudies) {
        ProcessingStudies dbObject = findByProcessingStudy(processingStudies.getProcessing(), processingStudies.getStudy());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, processingStudies);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ProcessingStudiesDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingStudies> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
