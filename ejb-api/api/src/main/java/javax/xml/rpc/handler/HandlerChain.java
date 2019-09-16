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

import java.util.List;
import java.util.Map;
import java.util.Iterator;

/** The <code>javax.xml.rpc.handler.HandlerChain</code> represents
 *  a list of handlers. All elements in the HandlerChain are of 
 *  the type <code>javax.xml.rpc.handler.Handler</code>.
 *
 *  <p>An implementation class for the <code>HandlerChain</code>
 *  interface abstracts the policy and mechanism for the invocation
 *  of the registered handlers. 
 *
 *  @version 1.0
 *  @author  Rahul Sharma
 *  @see HandlerChain
**/

public interface HandlerChain extends List {

  /** The <code>handleRequest</code> method initiates the request 
   *  processing for this handler chain.
   *
   *  @param context  MessageContext parameter provides access to
   *                  the request SOAP message.
   *  @return boolean Returns <code>true</code> if all handlers in
   *                  chain have been processed. Returns <code>false</code>
   *                  if a handler in the chain returned
   *                  <code>false</code> from its handleRequest
   *                  method.
   *  @throws JAXRPCException if any processing error happens 
   *  @see Handler#handleRequest
  **/
  public boolean handleRequest(MessageContext context);

  /** The <code>handleResponse</code> method initiates the response
   *  processing for this handler chain.
   *
   *  @param context MessageContext parameter provides access to
   *                 the response SOAP message.
   *  @return boolean Returns <code>true</code> if all handlers in
   *                  chain have been processed. Returns <code>false</code>
   *                  if a handler in the chain returned
   *                  <code>false</code> from its handleResponse method.
   *  @throws JAXRPCException if any processing error happens 
   *  @see Handler#handleResponse
  **/
  public boolean handleResponse(MessageContext context);

  /** The <code>handleFault</code> method initiates the SOAP
   *  fault processing for this handler chain.
   *
   *  @param context MessageContext parameter provides access
   *                 to the SOAP message.
   *  @return boolean Returns <code>true</code> if all handlers in
   *                  chain have been processed. Returns <code>false</code>
   *                  if a handler in the chain returned
   *                  <code>false</code> from its handleFault method.
   *  @throws JAXRPCException if any processing error happens 
   *  @see Handler#handleFault
  **/
  public boolean handleFault(MessageContext context);

  /** Initializes the configuration for a HandlerChain.
   *
   *  @param  config     Configuration for the initialization of
   *                     this handler chain
   *  @throws JAXRPCException If any error during initialization
  **/
  public void init(Map config);

  /** Indicates the end of lifecycle for a HandlerChain.
   *
   *  @throws JAXRPCException If any error during destroy
  **/
  public void destroy();

  /** Sets SOAP Actor roles for this <code>HandlerChain</code>. This 
   *  specifies the set of roles in which this HandlerChain is to act
   *  for the SOAP message processing at this SOAP node. These roles
   *  assumed by a HandlerChain must be invariant during the 
   *  processing of an individual SOAP message through the HandlerChain.
   *
   *  <p>A <code>HandlerChain</code> always acts in the role of the
   *  special SOAP actor <code>next</code>. Refer to the SOAP 
   *  specification for the URI name for this special SOAP actor. 
   *  There is no need to set this special role using this method.
   *
   *  @param soapActorNames   URIs for SOAP actor name 
   *
   *  @see javax.xml.rpc.NamespaceConstants
  **/
  public void setRoles(String[] soapActorNames);

  /** Gets SOAP actor roles registered for this HandlerChain at 
   *  this SOAP node. The returned array includes the special 
   *  SOAP actor <code>next</code>.
   *
   *  @return String[] SOAP Actor roles as URIs
   *  @see javax.xml.rpc.NamespaceConstants
  **/
  public String[] getRoles();

}
