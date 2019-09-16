package com.sun.faces.application;

import java.util.EnumSet;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewAction;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;


import com.sun.faces.util.MessageUtils;

/**
 * <p>ValidateFormComponentNesting performs component tree validation to assure {@link ActionSource}
 * , {@link ActionSource2} and {@link EditableValueHolder} components are placed inside a form.
 * ValidateFormComponentNesting is installed automatically if {@link ProjectStage#Development} is
 * active. </p>
 * 
 * @author dueni
 * 
 */
public class ValidateComponentNesting implements SystemEventListener {

    @Override
    public boolean isListenerForSource(Object source) {
        return (source instanceof UIViewRoot);
    }

    /**
     * Process PostAddToViewEvent on UIViewRoot to validate form - action/input nesting.
     */
    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        UIComponent root = (UIComponent) event.getSource();
        FacesContext jsf = FacesContext.getCurrentInstance();
        EnumSet<VisitHint> hints = EnumSet.of(VisitHint.SKIP_ITERATION);
        VisitContext visitContext = VisitContext.createVisitContext(jsf, null, hints);

        root.visitTree(visitContext, new ValidateFormNestingCallback());
    }

    static class ValidateFormNestingCallback implements VisitCallback {

        // report missing form problem only once
        boolean reportedOmittedFormOnce = false;

        // report missing metadata problem only once
        boolean reportedOmittedMetadataOnce = false;

        @Override
        public VisitResult visit(VisitContext context, UIComponent target) {
            // default result: continue tree walk
            VisitResult result = VisitResult.ACCEPT;

            if (target instanceof UIForm || target.getFamily().endsWith("Form")
                            || UIViewRoot.METADATA_FACET_NAME.equals(target.getId())) {
                // stop tree walk if component is of type UIForm or component family ends on "Form"
                // or if the component is the UIPanel with id METADATA_FACET_NAME 
                result = VisitResult.REJECT;
            } else if (target instanceof UIViewParameter || target instanceof UIViewAction) {
                if (reportedOmittedMetadataOnce) {
                    // report first detected problem only, then stop tree walk
                    result = VisitResult.COMPLETE;
                }
                addOmittedMessage(context.getFacesContext(), target.getClientId(context
                                .getFacesContext()), MessageUtils.MISSING_METADATA_ERROR);
                reportedOmittedMetadataOnce = true;

            } else if (target instanceof EditableValueHolder || target instanceof ActionSource
                            || target instanceof ActionSource2) {
                if (reportedOmittedFormOnce) {
                    // report first detected problem only, then stop tree walk
                    result = VisitResult.COMPLETE;
                }
                // if we find ActionSource, ActionSource2 or EditableValueHolder, that component
                // must be outside of a form add warning message
                addOmittedMessage(context.getFacesContext(), target.getClientId(context
                                .getFacesContext()), MessageUtils.MISSING_FORM_ERROR);
                reportedOmittedFormOnce = true;

            }
            return result;
        }
    }

    /**
     * method for adding a message regarding missing ancestor to context
     * 
     * @param ctx
     * @param clientId
     */
    private static void addOmittedMessage(FacesContext jsf, String clientId, String key) {
        Object[] params = new Object[] {};

        FacesMessage m = MessageUtils.getExceptionMessage(key, params);
        m.setSeverity(FacesMessage.SEVERITY_WARN);
        jsf.addMessage(clientId, m);
    }

}
