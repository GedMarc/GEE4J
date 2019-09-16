/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.faces.facelets.tag.jsf;

import com.sun.faces.RIConstants;
import com.sun.faces.context.StateContext;
import com.sun.faces.facelets.tag.jsf.core.FacetHandler;
import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.PartialStateSaving;
import com.sun.faces.util.Util;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;
import javax.faces.view.facelets.Tag;
import javax.faces.event.PhaseId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author Jacob Hookom
 * @version $Id$
 */
public final class ComponentSupport {

    private final static String MARK_DELETED = "com.sun.faces.facelets.MARK_DELETED";
    public final static String MARK_CREATED = "com.sun.faces.facelets.MARK_ID";

    // Expando boolean attribute used to identify parent components that have had
    // a dynamic child addition or removal.
    public final static String MARK_CHILDREN_MODIFIED = "com.sun.faces.facelets.MARK_CHILDREN_MODIFIED";
    
    // Expando Collection<String> attribute used to identify tagIds of child components that
    // have been removed from a parent component.
    public final static String REMOVED_CHILDREN = "com.sun.faces.facelets.REMOVED_CHILDREN";

    // Expando attribute used to mark dynamic UIComponents that have had their
    // ComponentSupport.MARK_CREATED expando removed.
    public static final String MARK_CREATED_REMOVED =  StateContext.class.getName() + "_MARK_CREATED_REMOVED";
    
    private final static String IMPLICIT_PANEL = "com.sun.faces.facelets.IMPLICIT_PANEL";
    
    /**
     * Key to a FacesContext scoped Map where the keys are UIComponent instances and the
     * values are Tag instances.
     */
    public static final String COMPONENT_TO_TAG_MAP_NAME = "com.sun.faces.facelets.COMPONENT_TO_LOCATION_MAP";
    
    public static boolean handlerIsResourceRelated(ComponentHandler handler){
      ComponentConfig config = handler.getComponentConfig();
      if ( !"javax.faces.Output".equals(config.getComponentType()) ) {
        return false;
      }
 
      String rendererType = config.getRendererType();
      return ("javax.faces.resource.Script".equals(rendererType) ||
                               "javax.faces.resource.Stylesheet".equals(rendererType));
    }
    
    public static boolean isBuildingNewComponentTree(FacesContext context){
      return !context.isPostback() || context.getCurrentPhaseId().equals(PhaseId.RESTORE_VIEW);
    }
 
    public static boolean isImplicitPanel(UIComponent component){
      return component.getAttributes().containsKey(IMPLICIT_PANEL);
    }
    
    /**
     * Used in conjunction with markForDeletion where any UIComponent marked
     * will be removed.
     * 
     * @param c
     *            UIComponent to finalize
     */
    public static void finalizeForDeletion(UIComponent c) {
        // remove any existing marks of deletion
        c.getAttributes().remove(MARK_DELETED);

        // finally remove any children marked as deleted
        int sz = c.getChildCount();
        if (sz > 0) {
            UIComponent cc = null;
            List cl = c.getChildren();
            while (--sz >= 0) {
                cc = (UIComponent) cl.get(sz);
                if (cc.getAttributes().containsKey(MARK_DELETED)) {
                    cl.remove(sz);
                }
            }
        }

        Map<String, UIComponent> facets = c.getFacets();
        // remove any facets marked as deleted
        if (facets.size() > 0) {
            Set<Entry<String, UIComponent>> col = facets.entrySet();
            UIComponent fc;
            Entry<String, UIComponent> curEntry;
            for (Iterator<Entry<String, UIComponent>> itr = col.iterator(); itr.hasNext();) {
                curEntry = itr.next();
                fc = curEntry.getValue();
                Map<String, Object> attrs = fc.getAttributes();
                if (attrs.containsKey(MARK_DELETED)) {
                    itr.remove();
               } else if (UIComponent.COMPOSITE_FACET_NAME.equals(curEntry.getKey()) ||
                          (attrs.containsKey(IMPLICIT_PANEL) &&
                           !curEntry.getKey().equals(UIViewRoot.METADATA_FACET_NAME))) {
                    List<UIComponent> implicitPanelChildren = fc.getChildren();
                    UIComponent innerChild;
                    for (Iterator<UIComponent> innerItr = implicitPanelChildren.iterator();
                         innerItr.hasNext();) {
                        innerChild = innerItr.next();
                        if (innerChild.getAttributes().containsKey(MARK_DELETED)) {
                            innerItr.remove();
                        }

                    }
                }
            }
        }
    }

    public static Tag setTagForComponent(FacesContext context, UIComponent c, Tag t) {
        Map<Object, Object> contextMap = context.getAttributes();
        Map<Integer, Tag> componentToTagMap;
        componentToTagMap = (Map<Integer, Tag>)
                contextMap.get(COMPONENT_TO_TAG_MAP_NAME);
        if (null == componentToTagMap) {
            componentToTagMap = new HashMap<>();
            contextMap.put(COMPONENT_TO_TAG_MAP_NAME, componentToTagMap);
        }
        return componentToTagMap.put((Integer) System.identityHashCode(c), t);
    }

    public static Tag getTagForComponent(FacesContext context, UIComponent c) {
        Tag result = null;
        Map<Object, Object> contextMap = context.getAttributes();
        Map<Integer, Tag> componentToTagMap;
        componentToTagMap = (Map<Integer, Tag>)
                contextMap.get(COMPONENT_TO_TAG_MAP_NAME);
        if (null != componentToTagMap) {
            result = componentToTagMap.get((Integer) System.identityHashCode(c));
        }

        return result;
    }
    

    /**
     * A lighter-weight version of UIComponent's findChild.
     * 
     * @param parent
     *            parent to start searching from
     * @param id
     *            to match to
     * @return UIComponent found or null
     */
    public static UIComponent findChild(UIComponent parent, String id) {
        int sz = parent.getChildCount();
        if (sz > 0) {
            UIComponent c = null;
            List cl = parent.getChildren();
            while (--sz >= 0) {
                c = (UIComponent) cl.get(sz);
                if (id.equals(c.getId())) {
                    return c;
                }
            }
        }
        return null;
    }
    
    // Obvious performance optimization.  First, assume this method
    // is only called from UIInstructionHandler.apply().  With that assumption
    // in place a few optimizations can be had on the cheap.
    
    // If this method is called on an initial page 
    // render it will always return null, so we can just return 
    // null in that case without any iteration.  
    
    // If this method is called during RestoreView, it will always return null
    // so we can just return null in that case without any iteration.  
    
    // If PartialStateSaving is false, the UIInstruction components will
    // never be in the tree at this point, so we can return null and skip iterating.
    
    public static UIComponent findUIInstructionChildByTagId(FacesContext context, UIComponent parent, String id) {
        UIComponent result = null;
        if (isBuildingNewComponentTree(context)) {
            return null;
        }
        Map<Object, Object> attrs = context.getAttributes();
        if (attrs.containsKey(PartialStateSaving)) {
            if ((Boolean)attrs.get(PartialStateSaving)) {
                result = findChildByTagId(context, parent, id);
            }
        }

        
        return result;
    }
    
    /**
     * By TagId, find Child
     * 
     * @param parent the parent UI component
     * @param id the id
     * @return the UI component
     */
    public static UIComponent findChildByTagId(FacesContext context, UIComponent parent, String id) {
        UIComponent c = null;
        UIViewRoot root = context.getViewRoot();
        boolean hasDynamicComponents = (null != root && 
                root.getAttributes().containsKey(RIConstants.TREE_HAS_DYNAMIC_COMPONENTS));
        String cid = null;
        List<UIComponent> components;
        String facetName = getFacetName(parent);
        if (null != facetName) {
            c = parent.getFacet(facetName);
            // We will have a facet name, but no corresponding facet in the
            // case of facets with composite components.  In this case,
            // we must do the brute force search.
            if (null != c) {
                cid = (String) c.getAttributes().get(MARK_CREATED);
                if (id.equals(cid)) {
                    return c;
                }
            } 
        }
        if (0 < parent.getFacetCount()) {
            components = new ArrayList<>();
            components.addAll(parent.getFacets().values());
            components.addAll(parent.getChildren());
        } else {
            components = parent.getChildren();
        }

        int len = components.size();
        for (int i = 0; i < len; i++) {
            c = components.get(i);
            cid = (String) c.getAttributes().get(MARK_CREATED);
            if (id.equals(cid)) {
                return c;
            }
            if (c instanceof UIPanel && c.getAttributes().containsKey(IMPLICIT_PANEL)) {
                for (UIComponent c2 : c.getChildren()) {
                    cid = (String) c2.getAttributes().get(MARK_CREATED);
                    if (id.equals(cid)) {
                        return c2;
                    }
                }
            }
            if (hasDynamicComponents) {
                /*
                 * Make sure we look for the child recursively it might have moved
                 * into a different parent in the parent hierarchy. Note currently
                 * we are only looking down the tree. Maybe it would be better
                 * to use the VisitTree API instead.
                 */
                UIComponent foundChild = findChildByTagId(context, c, id);
                if (foundChild != null) {
                    return foundChild;
                }
            }
        }

        return null;
    }
    
    /**
     * According to JSF 1.2 tag specs, this helper method will use the
     * TagAttribute passed in determining the Locale intended.
     * 
     * @param ctx
     *            FaceletContext to evaluate from
     * @param attr
     *            TagAttribute representing a Locale
     * @return Locale found
     * @throws TagAttributeException
     *             if the Locale cannot be determined
     */
    public static Locale getLocale(FaceletContext ctx, TagAttribute attr)
            throws TagAttributeException {
        Object obj = attr.getObject(ctx);
        if (obj instanceof Locale) {
            return (Locale) obj;
        }
        if (obj instanceof String) {
            String s = (String) obj;
            try {
                return Util.getLocaleFromString(s);
            }
            catch(IllegalArgumentException iae) {
                throw new TagAttributeException(attr, "Invalid Locale Specified: " + s);
            }
        } else {
            throw new TagAttributeException(attr,
                    "Attribute did not evaluate to a String or Locale: " + obj);
        }
    }

    /**
     * Tries to walk up the parent to find the UIViewRoot, if not found, then go
     * to FaceletContext's FacesContext for the view root.
     * 
     * @param ctx
     *            FaceletContext
     * @param parent
     *            UIComponent to search from
     * @return UIViewRoot instance for this evaluation
     */
    public static UIViewRoot getViewRoot(FaceletContext ctx,
            UIComponent parent) {
        UIComponent c = parent;
        do {
            if (c instanceof UIViewRoot) {
                return (UIViewRoot) c;
            } else {
                c = c.getParent();
            }
        } while (c != null);
        return ctx.getFacesContext().getViewRoot();
    }

    /**
     * Marks all direct children and Facets with an attribute for deletion.
     * 
     * @see #finalizeForDeletion(UIComponent)
     * @param c
     *            UIComponent to mark
     */
    public static void markForDeletion(UIComponent c) {
        // flag this component as deleted
        c.getAttributes().put(MARK_DELETED, Boolean.TRUE);

        // mark all children to be deleted
        int sz = c.getChildCount();
        if (sz > 0) {
            UIComponent cc = null;
            List cl = c.getChildren();
            while (--sz >= 0) {
                cc = (UIComponent) cl.get(sz);
                if (cc.getAttributes().containsKey(MARK_CREATED)) {
                    cc.getAttributes().put(MARK_DELETED, Boolean.TRUE);
                }
            }
        }

        // mark all facets to be deleted
        if (c.getFacets().size() > 0) {
            Set col = c.getFacets().entrySet();
            UIComponent fc;
            for (Iterator itr = col.iterator(); itr.hasNext();) {
               Map.Entry entry = (Map.Entry) itr.next();
               String facet = (String) entry.getKey();
                fc = (UIComponent) entry.getValue();
                Map<String, Object> attrs = fc.getAttributes();
                if (attrs.containsKey(MARK_CREATED)) {
                    attrs.put(MARK_DELETED, Boolean.TRUE);
                } else if (UIComponent.COMPOSITE_FACET_NAME.equals(facet)) {
                   // mark the inner pannel components to be deleted
                   sz = fc.getChildCount();
                    if (sz > 0) {
                        UIComponent cc = null;
                        List cl = fc.getChildren();
                        while (--sz >= 0) {
                            cc = (UIComponent) cl.get(sz);
                            cc.getAttributes().put(MARK_DELETED, Boolean.TRUE);
                        }
                    }
               } else if (attrs.containsKey(IMPLICIT_PANEL)) {
                    List<UIComponent> implicitPanelChildren = fc.getChildren();
                    Map<String, Object> innerAttrs = null;
                    for (UIComponent cur : implicitPanelChildren) {
                        innerAttrs = cur.getAttributes();
                        if (innerAttrs.containsKey(MARK_CREATED)) {
                            innerAttrs.put(MARK_DELETED, Boolean.TRUE);
                        }
                    }
                }
            }
        }
    }
    
    public static void encodeRecursive(FacesContext context,
            UIComponent viewToRender) throws IOException, FacesException {
        if (viewToRender.isRendered()) {
            viewToRender.encodeBegin(context);
            if (viewToRender.getRendersChildren()) {
                viewToRender.encodeChildren(context);
            } else if (viewToRender.getChildCount() > 0) {
                Iterator kids = viewToRender.getChildren().iterator();
                while (kids.hasNext()) {
                    UIComponent kid = (UIComponent) kids.next();
                    encodeRecursive(context, kid);
                }
            }
            viewToRender.encodeEnd(context);
        }
    }
    
    public static void removeTransient(UIComponent c) {
        UIComponent d, e;
        if (c.getChildCount() > 0) {
            for (Iterator itr = c.getChildren().iterator(); itr.hasNext();) {
                d = (UIComponent) itr.next();
                if (d.getFacets().size() > 0) {
                    for (Iterator jtr = d.getFacets().values().iterator(); jtr
                            .hasNext();) {
                        e = (UIComponent) jtr.next();
                        if (e.isTransient()) {
                            jtr.remove();
                        } else {
                            removeTransient(e);
                        }
                    }
                }
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    removeTransient(d);
                }
            }
        }
        if (c.getFacets().size() > 0) {
            for (Iterator itr = c.getFacets().values().iterator(); itr
                    .hasNext();) {
                d = (UIComponent) itr.next();
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    removeTransient(d);
                }
            }
        }
    }

    /**
     * <p class="changed_added_2_0">Add the child component to the parent. If the parent is a facet,
     * check to see whether the facet is already defined. If it is, wrap the existing component
     * in a panel group, if it's not already, then add the child to the panel group. If the facet
     * does not yet exist, make the child the facet.</p>
     */
    public static void addComponent(FaceletContext ctx, UIComponent parent, UIComponent child) {

        String facetName = getFacetName(parent);
        if (facetName == null) {
            if (child.getAttributes().containsKey(RIConstants.DYNAMIC_COMPONENT)) {
                int childIndex = (Integer) child.getAttributes().get(RIConstants.DYNAMIC_COMPONENT);
                if (childIndex >= parent.getChildCount() || childIndex == -1) {
                    parent.getChildren().add(child);
                } else {
                    parent.getChildren().add(childIndex, child);
                }                
            } else {
                parent.getChildren().add(child);
            }
        } else {
            UIComponent existing = parent.getFacets().get(facetName);
            if (existing != null && existing != child) {
                if (existing.getAttributes().get(ComponentSupport.IMPLICIT_PANEL) == null) {
                    // move existing component under a panel group
                    UIComponent panelGroup = ctx.getFacesContext().getApplication().createComponent(UIPanel.COMPONENT_TYPE);
                    parent.getFacets().put(facetName, panelGroup);
                    Map<String, Object> attrs = panelGroup.getAttributes();
                    attrs.put(ComponentSupport.IMPLICIT_PANEL, true);
                    panelGroup.getChildren().add(existing);
                    existing = panelGroup;
                }
                if (existing.getAttributes().get(ComponentSupport.IMPLICIT_PANEL) != null) {
                    // we have a panel group, so add the new component to it
                    existing.getChildren().add(child);
                } else {
                    parent.getFacets().put(facetName, child);
                }
            } else {
                parent.getFacets().put(facetName, child);
            }
        }
    }

    public static String getFacetName(UIComponent parent) {
        return (String) parent.getAttributes().get(FacetHandler.KEY);
    }

    public static boolean suppressViewModificationEvents(FacesContext ctx) {

        // NO UIViewRoot means this was called during restore view -
        // no need to suppress events at that time
        UIViewRoot root = ctx.getViewRoot();
        if (root != null) {
            String viewId = root.getViewId();
            if (viewId != null) {
                StateContext stateCtx = StateContext.getStateContext(ctx);
                return stateCtx.isPartialStateSaving(ctx, viewId);
            }
        }
        return false;

    }
    
    public static void copyPassthroughAttributes(FaceletContext ctx, UIComponent c, Tag t) {
        
        if (null == c || null == t) {
            return;
        }
        
        TagAttribute[] passthroughAttrs = t.getAttributes().getAll(PassThroughAttributeLibrary.Namespace);
        if (null != passthroughAttrs && 0 < passthroughAttrs.length) {
            Map<String, Object> componentPassthroughAttrs = c.getPassThroughAttributes(true);
            Object attrValue = null;
            for (TagAttribute cur : passthroughAttrs) {
                attrValue = (cur.isLiteral()) ? cur.getValue(ctx) : cur.getValueExpression(ctx, Object.class);
                componentPassthroughAttrs.put(cur.getLocalName(), attrValue);
            }
        }
    }

    // --------------------------------------------------------- private classes


//    private static UIViewRoot getViewRoot(FacesContext ctx, UIComponent parent) {
//
//        if (parent instanceof UIViewRoot) {
//            return (UIViewRoot) parent;
//        }
//        UIViewRoot root = ctx.getViewRoot();
//        if (root != null) {
//            return root;
//        }
//        UIComponent c = parent.getParent();
//        while (c != null) {
//            if (c instanceof UIViewRoot) {
//                root = (UIViewRoot) c;
//                break;
//            } else {
//                c = c.getParent();
//            }
//        }
//
//        return root;
//
//    }
}
