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
package net.sourceforge.seqware.common.dto.builders;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSortedSet;

import ca.on.oicr.gsi.provenance.util.Versioning;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceDtoFromObjects extends LaneProvenanceDto {

	private Lane lane;
	private SequencerRun sequencerRun;

	public LaneProvenanceDtoFromObjects setLane(Lane lane) {
		this.lane = lane;
		return this;
	}

	public LaneProvenanceDtoFromObjects setSequencerRun(SequencerRun sequencerRun) {
		this.sequencerRun = sequencerRun;
		return this;
	}

	@Override
	public String getSequencerRunName() {
		if (sequencerRun != null) {
			return sequencerRun.getName();
		} else {
			return null;
		}
	}

	@Override
	public SortedMap<String, SortedSet<String>> getSequencerRunAttributes() {
		SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
		if (sequencerRun != null) {
			for (SequencerRunAttribute attr : sequencerRun.getSequencerRunAttributes()) {
				SortedSet<String> values = attrs.get(attr.getTag());
				if (values == null) {
					values = new TreeSet<>();
					attrs.put(attr.getTag(), values);
				}
				values.add(attr.getValue());
			}
			if (sequencerRun.getFilePath() != null) {
				attrs.put("run_dir", ImmutableSortedSet.of(sequencerRun.getFilePath()));
			}

		}
		return attrs;
	}

	@Override
	public String getSequencerRunPlatformModel() {
		if (sequencerRun != null && sequencerRun.getPlatform() != null) {
			return sequencerRun.getPlatform().getName();
		} else {
			return null;
		}
	}

	@Override
	public String getLaneNumber() {
		if (lane == null || lane.getLaneIndex() == null) {
			return null;
		} else {
			return Integer.toString(lane.getLaneIndex() + 1);
		}
	}

	@Override
	public SortedMap<String, SortedSet<String>> getLaneAttributes() {
		SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
		if (lane != null) {
			for (LaneAttribute attr : lane.getLaneAttributes()) {
				SortedSet<String> values = attrs.get(attr.getTag());
				if (values == null) {
					values = new TreeSet<>();
					attrs.put(attr.getTag(), values);
				}
				values.add(attr.getValue());
			}
		}
		return attrs;
	}

	@Override
	public Boolean getSkip() {
		if (lane != null && Boolean.TRUE.equals(lane.getSkip())) {
			return true;
		}
		if (sequencerRun != null && Boolean.TRUE.equals(sequencerRun.getSkip())) {
			return true;
		}
		return false;
	}

	@Override
	public String getLaneProvenanceId() {
		return lane.getSwAccession().toString();
	}

	@Override
	public String getProvenanceId() {
		return getLaneProvenanceId();
	}

	@Override
	public String getVersion() {
		return Versioning.getSha256(this);
	}

	@Override
	public ZonedDateTime getLastModified() {
		return Stream.of(createdDate == null ? null : createdDate.toInstant(),
				lastModified == null ? null : lastModified.toInstant(),
				lane.getCreateTimestamp() == null ? null : lane.getCreateTimestamp().toInstant(),
				lane.getUpdateTimestamp() == null ? null : lane.getUpdateTimestamp().toInstant(),
				sequencerRun.getCreateTimestamp() == null ? null : sequencerRun.getCreateTimestamp().toInstant(),
				sequencerRun.getUpdateTimestamp() == null ? null : sequencerRun.getUpdateTimestamp().toInstant())
				.filter(Objects::nonNull)//
				.max(Instant::compareTo)//
				.map(d -> ZonedDateTime.ofInstant(d, ZoneId.of("Z")))//
				.orElse(null);
	}

	@Override
	public ZonedDateTime getCreatedDate() {
		return Stream
				.of(createdDate == null ? null : createdDate.toInstant(),
						lane.getCreateTimestamp() == null ? null : lane.getCreateTimestamp().toInstant(),
						sequencerRun.getCreateTimestamp() == null ? null
								: sequencerRun.getCreateTimestamp().toInstant())
				.filter(Objects::nonNull)//
				.min(Instant::compareTo)//
				.map(d -> ZonedDateTime.ofInstant(d, ZoneId.of("Z")))//
				.orElse(null);
	}

}
