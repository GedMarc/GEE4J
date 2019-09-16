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

// HtmlBasicInputRenderer.java

package com.sun.faces.renderkit.html_basic;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.sun.faces.util.MessageFactory;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.RequestStateManager;

/**
 * <B>HtmlBasicInputRenderer</B> is a base class for implementing renderers
 * that support UIInput type components
 */

public abstract class HtmlBasicInputRenderer extends HtmlBasicRenderer {


    private boolean hasStringConverter = false;

    private boolean hasStringConverterSet = false;

    // ---------------------------------------------------------- Public Methods


    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component,
                                    Object submittedValue)
          throws ConverterException {

        String newValue = (String) submittedValue;
        // if we have no local value, try to get the valueExpression.
        ValueExpression valueExpression = component.getValueExpression("value");
        Converter converter = null;

        // If there is a converter attribute, use it to to ask application
        // instance for a converter with this identifer.
        if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        if (null == converter && null != valueExpression) {
            Class converterType = valueExpression.getType(context.getELContext());
            // if converterType is null, assume the modelType is "String".
            if (converterType == null ||
                converterType == Object.class) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                               "No conversion necessary for value {0} of component {1}",
                               new Object[]{
                                     submittedValue,
                                     component.getId() });
                }
                return newValue;
            }

            // If the converterType is a String, and we don't have a 
            // converter-for-class for java.lang.String, assume the type is 
            // "String".
            if (converterType == String.class && !hasStringConverter(context)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                               "No conversion necessary for value {0} of component {1}",
                               new Object[]{
                                     submittedValue,
                                     component.getId()});
                }
                return newValue;
            }
            // if getType returns a type for which we support a default
            // conversion, acquire an appropriate converter instance.

            try {
                Application application = context.getApplication();
                converter = application.createConverter(converterType);
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE,
                               "Created converter ({0}) for type {1} for component {2}.",
                               new Object[] {
                                     converter.getClass().getName(),
                                     converterType.getClass().getName(),
                                     component.getId() });
                }
            } catch (Exception e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE,
                               "Could not instantiate converter for type {0}: {1}",
                               new Object[] {
                                     converterType,
                                     e.toString() });
                    logger.log(Level.SEVERE, "", e);
                }
                return (null);
            }
        } else if (converter == null) {
            // if there is no valueExpression and converter attribute set,
            // assume the modelType as "String" since we have no way of
            // figuring out the type. So for the selectOne and
            // selectMany, converter has to be set if there is no
            // valueExpression attribute set on the component.
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                            "No conversion necessary for value {0} of component {1}",
                            new Object[] {
                                  submittedValue,
                                  component.getId() });
                logger.fine(" since there is no explicitly registered converter "
                            + "and the component value is not bound to a model property");
            }
            return newValue;
        }

        if (converter != null) {
            // If the conversion eventually falls to needing to use EL type coercion,
            // make sure our special ConverterPropertyEditor knows about this value.
            RequestStateManager.set(context,
                                    RequestStateManager.TARGET_COMPONENT_ATTRIBUTE_NAME,
                                    component);
            return converter.getAsObject(context, component, newValue);
        } else {
            // throw converter exception.
            Object[] params = {
                  newValue,
                  "null Converter"
            };

            throw new ConverterException(MessageFactory.getMessage(
                  context, MessageUtils.CONVERSION_ERROR_MESSAGE_ID, params));
        }

    }


    @Override
    public void setSubmittedValue(UIComponent component, Object value) {

        if (component instanceof UIInput) {
            ((UIInput) component).setSubmittedValue(value);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Set submitted value " + value + " on component ");
            }
        }

    }

    // ------------------------------------------------------- Protected Methods


    @Override
    protected Object getValue(UIComponent component) {

        if (component instanceof ValueHolder) {
            Object value = ((ValueHolder) component).getValue();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("component.getValue() returned " + value);
            }
            return value;
        }

        return null;

    }

    // Returns the Behaviors map, but only if it contains some entry other
    // than those handled by renderOnchange().  This helps us optimize
    // renderPassThruAttributes() in the very common case where the
    // button only contains an "valueChange" (or "change") Behavior.  In that
    // we pass a null Behaviors map into renderPassThruAttributes(),
    // which allows us to take a more optimized code path.
    protected static Map<String, List<ClientBehavior>> getNonOnChangeBehaviors(UIComponent component) {
        return getPassThruBehaviors(component, "change", "valueChange");
    }

    // Returns the Behaviors map, but only if it contains some entry other
    // than those handled by renderOnchange().  This helps us optimize
    // renderPassThruAttributes() in the very common case where the
    // button only contains an "valueChange" (or "change") Behavior.  In that
    // we pass a null Behaviors map into renderPassThruAttributes(),
    // which allows us to take a more optimized code path.
    protected static Map<String, List<ClientBehavior>> getNonOnClickSelectBehaviors(UIComponent component) {
        return getPassThruBehaviors(component, "click", "valueChange");
    }

    // --------------------------------------------------------- Private Methods

    private boolean hasStringConverter(FacesContext context) {

        if (!hasStringConverterSet) {
            hasStringConverter = (null !=
                                  context.getApplication()
                                        .createConverter(String.class));
            hasStringConverterSet = true;
        }
        return hasStringConverter;

    }

} // end of class HtmlBasicInputRenderer
