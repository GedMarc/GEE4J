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

package javax.xml.rpc;

/** The <code>javax.xml.rpc.ParameterMode</code> is a type-safe
 *  enumeration for parameter mode. This class is used in the
 *  <code>Call</code>API to specify parameter passing modes.
 *
 *  @version 1.0
 *  @author  Rahul Sharma
 *  @see Call
**/

public class ParameterMode {
  
  private final String mode;

  private ParameterMode(String mode) { 
    this.mode = mode; 
  }

  /** Returns a <code>String</code> describing this <code>ParameterMode</code> object. 
   * 
   *  @return  A string representation of the object.
  **/
  public String toString() { return mode; }

  /** IN mode for parameter passing
  **/
  public static final ParameterMode IN = new ParameterMode("IN");

  /** OUT mode for parameter passing
  **/
  public static final ParameterMode OUT = new ParameterMode("OUT");

  /** INOUT mode for parameter passing
  **/
  public static final ParameterMode INOUT  = 
		      new ParameterMode("INOUT");

}
