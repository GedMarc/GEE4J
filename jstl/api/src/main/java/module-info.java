module javax.servlet.jsp.jstl.api {
	requires javax.servlet.api;
	requires javax.servlet.jsp.api;
	requires javax.el;
	requires java.sql;


	exports javax.servlet.jsp.jstl.core;
	exports javax.servlet.jsp.jstl.fmt;
	exports javax.servlet.jsp.jstl.sql;
	exports javax.servlet.jsp.jstl.tlv;
}