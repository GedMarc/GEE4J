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

package javax.xml.rpc.encoding;

import java.io.Serializable;
import javax.xml.rpc.JAXRPCException;
import java.util.Iterator;

/** The interface <code>javax.xml.rpc.encoding.TypeMappingRegistry</code>
 *  defines a registry of TypeMapping instances for various encoding
 *  styles.
 *
 *  @version   1.0
 *  @author    Rahul Sharma
**/

public interface TypeMappingRegistry extends Serializable {

  /** Registers a <code>TypeMapping</code> instance with the 
   *  <code>TypeMappingRegistry</code>. This method replaces any
   *  existing registered <code>TypeMapping</code> instance for 
   *  the specified <code>encodingStyleURI</code>.
   *
   *  @param encodingStyleURI An encoding style specified as an URI. 
   *                   An example is "http://schemas.xmlsoap.org/soap/encoding/"
   *  @param mapping   TypeMapping instance
   *  @return Previous TypeMapping associated with the specified
   *          <code>encodingStyleURI</code>, or <code>null</code> 
   *          if there was no TypeMapping associated with the specified 
   *          <code>encodingStyleURI</code>
   *  @throws JAXRPCException If there is an error in the
   *                   registration of the <code>TypeMapping</code> for
   *                   the specified <code>encodingStyleURI</code>.
  **/
  public TypeMapping register(String encodingStyleURI, TypeMapping mapping);

  /** Registers the <code>TypeMapping</code> instance that is default
   *  for all encoding styles supported by the 
   *  <code>TypeMappingRegistry</code>. A default <code>TypeMapping</code>
   *  should include serializers and deserializers that are independent 
   *  of and usable with any encoding style. Successive invocations 
   *  of the <code>registerDefault</code> method replace any existing 
   *  default <code>TypeMapping</code> instance.
   *
   *  <p>If the default <code>TypeMapping</code> is registered, any 
   *  other TypeMapping instances registered through the 
   *  <code>TypeMappingRegistry.register</code> method (for a set 
   *  of encodingStyle URIs) override the default <code>TypeMapping</code>.
   *
   *  @param mapping   TypeMapping instance
   *  @throws JAXRPCException If there is an error in the
   *                  registration of the default <code>TypeMapping</code>
  **/
  public void registerDefault(TypeMapping mapping);
  
  /** Gets the registered default <code>TypeMapping</code> instance. 
   *  This method returns <code>null</code> if there is no registered
   *  default TypeMapping in the registry.
   *  @return The registered default <code>TypeMapping</code> instance 
   *          or <code>null</code>
  **/
  public TypeMapping getDefaultTypeMapping();


  /** Returns a list of registered encodingStyle URIs in this
   *  <code>TypeMappingRegistry</code> instance.
   *
   *  @return Array of the registered encodingStyle URIs
  **/
  public String[] getRegisteredEncodingStyleURIs();

  /** Returns the registered <code>TypeMapping</code> for the specified 
   *  encodingStyle URI. If there is no registered <code>TypeMapping</code>
   *  for the specified <code>encodingStyleURI</code>, this method 
   *  returns <code>null</code>. 
   *
   *  @param encodingStyleURI Encoding style specified as an URI
   *  @return TypeMapping for the specified encodingStyleURI or
   *          <code>null</code>
  **/
  public TypeMapping getTypeMapping(String encodingStyleURI); 

  /** Creates a new empty <code>TypeMapping</code> object.
   *
   *  @return TypeMapping instance
  **/
  public TypeMapping createTypeMapping();

  /** Unregisters a TypeMapping instance, if present, from the 
   *  specified encodingStyleURI.
   *
   *  @param  encodingStyleURI Encoding style specified as an URI
   *  @return <code>TypeMapping</code> instance that has been unregistered
   *          or <code>null</code> if there was no TypeMapping 
   *          registered for the specified <code>encodingStyleURI</code>
  **/
  public TypeMapping unregisterTypeMapping(String encodingStyleURI);  

  /** Removes a <code>TypeMapping</code> from the TypeMappingRegistry. A
   *  <code>TypeMapping</code> is associated with 1 or more 
   *  encodingStyleURIs. This method unregisters the specified 
   *  <code>TypeMapping</code> instance from all associated 
   *  <code>encodingStyleURIs</code> and then removes this 
   *  TypeMapping instance from the registry.
   *
   *  @param mapping TypeMapping to be removed
   *  @return <code>true</code> if specified <code>TypeMapping</code> 
   *          is removed from the TypeMappingRegistry; <code>false</code>
   *          if the specified <code>TypeMapping</code> was not in the 
   *          <code>TypeMappingRegistry</code>
  **/
  public boolean removeTypeMapping(TypeMapping mapping);

  /** Removes all registered TypeMappings and encodingStyleURIs 
   *  from this TypeMappingRegistry.
  **/
  public void clear();
}
