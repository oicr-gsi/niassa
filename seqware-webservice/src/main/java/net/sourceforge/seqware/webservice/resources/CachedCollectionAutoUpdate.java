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

import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.restlet.Response;

/**
 *
 * @author mlaszloffy
 */
public abstract class CachedCollectionAutoUpdate<T, U> {

    private volatile List<T> cachedCollection = null;
    private volatile Instant modificationDate = null;
    private final ReentrantReadWriteLock cachedDataLock = new ReentrantReadWriteLock();

    /**
     * Process the request and modify the response entity with either cached or new data.
     * <p>
     * The auto update implementation of CachedCollectionAutoUpdate will either get updated data or if the updated data is currently being
     * calculated, it will use that data once it is completed calculating.
     * <p>
     * The response is modified by the writeResponse() implementation.
     *
     * @param response the response that is to be modified
     */
    public void processRequest(Response response) {
        if (cachedDataLock.writeLock().tryLock()) {
            //no other writer or readers accessing the cached document, update the document
            try {
                updateCachedData();
                writeResponse(response, cachedCollection, modificationDate);
            } finally {
                cachedDataLock.writeLock().unlock();
            }
        } else {
            //there are other reader theads or there is _one_ writer thread updating the cached document

            //if there are readers: obtaining the read lock will be successful and the cached document can be read
            //if there is a writer: obtaining the read lock will be blocked until the write lock has been released
            cachedDataLock.readLock().lock();

            try {
                writeResponse(response, cachedCollection, modificationDate);
            } finally {
                cachedDataLock.readLock().unlock();
            }
        }
    }

    private void updateCachedData() {
        //used for the http header "last-modified"
        modificationDate = Instant.now();

        cachedCollection = calculateCollection();
    }

    /**
     * Calculate the collection.
     * <p>
     * This method will be called once and if it is still executing, subsequent calls will use the first call's results.
     *
     * @return a list of type \<T\>
     */
    public abstract List<T> calculateCollection();

    /**
     * Writes out the collection from calculateCollection() to the response.
     *
     * @param response         the response entity of the current request
     * @param cachedCollection the cached collection to write to the response
     * @param modificationDate the cached collection calculation/modification date
     */
    public abstract void writeResponse(Response response, List<T> cachedCollection, Instant modificationDate);

}
