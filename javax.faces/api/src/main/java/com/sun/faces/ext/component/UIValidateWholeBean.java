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
package com.sun.faces.ext.component;

import static com.sun.faces.util.Util.reverse;
import static java.lang.Boolean.TRUE;
import static javax.faces.validator.BeanValidator.EMPTY_VALIDATION_GROUPS_PATTERN;
import static javax.faces.validator.BeanValidator.ENABLE_VALIDATE_WHOLE_BEAN_PARAM_NAME;
import static javax.faces.validator.BeanValidator.VALIDATION_GROUPS_DELIMITER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.validation.groups.Default;

public class UIValidateWholeBean extends UIInput implements PartialStateHolder {

    private static final String ERROR_MISSING_FORM
            = "f:validateWholeBean must be nested directly in an UIForm.";

    private static final String ERROR_MISPLACED_COMPONENT
            = "f:validateWholeBean must be placed at the end of UIForm.";

    public static final String FAMILY = "com.sun.faces.ext.validateWholeBean";

    private transient Class<?>[] cachedValidationGroups;
    private transient String validationGroups = "";
    
    private boolean transientValue;
    private boolean initialState;

    private enum PropertyKeys {
        ValidatorInstalled
    }

    @Override
    public String getFamily() {
        return FAMILY;
    }

    @Override
    public Object getSubmittedValue() {
        return getFamily();
    }

    @Override
    public void setConverter(Converter converter) {
    }

    @Override
    public final void addValidator(Validator validator) {
        if (validator instanceof WholeBeanValidator) {
            super.addValidator(validator);
            setValidatorInstalled(true);
        }
    }

    public void setValidationGroups(String validationGroups) {
        clearInitialState();
        String newValidationGroups = validationGroups;
        
        // Treat empty list as null
        if (newValidationGroups != null && newValidationGroups.matches(EMPTY_VALIDATION_GROUPS_PATTERN)) {
            newValidationGroups = null;
        }
        // Only clear cache of validation group classes if value is changing
        if (newValidationGroups == null && validationGroups != null) {
            cachedValidationGroups = null;
        }
        if (newValidationGroups != null && validationGroups != null && !newValidationGroups.equals(validationGroups)) {
            cachedValidationGroups = null;
        }
        if (newValidationGroups != null && validationGroups == null) {
            cachedValidationGroups = null;
        }
        this.validationGroups = newValidationGroups;
    }

    public String getValidationGroups() {
        return validationGroups;
    }

    @Override
    public void validate(FacesContext context) {
        if (!wholeBeanValidationEnabled(context)) {
            return;
        }

        if (!isValidatorInstalled()) {
            WholeBeanValidator validator = new WholeBeanValidator();
            addValidator(validator);
        }
        
        super.validate(context);
    }
    
    @Override
    public void updateModel(FacesContext context) {
    	// Don't update the model. #4313
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        // Check if the parent of this f:validateWholeBean is a form                   
        UIForm parent = getClosestParent(this, UIForm.class);
        if (parent == null) {
            throw new IllegalArgumentException(ERROR_MISSING_FORM);
        }

        misplacedComponentCheck(parent, getClientId());
    }

    private static void misplacedComponentCheck(UIComponent parentComponent, String clientId) throws IllegalArgumentException {
        try {
            reverse(parentComponent.getChildren()).stream().forEach((UIComponent childComponent) -> {
                if (childComponent.isRendered()) {
                    if ((childComponent instanceof EditableValueHolder) && (!(childComponent instanceof UIValidateWholeBean))) {
                        throw new IllegalArgumentException(ERROR_MISPLACED_COMPONENT);
                    } else {
                        if (!childComponent.getClientId().equals(clientId)) {
                            misplacedComponentCheck(childComponent, clientId);
                        } else {
                            throw new BreakException();
                        }
                    }
                }
            });
        } catch (BreakException be) {
            // STOP
        }
    }
    
    public static <C extends UIComponent> C getClosestParent(UIComponent component, Class<C> parentType) {
        UIComponent parent = component.getParent();

        while (parent != null && !parentType.isInstance(parent)) {
            parent = parent.getParent();
        }

        return parentType.cast(parent);
    }

    private boolean isValidatorInstalled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ValidatorInstalled, false);
    }

    private void setValidatorInstalled(boolean newValue) {
        getStateHelper().put(PropertyKeys.ValidatorInstalled, newValue);
    }

    Class<?>[] getValidationGroupsArray() {
        
        if (cachedValidationGroups != null) {
            return cachedValidationGroups;
        }
        
        String validationGroupsStr = getValidationGroups();
        List<Class<?>> validationGroupsList = new ArrayList<>();

        for (String className : validationGroupsStr.split(VALIDATION_GROUPS_DELIMITER)) {
            className = className.trim();
            if (className.length() == 0) {
                continue;
            }
            
            if (className.equals(Default.class.getName())) {
                validationGroupsList.add(Default.class);
            } else {
                validationGroupsList.add(classForName(className));
            }
        }
        
        cachedValidationGroups = validationGroupsList.toArray(new Class[validationGroupsList.size()]);
        
        return cachedValidationGroups;
    }

    private boolean wholeBeanValidationEnabled(FacesContext context) {
        return TRUE.equals(context.getAttributes().get(ENABLE_VALIDATE_WHOLE_BEAN_PARAM_NAME));
    }

    @Override
    public void markInitialState() {
        initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        return initialState;
    }

    @Override
    public void clearInitialState() {
        initialState = false;
    }

    @Override
    public boolean isTransient() {
        return this.transientValue;
    }

    @Override
    public void setTransient(boolean transientValue) {
        this.transientValue = transientValue;
    }

    // ----------------------------------------------------- StateHolder Methods
    
    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        Object[] result = null;
        if (!initialStateMarked()) {
            Object[] values = new Object[2];
            values[0] = validationGroups;
            values[1] = super.saveState(context);
            return values;
        }
        return result;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (state != null) {
            Object[] values = (Object[]) state;
            validationGroups = (String) values[0];
            Object parentState = values[1];
            super.restoreState(context, parentState);
        }
    }
    
    // ----------------------------------------------------- Private helper methods
    
    private Class<?> classForName(String className) {
        try {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e1) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e2) {
                throw new FacesException("Validation group not found: " + className);
            }
        }
    }
    
    private static class BreakException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
