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

package javax.faces.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.component.UIComponent;
import javax.faces.component.UIImportConstants;
import javax.faces.component.UIViewAction;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * <p class="changed_added_2_0"> <code>ViewMetadata</code> is
 * reponsible for extracting and providing view parameter metadata from
 * VDL views.  Because {@link ViewDeclarationLanguage#getViewMetadata}
 * is required to return <code>null</code> for JSP views and
 * non-<code>null</code> for views authored in Facelets for JSF 2, this
 * specification only applys to Facelets for JSF 2.  </p>
 *
 * @since 2.0
 */
public abstract class ViewMetadata {


    /**
     * <p class="changed_added_2_0">Get the view id</p>
     * 
     * @return the view ID for which this <code>ViewMetadata</code> instance
     *  was created
     */
    public abstract String getViewId();


    /**
     * <p class="changed_added_2_0"> Creates a new {@link UIViewRoot}
     * containing only view parameter metadata.  The processing of
     * building this <code>UIViewRoot</code> with metadata should not
     * cause any events to be published to the application.  The
     * implementation must call {@link FacesContext#setProcessingEvents}
     * passing <code>false</code> as the argument, at the beginning of
     * the method, and pass <code>true</code> to the same method at the
     * end.  The implementation must ensure that this happens regardless
     * of ant exceptions that may be thrown.</p>
     * 
     * <p class="changed_modified_2_3">
     *  Take note a compliant implementation has to ensure that:
     * </p>
     * <ul>
     *  <li>
     *   the new UIViewRoot must be set as the FacesContext's viewRoot 
     *   before applying the tag handlers, restoring the old FacesContext 
     *   in a finally block.
     *  </li>
     *  <li>
     *   The contents of the current UIViewRoot's ViewMap must be copied 
     *   to the ViewMap of the new UIViewRoot before applying the tag handlers.
     *  </li>
     *  <li class="changed_added_2_3">
     *   The {@link UIImportConstants} must be processed after applying the tag handlers.
     *  </li>
     * </ul>
     *
     * @param context the {@link FacesContext} for the current request
     * @return a <code>UIViewRoot</code> containing only view parameter metadata
     *  (if any)
     */
    public abstract UIViewRoot createMetadataView(FacesContext context);


    /**
     * <p class="changed_added_2_0"> Utility method to extract view
     * metadata from the provided {@link UIViewRoot}.  </p>
     *
     * @param root the {@link UIViewRoot} from which the metadata will
     * be extracted.
     *
     * @return a <code>Collection</code> of {@link UIViewParameter}
     * instances.  If the view has no metadata, the collection will be
     * empty.
     */
    public static Collection<UIViewParameter> getViewParameters(UIViewRoot root) {
        return getMetadataChildren(root, UIViewParameter.class);
    }

    /**
     * <p class="changed_added_2_2"> Utility method to extract view
     * metadata from the provided {@link UIViewRoot}.  </p>
     *
     * @param root the {@link UIViewRoot} from which the metadata will
     * be extracted.
     *
     * @return a <code>Collection</code> of {@link UIViewAction}
     * instances.  If the view has no metadata, the collection will be
     * empty.
     */
    public static Collection<UIViewAction> getViewActions(UIViewRoot root) {
        return getMetadataChildren(root, UIViewAction.class);
    }

    /**
     * <p class="changed_added_2_3">Utility method to extract view metadata from the provided {@link UIViewRoot}.</p>
     *
     * @param root The {@link UIViewRoot} from which the metadata will be extracted.
     *
     * @return A <code>Collection</code> of {@link UIImportConstants} instances.
     * If the view has no metadata, the collection will be empty.
     */
    public static Collection<UIImportConstants> getImportConstants(UIViewRoot root) {
        return getMetadataChildren(root, UIImportConstants.class);
    }

    /**
     * <p class="changed_added_2_2">Utility method to determine if the 
     * the provided {@link UIViewRoot} has metadata.  The default implementation will 
     * return true if the provided {@code UIViewRoot} has a facet 
     * named {@link UIViewRoot#METADATA_FACET_NAME} and that facet has children.
     * It will return  false otherwise.</p>
     *
     * @param root the {@link UIViewRoot} from which the metadata will
     * be extracted from
     *
     * @return true if the view has metadata, false otherwise.
     */
    public static boolean hasMetadata(UIViewRoot root) {    
        return getMetadataFacet(root).map(m -> m.getChildCount() > 0).orElse(false);
    }

    @SuppressWarnings("unchecked")
    private static <C extends UIComponent> List<C> getMetadataChildren(UIViewRoot root, Class<C> type) {
        return (List<C>) getMetadataFacet(root).map(m -> m.getChildren()).orElseGet(Collections::emptyList)
                                               .stream().filter(c -> type.isInstance(c)).collect(Collectors.toList());
    }

    private static Optional<UIComponent> getMetadataFacet(UIViewRoot root) {
        return Optional.ofNullable(root.getFacet(UIViewRoot.METADATA_FACET_NAME));
    }

}