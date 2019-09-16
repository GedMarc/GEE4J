import javax.enterprise.inject.spi.CDIProvider;

module jakarta.enterprise.cdi {
	requires jakarta.enterprise.interceptor;
	requires javax.inject;
	requires javax.el;

	exports javax.decorator;
	exports javax.enterprise.context;
	exports javax.enterprise.context.control;
	exports javax.enterprise.context.spi;

	exports javax.enterprise.event;
	exports javax.enterprise.inject;
	exports javax.enterprise.inject.literal;
	exports javax.enterprise.inject.se;
	exports javax.enterprise.inject.spi;

	exports javax.enterprise.util;

	uses javax.enterprise.inject.spi.Extension;
	uses javax.enterprise.inject.se.SeContainerInitializer;
	uses CDIProvider;

}