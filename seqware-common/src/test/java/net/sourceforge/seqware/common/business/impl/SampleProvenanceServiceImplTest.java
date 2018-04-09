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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SampleProvenanceService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 *
 * @author mlaszloffy
 */
public class SampleProvenanceServiceImplTest extends AbstractTestCase {

	@Autowired
	@Qualifier("sampleProvenanceService")
	SampleProvenanceService sps;

	@Autowired
	@Qualifier("IUSService")
	IUSService iusService;

	@Autowired
	@Qualifier("sampleService")
	SampleService sampleService;

	@Autowired
	@Qualifier("experimentService")
	ExperimentService experimentService;

	@Autowired
	@Qualifier("studyTypeService")
	StudyTypeService studyTypeService;

	@Autowired
	@Qualifier("studyService")
	StudyService studyService;

	@Autowired
	@Qualifier("sequencerRunService")
	SequencerRunService sequencerRunService;

	@Autowired
	@Qualifier("laneService")
	LaneService laneService;

	public SampleProvenanceServiceImplTest() {
	}

	@Test
	public void defaultTestData() {
		assertEquals(22, sps.list().size());
	}

	@Test
	public void multipleParentSamples() {
		final String searchKey = "TestStudy";
		Date defaultTstmp = DateTime.parse("2016-01-01T00:00:00Z").toDate();
		Date moreRecentlyUpdatedTstmp = DateTime.parse("2016-05-01T00:00:00Z").toDate();
		String expectedRootSampleName = "PARENT_2";

		Study study = new Study();
		study.setTitle(searchKey);
		study.setExistingType(studyTypeService.findByID(11));
		study.setCenterName("test center name");
		study.setCenterProjectName("test center project name");
		studyService.insert(study);

		Experiment experiment = new Experiment();
		experiment.setStudy(study);
		experimentService.insert(experiment);

		Sample parent1 = new Sample();
		parent1.setName("PARENT_1");
		parent1.setUpdateTimestamp(defaultTstmp);
		sampleService.insert(parent1);

		Sample parent2 = new Sample();
		parent2.setName(expectedRootSampleName);
		parent2.setUpdateTimestamp(moreRecentlyUpdatedTstmp);
		sampleService.insert(parent2);

		Sample parent3 = new Sample();
		parent3.setName("PARENT_3");
		parent3.setUpdateTimestamp(defaultTstmp);
		sampleService.insert(parent3);

		Sample sample = new Sample();
		sample.setExperiment(experiment);
		sample.setParents(ImmutableSet.of(parent1, parent2, parent3));
		sampleService.insert(sample);

		SequencerRun sequencerRun = new SequencerRun();
		sequencerRunService.insert(sequencerRun);

		Lane lane = new Lane();
		laneService.insert(lane);

		IUS ius = new IUS();
		ius.setSample(sample);
		ius.setLane(lane);
		iusService.insert(ius);

		SampleProvenanceDto sp = sps.list().stream().filter(input -> input.getStudyTitle().equals(searchKey))
				.findFirst().get();

		assertEquals(expectedRootSampleName, sp.getRootSampleName());
	}

}
