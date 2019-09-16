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

package com.sun.faces.facelets.tag.jsf.core;

import javax.faces.component.UIImportConstants;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.component.UIViewAction;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIWebsocket;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.NumberConverter;
import javax.faces.validator.BeanValidator;
import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.LengthValidator;
import javax.faces.validator.LongRangeValidator;
import javax.faces.validator.RegexValidator;
import javax.faces.validator.RequiredValidator;

import com.sun.faces.ext.component.UIValidateWholeBean;
import com.sun.faces.facelets.tag.AbstractTagLibrary;
import com.sun.faces.renderkit.html_basic.WebsocketRenderer;

/**
 * For Tag details, see JSF Core <a target="_new"
 * href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/tlddocs/f/tld-summary.html">taglib
 * documentation</a>.
 *
 * @author Jacob Hookom
 * @version $Id$
 */
public final class CoreLibrary extends AbstractTagLibrary {

    public final static String Namespace =    "http://java.sun.com/jsf/core";
    public final static String XMLNSNamespace = "http://xmlns.jcp.org/jsf/core";

    public final static CoreLibrary Instance = new CoreLibrary();

    public CoreLibrary() {
        this(Namespace);
    }
    
    public CoreLibrary(String namespace) {
        super(namespace);
        
        this.addTagHandler("actionListener", ActionListenerHandler.class);

        this.addTagHandler("ajax", AjaxHandler.class);

        this.addTagHandler("attribute", AttributeHandler.class);

        this.addTagHandler("attributes", AttributesHandler.class);

        this.addTagHandler("passThroughAttribute", PassThroughAttributeHandler.class);

        this.addTagHandler("passThroughAttributes", PassThroughAttributesHandler.class);

        this.addConverter("convertDateTime", DateTimeConverter.CONVERTER_ID, ConvertDateTimeHandler.class);

        this.addConverter("convertNumber", NumberConverter.CONVERTER_ID, ConvertNumberHandler.class);

        this.addConverter("converter", null, ConvertDelegateHandler.class);

        this.addTagHandler("event", EventHandler.class);

        this.addTagHandler("facet", FacetHandler.class);

        this.addTagHandler("metadata", MetadataHandler.class);

        this.addComponent("importConstants", UIImportConstants.COMPONENT_TYPE, null);

        this.addTagHandler("loadBundle", LoadBundleHandler.class);

        this.addTagHandler("resetValues", ResetValuesHandler.class);

        this.addComponent("viewParam", UIViewParameter.COMPONENT_TYPE, null);
        
        this.addComponent("viewAction", UIViewAction.COMPONENT_TYPE, null);

        this.addComponent("param", UIParameter.COMPONENT_TYPE, null);
        
        this.addTagHandler("phaseListener", PhaseListenerHandler.class);

        this.addComponent("selectItem", UISelectItem.COMPONENT_TYPE, null);

        this.addComponent("selectItems", UISelectItems.COMPONENT_TYPE, null);
        
        this.addTagHandler("setPropertyActionListener", SetPropertyActionListenerHandler.class);

        this.addComponent("subview", "javax.faces.NamingContainer", null);
        
        this.addValidator("validateBean", BeanValidator.VALIDATOR_ID);
        
        this.addValidator("validateLength", LengthValidator.VALIDATOR_ID);
        
        this.addValidator("validateLongRange", LongRangeValidator.VALIDATOR_ID);
        
        this.addValidator("validateDoubleRange", DoubleRangeValidator.VALIDATOR_ID);

        this.addValidator("validateRegex", RegexValidator.VALIDATOR_ID);
        
        this.addValidator("validateRequired", RequiredValidator.VALIDATOR_ID);
        
        this.addComponent("validateWholeBean", UIValidateWholeBean.FAMILY, null);

        this.addValidator("validator", null, ValidateDelegateHandler.class);

        this.addTagHandler("valueChangeListener",
                ValueChangeListenerHandler.class);

        this.addTagHandler("view", ViewHandler.class);
        
        this.addComponent("verbatim", "javax.faces.HtmlOutputText",
                          "javax.faces.Text", VerbatimHandler.class);       

        this.addComponent("websocket", UIWebsocket.COMPONENT_TYPE, WebsocketRenderer.RENDERER_TYPE);
    }
}
