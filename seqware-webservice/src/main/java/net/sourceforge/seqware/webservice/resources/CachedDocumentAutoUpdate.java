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

import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.joda.time.DateTime;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public abstract class CachedDocumentAutoUpdate {

    private volatile Document cachedDocument = null;
    private volatile DateTime lastModified = null;
    private final ReentrantReadWriteLock cachedDataLock = new ReentrantReadWriteLock();

    /**
     * Process the request and modify the response entity with either cached or new data.
     * 
     * The auto update implementation of CachedDocument will either get updated data or if the updated data is currently being
     * calculated, it will use that data once it is completed calculating.
     * 
     * The response provided as an argument is modified as follows:
     * - the response representation entity is set to the updated (or cached document if a subsequent request)
     * - the response representation modification date is set to the time that the document was produced
     * 
     * @param response the response that is to be modified
     */
    public void processRequest(Response response) {
        if (cachedDataLock.writeLock().tryLock()) {
            //no other writer or readers accessing the cached document, update the document

            try {
                updateCachedData();
                Representation r = XmlTools.getRepresentation(cachedDocument);
                r.setModificationDate(lastModified.toDate());
                response.setEntity(r);
            } finally {
                cachedDataLock.writeLock().unlock();
            }
        } else {
            //there are other reader theads or there is _one_ writer thread updating the cached document

            //if there are readers: obtaining the read lock will be successful and the cached document can be read
            //if there is a writer: obtaining the read lock will be blocked until the write lock has been released
            cachedDataLock.readLock().lock();

            try {
                Representation r = XmlTools.getRepresentation(cachedDocument);
                r.setModificationDate(lastModified.toDate());
                response.setEntity(r);
            } finally {
                cachedDataLock.readLock().unlock();
            }
        }
    }

    private void updateCachedData() {
        //used for the http header "last-modified"
        lastModified = DateTime.now();

        cachedDocument = calculateDocument();
    }

    public abstract Document calculateDocument();

}
