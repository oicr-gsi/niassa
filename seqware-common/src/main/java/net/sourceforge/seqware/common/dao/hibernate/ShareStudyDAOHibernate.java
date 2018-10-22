package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ShareStudyDAO;
import net.sourceforge.seqware.common.model.ShareStudy;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ShareStudyDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ShareStudyDAOHibernate extends HibernateDaoSupport implements ShareStudyDAO {
    private final Logger logger = LoggerFactory.getLogger(ShareStudyDAOHibernate.class);

    /**
     * <p>
     * Constructor for ShareStudyDAOHibernate.
     * </p>
     */
    public ShareStudyDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of ShareStudy into the database.
     * 
     * @param shareStudy
     */
    @Override
    public void insert(ShareStudy shareStudy) {
        this.getHibernateTemplate().save(shareStudy);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of ShareStudy in the database.
     * 
     * @param shareStudy
     */
    @Override
    public void update(ShareStudy shareStudy) {

        this.getHibernateTemplate().update(shareStudy);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of ShareStudy in the database.
     * 
     * @param shareStudy
     */
    @Override
    public void delete(ShareStudy shareStudy) {

        this.getHibernateTemplate().delete(shareStudy);
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of ShareStudy in the database by the ShareStudy ID.
     */
    @Override
    public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId) {
        String query = "from ShareStudy as shareStudy where shareStudy.studyId = ? and shareStudy.registration.registrationId = ?";
        ShareStudy shareStudy = null;
        Object[] parameters = { studyId, registrationId };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            shareStudy = (ShareStudy) list.get(0);
        }
        return shareStudy;
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of ShareStudy in the database by the ShareStudy ID.
     */
    @Override
    public ShareStudy findByID(Integer id) {
        String query = "from ShareStudy as shareStudy where shareStudy.shareStudyId = ?";
        ShareStudy shareStudy = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            shareStudy = (ShareStudy) list.get(0);
        }
        return shareStudy;
    }

    /** {@inheritDoc} */
    @Override
    public ShareStudy updateDetached(ShareStudy shareStudy) {
        ShareStudy dbObject = findByID(shareStudy.getShareStudyId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, shareStudy);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ShareStudyDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ShareStudy> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
