package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ExperimentSpotDesignReadSpecDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class ExperimentSpotDesignReadSpecDAOHibernate extends HibernateDaoSupport implements ExperimentSpotDesignReadSpecDAO {
    private final Logger logger = LoggerFactory.getLogger(ExperimentSpotDesignReadSpecDAOHibernate.class);

    /**
     * <p>
     * Constructor for ExperimentSpotDesignReadSpecDAOHibernate.
     * </p>
     */
    public ExperimentSpotDesignReadSpecDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void insert(ExperimentSpotDesignReadSpec obj) {
        this.getHibernateTemplate().save(obj);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ExperimentSpotDesignReadSpec obj) {
        this.getHibernateTemplate().update(obj);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(ExperimentSpotDesignReadSpec obj) {
        this.getHibernateTemplate().delete(obj);
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of ExperimentSpotDesignReadSpec in the database by the ExperimentSpotDesignReadSpec ID.
     */
    @Override
    public ExperimentSpotDesignReadSpec findByID(Integer id) {
        String query = "from ExperimentSpotDesignReadSpec as e where e.experimentSpotDesignReadSpecId = ?";
        ExperimentSpotDesignReadSpec obj = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (ExperimentSpotDesignReadSpec) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment) {
        ExperimentSpotDesignReadSpec dbObject = findByID(experiment.getExperimentSpotDesignReadSpecId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, experiment);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("ExperimentSpotDesignReadSpecDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentSpotDesignReadSpec> list() {
        List expmts = this.getHibernateTemplate().find("from ExperimentSpotDesignReadSpec as readSpec" // desc
        );
        return expmts;
    }
}
