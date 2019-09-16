/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDLGPL_1_1.html
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
package com.sun.faces.facelets.tag;

import com.sun.faces.facelets.tag.jsf.PassThroughAttributeLibrary;
import com.sun.faces.facelets.tag.jsf.PassThroughElementLibrary;
import com.sun.faces.facelets.tag.jsf.html.HtmlLibrary;

import javax.faces.render.Renderer;
import javax.faces.view.Location;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributes;
import javax.faces.view.facelets.TagDecorator;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple tag decorator to enable jsf: syntax
 */
class DefaultTagDecorator implements TagDecorator {

    private static enum Mapper {
        a(
                new ElementConverter("h:commandLink", "jsf:action"),
                new ElementConverter("h:commandLink", "jsf:actionListener"),
                new ElementConverter("h:outputLink", "jsf:value"),
                new ElementConverter("h:link", "jsf:outcome")),

        img("h:graphicImage"), body("h:body"), head("h:head"), label("h:outputLabel"), script("h:outputScript"),
        link("h:outputStylesheet"),

        form("h:form"), textarea("h:inputTextarea"),
        // TODO if we want the name of the button to become the id, we have to do .id("name")
        button(new ElementConverter("h:button", "jsf:outcome"), new ElementConverter("h:commandButton")),

        select(new ElementConverter("h:selectManyListbox", "multiple").id("name"),
                // TODO this is a little bit ugly to handle the name as if it were jsf:id. we should not support this
                new ElementConverter("h:selectOneListbox").id("name")),

        input(new ElementConverter("h:inputText", "type")
                // TODO this is a little bit ugly to handle the name as if it were jsf:id. we should not support this
                .id("name")
                .map("hidden", "inputHidden")
                .map("password", "inputSecret")
                .map("number", "inputText")
                .map("search", "inputText")
                .map("email", "inputText")
                .map("datetime", "inputText")
                .map("date", "inputText")
                .map("month", "inputText")
                .map("week", "inputText")
                .map("time", "inputText")
                .map("datetime-local", "inputText")
                .map("range", "inputText")
                .map("color", "inputText")
                .map("url", "inputText")
                .map("checkbox", "selectBooleanCheckbox")
                .map("file", "inputFile")
                .map("submit", "commandButton")
                .map("reset", "commandButton")
                .map("button", "button"));

        private ElementConverter elementConverter;

        private Mapper(final ElementConverter... elementConverters) {
            if (elementConverters.length == 1) {
                this.elementConverter = elementConverters[0];
            } else {
                this.elementConverter = new ElementConverter() {
                    @Override
                    public Tag decorate(Tag tag) {
                        for (ElementConverter converter : elementConverters) {
                            Tag decorated = converter.decorate(tag);
                            if (decorated != null) {
                                return decorated;
                            }
                        }
                        return null;
                    }
                };
            }
        }

        private Mapper(String faceletTag) {
            elementConverter = new ElementConverter(faceletTag);
        }
    }

    private static enum Namespace {
        p(PassThroughAttributeLibrary.Namespace),
        jsf(PassThroughElementLibrary.Namespace),
        h(HtmlLibrary.Namespace);

        private String uri;

        Namespace(String uri) {
            this.uri = uri;
        }
    }

    private ElementConverter defaultElementConverter = new ElementConverter("jsf:element");

    @Override
    public Tag decorate(Tag tag) {
        String ns = tag.getNamespace();
        if (!hasJsfAttribute(tag)) {
            // return immediately, if we have no jsf: attribute
            return null;
        }
        // we only handle html tags!
        if (!("".equals(ns) || "http://www.w3.org/1999/xhtml".equals(ns))) {
            throw new FaceletException("Elements with namespace " +
                    ns + " may not have attributes in namespace " +
                    Namespace.jsf.uri + "." +
                    " Namespace " + Namespace.jsf.uri +
                    " is intended for otherwise non-JSF-aware markup, such as <input type=\"text\" jsf:id >" +
                    " It is not valid to have <h:commandButton jsf:id=\"button\" />.");
        }
        for (Mapper mapper : Mapper.values()) {
            if (tag.getLocalName().equals(mapper.name())) {
                return mapper.elementConverter.decorate(tag);
            }
        }

        return defaultElementConverter.decorate(tag);
    }

    private boolean hasJsfAttribute(Tag tag) {
        for (String ns : tag.getAttributes().getNamespaces()) {
            if (Namespace.jsf.uri.equals(ns)) {
                return true;
            }
        }
        return false;
    }

    private static class ElementConverter implements TagDecorator {
        private String localName;
        private Namespace namespace;
        private String arbiterAttributeName;
        private String arbiterAttributeNamespace = "";
        private Map<String, String> additionalMappings = new HashMap<>();
        private String otherHtmlIdAttribute;

        private ElementConverter() {
            super();
        }

        private ElementConverter(String faceletsTag) {
            this(faceletsTag, null);
        }

        private ElementConverter(String faceletsTag, String arbiterAttributeName) {
            String[] strings = faceletsTag.split(":");
            this.namespace = Namespace.valueOf(strings[0]);
            this.localName = strings[1];
            this.arbiterAttributeName = arbiterAttributeName;

            if (arbiterAttributeName != null && arbiterAttributeName.indexOf(':') > 0) {
                strings = arbiterAttributeName.split(":");
                this.arbiterAttributeNamespace = Namespace.valueOf(strings[0]).uri;
                this.arbiterAttributeName = strings[1];
            }
        }

        private ElementConverter map(String arbiterAttributeValue, String faceletsTagLocalName) {
            additionalMappings.put(arbiterAttributeValue, faceletsTagLocalName);
            return this;
        }

        private ElementConverter id(String otherHtmlIdAttribute) {
            this.otherHtmlIdAttribute = otherHtmlIdAttribute;
            return this;
        }

        @Override
        public Tag decorate(Tag tag) {
            if (arbiterAttributeName == null) {
                // no arbiter
                return convertTag(tag, namespace, localName);
            }

            TagAttribute arbiterAttribute = tag.getAttributes().get(arbiterAttributeNamespace, arbiterAttributeName);

            if (arbiterAttribute == null) {
                // no arbiter
                return null;//convertTag(tag, namespace, localName);
            }

            // PENDING 
            /**
             if (!arbiterAttribute.isLiteral()) {
             // TODO should we throw an exception here?
             }
             **/

            String myLocalName = additionalMappings.get(arbiterAttribute.getValue());

            if (myLocalName == null) {
                myLocalName = this.localName;
            }

            return convertTag(tag, namespace, myLocalName);
        }

        protected Tag convertTag(Tag tag, Namespace namespace, String localName) {
            Location location = tag.getLocation();
            String ns = namespace.uri;
            String qName = namespace.name() + ":" + localName;

            TagAttributes attributes = convertAttributes(tag.getAttributes());

            Tag converted = new Tag(location, ns, localName, qName, attributes);

            for (TagAttribute tagAttribute : attributes.getAll()) {
                // set the correct tag
                tagAttribute.setTag(converted);
            }

            return converted;
        }

        protected TagAttributes convertAttributes(TagAttributes original) {
            Map<String, TagAttribute> attributes = new HashMap<>();
            TagAttribute elementName = createElementName(original.getTag());
            attributes.put(elementName.getQName(), elementName);

            for (TagAttribute attribute : original.getAll()) {
                TagAttribute converted = convertTagAttribute(attribute);
                // avoid duplicates
                attributes.put(converted.getQName(), converted);
            }

            return new TagAttributesImpl(attributes.values().toArray(new TagAttribute[attributes.size()]));
        }

        private TagAttribute createElementName(Tag tag) {
            Location location = tag.getLocation();
            String ns = Namespace.p.uri;
            String myLocalName = Renderer.PASSTHROUGH_RENDERER_LOCALNAME_KEY;
            String qName = "p:" + myLocalName;
            String value = tag.getLocalName();

            return new TagAttributeImpl(location, ns, myLocalName, qName, value);
        }


        protected TagAttribute convertTagAttribute(TagAttribute attribute) {
            Location location = attribute.getLocation();
            String ns = attribute.getNamespace();
            String myLocalName = attribute.getLocalName();
            String qName;
            String value = attribute.getValue();

            if (Namespace.jsf.uri.equals(attribute.getNamespace())) {
                // make this a component attribute
                qName = myLocalName;
                ns = "";
            } else {
                if (ns.length() != 0 && !ns.equals(attribute.getTag().getNamespace())) {
                    // the attribute has a different namespace than the tag. preserve it.
                    return attribute;
                }
                if (attribute.getLocalName().equals(otherHtmlIdAttribute)) {
                    // special case for input name
                    qName = "id";
                    myLocalName = "id";
                } else {
                    // make this a pass through attribute
                    qName = "p:" + myLocalName;
                    ns = Namespace.p.uri;
                }
            }
            return new TagAttributeImpl(location, ns, myLocalName, qName, value);
        }
    }

}
