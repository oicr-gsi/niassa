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
package net.sourceforge.seqware.webservice.resources.tables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.xml.sax.SAXException;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import net.sourceforge.seqware.webservice.resources.SeqwareResourceClient;

/**
 *
 * @author mtaschuk
 */
public class IusResourceTest extends DatabaseResourceTest {

    private final SeqwareResourceClient<IUS> iusClient;
    private final SeqwareResourceClient<LimsKey> limsKeyClient;

    public IusResourceTest() {
        super("/ius");

        iusClient = new SeqwareResourceClient<>(IUS.class, "/ius");
        limsKeyClient = new SeqwareResourceClient<>(LimsKey.class, "/limskey");
    }

    @Override
    public void testPost() {
        System.out.println(getRelativeURI() + " POST");
        IUS ius = new IUS();
        ius.setCreateTimestamp(new Date());
        ius.setSwAccession(6213);
        ius.setUpdateTimestamp(new Date());
        Sample s = new Sample();
        Lane l = new Lane();
        l.setLaneId(1);
        s.setSampleId(10);
        ius.setSample(s);
        ius.setLane(l);

        try {
            ius = iusClient.post(ius);
        } catch (IOException | SAXException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testPostWithLimsKey() {
        LimsKey lk = new LimsKey();
        lk.setId("1");
        lk.setLastModified(ZonedDateTime.now());
        lk.setProvider("provider");
        lk.setVersion("1");
        try {
            lk = limsKeyClient.post(lk);
        } catch (IOException | SAXException ex) {
            fail(ex.getMessage());
        }

        IUS ius = new IUS();
        ius.setLimsKey(lk);
        try {
            ius = iusClient.post(ius);
        } catch (IOException | SAXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(ius.getCreateTimestamp());
        assertNotNull(ius.getUpdateTimestamp());

        LimsKey returnedLimsKey = null;
        try {
            Representation rep = ClientResourceInstance.getChild("/ius/" + ius.getSwAccession() + "/limskey").get();
            returnedLimsKey = (LimsKey) XmlTools.unMarshal(new JaxbObject<LimsKey>(), LimsKey.class, rep.getText());
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertEquals(lk, returnedLimsKey);
    }

    @Test
    public void testPostWithMissingLimsKey() {
        LimsKey lk = new LimsKey();
        IUS ius = new IUS();
        ius.setLimsKey(lk); //set LimsKey that does not exist
        try {
            iusClient.post(ius);
            fail("Creating IUS with LimsKey that does not exist was successful");
        } catch (Exception ex) {
        }
    }

    @Override
    public void testDelete() {

        Sample s = new Sample();
        s.setSampleId(10);

        Lane l = new Lane();
        l.setLaneId(1);

        IUS ius = new IUS();
        ius.setSample(s);
        ius.setLane(l);

        try {
            iusClient.delete(ius);
            fail("Deletion of missing IUS should have failed");
        } catch (Exception ex) {
        }

        try {
            ius = iusClient.post(ius);
        } catch (IOException | SAXException ex) {
            fail("Creating IUS failed: " + ex.getMessage());
        }

        try {
            iusClient.delete(ius);
            fail("Deletion of IUS non-orphaned IUS was successful");
        } catch (Exception ex) {
        }

        try {
            ius = iusClient.getFromSwid(ius.getSwAccession());
        } catch (IOException | SAXException ex) {
            fail("Getting IUS failed: " + ex.getMessage());
        }

        //orphan IUS and update
        ius.setSample(new Sample());
        ius.setLane(new Lane());
        try {
            ius = iusClient.put(ius);
        } catch (IOException | SAXException ex) {
            fail("Update of IUS failed: " + ex.getMessage());
        }

        try {
            iusClient.delete(ius);
        } catch (IOException ex) {
            fail("Deletion of orphaned IUS failed: " + ex.getMessage());
        }
    }

}
