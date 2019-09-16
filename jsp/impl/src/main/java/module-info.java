open module javax.servlet.jsp {

	exports org.apache.jasper;
	//exports org.apache.jasper.util;
	exports org.apache.jasper.servlet;
	exports org.apache.jasper.compiler;
	exports org.apache.jasper.compiler.tagplugin;
	//exports org.apache.jasper.resources;
	exports org.apache.jasper.runtime;
	exports org.apache.jasper.security;
	//exports org.apache.jasper.tagplugins;
	exports org.apache.jasper.tagplugins.jstl;
	exports org.apache.jasper.xmlparser;



	requires java.logging;
	requires java.xml;
	requires javax.servlet.jsp.api;
	requires java.desktop;
	requires javax.el;
	requires javax.servlet.api;
	requires java.compiler;

}