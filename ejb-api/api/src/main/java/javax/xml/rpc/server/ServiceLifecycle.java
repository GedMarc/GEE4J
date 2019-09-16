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

package javax.xml.rpc.server;

import javax.xml.rpc.ServiceException;

/** The <code>javax.xml.rpc.server.ServiceLifecycle</code> defines
 *  a lifecycle interface for a JAX-RPC service endpoint. If the 
 *  service endpoint class implements the <code>ServiceLifeycle</code>
 *  interface, the servlet container based JAX-RPC runtime system 
 *  is required to manage the lifecycle of the corresponding service
 *  endpoint objects.
 *
 *  @version 1.0
 *  @author  Rahul Sharma
**/

public interface ServiceLifecycle {

  /** Used for initialization of a service endpoint. After a service
   *  endpoint instance (an instance of a service endpoint class) is 
   *  instantiated, the JAX-RPC runtime system invokes the 
   *  <code>init</code> method. The service endpoint class uses the
   *  <code>init</code> method to initialize its configuration 
   *  and setup access to any external resources. The context parameter
   *  in the <code>init</code> method enables the endpoint instance to
   *  access the endpoint context provided by the underlying JAX-RPC 
   *  runtime system.
   *  
   *  <p>The init method implementation should typecast the context
   *  parameter to an appropriate Java type. For service endpoints 
   *  deployed on a servlet container based JAX-RPC runtime system, 
   *  the <code>context</code> parameter is of the Java type 
   *  <code>javax.xml.rpc.server.ServletEndpointContext</code>. The
   *  <code>ServletEndpointContext</code> provides an endpoint context
   *  maintained by the underlying servlet container based JAX-RPC 
   *  runtime system    
   ** 
   *  @param context Endpoint context for a JAX-RPC service endpoint
   *  @throws ServiceException If any error in initialization of the
   *                 service endpoint; or if any illegal context has
   *                 been provided in the init method
  **/

  public void init(Object context) throws ServiceException;

  /** JAX-RPC runtime system ends the lifecycle of a service endpoint 
   *  instance by invoking the destroy method. The service endpoint 
   *  releases its resourcesin the implementation of the destroy 
   *  method.
   *
   *
  **/
  public void destroy();

} 
