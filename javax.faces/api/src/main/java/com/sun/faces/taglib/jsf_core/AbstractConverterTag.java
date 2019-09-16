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

import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.FacesLogger;

import javax.faces.webapp.ConverterELTag;
import javax.faces.convert.Converter;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.servlet.jsp.JspException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Base class for all <code>ConverterTag<code>s.</p>
 */
public class AbstractConverterTag extends ConverterELTag {

    private static final long serialVersionUID = -8219789624438804540L;

    private static final Logger LOGGER = FacesLogger.TAGLIB.getLogger();

    /**
     * <p>The {@link javax.el.ValueExpression} that evaluates to an object that
     * implements {@link javax.faces.convert.Converter}.</p>
     */
    protected ValueExpression binding = null;


    /**
     * <p>The identifier of the {@link javax.faces.convert.Converter}
     * instance to be created.</p>
     */
    protected ValueExpression converterId = null;

    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Set the expression that will be used to create a
     * {@link javax.el.ValueExpression} that references a backing bean property
     * of the {@link javax.faces.convert.Converter} instance to be created.</p>
     *
     * @param binding The new expression
     */
    public void setBinding(ValueExpression binding) {

        this.binding = binding;

    }


    /**
     * <p>Set the identifer of the {@link javax.faces.convert.Converter}
     * instance to be created.
     *
     * @param converterId The identifier of the converter instance to be
     *                    created.
     */
    public void setConverterId(ValueExpression converterId) {

        this.converterId = converterId;

    }

    // --------------------------------------------- Methods from ConverterELTag


    @Override
    protected Converter createConverter() throws JspException {

        try {
            return createConverter(converterId,
                                   binding,
                                   FacesContext.getCurrentInstance());
        } catch (FacesException fe) {
            throw new JspException(fe.getCause());
        }

    }


    protected static Converter createConverter(ValueExpression converterId,
                                               ValueExpression binding,
                                               FacesContext facesContext) {

        ELContext elContext = facesContext.getELContext();
        Converter converter = null;

        // If "binding" is set, use it to create a converter instance.
        if (binding != null) {
            try {
                converter = (Converter) binding.getValue(elContext);
                if (converter != null) {
                    return converter;
                }
            } catch (Exception e) {
                throw new FacesException(e);
            }
        }

        // If "converterId" is set, use it to create the converter
        // instance.  If "converterId" and "binding" are both set, store the
        // converter instance in the value of the property represented by
        // the ValueExpression 'binding'.
        if (converterId != null) {
            try {
                String converterIdVal = (String)
                     converterId.getValue(elContext);
                converter = facesContext.getApplication()
                     .createConverter(converterIdVal);
                if (converter != null && binding != null) {
                    binding.setValue(elContext, converter);
                }
            } catch (Exception e) {
                throw new FacesException(e);
            }
        }

        if (converter == null) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING,
                     MessageUtils.getExceptionMessageString(
                          MessageUtils.CANNOT_CONVERT_ID,
                          converterId != null ? converterId.getExpressionString() : "",
                          binding != null ? binding.getExpressionString() : ""));
            }
        }

        return converter;

    }

}
