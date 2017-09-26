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
package net.sourceforge.seqware.webservice.resources;

import java.io.IOException;
import net.sourceforge.seqware.common.model.FirstTierModel;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author mlaszloffy
 * @param <T extends FirstTierModel>
 */
public class SeqwareResourceClient<T extends FirstTierModel> {

    private final Class<T> type;
    private final String resourcePath;

    public SeqwareResourceClient(Class<T> type, String resourcePath) {
        this.type = type;
        this.resourcePath = resourcePath;
    }

    public T getFromSwid(Integer swid) throws IOException, SAXException {
        T returnedObj = null;
        ClientResource cr = null;
        Representation rep = null;
        try {
            cr = ClientResourceInstance.getChild(resourcePath + "/" + swid);
            rep = cr.get();
            returnedObj = (T) XmlTools.unMarshal(new JaxbObject<T>(), type, rep.getText());
        } finally {
            if (rep != null) {
                rep.exhaust();
                rep.release();
            }
            if (cr != null) {
                cr.release();
            }
        }
        return returnedObj;
    }

    public T getFromId(Integer id) throws IOException, SAXException {
        T returnedObj = null;
        ClientResource cr = null;
        Representation rep = null;
        try {
            cr = ClientResourceInstance.getChild(resourcePath + "?id=" + id);
            rep = cr.get();
            returnedObj = (T) XmlTools.unMarshal(new JaxbObject<T>(), type, rep.getText());
        } finally {
            if (rep != null) {
                rep.exhaust();
                rep.release();
            }
            if (cr != null) {
                cr.release();
            }
        }
        return returnedObj;
    }

    public T post(T obj) throws IOException, SAXException {
        T returnedObj = null;
        ClientResource cr = null;
        Representation rep = null;
        try {
            Document doc = XmlTools.marshalToDocument(new JaxbObject<T>(), obj, type);
            cr = ClientResourceInstance.getChild(resourcePath);
            rep = cr.post(XmlTools.getRepresentation(doc));
            returnedObj = (T) XmlTools.unMarshal(new JaxbObject<T>(), type, rep.getText());
        } finally {
            if (rep != null) {
                rep.exhaust();
                rep.release();
            }
            if (cr != null) {
                cr.release();
            }
        }
        return returnedObj;
    }

    public T put(T obj) throws IOException, SAXException {
        T returnedObj = null;
        ClientResource cr = null;
        Representation rep = null;
        try {
            Integer swid = obj.getSwAccession();
            Document doc = XmlTools.marshalToDocument(new JaxbObject<T>(), obj, type);
            cr = ClientResourceInstance.getChild(resourcePath + "/" + swid);
            rep = cr.put(XmlTools.getRepresentation(doc));
            returnedObj = (T) XmlTools.unMarshal(new JaxbObject<T>(), type, rep.getText());
        } finally {
            if (rep != null) {
                rep.exhaust();
                rep.release();
            }
            if (cr != null) {
                cr.release();
            }
        }
        return returnedObj;
    }

    public void delete(T obj) throws IOException {
        ClientResource cr = null;
        Representation rep = null;
        try {
            Integer swid = obj.getSwAccession();
            cr = ClientResourceInstance.getChild(resourcePath + "/" + swid);
            rep = cr.delete();
        } finally {
            if (rep != null) {
                rep.exhaust();
                rep.release();
            }
            if (cr != null) {
                cr.release();
            }
        }
    }

}
