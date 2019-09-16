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

/** The javax.xml.rpc.encoding.Serializer interface defines the 
 *  base interface for serializers. A Serializer converts
 *  a Java object to an XML representation using a specific XML
 *  processing mechanism and based on the specified type 
 *  mapping and encoding style. 
 *
 *  @version   1.0
 *  @author    Rahul Sharma
**/
public interface Serializer extends Serializable {

  /** Gets the type of the XML processing mechanism and 
   *  representation used by this Serializer.
   *
   *  @return XML processing mechanism type
  **/
  public String getMechanismType();
                                
}
