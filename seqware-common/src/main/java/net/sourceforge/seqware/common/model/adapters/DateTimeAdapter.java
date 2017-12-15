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
package net.sourceforge.seqware.common.model.adapters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author mlaszloffy
 */
public class DateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()//
			.appendPattern("yyyy-MM-dd")//
			.appendOptional(new DateTimeFormatterBuilder()//
					.appendLiteral('T')//
					.appendPattern("HH:mm:ss").appendOptional(new DateTimeFormatterBuilder()//
							.appendPattern(".SSS")//
							.toFormatter())
					.appendPattern("X")//
					.toFormatter())//
			.toFormatter();

	@Override
	public ZonedDateTime unmarshal(String date) throws Exception {
		return date == null ? null : ZonedDateTime.parse(date, FMT);
	}

	@Override
	public String marshal(ZonedDateTime date) throws Exception {
		return date == null ? "" : FORMATTER.format(date);
	}

}
