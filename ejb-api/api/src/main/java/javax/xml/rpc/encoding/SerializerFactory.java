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

import java.util.Iterator;
import javax.xml.rpc.JAXRPCException;

/** The javax.xml.rpc.encoding.SerializerFactory is a factory of 
 *  the serializers. A SerializerFactory is registered with a 
 *  TypeMapping object as part of the TypeMappingRegistry.
 *
 *  @version   1.0
 *  @author    Rahul Sharma
 *  @see Serializer
**/
public interface SerializerFactory extends java.io.Serializable {
 
  /** Returns a Serializer for the specified XML processing
   *  mechanism type.
   * 
   *  @param  mechanismType  XML processing mechanism type [TBD:
   *                         definition of valid constants]
   *  @throws JAXRPCException  If SerializerFactory does not 
   *          support the specified XML processing mechanism
   *  @throws IllegalArgumentException If an invalid
   *          mechanism type is specified.
  **/
  public Serializer getSerializerAs(String mechanismType);

  /** Returns a list of all XML processing mechanism types 
   *  supported by this SerializerFactory.
   *
   *  @return List of unique identifiers for the supported 
   *          XML processing mechanism types
  **/
  public Iterator getSupportedMechanismTypes();

}
