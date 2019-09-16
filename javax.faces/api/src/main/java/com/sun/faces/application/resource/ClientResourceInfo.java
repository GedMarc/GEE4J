/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.faces.application.resource;

import static javax.faces.application.ResourceHandler.JSF_SCRIPT_LIBRARY_NAME;
import static javax.faces.application.ResourceHandler.JSF_SCRIPT_RESOURCE_NAME;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.sun.faces.util.FacesLogger;

/**
 * <p/>
 * <code>ClientResourceInfo</code> is a simple wrapper class for information
 * pertinent to building a complete resource path using a Library.
 * <p/>
 */
public class ClientResourceInfo extends ResourceInfo {

    private static final Logger LOGGER = FacesLogger.RESOURCE.getLogger();
    private static final String COMPRESSED_CONTENT_DIRECTORY =
          "jsf-compressed";
    boolean cacheTimestamp;
    boolean isDevStage;
    String compressedPath;
    boolean compressible;
    boolean supportsEL;
    private volatile long lastModified = Long.MIN_VALUE;


    /**
     * Constructs a new <code>ClientResourceInfo</code> using the specified details.
     * The {@link ResourceHelper} of the resource will be the same as the
     * {@link ResourceHelper} of the {@link LibraryInfo}.
     * @param library the library containing this resource
     * @param name the resource name
     * @param version the version of this resource (if any)
     * @param compressible if this resource should be compressed
     * @param supportsEL <code>true</code> if this resource may contain
     *   EL expressions
     * @param isDevStage true if this context is development stage
     * @param cacheTimestamp <code>true</code> if the modification time of the
     *  resource should be cached.  The value of this parameter will be ignored
     *  when {@link #isDevStage} is <code>true</code>
     */
    public ClientResourceInfo(LibraryInfo library,
                        ContractInfo contract,
                        String name,
                        VersionInfo version,
                        boolean compressible,
                        boolean supportsEL,
                        boolean isDevStage,
                        boolean cacheTimestamp) {
        super(library, contract, name, version);
        this.compressible = compressible;
        this.supportsEL = supportsEL;
        this.isDevStage = isDevStage;
        this.cacheTimestamp = (!isDevStage && cacheTimestamp);
        initPath(isDevStage);
    }

    /**
     * Constructs a new <code>ClientResourceInfo</code> using the specified details.
     * @param name the resource name
     * @param version the version of the resource
     * @param localePrefix the locale prefix for this resource (if any)
     * @param helper helper the helper class for this resource
     * @param compressible if this resource should be compressed
     * @param supportsEL <code>true</code> if this resource may contain
     *   EL expressions
     * @param isDevStage true if this context is development stage
     * @param cacheTimestamp <code>true</code> if the modification time of the
     *  resource should be cached.  The value of this parameter will be ignored
     *  when {@link #isDevStage} is <code>true</code>
     */
    ClientResourceInfo(ContractInfo contract,
                 String name,
                 VersionInfo version,
                 String localePrefix,
                 ResourceHelper helper,
                 boolean compressible,
                 boolean supportsEL,
                 boolean isDevStage,
                 boolean cacheTimestamp) {
        super(contract, name, version, helper);
        this.name = name;
        this.version = version;
        this.localePrefix = localePrefix;
        this.helper = helper;
        this.compressible = compressible;
        this.supportsEL = supportsEL;
        this.isDevStage = isDevStage;
        this.cacheTimestamp = (!isDevStage && cacheTimestamp);
        initPath(isDevStage);
    }
    
    ClientResourceInfo(ClientResourceInfo other, boolean copyLocalePrefix) {
        super(other, copyLocalePrefix);
        this.cacheTimestamp = other.cacheTimestamp;
        this.compressedPath = other.compressedPath;
        this.compressible = other.compressible;
        this.isDevStage = other.isDevStage;
        this.lastModified = other.lastModified;
        this.supportsEL = other.supportsEL;
        initPath(isDevStage);
    }
    
    public void copy(ClientResourceInfo other) {
        super.copy(other);
        this.cacheTimestamp = other.cacheTimestamp;
        this.compressedPath = other.compressedPath;
        this.compressible = other.compressible;
        this.isDevStage = other.isDevStage;
        this.lastModified = other.lastModified;
        this.supportsEL = other.supportsEL;
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * @return the path to which the compressed bits for this resource
     *  reside.  If this resource isn't compressible and this method is called,
     *  it will return <code>null</code>
     */
    public String getCompressedPath() {
        return compressedPath;
    }

    /**
     * @return <code>true</code> if this resource should be compressed,
     *  otherwise <code>false</code>
     */
    public boolean isCompressable() {
        return compressible;
    }

    /**
     * @return <code>true</code> if the this resource may contain EL expressions
     *  that should be evaluated, otherwise, return <code>false</code>
     */
    public boolean supportsEL() {
        return supportsEL;
    }

    /**
     * Disables EL evaluation for this resource. 
     */
    public void disableEL() {
        this.supportsEL = false;
    }

    /**
     * Returns the time this resource was last modified.
     * If {@link com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter#CacheResourceModificationTimestamp}
     * is true, the value will be cached for the lifetime if this <code>ClientResourceInfo</code>
     * instance.
     *
     * @param ctx the {@link FacesContext} for the current request
     *
     * @return the time this resource was last modified (number of milliseconds
     *  since January 1, 1970 GMT).
     *
     */
    public long getLastModified(FacesContext ctx) {

        if (cacheTimestamp) {
            if (lastModified == Long.MIN_VALUE) {
                synchronized (this) {
                    if (lastModified == Long.MIN_VALUE) {
                        lastModified = helper.getLastModified(this, ctx);
                    }
                }
            }
            return lastModified;
        } else {
            return helper.getLastModified(this, ctx);
        }
        
    }

    @Override
    public String toString() {
        return "ResourceInfo{" +
               "name='" + name + '\'' +
               ", version=\'" + ((version != null) ? version : "NONE") + '\'' +
               ", libraryName='" + libraryName + '\'' +
               ", contractInfo='" + (contract != null ? contract.contract : "NONE") + '\'' +
               ", libraryVersion='" + ((library != null) ? library.getVersion() : "NONE") + '\'' +
               ", localePrefix='" + ((localePrefix != null) ? localePrefix : "NONE") + '\'' +
               ", path='" + path + '\'' +
               ", compressible='" + compressible + '\'' +
               ", compressedPath=" + compressedPath +
               '}';
    }

    // --------------------------------------------------------- Private Methods


    /**
     * Create the full path to the resource.  If the resource can be compressed,
     * setup the compressedPath ivar so that the path refers to the
     * directory refereneced by the context attribute <code>javax.servlet.context.tempdir</code>.  
     */
    private void initPath(boolean isDevStage) {

        StringBuilder sb = new StringBuilder(32);
        if (library != null) {
            sb.append(library.getPath());
        } else {
            if (null != contract) {
                sb.append(helper.getBaseContractsPath());
                sb.append("/").append(contract);
            } else {
                sb.append(helper.getBaseResourcePath());
            }
        }
        if (library == null && localePrefix != null) {
            sb.append('/').append(localePrefix);
        }
        // Specialcasing for handling JSF script in uncompressed state
        if (isDevStage && JSF_SCRIPT_LIBRARY_NAME.equals(libraryName) && JSF_SCRIPT_RESOURCE_NAME.equals(name)) {
            sb.append('/').append("jsf-uncompressed.js");
        } else {
            sb.append('/').append(name);
        }
        if (version != null) {
            sb.append('/').append(version.getVersion());
            String extension = version.getExtension();
            if (extension != null) {
                sb.append('.').append(extension);    
            }
        }
        path = sb.toString();

        if (compressible && !supportsEL) { // compression for static resources
            FacesContext ctx = FacesContext.getCurrentInstance();
            File servletTmpDir = (File) ctx.getExternalContext()
                  .getApplicationMap().get("javax.servlet.context.tempdir");
            if (servletTmpDir == null || !servletTmpDir.isDirectory()) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               "File ({0}) referenced by javax.servlet.context.tempdir attribute is null, or was is not a directory.  Compression for {1} will be unavailable.",
                               new Object[]{((servletTmpDir == null)
                                             ? "null"
                                             : servletTmpDir.toString()),
                                            path});
                }
                compressible = false;
            } else {
                String tPath = ((path.charAt(0) == '/') ? path : '/' + path);
                File newDir = new File(servletTmpDir, COMPRESSED_CONTENT_DIRECTORY
                                                      + tPath);

                try {
                    if (!newDir.exists()) {
                        if (newDir.mkdirs()) {
                            compressedPath = newDir.getCanonicalPath();
                        } else {
                            compressible = false;
                            if (LOGGER.isLoggable(Level.WARNING)) {
                                LOGGER.log(Level.WARNING,
                                           "jsf.application.resource.unable_to_create_compression_directory",
                                           newDir.getCanonicalPath());
                            }
                        }
                    } else {
                        compressedPath = newDir.getCanonicalPath();
                    }
                } catch (Exception e) {
                	if (LOGGER.isLoggable(Level.SEVERE)) {
	                    LOGGER.log(Level.SEVERE,
	                               e.toString(),
	                               e);
                	}
                    compressible = false;
                }
            }
        }
        
    }

}
