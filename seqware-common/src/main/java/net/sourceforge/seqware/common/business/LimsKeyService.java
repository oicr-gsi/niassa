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
package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.dao.LimsKeyDAO;

/**
 *
 * @author mlaszloffy
 */
public interface LimsKeyService {
    
    public void setLimsKeyDAO(LimsKeyDAO dao);
    
    public Integer insert(LimsKey limsKey);
    
    public void update(LimsKey limsKey);
    
    public void delete(LimsKey limsKey);
    
    public LimsKey findByID(Integer id);
    
    public LimsKey findBySWAccession(Integer swAccession);
    
    public List<LimsKey> list();
    
}
