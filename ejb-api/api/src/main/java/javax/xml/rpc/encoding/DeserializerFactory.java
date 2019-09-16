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

/** The javax.xml.rpc.encoding.DeserializerFactory is a factory of 
 *  deserializers. A DeserializerFactory is registered with a 
 *  TypeMapping instance as part of the TypeMappingRegistry.
 *
 *  @version   1.0
 *  @author    Rahul Sharma
 *  @see Serializer
**/
public interface DeserializerFactory extends java.io.Serializable {
  
  /** Returns a Deserializer for the specified XML processing
   *  mechanism type.
   * 
   *  @param  mechanismType  XML processing mechanism type [TBD:
   *                         definition of valid constants]
   *  @throws JAXRPCException  If DeserializerFactory does not 
   *          support the specified XML processing mechanism
  **/
  public Deserializer getDeserializerAs(String mechanismType);

  /** Returns a list of all XML processing mechanism types 
   *  supported by this DeserializerFactory.
   *
   *  @return List of unique identifiers for the supported 
   *          XML processing mechanism types
  **/
  public Iterator getSupportedMechanismTypes();
}
