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

/** Constants used in JAX-RPC for namespace prefixes and URIs
 *  @version 1.0
 *  @author  Rahul Sharma
**/

public class NamespaceConstants {

  /** Namespace prefix for SOAP Envelope
  **/
  public static final String NSPREFIX_SOAP_ENVELOPE = "soapenv"; 

  /** Namespace prefix for SOAP Encoding
  **/
  public static final String NSPREFIX_SOAP_ENCODING = "soapenc"; 

  /** Namespace prefix for XML schema XSD
  **/
  public static final String NSPREFIX_SCHEMA_XSD    = "xsd"; 

  /** Namespace prefix for XML Schema XSI
  **/
  public static final String NSPREFIX_SCHEMA_XSI    = "xsi"; 

  /** Nameapace URI for SOAP 1.1 Envelope
  **/
  public static final String NSURI_SOAP_ENVELOPE    = 
	    "http://schemas.xmlsoap.org/soap/envelope/";

  /** Nameapace URI for SOAP 1.1 Encoding
  **/  
  public static final String NSURI_SOAP_ENCODING    =
	    "http://schemas.xmlsoap.org/soap/encoding/";

  /** Nameapace URI for SOAP 1.1 next actor role
  **/
  public static final String NSURI_SOAP_NEXT_ACTOR  =
	    "http://schemas.xmlsoap.org/soap/actor/next";

  /** Namespace URI for XML Schema XSD
  **/
  public static final String NSURI_SCHEMA_XSD = 
            "http://www.w3.org/2001/XMLSchema";


  /** Namespace URI for XML Schema XSI
  **/
  public static final String NSURI_SCHEMA_XSI =
            "http://www.w3.org/2001/XMLSchema-instance";

}
