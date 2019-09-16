/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2016 Oracle and/or its affiliates. All rights reserved.
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

// FormRenderer.java

package com.sun.faces.renderkit.html_basic;

import static com.sun.faces.renderkit.RenderKitUtils.getParameterName;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;
import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;

/** <B>FormRenderer</B> is a class that renders a <code>UIForm<code> as a Form. */

public class FormRenderer extends HtmlBasicRenderer {

    private static final Attribute[] ATTRIBUTES =
          AttributeManager.getAttributes(AttributeManager.Key.FORMFORM);

    private boolean writeStateAtEnd;

    // ------------------------------------------------------------ Constructors


    public FormRenderer() {
        WebConfiguration webConfig = WebConfiguration.getInstance();
        writeStateAtEnd =
             webConfig.isOptionEnabled(
                  BooleanWebContextInitParameter.WriteStateAtFormEnd);
    }

    // ---------------------------------------------------------- Public Methods


    @Override
    public void decode(FacesContext context, UIComponent component) {

        rendererParamsNotNull(context, component);

        String clientId = decodeBehaviors(context, component);

        if (clientId == null) {
            clientId = component.getClientId(context);
        }
                
        // Was our form the one that was submitted?  If so, we need to set
        // the indicator accordingly..
        Map<String, String> requestParameterMap = context.getExternalContext()
              .getRequestParameterMap();
        if (requestParameterMap.containsKey(clientId)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                           "UIForm with client ID {0}, submitted",
                           clientId);
            }
            ((UIForm) component).setSubmitted(true);
        } else {
            ((UIForm) component).setSubmitted(false);
        }

    }


    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
          throws IOException {

        rendererParamsNotNull(context, component);

        if (!shouldEncode(component)) {
            return;
        }

        ResponseWriter writer = context.getResponseWriter();
        assert(writer != null);
        String clientId = component.getClientId(context);
        // since method and action are rendered here they are not added
        // to the pass through attributes in Util class.
        writer.write('\n');
        writer.startElement("form", component);
        writer.writeAttribute("id", clientId, "clientId");
        writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("method", "post", null);
        writer.writeAttribute("action", getActionStr(context), null);
        String styleClass =
              (String) component.getAttributes().get("styleClass");
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        String acceptcharset = (String)
              component.getAttributes().get("acceptcharset");
        if (acceptcharset != null) {
            writer.writeAttribute("accept-charset", acceptcharset,
                                  "acceptcharset");
        }

        RenderKitUtils.renderPassThruAttributes(context,
                                                writer,
                                                component,
                                                ATTRIBUTES);
        writer.writeText("\n", component, null);

        // this hidden field will be checked in the decode method to
        // determine if this form has been submitted.         
        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", "type");
        writer.writeAttribute("name", clientId,
                              "clientId");
        writer.writeAttribute("value", clientId, "value");
        writer.endElement("input");
        writer.write('\n');
        
        UIViewRoot viewRoot = context.getViewRoot();

        // Write out special hhidden field for partial submits
        String viewId = viewRoot.getViewId();
        String actionURL =
            context.getApplication().getViewHandler().getActionURL(context, viewId);
        ExternalContext externalContext = context.getExternalContext();
        String encodedActionURL = externalContext.encodeActionURL(actionURL);
        String encodedPartialActionURL = externalContext.encodePartialActionURL(actionURL);
        if (encodedPartialActionURL != null && 
            (!encodedPartialActionURL.equals(encodedActionURL))) {
            writer.startElement("input", null);
            writer.writeAttribute("type", "hidden", "type");
            writer.writeAttribute("name", getParameterName(context, "javax.faces.encodedURL"), null);
            writer.writeAttribute("value", encodedPartialActionURL, "value");
            writer.endElement("input");
            writer.write('\n');
        }

        if (!writeStateAtEnd) {
            context.getApplication().getViewHandler().writeState(context);
            writer.write('\n');
        }
    }


    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
          throws IOException {

        rendererParamsNotNull(context, component);
        if (!shouldEncode(component)) {
            return;
        }

        // Render the end tag for form
        ResponseWriter writer = context.getResponseWriter();
        assert(writer != null);
        
        // Render ay resources that have been targeted for this form.

        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator iter = (viewRoot.getComponentResources(context, "form")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent)iter.next();
            resource.encodeAll(context);
        }
        
        // Render the end tag for form
        if (writeStateAtEnd) {
            context.getApplication().getViewHandler().writeState(context);
        }
        
        writer.writeText("\n", component, null);
        writer.endElement("form");

    }

    // --------------------------------------------------------- Private Methods


    /**
     * @param context FacesContext for the response we are creating
     *
     * @return Return the value to be rendered as the <code>action</code> attribute
     *  of the form generated for this component.
     */
    private static String getActionStr(FacesContext context) {

        String viewId = context.getViewRoot().getViewId();
        String actionURL =
              context.getApplication().getViewHandler().
                    getActionURL(context, viewId);
        return (context.getExternalContext().encodeActionURL(actionURL));

    }

} // end of class FormRenderer
