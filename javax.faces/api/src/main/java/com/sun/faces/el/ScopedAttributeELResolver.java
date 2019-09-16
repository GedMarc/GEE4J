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

package com.sun.faces.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.beans.FeatureDescriptor;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;

import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.ELContext;
import javax.el.ELResolver;

import com.sun.faces.util.Util;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.mgbean.BeanManager;

public class ScopedAttributeELResolver extends ELResolver {

    public ScopedAttributeELResolver() {
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException {
        if (base != null) {
            return null;
        }
        if ( property == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "base and property"); // ?????
            throw new PropertyNotFoundException(message);
        }
        context.setPropertyResolved(true);
        String attribute = property.toString();
        FacesContext facesContext = (FacesContext)
            context.getContext(FacesContext.class);
        ExternalContext ec = facesContext.getExternalContext();

        // check request
        Object result = ec.getRequestMap().get(attribute);
        if (result != null) {
            return result;
        }

        // check UIViewRoot
        UIViewRoot root = facesContext.getViewRoot();
        if (root != null) {
            Map<String, Object> viewMap = root.getViewMap(false);
            if (viewMap != null) {
                result = viewMap.get(attribute);
            }
        }
        if (result != null) {
            return result;
        }

        // check session
        result = ec.getSessionMap().get(attribute);
        if (result != null) {
            return result;
        }

        // check application
        result = ec.getApplicationMap().get(attribute);
        if (result != null) {
            return result;
        }

        // if we get to this point, nothing was found in the standard scopes.
        // If the attribute refers to an entity handled by the BeanManager
        // try getting the value from there as the value may be in a custom
        // scope.
        ApplicationAssociate associate = ApplicationAssociate.getCurrentInstance();
        if (associate != null) {
            BeanManager manager = associate.getBeanManager();
            if (manager != null && manager.isManaged(attribute)) {
                return manager.getBeanFromScope(attribute, facesContext);
            }
        }

        return null;
        
    }


    @Override
    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException {
        if (base != null) {
            return null;
        }
        if ( property == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "base and property"); // ?????
            throw new PropertyNotFoundException(message);
        }

        context.setPropertyResolved(true);
        return Object.class;
    }

    @Override
    public void  setValue(ELContext context, Object base, Object property,
                          Object val) throws ELException {
        if (base != null) {
            return;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "base and property"); // ?????
            throw new PropertyNotFoundException(message);
        }

        context.setPropertyResolved(true);

        String attribute = (String) property;
        FacesContext facesContext = (FacesContext)
            context.getContext(FacesContext.class);
        ExternalContext ec = facesContext.getExternalContext();
        if ((ec.getRequestMap().get(attribute)) != null) {
            ec.getRequestMap().put(attribute, val);
        } else if ((facesContext.getViewRoot()) != null && (facesContext.getViewRoot().getViewMap().get(attribute)) != null) {
            facesContext.getViewRoot().getViewMap().put(attribute, val);
        } else if ((ec.getSessionMap().get(attribute)) != null) {
            ec.getSessionMap().put(attribute, val);
        } else if ((ec.getApplicationMap().get(attribute)) != null) {
            ec.getApplicationMap().put(attribute, val);
        } else {
            // if the property doesn't exist in any of the scopes, put it in
            // request scope.
            ec.getRequestMap().put(attribute, val);
        }

    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException {
        if (base != null) {
            return false;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "base and property"); // ?????
            throw new PropertyNotFoundException(message);
        }
        context.setPropertyResolved(true);
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {

       ArrayList<FeatureDescriptor> list = new ArrayList<>();

       FacesContext facesContext = (FacesContext)
           context.getContext(FacesContext.class);
       ExternalContext ec = facesContext.getExternalContext();

       // add attributes in request scope.
       Set<Entry<String,Object>> attrs = ec.getRequestMap().entrySet();
        for (Entry<String, Object> entry : attrs) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();
            list.add(Util.getFeatureDescriptor(attrName, attrName,
                                               "request scope attribute", false, false, true, attrValue.getClass(),
                                               Boolean.TRUE));
        }
        
       // add attributes in view scope.
        UIViewRoot root = facesContext.getViewRoot();
        if (root != null) {
            Map<String, Object> viewMap = root.getViewMap(false);
            if (viewMap != null && viewMap.size() != 0) {
                attrs = viewMap.entrySet();
                for (Entry<String, Object> entry : attrs) {
                    String attrName = entry.getKey();
                    Object attrValue = entry.getValue();
                    list.add(Util.getFeatureDescriptor(attrName, attrName,
                                                       "view scope attribute", false, false, true, attrValue.getClass(),
                                                       Boolean.TRUE));
                }
            }
        }
        

       // add attributes in session scope.
       attrs = ec.getSessionMap().entrySet();
        for (Entry<String, Object> entry : attrs) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();
            list.add(Util.getFeatureDescriptor(attrName, attrName,
                                               "session scope attribute", false, false, true, attrValue.getClass(),
                                               Boolean.TRUE));
        }

       // add attributes in application scope.
       attrs = ec.getApplicationMap().entrySet();
       for (Entry<String, Object> entry : attrs) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();
            list.add(Util.getFeatureDescriptor(attrName, attrName,
                                               "application scope attribute", false, false, true, attrValue.getClass(),
                                               Boolean.TRUE));
        }

       return list.iterator();
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base != null) {
            return null;
        }
        return Object.class;
    }

}
