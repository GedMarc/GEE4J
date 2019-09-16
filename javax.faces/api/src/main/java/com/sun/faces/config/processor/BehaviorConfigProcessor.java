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

package com.sun.faces.config.processor;

import com.sun.faces.config.Verifier;
import com.sun.faces.config.DocumentInfo;
import com.sun.faces.util.FacesLogger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import javax.faces.application.Application;
import javax.faces.component.behavior.Behavior;
import javax.faces.component.behavior.FacesBehavior;
import javax.servlet.ServletContext;
import javax.xml.xpath.XPathExpressionException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *  This <code>ConfigProcessor</code> handles all elements defined under
 *  <code>/faces-config/behavior</code>.
 * </p>
 */
public class BehaviorConfigProcessor extends AbstractConfigProcessor {

    private static final Logger LOGGER = FacesLogger.CONFIG.getLogger();

    /**
     * <p>/faces-config/behavior</p>
     */
    private static final String BEHAVIOR = "behavior";

    /**
     * <p>/faces-config/behavior/behavior-id</p>
     */
    private static final String BEHAVIOR_ID = "behavior-id";

    /**
     * <p>/faces-config/behavior/behavior-class</p>
     */
    private static final String BEHAVIOR_CLASS = "behavior-class";


    // -------------------------------------------- Methods from ConfigProcessor


    /**
     * @see ConfigProcessor#process(javax.servlet.ServletContext,com.sun.faces.config.DocumentInfo[])
     */
    @Override
    public void process(ServletContext sc, DocumentInfo[] documentInfos)
    throws Exception {

        // process annotated Behaviors first as Behaviors configured
        // via config files take precedence
        processAnnotations(FacesBehavior.class);

        for (int i = 0; i < documentInfos.length; i++) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           MessageFormat.format(
                                "Processing behavior elements for document: ''{0}''",
                                documentInfos[i].getSourceURI()));
            }
            Document document = documentInfos[i].getDocument();
            String namespace = document.getDocumentElement()
                 .getNamespaceURI();
            NodeList behaviors = document.getDocumentElement()
                 .getElementsByTagNameNS(namespace, BEHAVIOR);
            if (behaviors != null && behaviors.getLength() > 0) {
                addBehaviors(behaviors, namespace);
            }
        }
        invokeNext(sc, documentInfos);

    }

    // --------------------------------------------------------- Private Methods


    private void addBehaviors(NodeList behaviors, String namespace)
    throws XPathExpressionException {

        Application app = getApplication();
        Verifier verifier = Verifier.getCurrentInstance();
        for (int i = 0, size = behaviors.getLength(); i < size; i++) {
            Node behavior = behaviors.item(i);

            NodeList children = ((Element) behavior)
                 .getElementsByTagNameNS(namespace, "*");
            String behaviorId = null;
            String behaviorClass = null;
            for (int c = 0, csize = children.getLength(); c < csize; c++) {
                Node n = children.item(c);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    switch (n.getLocalName()) {
                        case BEHAVIOR_ID:
                            behaviorId = getNodeText(n);
                            break;
                        case BEHAVIOR_CLASS:
                            behaviorClass = getNodeText(n);
                            break;
                    }
                }
            }

            if (behaviorId != null && behaviorClass != null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               MessageFormat.format(
                                    "Calling Application.addBehavior({0},{1})",
                                    behaviorId,
                                    behaviorClass));
                }
                if (verifier != null) {
                    verifier.validateObject(Verifier.ObjectType.BEHAVIOR,
                                            behaviorClass,
                                            Behavior.class);
                }
                app.addBehavior(behaviorId, behaviorClass);
            }

        }
    }

}
