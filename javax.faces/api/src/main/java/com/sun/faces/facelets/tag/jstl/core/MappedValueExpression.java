/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2015 Oracle and/or its affiliates. All rights reserved.
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

import javax.el.ELContext;
import javax.el.ValueExpression;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Jacob Hookom
 * @version $Id$
 */
public final class MappedValueExpression extends ValueExpression {

    private final static class Entry implements Map.Entry, Serializable {

        private static final long serialVersionUID = 4361498560718735987L;
        private final Map src;
        private final Object key;

        public Entry(Map src, Object key) {
            this.src = src;
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return src.get(key);
        }

        @Override
        public Object setValue(Object value) {
            return src.put(key, value);
        }

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object key;

    private final ValueExpression orig;

    /**
     * 
     */
    public MappedValueExpression(ValueExpression orig, Map.Entry entry) {
        this.orig = orig;
        this.key = entry.getKey();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ValueExpression#getValue(javax.el.ELContext)
     */
    @Override
    public Object getValue(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(true);
            return new Entry((Map) base, key);
            
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ValueExpression#setValue(javax.el.ELContext,
     *      java.lang.Object)
     */
    @Override
    public void setValue(ELContext context, Object value) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            context.getELResolver().setValue(context, base, key, value);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ValueExpression#isReadOnly(javax.el.ELContext)
     */
    @Override
    public boolean isReadOnly(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            return context.getELResolver().isReadOnly(context, base, key);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.el.ValueExpression#getType(javax.el.ELContext)
     */
    @Override
    public Class getType(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            return context.getELResolver().getType(context, base, key);
        }
        return null;
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
        return 0;
    }

    /*
     * (non-Javadoc)eturn new Map.Entry<K, V>
     * 
     * @see javax.el.Expression#isLiteralText()
     */
    @Override
    public boolean isLiteralText() {
        return false;
    }

}
