/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.faces.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A simple PrintWriter implementation to allow us to query
 * whether or not the writer has been flushed or closed.  This is necessary
 * to better mimic the actual Servlet response.
 */
public class WebPrintWriter extends PrintWriter {

    public static final Writer NOOP_WRITER = new NoOpWriter();
    private boolean committed;

    public WebPrintWriter(Writer delegate) {
        super(delegate);
    }

    @Override
    public void close() {
        committed = true;
    }

    @Override
    public void flush() {
        committed = true;
    }

    public boolean isComitted() {
        return committed;
    }

    // ----------------------------------------------------------- Inner Classes


    private static class NoOpWriter extends Writer {

        public NoOpWriter() {}

        @Override
        public void write(char cbuf[], int off, int len) throws IOException {
            // no-op
        }

        @Override
        public void flush() throws IOException {
            // no-op
        }

        @Override
        public void close() throws IOException {
            // no-op
        }

        protected NoOpWriter(Object lock) {
            // no-op
        }

        @Override
        public void write(int c) throws IOException {
            // no-op
        }

        @Override
        public void write(char cbuf[]) throws IOException {
            // no-op
        }

        @Override
        public void write(String str) throws IOException {
            // no-op
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            // no-op
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            return this;
        }

        @Override
        public Writer append(CharSequence csq, int start, int end)
        throws IOException {
            return this;
        }

        @Override
        public Writer append(char c) throws IOException {
            return this;
        }
    }
}
