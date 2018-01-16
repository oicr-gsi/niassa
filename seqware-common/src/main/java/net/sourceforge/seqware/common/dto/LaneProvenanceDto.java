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

import java.time.ZonedDateTime;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.on.oicr.gsi.provenance.model.LaneProvenance;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
import net.sourceforge.seqware.common.model.adapters.SortedMapOfSortedSetAdapter;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement
public class LaneProvenanceDto implements LaneProvenance {

	private String sequencerRunName;
	private SortedMap<String, SortedSet<String>> sequencerRunAttributes;
	private String sequencerRunPlatformModel;
	private String laneNumber;
	private SortedMap<String, SortedSet<String>> laneAttributes;
	private Boolean skip;
	private String laneProvenanceId;
	private String version;
	protected ZonedDateTime lastModified;
	protected ZonedDateTime createdDate;

	@XmlElement

	public String getSequencerRunName() {
		return sequencerRunName;
	}

	public void setSequencerRunName(String sequencerRunName) {
		this.sequencerRunName = sequencerRunName;
	}

	@XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)

	public SortedMap<String, SortedSet<String>> getSequencerRunAttributes() {
		return sequencerRunAttributes;
	}

	public void setSequencerRunAttributes(SortedMap<String, SortedSet<String>> sequencerRunAttributes) {
		this.sequencerRunAttributes = sequencerRunAttributes;
	}

	@XmlElement

	public String getSequencerRunPlatformModel() {
		return sequencerRunPlatformModel;
	}

	public void setSequencerRunPlatformModel(String sequencerRunPlatformModel) {
		this.sequencerRunPlatformModel = sequencerRunPlatformModel;
	}

	@XmlElement

	public String getLaneNumber() {
		return laneNumber;
	}

	public void setLaneNumber(String laneNumber) {
		this.laneNumber = laneNumber;
	}

	@XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)

	public SortedMap<String, SortedSet<String>> getLaneAttributes() {
		return laneAttributes;
	}

	public void setLaneAttributes(SortedMap<String, SortedSet<String>> laneAttributes) {
		this.laneAttributes = laneAttributes;
	}

	@XmlElement

	public Boolean getSkip() {
		return skip;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	@XmlElement

	public String getLaneProvenanceId() {
		return laneProvenanceId;
	}

	public void setLaneProvenanceId(String laneProvenanceId) {
		this.laneProvenanceId = laneProvenanceId;
	}

	@XmlElement

	public String getProvenanceId() {
		return laneProvenanceId;
	}

	@XmlElement

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

	@XmlJavaTypeAdapter(DateTimeAdapter.class)

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder();
		b.append(getSequencerRunName());
		b.append(getSequencerRunAttributes());
		b.append(getSequencerRunPlatformModel());
		b.append(getLaneNumber());
		b.append(getLaneAttributes());
		b.append(getSkip());
		b.append(getLaneProvenanceId());
		b.append(getProvenanceId());
		b.append(getVersion());
		b.append(getLastModified() == null ? null : getLastModified().toInstant());
		b.append(getCreatedDate() == null ? null : getCreatedDate().toInstant());
		return b.toHashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof LaneProvenanceDto) {
			LaneProvenanceDto other = (LaneProvenanceDto) obj;
			EqualsBuilder b = new EqualsBuilder();
			b.append(getSequencerRunName(), other.getSequencerRunName());
			b.append(getSequencerRunAttributes(), other.getSequencerRunAttributes());
			b.append(getSequencerRunPlatformModel(), other.getSequencerRunPlatformModel());
			b.append(getLaneNumber(), other.getLaneNumber());
			b.append(getLaneAttributes(), other.getLaneAttributes());
			b.append(getSkip(), other.getSkip());
			b.append(getLaneProvenanceId(), other.getLaneProvenanceId());
			b.append(getProvenanceId(), other.getProvenanceId());
			b.append(getVersion(), other.getVersion());
			b.append(getLastModified() == null ? null : getLastModified().toInstant(),
					other.getLastModified() == null ? null : other.getLastModified().toInstant());
			b.append(getCreatedDate() == null ? null : getCreatedDate().toInstant(),
					other.getCreatedDate() == null ? null : other.getCreatedDate().toInstant());
			return b.isEquals();
		} else {
			return false;
		}

	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
