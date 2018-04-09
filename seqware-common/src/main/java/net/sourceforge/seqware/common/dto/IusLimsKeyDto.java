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
package net.sourceforge.seqware.common.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import ca.on.oicr.gsi.provenance.model.LimsKey;

/**
 *
 * @author mlaszloffy
 */
public class IusLimsKeyDto implements IusLimsKey {

    private Integer iusSWID;
    private LimsKeyDto limsKey;

    public Integer getIusSWID() {
        return iusSWID;
    }

    public void setIusSWID(Integer iusSWID) {
        this.iusSWID = iusSWID;
    }

    public LimsKey getLimsKey() {
        return limsKey;
    }

    public void setLimsKey(LimsKeyDto limsKey) {
        this.limsKey = limsKey;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iusSWID == null) ? 0 : iusSWID.hashCode());
		result = prime * result + ((limsKey == null) ? 0 : limsKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IusLimsKeyDto)) {
		return false;
		}
		IusLimsKeyDto other = (IusLimsKeyDto) obj;
		if (iusSWID == null) {
			if (other.iusSWID != null)
				return false;
		} else if (!iusSWID.equals(other.iusSWID))
			return false;
		if (limsKey == null) {
			if (other.limsKey != null)
				return false;
		} else if (!limsKey.equals(other.limsKey))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
