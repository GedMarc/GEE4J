/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.util.cdi11;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InjectionTargetFactory;
import javax.enterprise.util.AnnotationLiteral;

public class CDIUtilImpl implements Serializable, CDIUtil {
    
    private static final long serialVersionUID = -8101770583567814803L;
    
    public CDIUtilImpl() {
        
    }
    
    @Override
    public Bean createHelperBean(BeanManager beanManager, Class beanClass) {
       BeanWrapper result = null;
       
       AnnotatedType annotatedType = beanManager.createAnnotatedType(
               beanClass );
       
       InjectionTargetFactory factory = beanManager.getInjectionTargetFactory(annotatedType);
       
       result = new BeanWrapper(beanClass);
       //use this to create the class and inject dependencies
       final InjectionTarget injectionTarget =
               factory.createInjectionTarget(result);
       result.setInjectionTarget(injectionTarget);
       
       return result;
   }
   
   
   private static class BeanWrapper implements Bean {
       private Class beanClass;
       private InjectionTarget injectionTarget = null;
       
       public BeanWrapper( Class beanClass) {
           this.beanClass = beanClass;
           
       }
       private void setInjectionTarget(InjectionTarget injectionTarget) {
           this.injectionTarget = injectionTarget;
       }
       
       @Override
       public Class<?> getBeanClass() {
           return beanClass;
       }
       
       @Override
       public Set<InjectionPoint> getInjectionPoints() {
           return injectionTarget.getInjectionPoints();
       }
       
       @Override
       public String getName() {
           return null;
       }
       
       @Override
       public Set<Annotation> getQualifiers() {
           Set<Annotation> qualifiers = new HashSet<>();
           qualifiers.add( new DefaultAnnotationLiteral());
           qualifiers.add( new AnyAnnotationLiteral());
           return qualifiers;
       }
       
       public static class DefaultAnnotationLiteral extends AnnotationLiteral<Default> {
           private static final long serialVersionUID = -9065007202240742004L;           
           
       }
       
       public static class AnyAnnotationLiteral extends AnnotationLiteral<Any> {
           private static final long serialVersionUID = -4700109250603725375L;
       }
       
       @Override
       public Class<? extends Annotation> getScope() {
           return Dependent.class;
       }
       
       @Override
       public Set<Class<? extends Annotation>> getStereotypes() {
           return Collections.emptySet();
       }
       
       @Override
       public Set<Type> getTypes() {
           Set<Type> types = new HashSet<>();
           types.add( beanClass );
           types.add( Object.class );
           return types;
       }
       
       @Override
       public boolean isAlternative() {
           return false;
       }
       
       @Override
       public boolean isNullable() {
           return false;
       }
       
       @Override
       public Object create( CreationalContext ctx ) {
           Object instance = injectionTarget.produce( ctx );
           injectionTarget.inject( instance, ctx );
           injectionTarget.postConstruct( instance );
           return instance;
       }
       
       @Override
       public void destroy( Object instance, CreationalContext ctx ) {
           injectionTarget.preDestroy( instance );
           injectionTarget.dispose( instance );
           ctx.release();
       }
   }   
    
}
