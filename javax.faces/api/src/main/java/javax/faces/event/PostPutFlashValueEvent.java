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

package javax.faces.event;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

/**
 *
 * <p class="changed_added_2_2">This event must be published by a call
 * to {@link javax.faces.application.Application#publishEvent} when a value
 * is stored in the flash.</p>
 *
 * @since 2.2
 */
public class PostPutFlashValueEvent extends SystemEvent {

    private static final long serialVersionUID = -6422204761759384353L;;

    // ------------------------------------------------------------ Constructors


    /**
     * <p class="changed_added_2_2">Instantiate a new
     * <code>PostPutFlashValueEvent</code> that indicates the argument
     * <code>key</code> was just put to the flash. If the argument is 
     * <code>null</code>, the literal {@link Flash#NULL_VALUE} must be passed
     * to the superclass constructor.</p>

     * @param key the key in the flash that was just added.
     *
     */
    public PostPutFlashValueEvent(String key) {
        super(null == key ? Flash.NULL_VALUE : key);
    }
    
    /**
     * <p class="changed_added_2_2">Instantiate a new
     * <code>PostPutFlashValueEvent</code> that indicates the argument
     * <code>key</code> was just put to the flash. If the argument is 
     * <code>null</code>, the literal {@link Flash#NULL_VALUE} must be passed
     * to the superclass constructor.</p>
     * 
     * @param facesContext the Faces context.
     * @param key the key in the flash that was just added.
     *
     */
    public PostPutFlashValueEvent(FacesContext facesContext, String key) {
        super(facesContext, key);
    }
    
    public String getKey() {
        return getSource().toString();
    }

}
