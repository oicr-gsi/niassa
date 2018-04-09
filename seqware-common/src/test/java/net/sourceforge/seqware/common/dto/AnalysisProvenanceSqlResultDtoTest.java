/*
 * Copyright (C) 2018 SeqWare
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
package net.sourceforge.seqware.common.dto;

import java.time.ZonedDateTime;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceSqlResultDtoTest {

    public AnalysisProvenanceSqlResultDtoTest() {
    }

    @Test
    public void testOkayDateTimeFormats() {
        assertEquals("2012-12-19T16:28:15Z",
                ZonedDateTime.parse("2012-12-19 16:28:15+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.100Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.1+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.120Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.12+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.123+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123400Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.1234+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123450Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.12345+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123456Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.123456+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123456700Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.1234567+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123456780Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.12345678+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
        assertEquals("2012-12-19T16:28:15.123456789Z",
                ZonedDateTime.parse("2012-12-19 16:28:15.123456789+00", AnalysisProvenanceSqlResultDto.FMT).toInstant().toString());
    }

}
