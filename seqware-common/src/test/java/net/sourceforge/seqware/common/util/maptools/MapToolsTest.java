/*
 *  Copyright (C) 2011 SeqWare
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.seqware.common.util.maptools;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * MD5GeneratorTest class.
 * </p>
 * 
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.6.2
 */
public class MapToolsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapToolsTest.class);

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

    @Test
    public void testExpandVariables() throws Exception {
        String path = getClass().getResource("vars.ini").getPath();
        Map<String, String> raw = new HashMap<>();
        MapTools.ini2Map(path, raw);
        Map<String, String> provided = MapTools.providedMap("/u/seqware/provisioned-bundles", "1.0");
        Map<String, String> exp = MapTools.expandVariables(raw, provided);

        assertEquals(raw.size(), exp.size());

        assertEquals("b", exp.get("foo"));
        assertEquals("d", exp.get("bar"));
        assertEquals("abcde", exp.get("test-multi"));

        assertEquals("1.0", exp.get("test-bundle-seqware-version"));
        assertEquals("/u/seqware/provisioned-bundles", exp.get("test-bundle-dir"));
        assertEquals("/u/seqware/provisioned-bundles", exp.get("test-legacy-bundle-dir"));

        Integer.parseInt(exp.get("test-random"));
        Integer.parseInt(exp.get("test-legacy-random"));

        DatatypeConverter.parseDate(exp.get("test-date"));
        DatatypeConverter.parseDate(exp.get("test-legacy-date"));

        DatatypeConverter.parseDateTime(exp.get("test-datetime"));

        Long.parseLong(exp.get("test-timestamp"));

        UUID.fromString(exp.get("test-uuid"));
    }

}
