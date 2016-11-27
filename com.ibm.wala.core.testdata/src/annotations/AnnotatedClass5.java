/*******************************************************************************
 * Copyright (c) 2016 Joana IFC project,
 * Programming Paradigms Group,
 * Karlsruhe Institute of Technology (KIT).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    martin.hecker@kit.edu, KIT - 
 *******************************************************************************/
package annotations;

public class AnnotatedClass5 extends @TypeAnnotationTypeUse Object {
  
  @TypeAnnotationTypeUse Integer foo(@TypeAnnotationTypeUse int a, @TypeAnnotationTypeUse Object b) {
    
    @TypeAnnotationTypeUse
    int x = 3;
    
    @TypeAnnotationTypeUse
    Object y = new Object();
    
    if (y instanceof @TypeAnnotationTypeUse String) {
      x = 7;
    }
    
    try {
      throw new NullPointerException();
    } catch (@TypeAnnotationTypeUse RuntimeException e) {
      x = 911;
    }
    
    return x;
  }
}
