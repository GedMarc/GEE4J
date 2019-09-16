/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.context.flash;

import java.io.Serializable;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

class SessionHelper implements Serializable, HttpSessionActivationListener {
    
    private static final long serialVersionUID = -4146679754778263071L;
    
    static final String FLASH_SESSIONACTIVATIONLISTENER_ATTRIBUTE_NAME = 
            ELFlash.FLASH_ATTRIBUTE_NAME + "FSAL";

    private static final String FLASH_INNER_MAP_KEY = ELFlash.FLASH_ATTRIBUTE_NAME + "FIM";
    private boolean didPassivate;
    
    static SessionHelper getInstance(ExternalContext extContext) {
        return (SessionHelper) 
                extContext.getSessionMap().get(FLASH_SESSIONACTIVATIONLISTENER_ATTRIBUTE_NAME);
    }

    void update(ExternalContext extContext,
            ELFlash flash) {
        Map<String, Object> sessionMap = extContext.getSessionMap();
        if (didPassivate) {
            Map<String, Map<String, Object>> flashInnerMap = 
                    (Map<String, Map<String, Object>>) sessionMap.get(FLASH_INNER_MAP_KEY);
            flash.setFlashInnerMap(flashInnerMap);
            didPassivate = false;
        } else {
            sessionMap.put(FLASH_SESSIONACTIVATIONLISTENER_ATTRIBUTE_NAME, this);
            sessionMap.put(FLASH_INNER_MAP_KEY, flash.getFlashInnerMap());
        }
    }
    
    void remove(ExternalContext extContext) {
        Map<String, Object> sessionMap = extContext.getSessionMap();
        sessionMap.remove(FLASH_SESSIONACTIVATIONLISTENER_ATTRIBUTE_NAME);
        sessionMap.remove(FLASH_INNER_MAP_KEY);
    }
    
    
    @Override
    public void sessionDidActivate(HttpSessionEvent hse) {
        didPassivate = true;
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent hse) {
        didPassivate = true;

    }
    
    
    
}
