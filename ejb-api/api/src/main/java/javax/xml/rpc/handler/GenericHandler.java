/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package javax.xml.rpc.handler;

import javax.xml.rpc.JAXRPCException;
import javax.xml.namespace.QName;

/** The <code>javax.xml.rpc.handler.GenericHandler</code> class
 *  implements the <code>Handler</code> interface. SOAP Message
 *  Handler developers should typically subclass 
 *  <code>GenericHandler</code> class unless the Handler class 
 *  needs another class as a superclass.
 *
 *  <p>The <code>GenericHandler</code> class is a convenience abstract
 *  class that makes writing Handlers easy. This class provides 
 *  default implementations of the lifecycle methods <code>init</code>
 *  and <code>destroy</code> and also different handle methods. 
 *  A Handler developer should only override methods that it needs
 *  to specialize as part of the derived <code>Handler</code> 
 *  implementation class.
 *  
 *  @version 1.0
 *  @author  Rahul Sharma
**/

public abstract class GenericHandler implements Handler {

  /** Default constructor
  **/
  protected GenericHandler() {}

  /** The <code>handleRequest</code> method processes the request 
   *  SOAP message. The default implementation of this method returns 
   *  <code>true</code>. This indicates that the handler chain
   *  should continue processing of the request SOAP message.
   *  This method should be overridden if the derived Handler class
   *  needs to specialize implementation of this method.   
   *
   *  @see Handler#handleRequest
  **/
  public boolean handleRequest(MessageContext context) {
    return true;
  }

  /** The <code>handleResponse</code> method processes the response 
   *  message. The default implementation of this method returns 
   *  <code>true</code>. This indicates that the handler chain
   *  should continue processing of the response SOAP message.
   *  This method should be overridden if the derived Handler class 
   *  needs to specialize implementation of this method.
   *
   *  @see Handler#handleResponse
  **/
  public boolean handleResponse(MessageContext context) {
    return true;
  }

  /** The <code>handleFault</code> method processes the SOAP faults 
   *  based on the SOAP message processing model. The default
   *  implementation of this method returns <code>true</code>. This 
   *  indicates that the handler chain should continue processing
   *  of the SOAP fault. This method should be overridden if
   *  the derived Handler class needs to specialize implementation
   *  of this method.
   *
   *  @see Handler#handleFault
  **/
  public boolean handleFault(MessageContext context) {
    return true;
  }

  /** The <code>init</code> method to enable the Handler instance to 
   *  initialize itself. This method should be overridden if
   *  the derived Handler class needs to specialize implementation
   *  of this method.
   *
   *  @see Handler#init
  **/
  public void init(HandlerInfo config) {
  }

  /** The <code>destroy</code> method indicates the end of lifecycle 
   *  for a Handler instance. This method should be overridden if
   *  the derived Handler class needs to specialize implementation
   *  of this method.
   *
   *  @see Handler#destroy
  **/
  public void destroy() {
  }

  /** Gets the header blocks processed by this Handler instance.
   *
   *  @return  Array of QNames of header blocks processed by this
   *           handler instance. <code>QName</code> is the qualified 
   *           name of the outermost element of the Header block.
  **/
  public abstract QName[] getHeaders();
}
