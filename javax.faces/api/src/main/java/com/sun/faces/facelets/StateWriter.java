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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2005-2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.faces.facelets;

import com.sun.faces.facelets.util.FastWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * A class for handling state insertion.  Content is written
 * directly to "out" until an attempt to write state;  at that
 * point, it's redirected into a buffer that can be picked through
 * in theory, this buffer should be very small, since it only
 * needs to be enough to contain all the content after the close
 * of the first (and, hopefully, only) form.
 * <p>
 * Potential optimizations:
 * <ul>
 * <li>If we created a new FastWriter at each call to writingState(),
 * and stored a List of them, then we'd know that state tokens could
 * only possibly be near the start of each buffer (and might not be there
 * at all).  (There might be a close-element before the state token). Then,
 * we'd only need to check the start of the buffer for the state token;
 * if it's there, write out the real state, then blast the rest of the
 * buffer out.  This wouldn't even require toString(), which for
 * large buffers is expensive.  However, this optimization is only
 * going to be especially meaningful for the multi-form case.
 * </li>
 * <li>More of a FastWriter optimization than a StateWriter, but:
 *  it is far faster to create a set of small 1K buffers than constantly
 *  reallocating one big buffer.</li>
 * </ul>
 *
 * @author Adam Winer
 */
final class StateWriter extends Writer {

    private int initialSize;
    private Writer out;
    private FastWriter fast;
    private boolean writtenState;
    
    static public StateWriter getCurrentInstance() {
        return (StateWriter) CURRENT_WRITER.get();
    }

    public StateWriter(Writer initialOut, int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Initial Size cannot be less than 0");
        }
        
        this.initialSize = initialSize;
        this.out = initialOut;

        CURRENT_WRITER.set(this);
    }

    /**
     * Mark that state is about to be written.  Contrary to what you'd expect,
     * we cannot and should not assume that this location is really going
     * to have state;  it is perfectly legit to have a ResponseWriter that
     * filters out content, and ignores an attempt to write out state
     * at this point.  So, we have to check after the fact to see
     * if there really are state markers.
     */
    public void writingState() {
        if (!this.writtenState) {
            this.writtenState = true;
            this.out = this.fast = new FastWriter(this.initialSize);
        }
    }

    public boolean isStateWritten() {
        return this.writtenState;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }

    @Override
    public void flush() throws IOException {
        // do nothing
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.out.write(cbuf, off, len);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        this.out.write(cbuf);
    }

    @Override
    public void write(int c) throws IOException {
        this.out.write(c);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        this.out.write(str, off, len);
    }

    @Override
    public void write(String str) throws IOException {
        this.out.write(str);
    }
    
    public String getAndResetBuffer() {
        if (!this.writtenState) {
            throw new IllegalStateException(
                     "Did not write state;  no buffer is available");
        }

        String result = this.fast.toString();
        this.fast.reset();
        return result;
    }
  
    public void release() {
        CURRENT_WRITER.set(null);
    }

    static private final ThreadLocal CURRENT_WRITER = new ThreadLocal();
}
