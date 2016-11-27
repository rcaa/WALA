/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     KIT, Martin Hecker - adaptation to type annotations
 *******************************************************************************/
package com.ibm.wala.core.tests.ir;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.core.tests.util.JVMLTestAssertions;
import com.ibm.wala.core.tests.util.TestAssertions;
import com.ibm.wala.core.tests.util.WalaTestCase;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.annotations.Annotation;
import com.ibm.wala.types.annotations.TypeAnnotation;
import com.ibm.wala.util.collections.HashSetFactory;

public class TypeAnnotationTest extends WalaTestCase {

  private final IClassHierarchy cha;
  
  private final TestAssertions harness;
  
  protected TypeAnnotationTest(TestAssertions harness, IClassHierarchy cha) {
    this.cha = cha;
    this.harness = harness;
  }

  public TypeAnnotationTest() throws ClassHierarchyException, IOException {
    this(new JVMLTestAssertions(), WalaTestCase.makeCHA());
  }

  @Test
  public void testClassAnnotations5() throws Exception {
    TypeReference typeUnderTest = TypeReference.findOrCreate(ClassLoaderReference.Application, "Lannotations/AnnotatedClass5");

    Collection<TypeAnnotation> expectedRuntimeInvisibleAnnotations = HashSetFactory.make();
    expectedRuntimeInvisibleAnnotations.add(
        TypeAnnotation.make(
            Annotation.make(TypeReference.findOrCreate(ClassLoaderReference.Application, "Lannotations/TypeAnnotationUse")),
            new TypeAnnotation.SuperTypeTarget(TypeReference.JavaLangObject)
        )
    );

    Collection<TypeAnnotation> expectedRuntimeVisibleAnnotations = HashSetFactory.make();

    testClassAnnotations(typeUnderTest, expectedRuntimeInvisibleAnnotations, expectedRuntimeVisibleAnnotations);
  }

  private void testClassAnnotations(TypeReference typeUnderTest, Collection<TypeAnnotation> expectedRuntimeInvisibleAnnotations,
      Collection<TypeAnnotation> expectedRuntimeVisibleAnnotations) throws IOException, ClassHierarchyException,
      InvalidClassFileException {
    IClass classUnderTest = cha.lookupClass(typeUnderTest);
    harness.assertNotNull(typeUnderTest.toString() + " not found", classUnderTest);
    harness.assertTrue(classUnderTest + " must be BytecodeClass", classUnderTest instanceof ShrikeClass);
    ShrikeClass bcClassUnderTest = (ShrikeClass) classUnderTest;

    Collection<TypeAnnotation> runtimeInvisibleAnnotations = bcClassUnderTest.getTypeAnnotations(true);
    harness.assertEqualCollections(expectedRuntimeInvisibleAnnotations, runtimeInvisibleAnnotations);

    Collection<TypeAnnotation> runtimeVisibleAnnotations = bcClassUnderTest.getTypeAnnotations(false);
    harness.assertEqualCollections(expectedRuntimeVisibleAnnotations, runtimeVisibleAnnotations);
  }
}
