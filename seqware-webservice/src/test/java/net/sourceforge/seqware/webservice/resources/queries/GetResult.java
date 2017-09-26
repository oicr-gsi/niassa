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

import org.joda.time.DateTime;
import org.restlet.data.Status;

/**
 *
 * @author mlaszloffy
 */
public class GetResult<T> {

    private final T data;
    private final Status status;
    private final DateTime requestDate;
    private final DateTime responseDate;
    private final DateTime dataLastModificationDate;

    public GetResult(T data, Status status, DateTime requestDate, DateTime responseDate, DateTime modificationDate) {
        this.data = data;
        this.status = status;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
        this.dataLastModificationDate = modificationDate;
    }

    public T getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public DateTime getResponseDate() {
        return responseDate;
    }

    public DateTime getDataLastModificationDate() {
        return dataLastModificationDate;
    }

}
