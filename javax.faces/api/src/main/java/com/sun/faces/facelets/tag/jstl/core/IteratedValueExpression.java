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

package com.sun.faces.facelets.tag.jstl.core;

import java.util.Collection;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

public final class IteratedValueExpression extends ValueExpression {

    private static final long serialVersionUID = 1L;

    private ValueExpression orig;

    private int start;
    private int index;

    public IteratedValueExpression(ValueExpression orig, int start, int index) {
        this.orig = orig;
        this.start = start;
        this.index = index;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.ValueExpression#getValue(javax.el.ELContext)
     */
    @Override
    public Object getValue(ELContext context) {
        Collection collection = (Collection) orig.getValue(context);
        Iterator iterator = collection.iterator();
        Object result = null;
        int i = start;
        if (i != 0) {
            while(i != 0) {
                result = iterator.next();
                if (!iterator.hasNext()) {
                    throw new ELException("Unable to position start");
                }
                i--;
            }
        } else {
            result = iterator.next();
        }
        while(i < index) {
            if (!iterator.hasNext()) {
                throw new ELException("Unable to get given value");
            }
            i++;
            result = iterator.next();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.ValueExpression#setValue(javax.el.ELContext,
     *      java.lang.Object)
     */
    @Override
    public void setValue(ELContext context, Object value) {
        context.setPropertyResolved(false);
        throw new PropertyNotWritableException();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.ValueExpression#isReadOnly(javax.el.ELContext)
     */
    @Override
    public boolean isReadOnly(ELContext context) {
        context.setPropertyResolved(false);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.ValueExpression#getType(javax.el.ELContext)
     */
    @Override
    public Class getType(ELContext context) {
        context.setPropertyResolved(false);
        return Object.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.ValueExpression#getExpectedType()
     */
    @Override
    public Class getExpectedType() {
        return Object.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.Expression#getExpressionString()
     */
    @Override
    public String getExpressionString() {
        return this.orig.getExpressionString();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.Expression#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this.orig.equals(obj);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.Expression#hashCode()
     */
    @Override
    public int hashCode() {
        return this.orig.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.el.Expression#isLiteralText()
     */
    @Override
    public boolean isLiteralText() {
        return false;
    }

}
