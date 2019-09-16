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

package com.sun.faces.facelets.tag.composite;

import com.sun.faces.application.view.FaceletViewHandlingStrategy;
import com.sun.faces.facelets.tag.TagHandlerImpl;
import com.sun.faces.facelets.tag.jsf.ComponentSupport;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.MessageUtils;

import javax.faces.application.Resource;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagException;

public class InterfaceHandler extends TagHandlerImpl {

    private final Logger LOGGER = FacesLogger.TAGLIB.getLogger();

    private static final String[] ATTRIBUTES_DEV = {
          "displayName",
          "expert",
          "hidden",
          "preferred",
          "shortDescription",
          "name",
          "componentType"
    };



    private static final PropertyHandlerManager INTERFACE_HANDLERS =
          PropertyHandlerManager.getInstance(ATTRIBUTES_DEV);



    public final static String Name = "interface";

    
    public InterfaceHandler(TagConfig config) {
        super(config);
    }
    
    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        FacesContext context = ctx.getFacesContext();
        // only process if it's been created
        // Do not process if we're simply building metadata
        if (FaceletViewHandlingStrategy.isBuildingMetadata(context)) {
            imbueComponentWithMetadata(ctx, parent);
            this.nextHandler.apply(ctx, parent);
        } else {
            if (ProjectStage.Development == context.getApplication().getProjectStage()) {
                validateComponent(context, parent);
            }
        }
    }
    
    private void validateComponent(FacesContext context, UIComponent ccParent) throws TagException {
        UIComponent cc = ccParent.getParent();
        if (null == cc) {
            String clientId = ccParent.getClientId(context);

            throw new TagException(tag, MessageUtils.getExceptionMessageString(
                    MessageUtils.COMPONENT_NOT_FOUND_ERROR_MESSAGE_ID,
                    clientId + ".getParent()"));
        }
        Tag usingPageTag = ComponentSupport.getTagForComponent(context, cc);
        Map<String, Object> attrs = cc.getAttributes();
        BeanInfo componentMetadata = (BeanInfo) attrs.get(UIComponent.BEANINFO_KEY);

        if (null == componentMetadata) {
            String clientId = ccParent.getClientId(context);

            throw new TagException(usingPageTag, MessageUtils.getExceptionMessageString(
                    MessageUtils.MISSING_COMPONENT_METADATA, clientId));
        }

        PropertyDescriptor[] declaredAttributes = componentMetadata.getPropertyDescriptors();
        String key;
        Object requiredValue;
        boolean found = false, required = false;
        StringBuffer buf = null;
        String attrMessage = "", facetMessage = "";

        // Traverse the attributes of this component
        for (PropertyDescriptor cur : declaredAttributes) {
            required = false;
            requiredValue = cur.getValue("required");
            if (null != requiredValue) {
                if (requiredValue instanceof ValueExpression) {
                    requiredValue = ((ValueExpression) requiredValue).getValue(context.getELContext());
                    required = Boolean.parseBoolean(requiredValue.toString());
                }
            }
            if (required) {
                key = cur.getName();
                found = false;
                // Is the attribute a method expression?
                if (null != cur.getValue("method-signature") && null == cur.getValue("type")) {
                    // Yes, look for it as an EL expression.
                    found = (null != cc.getValueExpression(key));
                } else {
                    // No, look for it as an actual attribute
                    found = attrs.containsKey(key);
                    // Special case: nested composite components
                    if (!found) {
                        // Check if an EL expression was given.
                        found = (null!=cc.getValueExpression(key));
                    }
                }
                if (!found) {
                    if (null == buf) {
                        buf = new StringBuffer();
                        buf.append(key);
                    } else {
                        buf.append(", " + key);
                    }
                }
            }
        }
        if (null != buf) {
            attrMessage = MessageUtils.getExceptionMessageString(MessageUtils.MISSING_COMPONENT_ATTRIBUTE_VALUE,
                    buf.toString());
        }

        buf = null;

        // Traverse the declared facets
        Map<String, PropertyDescriptor> declaredFacets =
                (Map<String, PropertyDescriptor>) componentMetadata.getBeanDescriptor().getValue(UIComponent.FACETS_KEY);
        if (null != declaredFacets) {
            for (PropertyDescriptor cur : declaredFacets.values()) {
                required = false;
                requiredValue = cur.getValue("required");
                if (null != requiredValue) {
                    if (requiredValue instanceof ValueExpression) {
                        requiredValue = ((ValueExpression) requiredValue).getValue(context.getELContext());
                        required = Boolean.parseBoolean(requiredValue.toString());
                    }
                }
                if (required) {
                    key = cur.getName();
                    if (!cc.getFacets().containsKey(key)) {
                        if (null == buf) {
                            buf = new StringBuffer();
                            buf.append(key);
                        } else {
                            buf.append(", " + key);
                        }
                    }
                }
            }
        }
        if (null != buf) {
            facetMessage = MessageUtils.getExceptionMessageString(MessageUtils.MISSING_COMPONENT_FACET,
                    buf.toString());
        }

        if (0 < attrMessage.length() || 0 < facetMessage.length()) {
            throw new TagException(usingPageTag, attrMessage + " " + facetMessage);
        }
    }
    
    @SuppressWarnings({"unchecked"})
    private void imbueComponentWithMetadata(FaceletContext ctx, UIComponent parent) {
        // only process if it's been created
        if (null == parent || 
            (null == (parent = parent.getParent())) ||
            !(ComponentHandler.isNew(parent))) {
            return;
        }
        
        Map<String, Object> attrs = parent.getAttributes();

        CompositeComponentBeanInfo componentBeanInfo =
              (CompositeComponentBeanInfo) attrs.get(UIComponent.BEANINFO_KEY);

        if (componentBeanInfo == null) {
        
            componentBeanInfo = new CompositeComponentBeanInfo();
            attrs.put(UIComponent.BEANINFO_KEY, componentBeanInfo);
            BeanDescriptor componentDescriptor = new BeanDescriptor(parent.getClass());
            // PENDING(edburns): Make sure attributeNames() returns the right content
            // per the javadocs for ViewDeclarationLanguage.getComponentMetadata()
            componentBeanInfo.setBeanDescriptor(componentDescriptor);

            for (TagAttribute tagAttribute : this.tag.getAttributes().getAll()) {
                String attributeName = tagAttribute.getLocalName();
                PropertyHandler handler = INTERFACE_HANDLERS.getHandler(ctx, attributeName);
                if (handler != null) {
                    handler.apply(ctx,
                                  attributeName,
                                  componentDescriptor,
                                  tagAttribute);
                }

            }

            List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>)
              componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
            if (null == targetList) {
                targetList = new ArrayList<>();
                componentDescriptor.setValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY,
                        targetList);
            }

            Resource componentResource =
                    (Resource) attrs.get(Resource.COMPONENT_RESOURCE_KEY);
            if (null == componentResource) {
                throw new NullPointerException("Unable to find Resource for composite component");
            }
        }
    }

}
