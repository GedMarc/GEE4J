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

package com.sun.faces;

import javax.faces.render.RenderKitFactory;

import com.sun.faces.facelets.tag.jsf.core.CoreLibrary;
import com.sun.faces.facelets.tag.jsf.html.HtmlLibrary;
import com.sun.faces.facelets.tag.ui.UILibrary;

/**
 * This class contains literal strings used throughout the Faces RI.
 */
public class RIConstants {


    /**
     * Used to add uniqueness to the names.
     */
    public final static String FACES_PREFIX = "com.sun.faces.";

    public final static String HTML_BASIC_RENDER_KIT = FACES_PREFIX +
        RenderKitFactory.HTML_BASIC_RENDER_KIT;       

    public static final String SAVESTATE_FIELD_DELIMITER = "~";
    public static final String SAVESTATE_FIELD_MARKER = 
          SAVESTATE_FIELD_DELIMITER 
           + FACES_PREFIX 
           + "saveStateFieldMarker" 
           + SAVESTATE_FIELD_DELIMITER;

    public static final String SAVED_STATE = FACES_PREFIX + "savedState";


    /*
    * <p>TLV Resource Bundle Location </p>
    */
    public static final String TLV_RESOURCE_LOCATION =
        FACES_PREFIX + "resources.Resources";

    public static final String NO_VALUE = "";
      
    public static final String CORE_NAMESPACE = 
            CoreLibrary.Namespace;
    public static final String HTML_NAMESPACE = 
            HtmlLibrary.Namespace;
    
    public static final String CORE_NAMESPACE_NEW = 
            CoreLibrary.XMLNSNamespace;
    public static final String HTML_NAMESPACE_NEW = 
            HtmlLibrary.XMLNSNamespace;

    public static final String FACELET_NAMESPACE = 
        UILibrary.Namespace;
    public static final String FACELET_NAMESPACE_NEW = 
        UILibrary.XMLNSNamespace;

    public static final Class[] EMPTY_CLASS_ARGS = new Class[0];
    public static final Object[] EMPTY_METH_ARGS = new Object[0];

    /**
     *<p>ResponseWriter Content Types and Encoding</p>
     */
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String XHTML_CONTENT_TYPE = "application/xhtml+xml";
    public static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";
    public static final String TEXT_XML_CONTENT_TYPE = "text/xml";
    public static final String ALL_MEDIA = "*/*";
    public static final String CHAR_ENCODING = "UTF-8";
    public static final String FACELETS_ENCODING_KEY = "facelets.Encoding";
    public static final String DEFAULT_LIFECYCLE = 
          FACES_PREFIX + "DefaultLifecycle";
    public static final String DEFAULT_STATEMANAGER =
          FACES_PREFIX + "DefaultStateManager";

    public static final String ERROR_PAGE_PRESENT_KEY_NAME = 
	FACES_PREFIX + "errorPagePresent";

    public static final String FACES_INITIALIZER_MAPPINGS_ADDED =
          FACES_PREFIX + "facesInitializerMappingsAdded";

    public static final String VIEWID_KEY_NAME = FACES_PREFIX + "viewId";

    public static final String PUSH_RESOURCE_URLS_KEY_NAME = FACES_PREFIX + "resourceUrls";

    /**
     * Marker used when saving the list of component adds and removes.
     */
    public static final String DYNAMIC_ACTIONS =
            FACES_PREFIX + "DynamicActions";
    
    /**
     * Marker attached to a component that has dynamic children.
     */
    public static final String DYNAMIC_CHILD_COUNT =
            FACES_PREFIX + "DynamicChildCount";
    
    /**
     * Marker attached to a component that was added dynamically.
     */
    public static final String DYNAMIC_COMPONENT =
            FACES_PREFIX + "DynamicComponent";
    
    /**
     * Present in the attrs of UIViewRoot iff the tree has one or more
     * dynamic modifications
     */
    public static final String TREE_HAS_DYNAMIC_COMPONENTS =
            FACES_PREFIX + "TreeHasDynamicComponents";
    
    public static final String FLOW_DEFINITION_ID_SUFFIX = "-flow.xml";
    
    public static final int FLOW_DEFINITION_ID_SUFFIX_LENGTH = FLOW_DEFINITION_ID_SUFFIX.length();
    
    public static final String FLOW_IN_JAR_PREFIX = "META-INF/flows";
    
    public static final int FLOW_IN_JAR_PREFIX_LENGTH = FLOW_IN_JAR_PREFIX.length();
    
    public static final String FLOW_DISCOVERY_CDI_HELPER_BEAN_NAME = "csfFLOWDISCOVERYCDIHELPER";
    
    public static final String JAVAEE_XMLNS = "http://xmlns.jcp.org/xml/ns/javaee";
    
    /**
     * Convenience key to determine if CDI is available.
     */
    public static final String CDI_AVAILABLE = FACES_PREFIX + "cdi.AvailableFlag";

    /**
     * Convenience key to store / get BeanManager.
     */
    public static final String CDI_BEAN_MANAGER = FACES_PREFIX + "cdi.BeanManager";
    
    /**
     * Convenience key to determine if CDI is version 1.1 or later.
     */
    public static final String CDI_1_1_OR_LATER = FACES_PREFIX + "cdi.OneOneOrLater";
    
    /**
     * Convenience key in App map and FacesContext map for spec version of faces-config.xml
     */
    public static final String FACES_CONFIG_VERSION = FACES_PREFIX + "facesConfigVersion";
    
    /**
     * Convenience key to temporarily store the set of annotated classes in the servlet context.
     */
    public static final String ANNOTATED_CLASSES = FACES_PREFIX + "AnnotatedClasses";
    
    private RIConstants() {
        throw new IllegalStateException();
    }
}
