/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.contrib.compress;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.ExecutionContext;

/**
 * Server-side interceptor to handle Gzip-encoded responses.
 *
 *
 * @since 4.0
 */
public class ResponseGzipCompress implements HttpResponseInterceptor {

    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP_CODEC = "gzip";

    public void process(final HttpResponse response, final HttpContext context)
            throws HttpException, IOException {
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            Header aeheader = request.getFirstHeader(ACCEPT_ENCODING);
            if (aeheader != null) {
                HeaderElement[] codecs = aeheader.getElements();
                for (int i = 0; i < codecs.length; i++) {
                    if (codecs[i].getName().equalsIgnoreCase(GZIP_CODEC)) {
                        response.setEntity(new GzipCompressingEntity(entity));
                        return;
                    }
                }
            }
        }
    }
}
