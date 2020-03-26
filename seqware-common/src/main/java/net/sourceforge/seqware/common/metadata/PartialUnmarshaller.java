package net.sourceforge.seqware.common.metadata;

import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


final class PartialUnmarshaller implements Iterator<AnalysisProvenanceDto>, AutoCloseable {
    private static final Unmarshaller UNMARSHALLER;

    static {
        try {
            UNMARSHALLER = JAXBContext.newInstance(AnalysisProvenanceDto.class).createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    XMLStreamReader reader;

    public PartialUnmarshaller(Reader stream) throws XMLStreamException, FactoryConfigurationError, JAXBException {
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);

        /* ignore headers */
        skipElements(XMLStreamConstants.START_DOCUMENT, XMLStreamConstants.DTD);
        reader.nextTag(); // Root tag
        /* if there's no tag, ignore root element's end */
        skipElements(XMLStreamConstants.END_ELEMENT);
    }

    public AnalysisProvenanceDto next() {
        if (!hasNext())
            throw new NoSuchElementException();
        try {
            final AnalysisProvenanceDto value = UNMARSHALLER.unmarshal(reader, AnalysisProvenanceDto.class).getValue();

            skipElements(XMLStreamConstants.CHARACTERS, XMLStreamConstants.END_ELEMENT);
            return value;
        } catch (XMLStreamException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    private void skipElements(int... elements) throws XMLStreamException {
        int eventType = reader.getEventType();

        final List<Integer> types = Arrays.stream(elements).boxed().collect(Collectors.toList());
        while (types.contains(eventType))
            eventType = reader.next();
    }
}
