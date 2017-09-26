/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.common.util.xmltools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Reader;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import io.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.dto.builders.AnalysisProvenanceDtoBuilder;
import net.sourceforge.seqware.common.dto.builders.LaneProvenanceDtoFromObjects;
import net.sourceforge.seqware.common.dto.builders.SampleProvenanceDtoFromObjects;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.model.lists.SampleProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.WorkflowList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>
 * JaxbObjectTest class.
 * </p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
public class JaxbObjectTest {

	/**
	 * <p>
	 * Constructor for JaxbObjectTest.
	 * </p>
	 */
	public JaxbObjectTest() {
	}

	/**
	 * <p>
	 * setUpClass.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	/**
	 * <p>
	 * tearDownClass.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * <p>
	 * setUp.
	 * </p>
	 */
	@Before
	public void setUp() {
	}

	/**
	 * <p>
	 * tearDown.
	 * </p>
	 */
	@After
	public void tearDown() {
	}

	/**
	 * Test of marshal method, of class JaxbObject.
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testMarshal() throws Exception {
		Log.info("marshal");
		FileType type = new FileType();
		type.setDisplayName("Display Name");
		type.setExtension("extension");
		type.setFileTypeId(-1);
		type.setMetaType("meta-type");

		JaxbObject<FileType> instance = new JaxbObject<>();
		String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><FileType><displayName>Display Name</displayName><extension>extension</extension><fileTypeId>-1</fileTypeId><metaType>meta-type</metaType></FileType>";
		String result = instance.marshal(type, FileType.class);
		assertEquals(expResult, result);
	}

	/**
	 * Test of unMarshal method, of class JaxbObject.
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testUnMarshal() throws Exception {
		Log.info("unMarshal");
		FileType type = new FileType();
		type.setDisplayName("Display Name");
		type.setExtension("extension");
		type.setFileTypeId(-1);
		type.setMetaType("meta-type");

		JaxbObject<FileType> instance = new JaxbObject<>();
		String result = instance.marshal(type, FileType.class);

		Reader in = new StringReader(result);

		FileType type2 = instance.unMarshal(FileType.class, in);
		assertEquals(type, type2);
	}

	/**
	 * <p>
	 * testSampleJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testSampleJaxb() throws Exception {
		Sample sample = new Sample();
		sample.setSampleId(Integer.MIN_VALUE);

		SampleAttribute sa1 = new SampleAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		sample.getSampleAttributes().add(sa1);

		SampleAttribute sa2 = new SampleAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		sample.getSampleAttributes().add(sa2);

		JaxbObject<Sample> instance = new JaxbObject<>();
		String result = instance.marshal(sample, Sample.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Sample sample2 = instance.unMarshal(Sample.class, in);
		assertEquals(sample, sample2);
	}

	/**
	 * <p>
	 * testStudyJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testStudyJaxb() throws Exception {
		Study study = new Study();
		study.setStudyId(Integer.MIN_VALUE);

		StudyAttribute sa1 = new StudyAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		study.getStudyAttributes().add(sa1);

		StudyAttribute sa2 = new StudyAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		study.getStudyAttributes().add(sa2);

		JaxbObject<Study> instance = new JaxbObject<>();
		String result = instance.marshal(study, Study.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Study st = instance.unMarshal(Study.class, in);
		assertEquals(study, st);
	}

	/**
	 * <p>
	 * testIUSJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testIUSJaxb() throws Exception {
		IUS ius = new IUS();
		ius.setIusId(Integer.MIN_VALUE);

		IUSAttribute sa1 = new IUSAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		ius.getIusAttributes().add(sa1);

		IUSAttribute sa2 = new IUSAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		ius.getIusAttributes().add(sa2);

		JaxbObject<IUS> instance = new JaxbObject<>();
		String result = instance.marshal(ius, IUS.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		IUS st = instance.unMarshal(IUS.class, in);
		assertEquals(ius, st);
	}

	/**
	 * <p>
	 * testLaneJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testLaneJaxb() throws Exception {
		Lane lane = new Lane();
		lane.setLaneId(Integer.MIN_VALUE);

		LaneAttribute sa1 = new LaneAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		lane.getLaneAttributes().add(sa1);

		LaneAttribute sa2 = new LaneAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		lane.getLaneAttributes().add(sa2);

		JaxbObject<Lane> instance = new JaxbObject<>();
		String result = instance.marshal(lane, Lane.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Lane st = instance.unMarshal(Lane.class, in);
		assertEquals(lane, st);
	}

	/**
	 * <p>
	 * testExperimentJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testExperimentJaxb() throws Exception {
		Experiment ius = new Experiment();
		ius.setExperimentId(Integer.MIN_VALUE);

		ExperimentAttribute sa1 = new ExperimentAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		ius.getExperimentAttributes().add(sa1);

		ExperimentAttribute sa2 = new ExperimentAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		ius.getExperimentAttributes().add(sa2);

		JaxbObject<Experiment> instance = new JaxbObject<>();
		String result = instance.marshal(ius, Experiment.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Experiment st = instance.unMarshal(Experiment.class, in);
		assertEquals(ius, st);
	}

	/**
	 * <p>
	 * testSequencerRunJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testSequencerRunJaxb() throws Exception {
		SequencerRun ius = new SequencerRun();
		ius.setSequencerRunId(Integer.MIN_VALUE);

		SequencerRunAttribute sa1 = new SequencerRunAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		ius.getSequencerRunAttributes().add(sa1);

		SequencerRunAttribute sa2 = new SequencerRunAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		ius.getSequencerRunAttributes().add(sa2);

		JaxbObject<SequencerRun> instance = new JaxbObject<>();
		String result = instance.marshal(ius, SequencerRun.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		SequencerRun st = instance.unMarshal(SequencerRun.class, in);
		assertEquals(ius, st);
	}

	/**
	 * <p>
	 * testProcessingJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testProcessingJaxb() throws Exception {
		Processing ius = new Processing();
		ius.setProcessingId(Integer.MIN_VALUE);

		ProcessingAttribute sa1 = new ProcessingAttribute();
		sa1.setTag("t1");
		sa1.setValue("v1");
		ius.getProcessingAttributes().add(sa1);

		ProcessingAttribute sa2 = new ProcessingAttribute();
		sa2.setTag("t2");
		sa2.setValue("t2");
		ius.getProcessingAttributes().add(sa2);

		JaxbObject<Processing> instance = new JaxbObject<>();
		String result = instance.marshal(ius, Processing.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Processing st = instance.unMarshal(Processing.class, in);
		assertEquals(ius, st);
	}

	/**
	 * <p>
	 * testNullProcessingAttribute.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testNullProcessingAttribute() throws Exception {
		Processing ius = new Processing();
		ius.setProcessingId(Integer.MIN_VALUE);

		JaxbObject<Processing> instance = new JaxbObject<>();
		String result = instance.marshal(ius, Processing.class);
		System.out.println(result);
		Reader in = new StringReader(result);
		Processing st = instance.unMarshal(Processing.class, in);
		assertEquals(ius, st);
	}

	/**
	 * Test marshalling and unmarshalling of WorkflowRun object.
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testWorkflowRunJaxb() throws Exception {
		Log.info("workflowRunJaxb");
		WorkflowRun wr = new WorkflowRun();
		wr.setWorkflowRunId(Integer.MIN_VALUE);

		Processing p = new Processing();
		p.setProcessingId(Integer.MAX_VALUE);

		IUS ius = new IUS();
		ius.setIusId(Integer.MAX_VALUE);

		Lane lane = new Lane();
		lane.setLaneId(Integer.MIN_VALUE);

		SortedSet<IUS> iuses = new TreeSet<>();
		iuses.add(ius);

		SortedSet<Lane> lanes = new TreeSet<>();
		lanes.add(lane);

		wr.setIus(iuses);
		wr.setLanes(lanes);
		p.setIUS(iuses);
		p.setLanes(lanes);

		JaxbObject<WorkflowRun> jaxb = new JaxbObject<>();
		String wrResult = jaxb.marshal(wr, WorkflowRun.class);

		JaxbObject<Processing> jaxbP = new JaxbObject<>();
		String pResult = jaxbP.marshal(p, Processing.class);

		WorkflowRun x = (WorkflowRun) XmlTools.unMarshal(jaxb, WorkflowRun.class, wrResult);
		Processing y = (Processing) XmlTools.unMarshal(jaxbP, Processing.class, pResult);
		assertEquals(wr.getWorkflowRunId(), x.getWorkflowRunId());
		assertEquals(p.getProcessingId(), y.getProcessingId());

		assertEquals(1, y.getLanes().size());
		assertEquals(1, x.getLanes().size());
		assertEquals(1, y.getIUS().size());
		assertEquals(1, x.getIus().size());
	}

	/**
	 * <p>
	 * testWorkflowRunListJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testWorkflowRunListJaxb() throws Exception {
		WorkflowRun r1 = new WorkflowRun();
		r1.setCommand("r1");
		WorkflowRun r2 = new WorkflowRun();
		r2.setCommand("r2");

		Sample s1 = new Sample();
		s1.setAlias("alias1");
		SortedSet<Sample> samples = new TreeSet<>();
		r1.setSamples(samples);

		ArrayList<WorkflowRun> list = new ArrayList<>();
		list.add(r1);
		list.add(r2);

		WorkflowRunList2 rvl = new WorkflowRunList2();
		rvl.setList(list);

		JaxbObject<WorkflowRunList2> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(rvl, WorkflowRunList2.class);
		if (!text.contains("r1") && !text.contains("r2") && !text.contains("alias1")) {
			Assert.fail("Marshalling WorkflowRunList failed");
		}

		Log.info(text);

		WorkflowRunList2 unmarshalledList = (WorkflowRunList2) XmlTools.unMarshal(jaxb, WorkflowRunList2.class, text);
		Assert.assertEquals(2, unmarshalledList.getList().size());
		for (WorkflowRun rv : unmarshalledList.getList()) {
			Log.info(rv.getCommand());
		}
	}

	/**
	 * <p>
	 * testWorkflowListJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testWorkflowListJaxb() throws Exception {
		Workflow r1 = new Workflow();
		r1.setCommand("r1");
		Workflow r2 = new Workflow();
		r2.setCommand("r2");

		// Sample s1 = new Sample();
		// s1.setAlias("alias1");
		// SortedSet<Sample> samples = new TreeSet<Sample>();
		// r1.setSamples(samples);
		ArrayList<Workflow> list = new ArrayList<>();
		list.add(r1);
		list.add(r2);

		WorkflowList rvl = new WorkflowList();
		rvl.setList(list);

		JaxbObject<WorkflowList> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(rvl, WorkflowList.class);
		if (!text.contains("r1") && !text.contains("r2") && !text.contains("alias1")) {
			Assert.fail("Marshalling WorkflowRunList failed");
		}

		WorkflowList unmarshalledList = (WorkflowList) XmlTools.unMarshal(jaxb,  WorkflowList.class, text);
		Assert.assertEquals(2, unmarshalledList.getList().size());
		for (Workflow rv : unmarshalledList.getList()) {
			Log.info(rv.getCommand());
		}
	}

	/**
	 * <p>
	 * testReturnValueJaxb.
	 * </p>
	 *
	 * @throws java.lang.Exception
	 *             if any.
	 */
	@Test
	public void testReturnValueJaxb() throws Exception {
		ReturnValue r1 = new ReturnValue();
		r1.setAlgorithm("r1");
		ReturnValue r2 = new ReturnValue();
		r2.setAlgorithm("r2");

		FileMetadata fm = new FileMetadata("filepath", "metatype");
		ArrayList<FileMetadata> fmlist = new ArrayList<>();
		fmlist.add(fm);
		r1.setFiles(fmlist);

		r1.getAttributes().put("key", "value1");
		r2.getAttributes().put("key", "value2");

		ArrayList<ReturnValue> list = new ArrayList<>();
		list.add(r1);
		list.add(r2);

		ReturnValueList rvl = new ReturnValueList();
		rvl.setList(list);

		JaxbObject<ReturnValueList> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(rvl, ReturnValueList.class);
		if (!text.contains("r1") && text.contains("r2")) {
			Assert.fail("Marshalling ReturnValueList failed - simple types");
		}

		if (!text.contains("key") && text.contains("value1") && text.contains("value2")) {
			Assert.fail("Marshalling ReturnValueList failed - attributes");
		}

		ReturnValueList unmarshalledList = (ReturnValueList) XmlTools.unMarshal(jaxb, ReturnValueList.class, text);
		Assert.assertEquals(2, unmarshalledList.getList().size());
		for (ReturnValue rv : unmarshalledList.getList()) {
			Assert.assertNotNull("Algorithm should not be null", rv.getAlgorithm());
			Assert.assertNotNull("Attribute key is null.", rv.getAttribute("key"));
		}

	}

	@Test
	public void testLimsKey() throws Exception {
		LimsKey lk = new LimsKey();
		lk.setLastModified(ZonedDateTime.now());
		JaxbObject<LimsKey> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(lk, LimsKey.class);
		LimsKey returnedLimsKey = (LimsKey) XmlTools.unMarshal(new JaxbObject<>(), LimsKey.class, text);
		Assert.assertNotNull(returnedLimsKey.getLastModified());
	}

	@Test
	public void testAnalysisProvenance_empty() throws JAXBException, SAXException {
		AnalysisProvenanceDtoList expectedAnalysisProvenanceList = new AnalysisProvenanceDtoList();
		expectedAnalysisProvenanceList.setAnalysisProvenanceDtos(new ArrayList<AnalysisProvenanceDto>());
		JaxbObject<AnalysisProvenanceDtoList> jaxb0 = new JaxbObject<>();
		String text0 = jaxb0.marshal(expectedAnalysisProvenanceList, AnalysisProvenanceDtoList.class);
		AnalysisProvenanceDtoList actualAnalysisProvenanceList = (AnalysisProvenanceDtoList) XmlTools
				.unMarshal(new JaxbObject<>(), AnalysisProvenanceDtoList.class, text0);

		assertEquals(0, actualAnalysisProvenanceList.getAnalysisProvenanceDtos().size());
		assertEquals(expectedAnalysisProvenanceList.getAnalysisProvenanceDtos().toString(),
				actualAnalysisProvenanceList.getAnalysisProvenanceDtos().toString());
		assertEquals(expectedAnalysisProvenanceList.getAnalysisProvenanceDtos(),
				actualAnalysisProvenanceList.getAnalysisProvenanceDtos());
	}

	@Test
	public void testAnalysisProvenance_normal() throws JAXBException, SAXException {
		Workflow w = new Workflow();
		w.setName("test_workflow");

		WorkflowRun wr = new WorkflowRun();
		wr.setStatus(WorkflowRunStatus.completed);
		wr.setInputFileAccessions(Collections.emptySet());

		Processing p = new Processing();
		p.setAlgorithm("pfo");
		p.setCreateTimestamp(new Date());

		File f = new File();
		f.setFilePath("/tmp/out");

		ZonedDateTime limsLastModified = ZonedDateTime.now(ZoneId.of("Z"));

		LimsKey lk1 = new LimsKey();
		lk1.setProvider("seqware");
		lk1.setId("1");
		lk1.setVersion("1");
		lk1.setLastModified(limsLastModified);

		IUS ius1 = new IUS();
		ius1.setSwAccession(1234);
		ius1.setLimsKey(lk1);

		LimsKey lk2 = new LimsKey();
		lk2.setProvider("seqware");
		lk2.setId("2");
		lk2.setVersion("1");
		lk2.setLastModified(limsLastModified);

		IUS ius2 = new IUS();
		ius2.setSwAccession(4567);
		ius2.setLimsKey(lk2);

		AnalysisProvenanceDtoBuilder ap = new AnalysisProvenanceDtoBuilder();
		ap.setFile(f);
		ap.setProcessing(p);
		ap.setWorkflow(w);
		ap.setWorkflowRun(wr);
		ap.addIuses(Sets.newHashSet(ius1, ius2));

		List<AnalysisProvenanceDto> apDtos = new ArrayList<>();
		apDtos.add(ap.build());

		AnalysisProvenanceDtoList expectedAnalysisProvenanceList = new AnalysisProvenanceDtoList();
		expectedAnalysisProvenanceList.setAnalysisProvenanceDtos(apDtos);

		JaxbObject<AnalysisProvenanceDtoList> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(expectedAnalysisProvenanceList, AnalysisProvenanceDtoList.class);

		AnalysisProvenanceDtoList actualAnalysisProvenanceList = (AnalysisProvenanceDtoList) XmlTools
				.unMarshal(new JaxbObject<>(), AnalysisProvenanceDtoList.class, text);
		for (AnalysisProvenanceDto dto : actualAnalysisProvenanceList.getAnalysisProvenanceDtos()) {
			for (IusLimsKeyDto ik : dto.getIusLimsKeys()) {
				assertEquals("seqware", ik.getLimsKey().getProvider());
			}
			assertEquals("test_workflow", dto.getWorkflowName());
			assertEquals("pfo", dto.getProcessingAlgorithm());
			assertEquals("/tmp/out", dto.getFilePath());
			assertEquals(Collections.EMPTY_SET, dto.getWorkflowRunInputFileIds());
		}

		assertEquals(1, actualAnalysisProvenanceList.getAnalysisProvenanceDtos().size());
		assertEquals(expectedAnalysisProvenanceList.getAnalysisProvenanceDtos(),
				actualAnalysisProvenanceList.getAnalysisProvenanceDtos());
	}

	@Test
	public void testSampleProvenance() throws JAXBException, SAXException {
		Study st = new Study();
		st.setCreateTimestamp(new Date());
		st.setTitle("test_study");

		Experiment e = new Experiment();
		e.setCreateTimestamp(new Date());
		e.setCenterName("test");

		SequencerRun sr = new SequencerRun();
		sr.setCreateTimestamp(new Date());
		sr.setName("test_sequencer_run");

		Lane l = new Lane();
		l.setCreateTimestamp(new Date());
		l.setLaneIndex(0);

		SampleAttribute sa = new SampleAttribute(0, null, "tissue_type", "R");
		SampleAttribute sa2 = new SampleAttribute(0, null, "tissue_type", "P");
		SampleAttribute sa3 = new SampleAttribute(0, null, "tissue_origin", "Ly");

		Sample s = new Sample();
		s.setCreateTimestamp(new Date());
		s.setSampleAttributes(Sets.newHashSet(sa, sa2, sa3));
		s.setName("test_sample");

		IUS i = new IUS();
		i.setCreateTimestamp(new Date());
		i.setSwAccession(1111111);

		SampleProvenanceDtoFromObjects sp = new SampleProvenanceDtoFromObjects();
		sp.setStudy(st);
		sp.setExperiment(e);
		sp.setSequencerRun(sr);
		sp.setLane(l);
		sp.setSample(s);
		sp.setIus(i);

		List<SampleProvenanceDto> sps = new ArrayList<>();
		sps.add(sp);

		SampleProvenanceDtoList expectedSampleProvenanceList = new SampleProvenanceDtoList();
		expectedSampleProvenanceList.setSampleProvenanceDtos(sps);

		JaxbObject<SampleProvenanceDtoList> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(expectedSampleProvenanceList, SampleProvenanceDtoList.class);

		SampleProvenanceDtoList actualSampleProvenanceSet = (SampleProvenanceDtoList) XmlTools
				.unMarshal(new JaxbObject<>(), SampleProvenanceDtoList.class, text);

		assertNotNull(sp.getLastModified());
		assertEquals(expectedSampleProvenanceList.getSampleProvenanceDtos(),
				actualSampleProvenanceSet.getSampleProvenanceDtos());
		assertEquals(sp.getLastModified().toInstant(),
				actualSampleProvenanceSet.getSampleProvenanceDtos().get(0).getLastModified().toInstant());
		assertEquals("test_study",
				Iterables.getFirst(actualSampleProvenanceSet.getSampleProvenanceDtos(), new SampleProvenanceDto())
						.getStudyTitle());
		assertEquals(Sets.newHashSet("R", "P"), sps.get(0).getSampleAttributes().get("tissue_type"));
		assertEquals(Sets.newHashSet("R", "P"),
				actualSampleProvenanceSet.getSampleProvenanceDtos().get(0).getSampleAttributes().get("tissue_type"));
		assertEquals(Sets.newHashSet("Ly"),
				actualSampleProvenanceSet.getSampleProvenanceDtos().get(0).getSampleAttributes().get("tissue_origin"));
		assertEquals(sp.getLastModified().toInstant(),
				actualSampleProvenanceSet.getSampleProvenanceDtos().get(0).getLastModified().toInstant());
	}

	@Test
	public void testLaneProvenance() throws JAXBException, SAXException {
		Date createTstmp = new Date(2016, 01, 01);
		Date updateTstmp = new Date(2016, 02, 01);
		String sequencerRunName = "test_sequencer_run";
		Integer laneIndex = 0;

		SequencerRun sr = new SequencerRun();
		sr.setCreateTimestamp(createTstmp);
		sr.setName(sequencerRunName);

		Lane l = new Lane();
		l.setSwAccession(1);
		l.setCreateTimestamp(createTstmp);
		l.setUpdateTimestamp(updateTstmp);
		l.setLaneIndex(laneIndex);

		LaneProvenanceDtoFromObjects lp = new LaneProvenanceDtoFromObjects();
		lp.setSequencerRun(sr);
		lp.setLane(l);

		List<LaneProvenanceDto> lps = new ArrayList<>();
		lps.add(lp);

		LaneProvenanceDtoList expectedLaneProvenanceDtoList = new LaneProvenanceDtoList();
		expectedLaneProvenanceDtoList.setLaneProvenanceDtos(lps);

		JaxbObject<LaneProvenanceDtoList> jaxb = new JaxbObject<>();
		String text = jaxb.marshal(expectedLaneProvenanceDtoList, LaneProvenanceDtoList.class);

		LaneProvenanceDtoList actualLaneProvenanceDtoList = (LaneProvenanceDtoList) XmlTools
				.unMarshal(new JaxbObject<>(), LaneProvenanceDtoList.class, text);

		assertEquals(updateTstmp.toInstant(), lp.getLastModified().toInstant());
		assertEquals("48f2296813664951625e0b87d1f2e7b0b5912ad93f6fcdff8eff2576c1eed0b4", lp.getVersion());
		assertEquals("1", lp.getLaneProvenanceId());
		assertEquals(expectedLaneProvenanceDtoList.getLaneProvenanceDtos(),
				actualLaneProvenanceDtoList.getLaneProvenanceDtos());

		LaneProvenanceDto actualLaneProvenanceDto = Iterables
				.getOnlyElement(actualLaneProvenanceDtoList.getLaneProvenanceDtos());
		assertEquals(Integer.toString(laneIndex + 1), actualLaneProvenanceDto.getLaneNumber());
		assertEquals(updateTstmp.toInstant(), actualLaneProvenanceDto.getLastModified().toInstant());
	}
}
