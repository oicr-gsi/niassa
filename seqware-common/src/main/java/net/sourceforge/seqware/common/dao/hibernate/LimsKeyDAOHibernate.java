/*
 * Copyright (C) 2016 SeqWare
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
package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import net.sourceforge.seqware.common.dao.LimsKeyDAO;

/**
 *
 * @author mlaszloffy
 */
public class LimsKeyDAOHibernate extends HibernateDaoSupport implements LimsKeyDAO {

    public LimsKeyDAOHibernate() {
        super();
    }

    @Override
    public Integer insert(LimsKey limsKey) {
        this.getHibernateTemplate().save(limsKey);
    getSessionFactory().getCurrentSession().flush();
        return limsKey.getSwAccession();
    }

    @Override
    public void update(LimsKey limsKey) {
        this.getHibernateTemplate().update(limsKey);
    getSessionFactory().getCurrentSession().flush();
    }

    @Override
    public void delete(LimsKey limsKey) {
        this.getHibernateTemplate().delete(limsKey);
    getSessionFactory().getCurrentSession().flush();
    }

    @Override
    public LimsKey findByID(Integer id) throws DataAccessException {
        String query = "from LimsKey as lk where lk.limsKeyId = ?";
        LimsKey limsKey = null;
        Object[] params = {id};
        List<?> list = this.getHibernateTemplate().find(query, params);
        if (list.size() > 0) {
            limsKey = (LimsKey) list.get(0);
        }
        return limsKey;
    }

    @Override
    public LimsKey findBySWAccession(Integer swAccession) throws DataAccessException {
        String query = "from LimsKey as lk where lk.swAccession = ?";
        LimsKey limsKey = null;
        Object[] params = {swAccession};
        List<?> list = this.getHibernateTemplate().find(query, params);
        if (list.size() > 0) {
            limsKey = (LimsKey) list.get(0);
        }
        return limsKey;
    }

    @Override
    public LimsKey updateDetached(LimsKey limsKey) {
        LimsKey dbObject = findByID(limsKey.getLimsKeyId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, limsKey);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Error updating detached ius", e);
        }
        return null;
    }

    @Override
    public List<LimsKey> list() {
        List<LimsKey> l = new ArrayList<>();
        String query = "from LimsKey";
        List list = this.getHibernateTemplate().find(query);
        for (Object e : list) {
            l.add((LimsKey) e);
        }
        return l;
    }

}
