/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.webservice.resources.filters;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.lists.SampleList;

import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mtaschuk
 */
public class SampleIDFilterTest extends AbstractResourceTest {
    private final Logger logger = LoggerFactory.getLogger(SampleIDFilterTest.class);

    public SampleIDFilterTest() {
        super("");
    }

    @Override
    public void testGet() {
    }

    @Test
    public void testGetFromExperiment() throws Exception {
        List<Sample> samples = getSamples("/experiments/6157/samples");
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetFromParentSample() throws Exception {
        List<Sample> samples = getSamples("/samples/1940/children");
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetFromChildSample() throws Exception {
        List<Sample> samples = getSamples("/samples/1944/parents");
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetFromExperimentNotFound() throws Exception {
        try {
            getSamples("/experiments/61570/samples");
            Assert.fail("This experiment should not exist, should not have samples, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromParentSampleNotFound() throws Exception {
        try {
            getSamples("/samples/19400/children");
            Assert.fail("This sample should not exist, should not have children, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromChildSampleNotFound() throws Exception {
        try {
            getSamples("/samples/19400/parents");
            Assert.fail("This sample should not exist, should not have parents, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromParentSampleEmpty() throws Exception {
        try {
            getSamples("/samples/1944/children");
            Assert.fail("This sample should not have children and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromChildSampleEmpty() throws Exception {
        try {
            List<Sample> samples = getSamples("/samples/1940/parents");
            Assert.fail("This sample should not have parents and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    // 1796 is leaf
    // 1792 is root
    private List<Sample> getSamples(String relativeURI) throws ResourceException, Exception {
        resource = ClientResourceInstance.getChild(relativeURI);
        logger.info(getRelativeURI() + " GET");
        SampleList parent = new SampleList();
        JaxbObject<SampleList> jaxb = new JaxbObject<>();
        try {
            Representation rep = resource.get();
            String text = rep.getText();
            parent = (SampleList) XmlTools.unMarshal(jaxb, SampleList.class, text);
            rep.exhaust();
            rep.release();
        } catch (ResourceException e) {
            logger.error("SampleIDFilterTest.getSamples resource exception:",e);
            throw e;
        } catch (Exception e) {
            logger.error("SampleIDFilterTest.getSamples exception:",e);
            throw e;
        }
        return parent.getList();
    }

    @Override
    public void testPut() {
    }

    @Override
    public void testPost() {
    }

    @Override
    public void testDelete() {
    }
}
