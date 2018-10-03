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
package net.sourceforge.seqware.webservice.resources.queries;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;

import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;

/**
 *
 * @author mlaszloffy
 */
public class Get<T> implements Callable<GetResult<T>> {

	String url;
	Class<T> expectedType;

	public Get(String url, Class<T> expectedType) {
		this.url = url;
		this.expectedType = expectedType;
	}

	public Get(String url) {
		this(url, null);
	}

	@Override
	public GetResult<T> call() throws Exception {
		GetResult<T> result = null;
		try {
			ZonedDateTime requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);

			ClientResource c = ClientResourceInstance.getChild(url);
			Representation rep = c.get();

			ZonedDateTime responseDate = null;
			if (c.getResponse().getDate() != null) {
				responseDate = ZonedDateTime.ofInstant(c.getResponse().getDate().toInstant(), ZoneId.of("Z"));
			}

			ZonedDateTime lastModified = null;
			if (rep.getModificationDate() != null) {
				lastModified = ZonedDateTime.ofInstant(rep.getModificationDate().toInstant(), ZoneId.of("Z"));
			}
			try {
				T data = null;
				if (expectedType != null) {
					data = (T) XmlTools.unMarshal(new JaxbObject<>(), expectedType, rep.getText());
				}

				result = new GetResult<>(data, c.getResponse().getStatus(), requestDate, responseDate, lastModified);
			} finally {
				rep.exhaust();
				rep.release();
				if (c.getResponseEntity() != null) {
					c.getResponseEntity().release();
				}
				c.release();
			}
		} catch (ResourceException | IOException | SAXException e) {
			Assert.fail(e.getMessage());
		}
		return result;
	}

}
