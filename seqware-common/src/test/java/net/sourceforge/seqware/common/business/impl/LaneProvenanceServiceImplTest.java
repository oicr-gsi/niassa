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

import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.LaneProvenanceService;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author mlaszloffy
 */
public class LaneProvenanceServiceImplTest extends AbstractTestCase {

    @Autowired
    @Qualifier("laneProvenanceService")
    LaneProvenanceService lps;

    @Test
    public void defaultTestData() {
        assertEquals(30, lps.list().size());
    }

}
