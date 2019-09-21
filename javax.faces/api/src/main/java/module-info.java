open module jakarta.faces {

	exports javax.faces.model;
	exports javax.faces.annotation;
	exports javax.faces.bean;
	exports javax.faces.application;
	exports javax.faces.component;
	exports javax.faces.context;
	exports javax.faces.convert;
	exports javax.faces.el;
	exports javax.faces.event;
	exports javax.faces.flow;
	exports javax.faces.lifecycle;

	exports javax.faces.push;
	exports javax.faces.render;
	exports javax.faces.validator;
	exports javax.faces.view;
	exports javax.faces.webapp;
	exports javax.faces;


	exports com.sun.faces;
	exports com.sun.faces.action;
	exports com.sun.faces.application;
	exports com.sun.faces.application.annotation;
	exports com.sun.faces.application.resource;
	exports com.sun.faces.application.view;
	exports com.sun.faces.cdi;
	exports com.sun.faces.component;
	exports com.sun.faces.component.behavior;
	exports com.sun.faces.component.search;
	exports com.sun.faces.component.validator;
	exports com.sun.faces.component.visit;
	exports com.sun.faces.config;
	exports com.sun.faces.config.configprovider;
	exports com.sun.faces.config.processor;
	exports com.sun.faces.context;
	exports com.sun.faces.context.flash;
	exports com.sun.faces.el;
	exports com.sun.faces.ext.component;
	exports com.sun.faces.ext.render;
	exports com.sun.faces.ext.taglib;
	exports com.sun.faces.ext.validator;
	exports com.sun.faces.facelets;
	exports com.sun.faces.facelets.compiler;
	exports com.sun.faces.facelets.component;
	exports com.sun.faces.facelets.el;
	exports com.sun.faces.facelets.impl;
	exports com.sun.faces.facelets.tag;
	exports com.sun.faces.facelets.tag.composite;
	exports com.sun.faces.facelets.tag.jsf;
	exports com.sun.faces.facelets.tag.jsf.core;
	exports com.sun.faces.facelets.tag.jsf.html;
	//exports com.sun.faces.facelets.tag.jstl;
	exports com.sun.faces.facelets.tag.jstl.core;
	exports com.sun.faces.facelets.tag.jstl.fn;
	exports com.sun.faces.facelets.tag.ui;
	exports com.sun.faces.facelets.util;
	exports com.sun.faces.flow;
	exports com.sun.faces.flow.builder;
	exports com.sun.faces.io;
	exports com.sun.faces.lifecycle;
	exports com.sun.faces.mgbean;
	exports com.sun.faces.push;
	exports com.sun.faces.renderkit;
	exports com.sun.faces.renderkit.html_basic;
	//exports com.sun.faces.resources;
	exports com.sun.faces.scripting;
	exports com.sun.faces.spi;
	exports com.sun.faces.taglib;
	exports com.sun.faces.taglib.html_basic;
	exports com.sun.faces.taglib.jsf_core;
	exports com.sun.faces.util;
	exports com.sun.faces.util.cdi11;
	exports com.sun.faces.util.copier;
	exports com.sun.faces.vendor;
//	exports com.sun.faces.xhtml;


	requires java.logging;
	requires javax.servlet.api;
	requires java.xml;
	requires javax.el;
	requires javax.servlet.jsp.jstl;
	requires java.sql;
	requires java.naming;
	requires java.desktop;
	requires javax.servlet.jsp;
	requires javax.inject;
	requires jakarta.enterprise.cdi;
	requires java.persistence;
	requires java.validation;
	requires javax.ejb;
	requires java.annotation;
	requires java.json;
	requires javax.websocket.api;
	requires java.xml.bind;
	requires javax.servlet.jsp.api;

	provides javax.enterprise.inject.spi.Extension with com.sun.faces.application.view.ViewScopeExtension,
			com.sun.faces.flow.FlowCDIExtension,
			com.sun.faces.flow.FlowDiscoveryCDIExtension,
			com.sun.faces.cdi.CdiExtension;

	provides javax.servlet.ServletContainerInitializer with com.sun.faces.config.FacesInitializer;

	uses com.sun.faces.util.cdi11.CDIUtil;
	uses com.sun.faces.spi.FacesConfigResourceProvider;
	uses com.sun.faces.spi.AnnotationProvider;
	uses com.sun.faces.spi.ConfigurationResourceProvider;
	uses com.sun.faces.spi.DiscoverableInjectionProvider;
	uses com.sun.faces.spi.FaceletConfigResourceProvider;
	uses com.sun.faces.spi.InjectionProvider;
	uses com.sun.faces.spi.SerializationProvider;
	uses javax.faces.application.ApplicationConfigurationPopulator;
	

}