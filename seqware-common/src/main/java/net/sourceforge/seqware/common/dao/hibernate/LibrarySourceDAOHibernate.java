package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.dao.LibrarySourceDAO;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * LibrarySourceDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class LibrarySourceDAOHibernate extends HibernateDaoSupport implements LibrarySourceDAO {
    private final Logger logger = LoggerFactory.getLogger(LibrarySourceDAOHibernate.class);

    /**
     * <p>
     * Constructor for LibrarySourceDAOHibernate.
     * </p>
     */
    public LibrarySourceDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LibrarySource> list(Registration registration) {
        if (registration == null) {
            return null;
        }
        return (list());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LibrarySource findByID(Integer id) {
        String query = "from LibrarySource as l where l.librarySourceId = ?";
        LibrarySource obj = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (LibrarySource) list.get(0);
        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LibrarySource updateDetached(LibrarySource librarySource) {
        LibrarySource dbObject = findByID(librarySource.getLibrarySourceId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, librarySource);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("LibrarySourceDAOHibernate.updateDetached IllegalAccessException or InvocationTargetException exception:",e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LibrarySource> list() {
        ArrayList<LibrarySource> objects = new ArrayList<>();

        List expmts = this.getHibernateTemplate().find("from LibrarySource as ls order by ls.librarySourceId asc" // desc
        );

        for (Object object : expmts) {
            objects.add((LibrarySource) object);
        }
        return objects;
    }
}
