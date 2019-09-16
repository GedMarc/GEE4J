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

package com.sun.faces.context;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletRequest;

import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Util;

/**
 * This {@link FacesContextFactory} is responsible for injecting the
 * default {@link FacesContext} instance into the top-level {@link FacesContext}
 * as configured by the runtime.  Doing this allows us to preserve backwards
 * compatibility as the API evolves without having the API rely on implementation
 * specific details.  
 */
public class InjectionFacesContextFactory extends FacesContextFactory {

    private static final Logger LOGGER = FacesLogger.CONTEXT.getLogger();
    private Field defaultFacesContext;
    private Field defaultExternalContext;



    // ------------------------------------------------------------ Constructors


    public InjectionFacesContextFactory(FacesContextFactory delegate) {
        super(delegate);

        Util.notNull("facesContextFactory", delegate);

         try {
            defaultFacesContext = FacesContext.class.getDeclaredField("defaultFacesContext");
            defaultFacesContext.setAccessible(true);
        } catch (NoSuchFieldException nsfe) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Unable to find private field named 'defaultFacesContext' in javax.faces.context.FacesContext.");
            }
        } catch (SecurityException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            defaultFacesContext = null;
        }
        try {
            defaultExternalContext = ExternalContext.class.getDeclaredField("defaultExternalContext");
            defaultExternalContext.setAccessible(true);
        } catch (NoSuchFieldException nsfe) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Unable to find private field named 'defaultExternalContext' in javax.faces.context.ExternalContext.");
            }
        } catch (SecurityException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            defaultExternalContext = null;
        }

    }


    // ---------------------------------------- Methods from FacesContextFactory


    @Override
    public FacesContext getFacesContext(Object context,
                                        Object request,
                                        Object response,
                                        Lifecycle lifecycle)
    throws FacesException {

        FacesContext ctx = getWrapped().getFacesContext(context,
                                                    request,
                                                    response,
                                                    lifecycle);
        if (ctx == null) {
            // No i18n here
            String message = MessageFormat
                  .format("Delegate FacesContextFactory, {0}, returned null when calling getFacesContext().",
                          getWrapped().getClass().getName());
            throw new IllegalStateException(message);
        }
        injectDefaults(ctx, request);
        return ctx;

    }


    // --------------------------------------------------------- Private Methods


    private void injectDefaults(FacesContext target, Object request) {

        if (defaultFacesContext != null) {
            FacesContext defaultFC =
                  FacesContextImpl.getDefaultFacesContext();
            if (defaultFC != null) {
                try {
                    defaultFacesContext.set(target, defaultFC);
                } catch (IllegalAccessException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                }
            }
        }
        if (defaultExternalContext != null) {
            ExternalContext defaultExtContext = null;
            if (request instanceof ServletRequest) {
                ServletRequest reqObj = (ServletRequest) request;
                defaultExtContext = (ExternalContext) reqObj.getAttribute(ExternalContextFactoryImpl.DEFAULT_EXTERNAL_CONTEXT_KEY);
                if (defaultExtContext != null) {
                    reqObj.removeAttribute(ExternalContextFactoryImpl.DEFAULT_EXTERNAL_CONTEXT_KEY);
                }
            }
            if (defaultExtContext != null) {
                try {
                    defaultExternalContext.set(target.getExternalContext(), defaultExtContext);
                } catch (IllegalAccessException e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                }
            }
        }

    }

}
