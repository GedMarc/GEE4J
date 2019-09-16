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

import javax.faces.validator.RegexValidator;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.el.ValueExpression;
import javax.el.ExpressionFactory;
import javax.servlet.jsp.JspException;


/**
 * Tag for the Regular Expression Validator.  Can accept a regex pattern as a
 * property - this will be used to validate against.
 * @author driscoll
 */
public class RegexValidatorTag extends AbstractValidatorTag {

    private static final long serialVersionUID = 5353063400995625645L;
    private ValueExpression regex;
    private ValueExpression VALIDATOR_ID_EXPR;

    // ------------------------------------------------------------ Constructors


    public RegexValidatorTag() {
        if (VALIDATOR_ID_EXPR == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExpressionFactory factory =
                context.getApplication().getExpressionFactory();
            VALIDATOR_ID_EXPR =
                factory.createValueExpression(context.getELContext(),
                    "javax.faces.RegularExpression",String.class);
        }
    }

    /**
     * Set the Regular Expression to use for validation.
     * @param pattern A regular expression - needs to be escaped, @see java.util.regex .
     */
    public void setPattern(ValueExpression pattern) {
        this.regex = pattern;
    }

    @Override
    protected Validator createValidator() throws JspException {
        super.setValidatorId(VALIDATOR_ID_EXPR);
        RegexValidator validator = (RegexValidator) super.createValidator();
        assert (validator != null);
        if (regex != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            validator.setPattern((String) regex.getValue(ctx.getELContext()));
        }
        return validator;
        
    }
}
