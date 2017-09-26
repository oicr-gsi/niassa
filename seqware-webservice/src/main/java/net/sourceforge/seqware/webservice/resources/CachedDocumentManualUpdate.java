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

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.joda.time.DateTime;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 *
 * @author mlaszloffy
 */
public abstract class CachedDocumentManualUpdate {

    public enum Operation {
        GET, INVALIDATE, REFRESH
    }
    private volatile Document cachedDocument = null;
    private volatile DateTime lastModified = null;
    private volatile Boolean cacheIsValid = false;
    private final ReentrantReadWriteLock CACHED_DATA_LOCK = new ReentrantReadWriteLock();
    private final ReentrantLock REFRESH_LOCK = new ReentrantLock();

    /**
     * Process the request and modify the response entity with cached data.
     * 
     * The manual update implementation of CachedDocument does the following:
     * 
     * GET operation:
     * - if cached document has not be produced or the cache has been invalidated - update document (blocking operation) and return document
     * - if the cached document is valid, - return the cached document
     * 
     * REFRESH operation:
     * - update the cached document (blocking operation if only refresh operation, non-blocking for subsequent refresh operations)
     * 
     * INVALIDATE operation:
     * - immediately invalidate the cached document (non-blocking operation)
     * 
     * 
     * The response provided as an argument is modified as follows:
     * GET operation:
     * - the response representation entity is set to the cached document
     * - the response representation modification date is set to the time that the document was produced
     * 
     * REFRESH operation:
     * - FIRST refresh request: the response status is set to SUCCESS(200)
     * - if other refresh request is processing (subsequent refresh requests): the response status is set to ACCEPTED(202)
     * 
     * INVALIDATE operation:
     * - the response status is set to SUCCESS(200)
     *
     * @param response the response that is to be modified
     * @param op       the cache operation that is to be performed
     */
    public void processRequest(Response response, Operation op) {
        if (Operation.GET.equals(op)) {
            //reading thread - acquire "cached document" read lock
            CACHED_DATA_LOCK.readLock().lock();

            if (!cacheIsValid) {
                //"cached document" is no longer valid, release read lock and try to acquire the "refresh" lock
                CACHED_DATA_LOCK.readLock().unlock();
                if (REFRESH_LOCK.tryLock()) {
                    //wait for readers to complete then acquire the write lock for "cached document"
                    CACHED_DATA_LOCK.writeLock().lock();
                    try {
                        //verify that no other threads updated the precondition
                        if (!cacheIsValid) {
                            updateCachedData();
                        }

                        //downgrade "cached document" write lock to a read lock before releasing the write lock
                        CACHED_DATA_LOCK.readLock().lock();
                    } finally {
                        //done refreshing, release refresh and cache locks
                        REFRESH_LOCK.unlock();
                        CACHED_DATA_LOCK.writeLock().unlock();
                    }
                } else {
                    //was not able to acquire "refresh" lock, another thread is already refreshing
                    CACHED_DATA_LOCK.readLock().lock();
                }
            }

            try {
                //okay if cache is invalid, "cached document" only needs to be valid at invocation
                Representation r = XmlTools.getRepresentation(cachedDocument);
                r.setModificationDate(lastModified.toDate());
                response.setEntity(r);
            } finally {
                CACHED_DATA_LOCK.readLock().unlock();
            }
        } else if (Operation.REFRESH.equals(op)) {
            if (REFRESH_LOCK.tryLock()) {
                //"refresh" requested and no other refresh threads executing

                //wait for readers to complete then acquire the write lock for "cached document"
                CACHED_DATA_LOCK.writeLock().lock();
                try {
                    updateCachedData();
                } finally {
                    //done refreshing, release refresh and cache locks
                    REFRESH_LOCK.unlock();
                    CACHED_DATA_LOCK.writeLock().unlock();
                }
                response.setStatus(Status.SUCCESS_OK);
            } else {
                //was not able to acquire "refresh" lock, another thread is already refreshing
                response.setStatus(Status.SUCCESS_ACCEPTED);
            }
        } else if (Operation.INVALIDATE.equals(op)) {
            //no need to acquire lock, threads that are reading are okay to return "cached document" as it only needs to be valid at invocation
            //any new requests will need to wait until the "cached document" has been updated and cacheIsValid set to true
            cacheIsValid = false;
        } else {
            response.setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
        }
    }

    private void updateCachedData() {
        //set cache valid now so that it if an invalidation is called while updating, cache will be set to invalid
        //NOTE: no other thread can read the cache during an update, so it is okay that the cache is valid eventhough the cached data has not completed updating
        cacheIsValid = true;

        //used for the http header "last-modified"
        lastModified = DateTime.now();

        cachedDocument = calculateDocument();
    }

    public abstract Document calculateDocument();

}
