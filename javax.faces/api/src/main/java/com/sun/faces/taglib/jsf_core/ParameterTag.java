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

// ParameterTag.java

package com.sun.faces.taglib.jsf_core;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.webapp.UIComponentELTag;

public class ParameterTag extends UIComponentELTag {

//
// Protected Constants
//

//
// Class Variables
//

//
// Instance Variables
//
    private ValueExpression name;
    private ValueExpression value;

// Attribute Instance Variables


// Relationship Instance Variables

//
// Constructors and Initializers
//

    public ParameterTag() {
        super();
    }

//
// Class methods
//

//
// Accessors
//
    public void setName(ValueExpression name) {
        this.name = name;
    }


    public void setValue(ValueExpression value) {
        this.value = value;
    }

//
// General Methods
//

    @Override
    public String getRendererType() {
        return null;
    }


    @Override
    public String getComponentType() {
        return "javax.faces.Parameter";
    }


    @Override
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        UIParameter parameter = (UIParameter) component;

        if (name != null) {
            if (!name.isLiteralText()) {
                parameter.setValueExpression("name", name);
            } else {
                parameter.setName(name.getExpressionString());
            }
        }
        // if component has non null value, do not call setValue().
        if (value != null) {
            if (!value.isLiteralText()) {
                component.setValueExpression("value", value);
            } else {
                parameter.setValue(value.getExpressionString());
            }
        }
    }
}
