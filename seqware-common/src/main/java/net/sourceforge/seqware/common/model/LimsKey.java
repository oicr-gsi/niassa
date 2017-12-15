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
package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement
public class LimsKey implements Serializable, FirstTierModel {

	private static final long serialVersionUID = 1L;
	@Id
	private Integer limsKeyId;
	private Integer swAccession;
	private String provider;
	private String id;
	private String version;

	private ZonedDateTime lastModified;
	private Date createTimestamp;
	private Date updateTimestamp;

	public Integer getLimsKeyId() {
		return limsKeyId;
	}

	public void setLimsKeyId(Integer limsKeyId) {
		this.limsKeyId = limsKeyId;
	}

	@Override
	public Integer getSwAccession() {
		return swAccession;
	}

	public void setSwAccession(Integer swAccession) {
		this.swAccession = swAccession;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public ZonedDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(ZonedDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, true);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LimsKey))
			return false;
		final LimsKey other = (LimsKey) obj;
		EqualsBuilder b = new EqualsBuilder();
		b.append(getLimsKeyId(), other.getLimsKeyId());
		b.append(getSwAccession(), other.getSwAccession());
		b.append(getProvider(), other.getProvider());
		b.append(getId(), other.getId());
		b.append(getVersion(), other.getVersion());
		b.append(getLastModified() == null ? null : getLastModified().toInstant(),
				other.getLastModified() == null ? null : other.getLastModified().toInstant());
		b.append(getCreateTimestamp(), other.getCreateTimestamp());
		b.append(getUpdateTimestamp(), other.getUpdateTimestamp());
		return b.isEquals();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public LimsKey asDetached() {
		LimsKey detachedLimsKey = new LimsKey();
		detachedLimsKey.setCreateTimestamp(getCreateTimestamp());
		detachedLimsKey.setId(getId());
		detachedLimsKey.setLastModified(getLastModified());
		detachedLimsKey.setLimsKeyId(getLimsKeyId());
		detachedLimsKey.setProvider(getProvider());
		detachedLimsKey.setSwAccession(getSwAccession());
		detachedLimsKey.setUpdateTimestamp(getUpdateTimestamp());
		detachedLimsKey.setVersion(getVersion());
		return detachedLimsKey;
	}

}
