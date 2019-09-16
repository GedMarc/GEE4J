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

/** The <code>javax.xml.rpc.handler.Handler</code> interface is 
 *  required to be implemented by a SOAP message handler. The 
 *  <code>handleRequest</code>, <code>handleResponse</code> 
 *  and <code>handleFault</code> methods for a SOAP message 
 *  handler get access to the <code>SOAPMessage</code> from the
 *  <code>SOAPMessageContext</code>. The implementation of these
 *  methods can modify the <code>SOAPMessage</code> including the
 *  headers and body elements.
 *  
 *  @version 1.0
 *  @author  Rahul Sharma
**/

public interface Handler {

  /** The <code>handleRequest</code> method processes the request 
   *  message. 
   *
   *  @param context <code>MessageContext</code> parameter provides 
   *                 access to the request message.
   *  @return boolean Indicates the processing mode
   *                 <UL>
   *                 <LI>Return <code>true</code> to indicate continued 
   *                     processing of the request handler chain. The 
   *                     <code>HandlerChain</code>
   *                     takes the responsibility of invoking the next 
   *                     entity. The next entity may be the next handler 
   *                     in the <code>HandlerChain</code> or if this 
   *                     handler is the last handler in the chain, the 
   *                     next entity is the service endpoint object.
   *                 <LI>Return <code>false</code> to indicate blocking 
   *                     of the request handler chain. In this case, 
   *                     further processing of the request handler chain
   *                     is blocked and the target service endpoint is 
   *                     not dispatched. The JAX-RPC runtime system takes
   *                     the responsibility of invoking the response 
   *                     handler chain next with the SOAPMessageContext. 
   *                     The Handler implementation class has the the 
   *                     responsibility of setting the appropriate response
   *                     SOAP message in either handleRequest and/or 
   *                     handleResponse method. In the default processing
   *                     model, the response handler chain starts processing
   *                     from the same Handler instance (that returned false)
   *                     and goes backward in the execution sequence.
   *                  </UL>
   *  @throws JAXRPCException This exception indicates handler
   *                     specific runtime error. If JAXRPCException is thrown
   *                     by a handleRequest method, the HandlerChain 
   *                     terminates the further processing of this handler
   *                     chain. On the server side, the HandlerChain 
   *                     generates a SOAP fault that indicates that the 
   *                     message could not be processed for reasons not 
   *                     directly attributable to the contents of the 
   *                     message itself but rather to a runtime error 
   *                     during the processing of the message. On the 
   *                     client side, the exception is propagated to 
   *                     the client code

  **/
  public boolean handleRequest(MessageContext context);

  /** The <code>handleResponse</code> method processes the response 
   *  SOAP message.
   *
   *  @param context MessageContext parameter provides access to
   *                 the response SOAP message
   *  @return boolean Indicates the processing mode
   *                 <UL>
   *                 <LI>Return <code>true</code> to indicate continued 
   *                     processing ofthe response handler chain. The 
   *                     HandlerChain invokes the <code>handleResponse</code>
   *                     method on the next <code>Handler</code> in 
   *                     the handler chain.
   *                 <LI>Return <code>false</code> to indicate blocking 
   *                     of the response handler chain. In this case, no
   *                     other response handlers in the handler chain 
   *                     are invoked.
   *                 </UL>
   * @throws JAXRPCException Indicates handler specific runtime error. 
   *                     If JAXRPCException is thrown by a handleResponse
   *                     method, the HandlerChain terminates the further 
   *                     processing of this handler chain. On the server side, 
   *                     the HandlerChain generates a SOAP fault that 
   *                     indicates that the message could not be processed
   *                     for reasons not directly attributable to the contents
   *                     of the message itself but rather to a runtime error
   *                     during the processing of the message. On the client 
   *                     side, the runtime exception is propagated to the
   *                     client code.
   * 
  **/
  public boolean handleResponse(MessageContext context);

  /** The <code>handleFault</code> method processes the SOAP faults 
   *  based on the SOAP message processing model.
   *
   *  @param context MessageContext parameter provides access to
   *                 the SOAP message
   *  @return boolean Indicates the processing mode
   *                 <UL>
   *                 <LI>Return <code>true</code> to indicate continued 
   *                     processing of SOAP Fault. The HandlerChain invokes
   *                     the <code>handleFault</code> method on the 
   *                     next <code>Handler</code> in the handler chain.
   *                 <LI>Return <code>false</code> to indicate end 
   *                     of the SOAP fault processing. In this case, no 
   *                     other handlers in the handler chain 
   *                     are invoked.
   *                 </UL>
   * @throws JAXRPCException Indicates handler specific runtime error. 
   *                     If JAXRPCException is thrown by a handleFault
   *                     method, the HandlerChain terminates the further 
   *                     processing of this handler chain. On the server side, 
   *                     the HandlerChain generates a SOAP fault that 
   *                     indicates that the message could not be processed
   *                     for reasons not directly attributable to the contents
   *                     of the message itself but rather to a runtime error
   *                     during the processing of the message. On the client 
   *                     side, the JAXRPCException is propagated to the
   *                     client code.
  **/
  public boolean handleFault(MessageContext context);

  /** The <code>init</code> method enables the Handler instance to 
   *  initialize itself. The <code>init</code> method passes the 
   *  handler configuration as a <code>HandlerInfo</code> instance.
   *  The HandlerInfo is used to configure the Handler (for example: 
   *  setup access to an external resource or service) during the
   *  initialization.
   *
   *  <p>In the init method, the Handler class may get access to 
   *  any resources (for example; access to a logging service or
   *  database) and maintain these as part of its instance variables.
   *  Note that these instance variables must not have any state 
   *  specific to the SOAP message processing performed in the 
   *  various handle method.
   *
   *  @param config Configuration for the initialization of
   *                     this handler
   *  @throws JAXRPCException If initialization of the handler fails
  **/
  public void init(HandlerInfo config);

  /** The <code>destroy</code> method indicates the end of lifecycle 
   *  for a Handler instance.  The Handler implementation class should
   *  release its resources and perform cleanup in the implementation
   *  of the <code>destroy</code> method.
   *
   *  @throws JAXRPCException If any error during destroy 
  **/
  public void destroy();

  /** Gets the header blocks that can be processed by this Handler
   *  instance.
   *
   *  @return Array of QNames of header blocks processed by this
   *           handler instance. <code>QName</code> is the qualified 
   *           name of the outermost element of the Header block.
  **/
  public QName[] getHeaders();
}
