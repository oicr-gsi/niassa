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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

import ca.on.oicr.gsi.provenance.model.SampleProvenance;
import ca.on.oicr.gsi.provenance.util.Versioning;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;

/**
 *
 * @author mlaszloffy
 */
public class SampleProvenanceDtoFromObjects extends SampleProvenanceDto {

	private IUS ius;
	private Lane lane;
	private SequencerRun sequencerRun;
	private Sample sample;
	private List<Sample> parentSamples;
	private Experiment experiment;
	private Study study;

	public SampleProvenanceDtoFromObjects setIus(IUS ius) {
		this.ius = ius;
		return this;
	}

	public SampleProvenanceDtoFromObjects setLane(Lane lane) {
		this.lane = lane;
		return this;
	}

	public SampleProvenanceDtoFromObjects setSequencerRun(SequencerRun sequencerRun) {
		this.sequencerRun = sequencerRun;
		return this;
	}

	public SampleProvenanceDtoFromObjects setSample(Sample sample) {
		this.sample = sample;
		return this;
	}

	public SampleProvenanceDtoFromObjects setParentSamples(List<Sample> parentSamples) {
		this.parentSamples = parentSamples;
		return this;
	}

	public SampleProvenanceDtoFromObjects setExperiment(Experiment experiment) {
		this.experiment = experiment;
		return this;
	}

	public SampleProvenanceDtoFromObjects setStudy(Study study) {
		this.study = study;
		return this;
	}

	@Override
	public String getStudyTitle() {
		return study.getTitle();
	}

	@Override
	public SortedMap<String, SortedSet<String>> getStudyAttributes() {
		SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
		for (StudyAttribute attr : study.getStudyAttributes()) {
			SortedSet<String> values = attrs.get(attr.getTag());
			if (values == null) {
				values = new TreeSet<>();
				attrs.put(attr.getTag(), values);
			}
			values.add(attr.getValue());
		}
		return attrs;
	}

	@Override
	public String getRootSampleName() {
		if (parentSamples == null || parentSamples.isEmpty()) {
			return null;
		} else {
			return Iterables.getLast(parentSamples).getName();
		}
	}

	@Override
	public String getParentSampleName() {
		List<String> parentSampleNames = new ArrayList<>();
		if (parentSamples != null) {
			for (Sample s : parentSamples) {
				parentSampleNames.add(s.getName());
			}
			return Joiner.on(":").join(parentSampleNames);
		} else {
			return null;
		}
	}

	@Override
	public String getSampleName() {
		if (sample != null) {
			return sample.getName();
		} else {
			return null;
		}
	}

	@Override
	public SortedMap<String, SortedSet<String>> getSampleAttributes() {
		SortedMap<String, SortedSet<String>> attrs = new TreeMap<>();
		String organism = null;

		if (parentSamples != null) {
			for (Sample s : parentSamples) {
				if (s.getOrganism() != null) {
					organism = s.getOrganism().getName();
				}

				for (SampleAttribute attr : s.getSampleAttributes()) {
					SortedSet<String> values = attrs.get(attr.getTag());
					if (values == null) {
						values = new TreeSet<>();
						attrs.put(attr.getTag(), values);
					}
					values.add(attr.getValue());
				}
			}
		}

		if (sample != null) {
			if (sample.getOrganism() != null) {
				organism = sample.getOrganism().getName();
			}

			for (SampleAttribute attr : sample.getSampleAttributes()) {
				SortedSet<String> values = attrs.get(attr.getTag());
				if (values == null) {
					values = new TreeSet<>();
					attrs.put(attr.getTag(), values);
				}
				values.add(attr.getValue());
			}
		}

		if (organism != null) {
			attrs.put("geo_organism", ImmutableSortedSet.of(organism));
		}

		if (ius != null) {
			for (IUSAttribute attr : ius.getIusAttributes()) {
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
	public String getIusTag() {
		if (ius != null) {
			return ius.getTag();
		} else {
			return null;
		}
	}

	@Override
	public Boolean getSkip() {
		if (ius != null && Boolean.TRUE.equals(ius.getSkip())) {
			return true;
		}
		if (sample != null && Boolean.TRUE.equals(sample.getSkip())) {
			return true;
		}
		if (lane != null && Boolean.TRUE.equals(lane.getSkip())) {
			return true;
		}
		if (sequencerRun != null && Boolean.TRUE.equals(sequencerRun.getSkip())) {
			return true;
		}
		// study skip is not supported
		// experiment skip is not supported
		if (parentSamples != null) {
			for (Sample s : parentSamples) {
				if (Boolean.TRUE.equals(s.getSkip())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getSampleProvenanceId() {
		return ius.getSwAccession().toString();
	}

	@Override
	public String getProvenanceId() {
		return getSampleProvenanceId();
	}

	@Override
	public String getVersion() {
		return Versioning.getSha256(new SampleProvenance() {

			@Override
			public String getVersion() {
				return SampleProvenanceDtoFromObjects.this.getVersion();
			}

			@Override
			public String getProvenanceId() {
				return SampleProvenanceDtoFromObjects.this.getProvenanceId();
			}

			@Override
			public DateTime getLastModified() {
				return LaneProvenanceDtoFromObjects.convert(SampleProvenanceDtoFromObjects.this.getLastModified());
			}

			@Override
			public String getStudyTitle() {
				return SampleProvenanceDtoFromObjects.this.getStudyTitle();
			}

			@Override
			public SortedMap<String, SortedSet<String>> getStudyAttributes() {
				return SampleProvenanceDtoFromObjects.this.getStudyAttributes();
			}

			@Override
			public Boolean getSkip() {
				return SampleProvenanceDtoFromObjects.this.getSkip();
			}

			@Override
			public String getSequencerRunPlatformModel() {
				return SampleProvenanceDtoFromObjects.this.getSequencerRunPlatformModel();
			}

			@Override
			public String getSequencerRunName() {
				return SampleProvenanceDtoFromObjects.this.getSequencerRunName();
			}

			@Override
			public SortedMap<String, SortedSet<String>> getSequencerRunAttributes() {
				return SampleProvenanceDtoFromObjects.this.getSequencerRunAttributes();
			}

			@Override
			public String getSampleProvenanceId() {
				return SampleProvenanceDtoFromObjects.this.getSampleProvenanceId();
			}

			@Override
			public String getSampleName() {
				return SampleProvenanceDtoFromObjects.this.getSampleName();
			}

			@Override
			public SortedMap<String, SortedSet<String>> getSampleAttributes() {
				return SampleProvenanceDtoFromObjects.this.getSampleAttributes();
			}

			@Override
			public String getRootSampleName() {
				return SampleProvenanceDtoFromObjects.this.getRootSampleName();
			}

			@Override
			public String getParentSampleName() {
				return SampleProvenanceDtoFromObjects.this.getParentSampleName();
			}

			@Override
			public String getLaneNumber() {
				return SampleProvenanceDtoFromObjects.this.getLaneNumber();
			}

			@Override
			public SortedMap<String, SortedSet<String>> getLaneAttributes() {
				return SampleProvenanceDtoFromObjects.this.getLaneAttributes();
			}

			@Override
			public String getIusTag() {
				return SampleProvenanceDtoFromObjects.this.getIusTag();
			}

			@Override
			public DateTime getCreatedDate() {
				return LaneProvenanceDtoFromObjects.convert(SampleProvenanceDtoFromObjects.this.getCreatedDate());
			}
		});
	}

	@Override
	public ZonedDateTime getLastModified() {
		return Stream.<Instant>concat(//
				Stream.of(//
						createdDate == null ? null : createdDate.toInstant(),
						lastModified == null ? null : lastModified.toInstant()),
				Stream.<Date>concat(//
						Stream.of(ius == null ? null : ius.getCreateTimestamp(),
								ius == null ? null : ius.getUpdateTimestamp(),
								lane == null ? null : lane.getCreateTimestamp(),
								lane == null ? null : lane.getUpdateTimestamp(),
								sequencerRun == null ? null : sequencerRun.getCreateTimestamp(),
								sequencerRun == null ? null : sequencerRun.getUpdateTimestamp(),
								sample == null ? null : sample.getCreateTimestamp(),
								sample == null ? null : sample.getUpdateTimestamp(),
								experiment == null ? null : experiment.getCreateTimestamp(),
								experiment == null ? null : experiment.getUpdateTimestamp(),
								study == null ? null : study.getCreateTimestamp(),
								study == null ? null : study.getUpdateTimestamp()),
						parentSamples == null ? Stream.empty()
								: parentSamples.stream().flatMap(
										parent -> Stream.of(parent.getCreateTimestamp(), parent.getUpdateTimestamp())))//
						.filter(Objects::nonNull)//
						.map(Date::toInstant))
				.filter(Objects::nonNull)//
				.max(Instant::compareTo)//
				.map(i -> ZonedDateTime.ofInstant(i, ZoneId.of("Z")))//
				.orElse(null);
	}

	@Override
	public ZonedDateTime getCreatedDate() {
		return Stream.<Instant>concat(Stream.of(createdDate == null ? null : createdDate)//
				.filter(Objects::nonNull)//
				.map(ZonedDateTime::toInstant),
				Stream.of(lane == null ? null : lane.getCreateTimestamp(),
						sequencerRun == null ? null : sequencerRun.getCreateTimestamp())//
						.filter(Objects::nonNull)//
						.map(Date::toInstant))
				.min(Instant::compareTo)//
				.map(i -> ZonedDateTime.ofInstant(i, ZoneId.of("Z")))//
				.orElse(null);
	}

}
