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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2005-2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.faces.facelets.tag;

import com.sun.faces.facelets.tag.jsf.CompositeComponentTagHandler;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.view.facelets.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.view.Location;

/**
 * Base class for defining TagLibraries in Java
 * 
 * @author Jacob Hookom
 */
public abstract class AbstractTagLibrary implements TagLibrary {

    private static class ValidatorConfigWrapper implements ValidatorConfig {

        private final TagConfig parent;
        private final String validatorId;
        
        public ValidatorConfigWrapper(TagConfig parent, String validatorId) {
            this.parent = parent;
            this.validatorId = validatorId;
        }
        
        @Override
        public String getValidatorId() {
            return this.validatorId;
        }

        @Override
        public FaceletHandler getNextHandler() {
            return this.parent.getNextHandler();
        }

        @Override
        public Tag getTag() {
            return this.parent.getTag();
        }

        @Override
        public String getTagId() {
            return this.parent.getTagId();
        }  
    }
    
    private static class ConverterConfigWrapper implements ConverterConfig {
        private final TagConfig parent;
        private final String converterId;
        
        public ConverterConfigWrapper(TagConfig parent, String converterId) {
            this.parent = parent;
            this.converterId = converterId;
        }
        
        @Override
        public String getConverterId() {
            return this.converterId;
        }
        @Override
        public FaceletHandler getNextHandler() {
            return this.parent.getNextHandler();
        }
        @Override
        public Tag getTag() {
            return this.parent.getTag();
        }
        @Override
        public String getTagId() {
            return this.parent.getTagId();
        }
    }
    
    private static final class BehaviorConfigWrapper implements BehaviorConfig {
        private final TagConfig parent;
        private final String behaviorId;
		/**
		 * <p class="changed_added_2_0"></p>
		 * @param parent
		 * @param behaviorId
		 */
		public BehaviorConfigWrapper(TagConfig parent, String behaviorId) {
			this.parent = parent;
			this.behaviorId = behaviorId;
		}
		/**
		 * <p class="changed_added_2_0"></p>
		 * @return the behaviorId
		 */
        @Override
		public String getBehaviorId() {
			return behaviorId;
		}
		/**
		 * <p class="changed_added_2_0"></p>
		 * @return
		 * @see javax.faces.view.facelets.TagConfig#getNextHandler()
		 */
        @Override
		public FaceletHandler getNextHandler() {
			return parent.getNextHandler();
		}
		/**
		 * <p class="changed_added_2_0"></p>
		 * @return
		 * @see javax.faces.view.facelets.TagConfig#getTag()
		 */
        @Override
		public Tag getTag() {
			return parent.getTag();
		}
		/**
		 * <p class="changed_added_2_0"></p>
		 * @return
		 * @see javax.faces.view.facelets.TagConfig#getTagId()
		 */
        @Override
		public String getTagId() {
			return parent.getTagId();
		}
        
    	
    }
    
    private static class HandlerFactory implements TagHandlerFactory {
        private final static Class[] CONSTRUCTOR_SIG = new Class[] { TagConfig.class };

        protected final Class handlerType;

        public HandlerFactory(Class handlerType) {
            this.handlerType = handlerType;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            try {
                return (TagHandler) this.handlerType.getConstructor(
                        CONSTRUCTOR_SIG).newInstance(new Object[] { cfg });
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getCause();
                if (t instanceof FacesException) {
                    throw (FacesException) t;
                } else if (t instanceof ELException) {
                    throw (ELException) t;
                } else {
                    throw new FacesException("Error Instantiating: "
                            + this.handlerType.getName(), t);
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                throw new FacesException("Error Instantiating: "
                        + this.handlerType.getName(), e);
            }
        }
    }

    protected static class ComponentConfigWrapper implements ComponentConfig {

        protected final TagConfig parent;

        protected final String componentType;

        protected final String rendererType;

        public ComponentConfigWrapper(TagConfig parent, String componentType,
                String rendererType) {
            this.parent = parent;
            this.componentType = componentType;
            this.rendererType = rendererType;
        }

        @Override
        public String getComponentType() {
            return this.componentType;
        }

        @Override
        public String getRendererType() {
            return this.rendererType;
        }

        @Override
        public FaceletHandler getNextHandler() {
            return this.parent.getNextHandler();
        }

        @Override
        public Tag getTag() {
            return this.parent.getTag();
        }

        @Override
        public String getTagId() {
            return this.parent.getTagId();
        }
    }

    private static class UserTagFactory implements TagHandlerFactory {
        protected final URL location;

        public UserTagFactory(URL location) {
            this.location = location;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            return new UserTagHandler(cfg, this.location);
        }
    }

    private static class CompositeComponentTagFactory implements TagHandlerFactory {
        protected final String resourceId;

        public CompositeComponentTagFactory(String resourceId) {
            this.resourceId = resourceId;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            ComponentConfig componentConfig = 
                    new ComponentConfigWrapper(cfg, 
                    "javax.faces.NamingContainer",
                    "javax.faces.Composite");
            ResourceHandler resourceHandler = 
                    FacesContext.getCurrentInstance().getApplication().getResourceHandler();
            TagHandler result = null;
            // Use the naming convention to extract the library name and
            // component name from the resourceId.
            Resource resource = resourceHandler.createResourceFromId(resourceId);
            if (null != resource) {
                result = new CompositeComponentTagHandler(resource, componentConfig);
            } else {
                Location loc = new Location(resourceId, 0, 0);
                Tag tag = new Tag(loc, "", "", "", null);
                throw new TagException(tag, "Cannot create composite component tag handler for composite-source element in taglib.xml file");
            }
            return result;
        }
    }

    private static class ComponentHandlerFactory implements TagHandlerFactory {

        protected final String componentType;

        protected final String renderType;

        public ComponentHandlerFactory(String componentType, String renderType) {
            this.componentType = componentType;
            this.renderType = renderType;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            ComponentConfig ccfg = new ComponentConfigWrapper(cfg,
                    this.componentType, this.renderType);
            return new ComponentHandler(ccfg);
        }
    }

    private static class UserComponentHandlerFactory implements
            TagHandlerFactory {

        private final static Class[] CONS_SIG = new Class[] { ComponentConfig.class };

        protected final String componentType;

        protected final String renderType;

        protected final Class type;

        protected final Constructor constructor;

        public UserComponentHandlerFactory(String componentType,
                String renderType, Class type) {
            this.componentType = componentType;
            this.renderType = renderType;
            this.type = type;
            try {
                this.constructor = this.type.getConstructor(CONS_SIG);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new FaceletException(
                        "Must have a Constructor that takes in a ComponentConfig",
                        e);
            }
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            try {
                ComponentConfig ccfg = new ComponentConfigWrapper(cfg,
                        this.componentType, this.renderType);
                return (TagHandler) this.constructor
                        .newInstance(new Object[] { ccfg });
            } catch (InvocationTargetException e) {
                throw new FaceletException(e.getCause().getMessage(), e.getCause().getCause());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                throw new FaceletException("Error Instantiating ComponentHandler: "+this.type.getName(), e);
            }
        }
    }

    private static class ValidatorHandlerFactory implements TagHandlerFactory {

        protected final String validatorId;

        public ValidatorHandlerFactory(String validatorId) {
            this.validatorId = validatorId;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            return new ValidatorHandler(new ValidatorConfigWrapper(cfg, this.validatorId));
        }
    }

    private static class ConverterHandlerFactory implements TagHandlerFactory {

        protected final String converterId;

        public ConverterHandlerFactory(String converterId) {
            this.converterId = converterId;
        }

        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
                ELException {
            return new ConverterHandler(new ConverterConfigWrapper(cfg, this.converterId));
        }
    }
    
    private static final class BehaviorHandlerFactory implements TagHandlerFactory {
    	private final String behaviorId;

		/**
		 * <p class="changed_added_2_0"></p>
		 * @param behaviorId
		 */
		public BehaviorHandlerFactory(String behaviorId) {
			this.behaviorId = behaviorId;
		}
    	
            @Override
		public TagHandler createHandler(TagConfig cfg) throws FacesException,
				ELException {
			return new BehaviorHandler(new BehaviorConfigWrapper(cfg,behaviorId));
		}
    }

    private static class UserConverterHandlerFactory implements TagHandlerFactory {
        private final static Class[] CONS_SIG = new Class[] { ConverterConfig.class };
        
        protected final String converterId;
        
        protected final Class type;

        protected final Constructor constructor;
        
        public UserConverterHandlerFactory(String converterId, Class type) {
            this.converterId = converterId;
            this.type = type;
            try {
                this.constructor = this.type.getConstructor(CONS_SIG);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new FaceletException(
                        "Must have a Constructor that takes in a ConverterConfig",
                        e);
            }
        }
        
        @Override
        public TagHandler createHandler(TagConfig cfg) throws FacesException,
        ELException {
            try {
                ConverterConfig ccfg = new ConverterConfigWrapper(cfg,
                        this.converterId);
                return (TagHandler) this.constructor
                        .newInstance(new Object[] { ccfg });
            } catch (InvocationTargetException e) {
                throw new FaceletException(e.getCause().getMessage(), e.getCause().getCause());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                throw new FaceletException("Error Instantiating ConverterHandler: "+this.type.getName(), e);
            }
        }
    }
    
    private static class UserValidatorHandlerFactory implements
			TagHandlerFactory {
		private final static Class[] CONS_SIG = new Class[] { ValidatorConfig.class };

		protected final String validatorId;

		protected final Class type;

		protected final Constructor constructor;

		public UserValidatorHandlerFactory(String validatorId, Class type) {
			this.validatorId = validatorId;
			this.type = type;
			try {
				this.constructor = this.type.getConstructor(CONS_SIG);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new FaceletException(
						"Must have a Constructor that takes in a ValidatorConfig",
						e);
			}
		}

                @Override
		public TagHandler createHandler(TagConfig cfg) throws FacesException,
				ELException {
			try {
				ValidatorConfig ccfg = new ValidatorConfigWrapper(cfg,
						this.validatorId);
				return (TagHandler) this.constructor
						.newInstance(new Object[] { ccfg });
			} catch (InvocationTargetException e) {
				throw new FaceletException(e.getCause().getMessage(), e
						.getCause().getCause());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new FaceletException(
						"Error Instantiating ValidatorHandler: "
								+ this.type.getName(), e);
			}
		}
	}
 
    private static class UserBehaviorHandlerFactory implements
			TagHandlerFactory {
		private final static Class[] CONS_SIG = new Class[] { BehaviorConfig.class };

		protected final String behaviorId;

		protected final Class type;

		protected final Constructor constructor;

		public UserBehaviorHandlerFactory(String behaviorId, Class type) {
			this.behaviorId = behaviorId;
			this.type = type;
			try {
				this.constructor = this.type.getConstructor(CONS_SIG);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new FaceletException(
						"Must have a Constructor that takes in a BehaviorConfig",
						e);
			}
		}

                @Override
		public TagHandler createHandler(TagConfig cfg) throws FacesException,
				ELException {
			try {
				BehaviorConfig ccfg = new BehaviorConfigWrapper(cfg,
						this.behaviorId);
				return (TagHandler) this.constructor
						.newInstance(new Object[] { ccfg });
			} catch (InvocationTargetException e) {
				throw new FaceletException(e.getCause().getMessage(), e
						.getCause().getCause());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new FaceletException(
						"Error Instantiating BehaviorHandler: "
								+ this.type.getName(), e);
			}
		}
	}
        
    private final Map factories;

    private final String namespace;

    private final Map functions;

    public AbstractTagLibrary(String namespace) {
        this.namespace = namespace;
        this.factories = new HashMap();
        this.functions = new HashMap();
    }

    /**
     * Add a ComponentHandlerImpl with the specified componentType and rendererType,
     * aliased by the tag name.
     * 
     * @see javax.faces.application.Application#createComponent(java.lang.String)
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param componentType
     *            componentType to use
     * @param rendererType
     *            rendererType to use
     */
    protected final void addComponent(String name, String componentType,
            String rendererType) {
        this.factories.put(name, new ComponentHandlerFactory(componentType,
                rendererType));
    }

    /**
     * Add a ComponentHandlerImpl with the specified componentType and rendererType,
     * aliased by the tag name. The Facelet will be compiled with the specified
     * HandlerType (which must extend AbstractComponentHandler).
     * 
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param componentType
     *            componentType to use
     * @param rendererType
     *            rendererType to use
     * @param handlerType
     *            a Class that extends ComponentHandler
     */
    protected final void addComponent(String name, String componentType,
            String rendererType, Class handlerType) {
        this.factories.put(name, new UserComponentHandlerFactory(componentType,
                rendererType, handlerType));
    }

    /**
     * Add a ConverterHandler for the specified converterId
     * 
     * @see ConverterHandler
     * @see javax.faces.application.Application#createConverter(java.lang.String)
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param converterId
     *            id to pass to Application instance
     */
    protected final void addConverter(String name, String converterId) {
        this.factories.put(name, new ConverterHandlerFactory(converterId));
    }
    
    /**
     * Add a ConverterHandler for the specified converterId of a TagHandler type
     * 
     * @see ConverterHandler
     * @see ConverterConfig
     * @see javax.faces.application.Application#createConverter(java.lang.String)
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param converterId
     *            id to pass to Application instance
     * @param type
     *            TagHandler type that takes in a ConverterConfig
     */
    protected final void addConverter(String name, String converterId, Class type) {
        this.factories.put(name, new UserConverterHandlerFactory(converterId, type));
    }

    /**
     * Add a ValidatorHandler for the specified validatorId
     * 
     * @see ValidatorHandler
     * @see javax.faces.application.Application#createValidator(java.lang.String)
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param validatorId
     *            id to pass to Application instance
     */
    protected final void addValidator(String name, String validatorId) {
        this.factories.put(name, new ValidatorHandlerFactory(validatorId));
    }
    
    /**
     * Add a ValidatorHandler for the specified validatorId
     * 
     * @see ValidatorHandler
     * @see ValidatorConfig
     * @see javax.faces.application.Application#createValidator(java.lang.String)
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param validatorId
     *            id to pass to Application instance
     * @param type
     *            TagHandler type that takes in a ValidatorConfig
     */
    protected final void addValidator(String name, String validatorId, Class type) {
        this.factories.put(name, new UserValidatorHandlerFactory(validatorId, type));
    }

    /**
     * <p class="changed_added_2_0"></p>
     * @param name
     * @param behaviorId
     */
    protected final void addBehavior(String name, String behaviorId){
		this.factories.put(name, new BehaviorHandlerFactory(behaviorId));
	}

    protected final void addBehavior(String name, String behaviorId, Class type) {
        this.factories.put(name, new UserBehaviorHandlerFactory(behaviorId, type));
    }
	/**
     * Use the specified HandlerType in compiling Facelets. HandlerType must
     * extend TagHandler.
     * 
     * @see TagHandler
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param handlerType
     *            must extend TagHandler
     */
    protected final void addTagHandler(String name, Class handlerType) {
        this.factories.put(name, new HandlerFactory(handlerType));
    }

    /**
     * Add a UserTagHandler specified a the URL source.
     * 
     * @see UserTagHandler
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param source source where the Facelet (Tag) source is
     */
    protected final void addUserTag(String name, URL source) {
        this.factories.put(name, new UserTagFactory(source));
    }
    
    /**
     * Add a CompositeComponentTagHandler for the specified resource.
     * 
     * @see UserTagHandler
     * @param name
     *            name to use, "foo" would be &lt;my:foo />
     * @param resourceId source where the Facelet (Tag) source is
     */
    protected final void addCompositeComponentTag(String name, String resourceId) {
        this.factories.put(name, new CompositeComponentTagFactory(resourceId));
    }
    
    
    /**
     * Add a Method to be used as a Function at Compilation.
     * 
     * @see javax.el.FunctionMapper
     * 
     * @param name (suffix) of function name
     * @param method method instance 
     */
    protected final void addFunction(String name, Method method) {
        this.functions.put(name, method);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.TagLibrary#containsNamespace(java.lang.String)
     */
    @Override
    public boolean containsNamespace(String ns, Tag t) {
        return this.namespace.equals(ns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.TagLibrary#containsTagHandler(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean containsTagHandler(String ns, String localName) {
        if (this.namespace.equals(ns)) {
            if (this.factories.containsKey(localName)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.TagLibrary#createTagHandler(java.lang.String,
     *      java.lang.String, com.sun.facelets.TagConfig)
     */
    @Override
    public TagHandler createTagHandler(String ns, String localName,
            TagConfig tag) throws FacesException {
        if (this.namespace.equals(ns)) {
            TagHandlerFactory f = (TagHandlerFactory) this.factories
                    .get(localName);
            if (f != null) {
                return f.createHandler(tag);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.TagLibrary#containsFunction(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean containsFunction(String ns, String name) {
        if (this.namespace.equals(ns)) {
            return this.functions.containsKey(name);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.TagLibrary#createFunction(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Method createFunction(String ns, String name) {
        if (this.namespace.equals(ns)) {
            return (Method) this.functions.get(name);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TagLibrary && obj.hashCode() == this.hashCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.namespace.hashCode();
    }

    public String getNamespace() {
        return namespace;
    }
}
