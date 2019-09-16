import javax.ejb.EJBContext;
import javax.ejb.spi.EJBContainerProvider;

module javax.ejb {
	requires java.rmi;
	requires java.transaction;
	requires java.xml;
	requires javax.servlet.api;
	requires java.naming;
	exports javax.ejb;
	exports javax.ejb.embeddable;
	exports javax.ejb.spi;

	uses EJBContainerProvider;
	uses EJBContext;
}