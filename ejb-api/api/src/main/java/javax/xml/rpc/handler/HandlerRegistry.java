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
import javax.xml.rpc.JAXRPCException;
import javax.xml.namespace.QName;

/** The <code>javax.xml.rpc.handler.HandlerRegistry</code> 
 *  provides support for the programmatic configuration of 
 *  handlers in a <code>HandlerRegistry</code>.
 *
 *  <p>A handler chain is registered per service endpoint, as 
 *  indicated by the qualified name of a port. The getHandlerChain
 *  returns the handler chain (as a java.util.List) for the 
 *  specified service endpoint. The returned handler chain is 
 *  configured using the java.util.List interface. Each element 
 *  in this list is required to be of the Java type 
 *  <code>javax.xml.rpc.handler.HandlerInfo</code>.
 *
 *  @version 1.0
 *  @author  Rahul Sharma
 *  @see javax.xml.rpc.Service
**/

public interface HandlerRegistry extends java.io.Serializable {

  /** Gets the handler chain for the specified service endpoint.
   *  The returned <code>List</code> is used to configure this
   *  specific handler chain in this <code>HandlerRegistry</code>.
   *  Each element in this list is required to be of the Java type 
   *  <code>javax.xml.rpc.handler.HandlerInfo</code>.
   *
   *  @param portName Qualified name of the target service endpoint
   *  @return java.util.List Handler chain
   *  @throws IllegalArgumentException If an invalid
   *          <code>portName</code> is specified
  **/
  public List getHandlerChain(QName portName);

  /** Sets the handler chain for the specified service endpoint
   *  as a <code>java.util.List</code>. Each element in this list
   *  is required to be of the Java type 
   *  <code>javax.xml.rpc.handler.HandlerInfo</code>.
   *
   *  @param portName Qualified name of the target service endpoint
   *  @param chain    A List representing configuration for the
   *                  handler chain
   *  @throws JAXRPCException If any error in the configuration of
   *                  the handler chain
   *  @throws UnsupportedOperationException If this
   *          set operation is not supported. This is done to
   *          avoid any overriding of a pre-configured handler
   *          chain.
   *  @throws IllegalArgumentException If an invalid
   *          <code>portName</code> is specified
  **/
  public void setHandlerChain(QName portName, List chain);

}
