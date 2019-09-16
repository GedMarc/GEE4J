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
 */

package com.sun.faces.taglib.jsf_core;

import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.el.ELUtils;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.ReflectionUtils;
import com.sun.faces.util.Util;
import com.sun.faces.util.RequestStateManager;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Tag action that loads the specified ResourceBundle as a Map into
 * the request scope of the current {@link
 * javax.faces.context.FacesContext}.</p>
 * <p/>
 * <p>The user is discouraged from using multiple dot syntax in their
 * resource bundle keys.  For example, for the bundle loaded under the
 * var <code>msgs</code>, this key: <code>index.page.title</code> is
 * discouraged.  If your application requires this syntax for resource
 * bundle keys, they may be referred to in the page with a syntax like
 * this: <code>#{msgs["index.page.title"]}.</code></p>
 */

public class LoadBundleTag extends TagSupport {
    
    private static final long serialVersionUID = -584139192758868254L;
    static final String 
            PRE_VIEW_LOADBUNDLES_LIST_ATTR_NAME = 
            "com.sun.faces.taglib.jsf_core.PRE_VIEW_LOADBUNDLES_LIST";
    private static final Logger LOGGER = FacesLogger.TAGLIB.getLogger();


    // ------------------------------------------------------------- Attributes

    private ValueExpression basenameExpression;


    /**
     * <p>Set the base name of the <code>ResourceBundle</code> to be
     * loaded.</p>
     * @param basename the ValueExpression which will resolve the basename
     */
    public void setBasename(ValueExpression basename) {
        this.basenameExpression = basename;
    }


    private String var;


    /**
     * <p>Set the name of the attribute in the request scope under which
     * to store the <code>ResourceBundle</code> <code>Map</code>.</p>
     * @param var the variable name to export the loaded ResourceBundle to
     */
    public void setVar(String var) {
        this.var = var;
    }



    // --------------------------------------------------------- Public Methods


    /**
     * <p>Load the <code>ResourceBundle</code> named by our
     * <code>basename</code> property.</p>  Wrap it in an immutable
     * <code>Map</code> implementation and store the <code>Map</code> in
     * the request attr set of under the key given by our
     * <code>var</code> property.
     *
     * @throws JspException if a JSP error occurs
     */
    @Override
    public int doStartTag() throws JspException {

        FacesContext context = FacesContext.getCurrentInstance();

        // evaluate any VB expression that we were passed
        String basename;

        basename = (String)
            ELUtils.evaluateValueExpression(basenameExpression,
                                         context.getELContext());


        if (null == basename) {
        	String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "baseName");
        	throw new NullPointerException(message);
        }
        if (null == var) {
        	String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "baseName");
        	throw new NullPointerException(message);
        }

        final ResourceBundle bundle =
            ResourceBundle.getBundle(basename,
                                     context.getViewRoot().getLocale(),
                                     Util.getCurrentLoader(this));
        if (null == bundle) {
            throw new JspException("null ResourceBundle for " + basename);
        }

        Map toStore =
            new Map() {
                // this is an immutable Map
            
            @Override
            public String toString() {
                StringBuffer sb = new StringBuffer();
                Iterator<Map.Entry<String,Object>> entries = 
                        this.entrySet().iterator();
                Map.Entry<String,Object> cur;
                while (entries.hasNext()) {
                    cur = entries.next();
                    sb.append(cur.getKey()).append(": ").append(cur.getValue()).append('\n');
                }

                return sb.toString();
            }

                // Do not need to implement for immutable Map
            @Override
                public void clear() {
                    throw new UnsupportedOperationException();
                }


            @Override
                public boolean containsKey(Object key) {
                    boolean result = false;
                    if (null != key) {
                        result = (null != bundle.getObject(key.toString()));
                    }
                    return result;
                }


            @Override
                public boolean containsValue(Object value) {
                    Enumeration<String> keys = bundle.getKeys();
                    boolean result = false;
                    while (keys.hasMoreElements()) {
                        Object curObj = bundle.getObject(keys.nextElement());
                        if ((curObj == value) ||
                            ((null != curObj) && curObj.equals(value))) {
                            result = true;
                            break;
                        }
                    }
                    return result;
                }


            @Override
                public Set<Map.Entry<String,Object>> entrySet() {
                    HashMap<String,Object> mappings = new HashMap<>();
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        Object value = bundle.getObject(key);
                        mappings.put(key, value);
                    }
                    return mappings.entrySet();
                }


            @Override
                public boolean equals(Object obj) {
                    return !((obj == null) || !(obj instanceof Map))
                           && entrySet().equals(((Map) obj).entrySet());

                }


            @Override
                public Object get(Object key) {
                    if (null == key) {
                        return null;
                    }
                    try {
                        return bundle.getObject(key.toString());
                    } catch (MissingResourceException e) {
                        return "???" + key + "???";
                    }
                }


            @Override
                public int hashCode() {
                    return bundle.hashCode();
                }


            @Override
                public boolean isEmpty() {
                    Enumeration<String> keys = bundle.getKeys();
                    return !keys.hasMoreElements();
                }


            @Override
                public Set keySet() {
                    Set<String> keySet = new HashSet<>();
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        keySet.add(keys.nextElement());
                    }
                    return keySet;
                }


                // Do not need to implement for immutable Map
            @Override
                public Object put(Object k, Object v) {
                    throw new UnsupportedOperationException();
                }


                // Do not need to implement for immutable Map
            @Override
                public void putAll(Map t) {
                    throw new UnsupportedOperationException();
                }


                // Do not need to implement for immutable Map
            @Override
                public Object remove(Object k) {
                    throw new UnsupportedOperationException();
                }


            @Override
                public int size() {
                    int result = 0;
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        keys.nextElement();
                        result++;
                    }
                    return result;
                }


            @Override
                public java.util.Collection values() {
                    ArrayList<Object> result = new ArrayList<>();
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        result.add(
                            bundle.getObject(keys.nextElement()));
                    }
                    return result;
                }
            };

        ExternalContext extContext = context.getExternalContext();
        extContext.getRequestMap().put(var, toStore);

        if (WebConfiguration.getInstance(extContext)
              .isOptionEnabled
                    (BooleanWebContextInitParameter.EnableLoadBundle11Compatibility)) {
            // the UIComponent that wraps the Map
            UIComponent bundleComponent =
                  createNewLoadBundleComponent(var, toStore);
            UIComponentClassicTagBase parentTag = getParentUIComponentTag();

            // Is this loadBundle tag instance outside of <f:view>?
            if (null == parentTag) {
                // Yes.  Store the bundleComponent in a list so the <f:view> tag
                // can add the list contents as the first children.
                List<UIComponent> preViewBundleComponents =
                      getPreViewLoadBundleComponentList();
                preViewBundleComponents.add(bundleComponent);
            } else {
                // No.  Use addChild to add the bundeComponent to the tree.
                addChildToParentTagAndParentComponent(bundleComponent, parentTag);
            }
        }

        return (EVAL_BODY_INCLUDE);

    }
    
    static void addChildToParentTagAndParentComponent(UIComponent child,
            UIComponentClassicTagBase parentTag) {
        
        Method addChildToComponentAndTag;
        
        if (null != (addChildToComponentAndTag = 
                ReflectionUtils.lookupMethod(UIComponentClassicTagBase.class,
                "addChildToComponentAndTag",
                UIComponent.class))) {
            try {
                addChildToComponentAndTag.setAccessible(true);
                addChildToComponentAndTag.invoke(parentTag,
                        child);
            }
            catch (IllegalAccessException | IllegalArgumentException accessException) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, 
                            "Unable to add " + child + " to tree:", accessException);
                }
                
            }
            catch (InvocationTargetException targetException) {
                Throwable cause = targetException.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
            }
        }
    }
    
    static List<UIComponent> getPreViewLoadBundleComponentList() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String,Object> stateMap = RequestStateManager.getStateMap(ctx);

        //noinspection unchecked
        List<UIComponent> result = (List<UIComponent>)
              stateMap.get(PRE_VIEW_LOADBUNDLES_LIST_ATTR_NAME);
        if (result == null) {
            result = new ArrayList<>();
            stateMap.put(PRE_VIEW_LOADBUNDLES_LIST_ATTR_NAME, result);
        }
        
        return result;
    }
    
    private UIComponent createNewLoadBundleComponent(String var, 
            Map toStore) {
        UIComponent result = new LoadBundleComponent(var, toStore);
        result.setTransient(true);
        return result;
    }

    /**
     * @return the <code>UIComponentClassicTagBase</code> instance
     *  that represents the tag in the page to which the special
     *  component should be added as a child
     */
    private UIComponentClassicTagBase getParentUIComponentTag() {
        Tag parent = this.getParent();
        while (null != parent && 
                (!(parent instanceof UIComponentClassicTagBase))) {
            parent = this.getParent();
        }
        UIComponentClassicTagBase result = (UIComponentClassicTagBase) parent;
        
        // Check for case where the <f:loadBundle> is inside of an included page,
        // but outside of the <f:subview> for that page.  This can happen
        // either when the <f:subview> is in the includING page *OR* when
        // the <f:subview> is in the includED page, yet the <f:loadBundle> is
        // outside of the <f:subview> in the includED page.
        Stack<UIComponentClassicTagBase> viewTagStack = SubviewTag.getViewTagStack();
        if (!viewTagStack.empty()) {
            result = viewTagStack.peek();
        }
        
        return result;
    }


    /**
     * <p>Release references to any acquired resources.
     */
    @Override
    public void release() {

        this.basenameExpression = null;
        this.var = null;

    }
    
    private static class LoadBundleComponent extends UIComponentBase {
            private String var;
            private Map toStore;
            
            public LoadBundleComponent(String var, Map toStore) {
                this.var = var;
                this.toStore = toStore;
            }
        
            @Override
            public String getFamily() {
                return null;
            }
            
            @Override
            public void encodeBegin(FacesContext context) throws IOException {
                Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
                
                requestMap.put(var, toStore);
            }
            
            @Override
            public void encodeEnd(FacesContext context) throws IOException {
            }

            @Override
            public void encodeChildren(FacesContext context) throws IOException {
            }
            
            @Override
            public String toString() {

                return "LoadBundleComponent: var: " + var + " keys: " +
                        toStore.toString();
            }
        
    }
    

}
