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

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LaneProvenanceDto;
import net.sourceforge.seqware.common.dto.LimsKeyDto;
import net.sourceforge.seqware.common.dto.SampleProvenanceDto;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.model.adapters.IntegerSet;
import net.sourceforge.seqware.common.model.adapters.MapOfSetAdapter;
import net.sourceforge.seqware.common.model.lists.AnalysisProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.ExperimentLibraryDesignList;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.model.lists.ExperimentSpotDesignList;
import net.sourceforge.seqware.common.model.lists.ExperimentSpotDesignReadSpecList;
import net.sourceforge.seqware.common.model.lists.FileList;
import net.sourceforge.seqware.common.model.lists.IUSList;
import net.sourceforge.seqware.common.model.lists.IntegerList;
import net.sourceforge.seqware.common.model.lists.LaneList;
import net.sourceforge.seqware.common.model.lists.LaneProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.LibrarySelectionList;
import net.sourceforge.seqware.common.model.lists.LibrarySourceList;
import net.sourceforge.seqware.common.model.lists.LibraryStrategyList;
import net.sourceforge.seqware.common.model.lists.LimsKeyList;
import net.sourceforge.seqware.common.model.lists.OrganismList;
import net.sourceforge.seqware.common.model.lists.PlatformList;
import net.sourceforge.seqware.common.model.lists.ProcessingList;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.model.lists.SampleList;
import net.sourceforge.seqware.common.model.lists.SampleProvenanceDtoList;
import net.sourceforge.seqware.common.model.lists.SequencerRunList;
import net.sourceforge.seqware.common.model.lists.StudyList;
import net.sourceforge.seqware.common.model.lists.StudyTypeList;
import net.sourceforge.seqware.common.model.lists.WorkflowList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.model.types.MapOfSetEntryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for converting objects into JAXB XML.
 * 
 * @author mtaschuk
 * @version $Id: $Id
 * @param <T>
 */
public class JaxbObject<T> {
private static final Logger LOGGER = LoggerFactory.getLogger(JaxbObject.class);

	private static JAXBContext context = null;

	/**
	 * JAXB has a memory leak when it comes to making new JAXBContext instances, so
	 * one JAXBContext is created for the entire lifetime of the program and
	 * initialized with all of the classes that we marshal and unmarshal at the
	 * moment, as per the instructions at *
	 * http://whileonefork.blogspot.com/2010/09/leaking-of-jaxb.html and *
	 * http://jaxb.java.net/guide/Performance_and_thread_safety.html . In order to
	 * marshall or unmarshall new objects, they must be added to the context
	 * creation in this constructor.
	 */
	public JaxbObject() {
		try {
			if (context == null) {
				context = JAXBContext.newInstance(AnalysisProvenanceDto.class, //
						AnalysisProvenanceDtoList.class, //
						Experiment.class, //
						ExperimentAttribute.class, //
						ExperimentLibraryDesign.class, //
						ExperimentSpotDesign.class, //
						ExperimentSpotDesignReadSpec.class, //
						File.class, //
						FileType.class, //
						IUS.class, //
						IUSAttribute.class, //
						Lane.class, //
						LaneAttribute.class, //
						LaneProvenanceDto.class, //
						LaneProvenanceDtoList.class, //
						LibrarySelection.class, //
						LibrarySource.class, //
						LibraryStrategy.class, //
						IusLimsKeyDto.class, //
						LimsKey.class, //
						LimsKeyDto.class, //
						LimsKeyList.class, //
						MapOfSetAdapter.class, //
						MapOfSetEntryType.class, //
						Organism.class, //
						Platform.class, //
						Processing.class, //
						ProcessingAttribute.class, //
						Registration.class, //
						Sample.class, //
						SampleAttribute.class, //
						SampleProvenanceDto.class, //
						SampleProvenanceDtoList.class, //
						SequencerRun.class, //
						Study.class, //
						StudyAttribute.class, //
						StudyType.class, //
						Workflow.class, //
						WorkflowRun.class, //
						WorkflowRunParam.class, //
						ExperimentList.class, //
						ExperimentLibraryDesignList.class, //
						ExperimentSpotDesignList.class, //
						ExperimentSpotDesignReadSpecList.class, //
						FileList.class, //
						IUSList.class, //
						LaneList.class, //
						LibrarySelectionList.class, //
						LibrarySourceList.class, //
						LibraryStrategyList.class, //
						OrganismList.class, //
						PlatformList.class, //
						ProcessingList.class, //
						ReturnValueList.class, //
						SampleList.class, //
						SequencerRunList.class, //
						StudyList.class, //
						StudyTypeList.class, //
						WorkflowList.class, //
						WorkflowRunList.class, //
						WorkflowRunList2.class, //
						ArrayList.class, //
						IntegerList.class, //
						IntegerSet.class, //
						WorkflowParam.class);
			}
		} catch (JAXBException e) {
            LOGGER.error("JaxbObject constructor exception:", e);
		}
	}

	/**
	 * Turn an object into XML using JAXB and provide the result in a StreamResult.
	 * 
	 * @param t
	 *            The object to XMLize.
	 * @return the XML. The StreamResult was created with a StringWriter, which can
	 *         be used to retrieve the XML.
	 * @throws javax.xml.bind.JAXBException
	 *             if any.
	 */
	public Document marshalToDocument(T t, Class<T> type) throws JAXBException {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();

			T object = t;

			// get the XML
			// JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller m = context.createMarshaller();
			m.marshal(new JAXBElement<T>(new QName(object.getClass().getSimpleName()), type, object), doc);

			// try {
			// XmlTools.getDocument(output);
			// } catch (Exception ex) {
			// Log.info("Exception while marshaling: " + ex.getMessage() + ". Trying
			// again.");
			// output = marshal(t);
			// }

		} catch (ParserConfigurationException ex) {
			LOGGER.error("JaxbObject.marshalToDocument:",ex.toString(), ex);
		} catch (JAXBException jbe) {
			LOGGER.error("JaxbObject.marshalToDocument exception:",jbe);
			throw jbe;
		}
		return doc;
	}

	/**
	 * Turn an object into XML using JAXB and provide the result in a StreamResult.
	 * 
	 * @param t
	 *            The object to XMLize.
	 * @return the XML. The StreamResult was created with a StringWriter, which can
	 *         be used to retrieve the XML.
	 * @throws javax.xml.bind.JAXBException
	 *             if any.
	 */
	public String marshal(T t, Class<T> type) throws JAXBException {
		String output = null;
		try {
			StreamResult result = new StreamResult(new StringWriter());
			T object = t;

			// get the XML
			Marshaller m = context.createMarshaller();
			m.marshal(new JAXBElement<T>(new QName(object.getClass().getSimpleName()), type, object), result);

			// convert to String
			StringWriter writer = (StringWriter) result.getWriter();
			StringBuffer buffer = writer.getBuffer();
			output = buffer.toString();
		} catch (JAXBException jbe) {
                        LOGGER.error("JaxbObject.unMarshal exception:",jbe);
			throw jbe;
		}
		return output;
	}

	/**
	 * Turn an XML stream into an object, if possible.
	 * 
	 * @param expectedType
	 *            a T object.
	 * @param reader
	 *            a {@link java.io.Reader} object.
	 * @return a T object.
	 * @throws javax.xml.bind.JAXBException
	 *             if any.
	 */
	public T unMarshal(Class<T> expectedType, Reader reader) throws JAXBException {
		T object = null;
		try {
			Unmarshaller m = context.createUnmarshaller();
			JAXBElement<T> o = m.unmarshal(new StreamSource(reader), expectedType);
			object = o.getValue();
		} catch (JAXBException jbe) {
                        LOGGER.error("JaxbObject.unMarshal exception:",jbe);
			throw jbe;
		}
		return object;
	}

	/**
	 * Turn an XML stream into an object, if possible.
	 * 
	 * @param expectedType
	 *            a T object.
	 * @param d
	 *            a {@link org.w3c.dom.Document} object.
	 * @return a T object.
	 * @throws javax.xml.bind.JAXBException
	 *             if any.
	 */
	public T unMarshal(Document d, Class<T> expectedType) throws JAXBException {
		T object = null;
		try {
			// JAXBContext context = JAXBContext.newInstance(expectedType.getClass());
			Unmarshaller m = context.createUnmarshaller();
			JAXBElement<T> o = m.unmarshal(d, expectedType);
			object = o.getValue();
		} catch (JAXBException jbe) {
			LOGGER.error("JaxbObject.unMarshal exception2:",jbe);
			throw jbe;
		}
		return object;
	}
}
