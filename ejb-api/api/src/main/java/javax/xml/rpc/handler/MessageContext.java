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

/** The interface <code>MessageContext</code> abstracts the message
 *  context that is processed by a handler in the <code>handle</code>
 *  method. 
 *
 *  <p>The <code>MessageContext</code> interface provides methods to 
 *  manage a property set. <code>MessageContext</code> properties 
 *  enable handlers in a handler chain to share processing related
 *  state.
 *
 *  @version 1.1
 *  @author  Rahul Sharma
 *  @author  Roberto Chinnici
 *  @see Handler
**/

public interface MessageContext {

  /** Sets the name and value of a property associated with the
   *  <code>MessageContext</code>. If the <code>MessageContext</code>
   *  contains a value of the same property, the old value is replaced.
   *
   *  @param name Name of the property associated with the 
   *              <code>MessageContext</code>
   *  @param value Value of the property
   *  @throws IllegalArgumentException If some aspect
   *              of the property is prevents it from being stored
   *              in the context
   *  @throws UnsupportedOperationException If this method is 
   *              not supported.
   *             
  **/
  public void setProperty(String name, Object value);

  /** Gets the value of a specific property from the 
   *  <code>MessageContext</code>
   *  @param  name Name of the property whose value is to be
   *               retrieved
   *  @return Value of the property
   *  @throws IllegalArgumentException if an illegal
   *              property name is specified
  **/
  public Object getProperty(String name);

  /** Removes a property (name-value pair) from the <code>MessageContext</code>
   *  @param name Name of the property to be removed
   *  @throws IllegalArgumentException if an illegal
   *              property name is specified
  **/
  public void removeProperty(String name);

  /** Returns true if the <code>MessageContext</code> contains a property
   *  with the specified name.
   *  @param name Name of the property whose presense is to be
   *              tested
   *  @return Returns true if the MessageContext contains the
   *          property; otherwise false
  **/
  public boolean containsProperty(String name);

  /** Returns an Iterator view of the names of the properties
   *  in this <code>MessageContext</code>
   *
   *  @return Iterator for the property names
  **/
  public java.util.Iterator getPropertyNames();
 
}
