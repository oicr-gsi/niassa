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
package net.sourceforge.seqware.common.model.types;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author mlaszloffy
 */
public class IusLimsKeyDtoType {

    private Integer iusSwid;
    private LimsKeyType limsKey;

    @XmlAttribute
    public Integer getIusSwid() {
        return iusSwid;
    }

    public void setIusSwid(Integer iusSwid) {
        this.iusSwid = iusSwid;
    }

    public LimsKeyType getLimsKey() {
        return limsKey;
    }

    public void setLimsKey(LimsKeyType limsKey) {
        this.limsKey = limsKey;
    }

}
