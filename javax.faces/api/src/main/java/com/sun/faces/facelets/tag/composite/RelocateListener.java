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

package com.sun.faces.facelets.tag.composite;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.context.FacesContext;
import javax.faces.view.Location;
import javax.faces.application.Resource;

/**
 * Base class for listeners used to relocate children and facets within the context
 * of composite components.
 */
abstract class RelocateListener implements ComponentSystemEventListener, StateHolder {


    // ------------------------------------------------ Methods from StateHolder


    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return null;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        // no-op
    }


    // ----------------------------------------------------- Private Methods



    /**
     * @return the <code>Resource</code> instance that was used to create
     *         the argument composite component.
     */
    protected Resource getBackingResource(UIComponent component) {

        assert (UIComponent.isCompositeComponent(component));
        Resource resource = (Resource) component.getAttributes()
              .get(Resource.COMPONENT_RESOURCE_KEY);
        if (resource == null) {
            throw new IllegalStateException(
                  "Backing resource information not found in composite component attribute map");
        }
        return resource;

    }


    /**
     * @return <code>true</code> if the argument handler is from the same
     *         template source as the argument <code>Resource</code> otherwise
     *         <code>false</code>
     */
    protected boolean resourcesMatch(Resource compositeResource,
                                     Location handlerLocation) {

        String resName = compositeResource.getResourceName();
        return (handlerLocation.getPath().contains(resName));

    }

}
