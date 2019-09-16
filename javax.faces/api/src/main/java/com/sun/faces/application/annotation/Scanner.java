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

package com.sun.faces.application.annotation;

import java.lang.annotation.Annotation;

/**
 * <p>Instances of this interface are responsible for scanning a <code>Class</code>
 * for a specific annotation types.</p>
 *
 * <p>Scanner implementations <em>must</em> be thread safe.</p>
 */
interface Scanner {

    /**
     * @return the {@link java.lang.annotation.Annotation} this <code>Scanner</code>
     *  is responsible for handling.  <em>NOTE</em>: while a particular <code>Scanner</code>
     *  instance may handle a plural version of an <code>Annotation</code> in
     *  additional to a singular, this method must return the singular version
     *  only.
     */
    public Class<? extends Annotation> getAnnotation();

    
    /**
     * Scan the target class for the {@link java.lang.annotation.Annotation}s this scanner handles.
     * @param clazz the target class
     * @return a new {@link RuntimeAnnotationHandler} instance capable of processing the
     *  annotations defined on this class.  If no relevant {@link java.lang.annotation.Annotation}s
     *  are found, return <code>null</code>.
     */
    public RuntimeAnnotationHandler scan(Class<?> clazz);
    
}
