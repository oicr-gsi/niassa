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
package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import net.sourceforge.seqware.common.business.LimsKeyService;
import net.sourceforge.seqware.common.dao.LimsKeyDAO;
import net.sourceforge.seqware.common.model.LimsKey;

/**
 *
 * @author mlaszloffy
 */
public class LimsKeyServiceImpl implements LimsKeyService {

    private LimsKeyDAO dao = null;
    private final Logger log;

    public LimsKeyServiceImpl() {
        super();
        log = LoggerFactory.getLogger(LimsKeyServiceImpl.class);
    }

    @Override
    public void setLimsKeyDAO(LimsKeyDAO dao) {
        this.dao = dao;
    }

    @Override
    public Integer insert(LimsKey limsKey) {
    	Date d = new Date();
        limsKey.setCreateTimestamp(d);
        limsKey.setUpdateTimestamp(d);
        return dao.insert(limsKey);
    }

    @Override
    public void update(LimsKey limsKey) {
        limsKey.setUpdateTimestamp(new Date());
        dao.update(limsKey);
    }

    @Override
    public void delete(LimsKey limsKey) throws ConstraintViolationException, PersistenceException{
        dao.delete(limsKey);
    }

    @Override
    public LimsKey findByID(Integer id) {
        LimsKey limsKey = null;
        if (id != null) {
            try {
                limsKey = dao.findByID(id);
            } catch (DataAccessException dae) {
                log.error("Cannot find LimsKey by id " + id);
                log.error(dae.getMessage());
            }
        }
        return limsKey;
    }

    @Override
    public LimsKey findBySWAccession(Integer swAccession) {
        LimsKey limsKey = null;
        if (swAccession != null) {
            try {
                limsKey = dao.findBySWAccession(swAccession);
            } catch (DataAccessException dae) {
                log.error("Cannot find LimsKey by swAccession " + swAccession);
                log.error(dae.getMessage());
            }
        }
        return limsKey;
    }

    @Override
    public List<LimsKey> list() {
        return dao.list();
    }

}
