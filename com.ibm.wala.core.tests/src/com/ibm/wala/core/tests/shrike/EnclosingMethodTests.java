/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.core.tests.shrike;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.wala.cfg.exc.intra.IntraprocNullPointerAnalysis;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderFactoryImpl;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.core.tests.util.TestConstants;
import com.ibm.wala.core.tests.util.WalaTestCase;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;
import com.ibm.wala.util.warnings.Warnings;

/**
 * Test validity and precision of intra-procedurel NullpointerException-Analysis {@link IntraprocNullPointerAnalysis}
 * 
 */
public class EnclosingMethodTests extends WalaTestCase {

  private static AnalysisScope scope;

  private static ClassHierarchy cha;

  @BeforeClass
  public static void beforeClass() throws Exception {

    scope = AnalysisScopeReader.readJavaScope(TestConstants.WALA_TESTDATA,
        (new FileProvider()).getFile("J2SEClassHierarchyExclusions.txt"), EnclosingMethodTests.class.getClassLoader());
    ClassLoaderFactory factory = new ClassLoaderFactoryImpl(scope.getExclusions());

    try {
      cha = ClassHierarchy.make(scope, factory);
    } catch (ClassHierarchyException e) {
      throw new Exception();
    }
  }

  @AfterClass
  public static void afterClass() throws Exception {
    Warnings.clear();
    scope = null;
    cha = null;
  }

  public static void main(String[] args) {
    justThisTest(EnclosingMethodTests.class);
  }

  @Test
  public void testParam() throws InvalidClassFileException {
    final TypeReference tEnclosingMethods = TypeReference.findOrCreate(
      ClassLoaderReference.Application,
      TypeName.string2TypeName("Linner/EnclosingMethods")
    );
    final TypeReference tOuter = TypeReference.findOrCreate(
        ClassLoaderReference.Application,
        TypeName.string2TypeName("Linner/EnclosingMethods$Outer")
    );
    final TypeReference tInner = TypeReference.findOrCreate(
        ClassLoaderReference.Application,
        TypeName.string2TypeName("Linner/EnclosingMethods$Outer$Inner")
    );
    final TypeReference tOuterA1 = TypeReference.findOrCreate(
        ClassLoaderReference.Application,
        TypeName.string2TypeName("Linner/EnclosingMethods$Outer$1")
    );
    final TypeReference tInnerA1 = TypeReference.findOrCreate(
        ClassLoaderReference.Application,
        TypeName.string2TypeName("Linner/EnclosingMethods$Outer$Inner$1")
    );


    final ShrikeClass enclosingMethods = (ShrikeClass) cha.lookupClass(tEnclosingMethods);
    final ShrikeClass outer = (ShrikeClass) cha.lookupClass(tOuter);
    final ShrikeClass inner = (ShrikeClass) cha.lookupClass(tInner);
    final ShrikeClass outerA1 = (ShrikeClass) cha.lookupClass(tOuterA1);
    final ShrikeClass innerA1 = (ShrikeClass) cha.lookupClass(tInnerA1);

    final IMethod foo = inner.getAllMethods().toArray(new IMethod[2])[1];
    final MethodReference fooRef = foo.getReference();

    //final IMethod bar = innerA1.getAllMethods().toArray(new IMethod[2])[1];
    //final MethodReference barRef = bar.getReference();
    
    {
      final MethodReference enclosingRef = innerA1.getEnclosingMethodReference();
      final IMethod enclosing = cha.resolveMethod(enclosingRef);
      Assert.assertEquals(foo, enclosing);
    }
    
    {
      final MethodReference enclosingRef = outerA1.getEnclosingMethodReference();
      Assert.assertNull(enclosingRef);
    }
  }
}
