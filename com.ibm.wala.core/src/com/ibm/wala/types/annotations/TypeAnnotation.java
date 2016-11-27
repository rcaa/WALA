/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.types.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.shrikeCT.AnnotationsReader;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader;
import com.ibm.wala.shrikeCT.AnnotationsReader.AnnotationAttribute;
import com.ibm.wala.shrikeCT.AnnotationsReader.ElementValue;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TargetInfo;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TargetType;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeAnnotationAttribute;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeAnnotationLocation;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeAnnotationTargetVisitor;
import com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypePathKind;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.Pair;


public class TypeAnnotation {
  
  private final Annotation annotation;
  private final List<Pair<TypePathKind, Integer>> typePath;
  private final TypeAnnotationTarget typeAnnotationTarget;
    
  private TypeAnnotation(Annotation annotation, List<Pair<TypePathKind, Integer>> typePath, TypeAnnotationTarget typeAnnotationTarget) {
    this.annotation = annotation;
    this.typePath = Collections.unmodifiableList(typePath);
    this.typeAnnotationTarget = typeAnnotationTarget;
  }
  
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
    result = prime * result + ((typeAnnotationTarget == null) ? 0 : typeAnnotationTarget.hashCode());
    result = prime * result + ((typePath == null) ? 0 : typePath.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TypeAnnotation other = (TypeAnnotation) obj;
    if (annotation == null) {
      if (other.annotation != null)
        return false;
    } else if (!annotation.equals(other.annotation))
      return false;
    if (typeAnnotationTarget == null) {
      if (other.typeAnnotationTarget != null)
        return false;
    } else if (!typeAnnotationTarget.equals(other.typeAnnotationTarget))
      return false;
    if (typePath == null) {
      if (other.typePath != null)
        return false;
    } else if (!typePath.equals(other.typePath))
      return false;
    return true;
  }


  public static Collection<TypeAnnotation> getTypeAnnotationsFromReader(TypeAnnotationsReader r, TypeAnnotationTargetConverter converter, ClassLoaderReference clRef) throws InvalidClassFileException {
    if (r != null) {
      TypeAnnotationAttribute[] allTypeAnnotations = r.getAllTypeAnnotations();
      
      Collection<TypeAnnotation> result = new ArrayList<>(allTypeAnnotations.length);
      for (TypeAnnotationAttribute tatt : allTypeAnnotations) {
        final Collection<Annotation> annotations = 
            Annotation.convertToAnnotations(clRef, new AnnotationAttribute[] { tatt.annotationAttribute});
        assert annotations.size() == 1;
        
        final Annotation annotation = annotations.iterator().next();
        
        result.add(
          new TypeAnnotation(
            annotation,
            tatt.typePath,
            tatt.annotationTarget.acceptVisitor(converter)
          )
        );
      }
      return result;
    } else {
      return Collections.emptySet();
    }
    
  }
  
  public static TypeAnnotation make(Annotation annotation, List<Pair<TypePathKind, Integer>> typePath, TypeAnnotationTarget typeAnnotationTarget) {
    return new TypeAnnotation(annotation, typePath, typeAnnotationTarget);
  }

  public static TypeAnnotation make(Annotation annotation, TypeAnnotationTarget typeAnnotationTarget) {
    return new TypeAnnotation(annotation, TypeAnnotationsReader.TYPEPATH_EMPTY, typeAnnotationTarget);
  }
  
  public static abstract class TypeAnnotationTarget {
    public static final int INSTRUCTION_INDEX_UNAVAILABLE = -1;
  }
  
  
  public static class TypeParameterTarget extends TypeAnnotationTarget {
    private final int type_parameter_index;
    public TypeParameterTarget(int type_parameter_index) {
      this.type_parameter_index = type_parameter_index;
    }

    public int getIndex() {
      return type_parameter_index;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + type_parameter_index;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TypeParameterTarget other = (TypeParameterTarget) obj;
      if (type_parameter_index != other.type_parameter_index)
        return false;
      return true;
    }
  }

  public static class SuperTypeTarget extends TypeAnnotationTarget {
    private final TypeReference superType;
    public SuperTypeTarget(TypeReference superType) {
      this.superType = superType;
    }

    public TypeReference getSuperType() {
      return superType;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((superType == null) ? 0 : superType.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SuperTypeTarget other = (SuperTypeTarget) obj;
      if (superType == null) {
        if (other.superType != null)
          return false;
      } else if (!superType.equals(other.superType))
        return false;
      return true;
    }
  }
  
  public static class TypeParameterBoundTarget extends TypeAnnotationTarget {
    private final int type_parameter_index;
    private final int bound_index;
    
    public TypeParameterBoundTarget(int type_parameter_index, int bound_index) {
      this.type_parameter_index = type_parameter_index;
      this.bound_index = bound_index;
    }

    public int getParameterIndex() {
      return type_parameter_index;
    }

    public int getBoundIndex() {
      return bound_index;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + bound_index;
      result = prime * result + type_parameter_index;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TypeParameterBoundTarget other = (TypeParameterBoundTarget) obj;
      if (bound_index != other.bound_index)
        return false;
      if (type_parameter_index != other.type_parameter_index)
        return false;
      return true;
    }

    
  }

  public static class EmptyTarget extends TypeAnnotationTarget {
    public EmptyTarget() {
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      return true;
    }
  }

  
  public static class FormalParameterTarget  extends TypeAnnotationTarget {
    private final int formal_parameter_index;
    public FormalParameterTarget(int index) {
      this.formal_parameter_index = index;
    }

    public int getIndex() {
      return formal_parameter_index;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + formal_parameter_index;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      FormalParameterTarget other = (FormalParameterTarget) obj;
      if (formal_parameter_index != other.formal_parameter_index)
        return false;
      return true;
    }
  }
  
  public static class ThrowsTarget extends TypeAnnotationTarget {
    private final TypeReference throwType;
    public ThrowsTarget(TypeReference throwType) {
      this.throwType = throwType;
    }

    public TypeReference getThrowType() {
      return throwType;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((throwType == null) ? 0 : throwType.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ThrowsTarget other = (ThrowsTarget) obj;
      if (throwType == null) {
        if (other.throwType != null)
          return false;
      } else if (!throwType.equals(other.throwType))
        return false;
      return true;
    }
  }
  
  public static class LocalVarTarget extends TypeAnnotationTarget {
    // TODO: we might want to relate bytecode-ranges to ranges in the
    // IInstruction[] array returned by IBytecodeMethod.getInstructions(),
    // but is this even meaningful?
    private final int     varIindex;
    private final String  name;
    public LocalVarTarget(int varIindex, String name) {
      this.varIindex = varIindex;
      this.name = name;
    }

    public int getIndex() {
      return varIindex;
    }

    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + varIindex;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      LocalVarTarget other = (LocalVarTarget) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (varIindex != other.varIindex)
        return false;
      return true;
    }
  }
  
  public static class CatchTarget extends TypeAnnotationTarget {
    // TODO: as per LocalVarTarget, can we record a meaningful range in terms of IInstriction[]
    private final int catchIIndex;
    private final TypeReference catchType;

    // TODO: or should this be TypeReference.JavaLangThrowable?
    public static final TypeReference ALL_EXCEPTIONS = null;

    public CatchTarget(int catchIIndex, TypeReference catchType) {
      this.catchIIndex = catchIIndex;
      this.catchType = catchType;
    }

    /**
     * @return the handlers type, or {@link CatchTarget#ALL_EXCEPTIONS}
     */
    public TypeReference getCatchType() {
      return catchType;
    }

    /**
     * @return the handlers instruction index (if available),
     *         or {@link TypeAnnotationTarget#INSTRUCTION_INDEX_UNAVAILABLE} otherwise.
     */
    public int getCatchIIndex() {
      return catchIIndex;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + catchIIndex;
      result = prime * result + ((catchType == null) ? 0 : catchType.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CatchTarget other = (CatchTarget) obj;
      if (catchIIndex != other.catchIIndex)
        return false;
      if (catchType == null) {
        if (other.catchType != null)
          return false;
      } else if (!catchType.equals(other.catchType))
        return false;
      return true;
    }
  }
  
  public static class OffsetTarget extends TypeAnnotationTarget {
    private final int iindex;

    public OffsetTarget(int iindex) {
      this.iindex = iindex;
    }

    /**
     * @return the targets instruction index (if available), 
     *         or {@link TypeAnnotationTarget#INSTRUCTION_INDEX_UNAVAILABLE} otherwise.
     */
    public int getIIndex() {
      return iindex;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + iindex;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      OffsetTarget other = (OffsetTarget) obj;
      if (iindex != other.iindex)
        return false;
      return true;
    }
  }
  
  public static class TypeArgumentTarget extends TypeAnnotationTarget {
    private final int iindex;
    private final int type_argument_index;

    public TypeArgumentTarget(int iindex, int type_argument_index) {
      this.iindex = iindex;
      this.type_argument_index = type_argument_index;
    }

    public int getOffset() {
      return iindex;
    }

    public int getType_argument_index() {
      return type_argument_index;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + iindex;
      result = prime * result + type_argument_index;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TypeArgumentTarget other = (TypeArgumentTarget) obj;
      if (iindex != other.iindex)
        return false;
      if (type_argument_index != other.type_argument_index)
        return false;
      return true;
    }
  }
  
  private static TypeReference fromString(ClassLoaderReference clRef, String typeName) {
    return TypeReference.findOrCreate(clRef, typeName.replaceAll(";", ""));
  }

  private static boolean mayAppearIn(TargetInfo info, TypeAnnotationLocation location) {
    for (TargetType targetType : TargetType.values()) {
      if (targetType.target_info == info && targetType.location == location) return true; 
    }
    return false;
  }

  public static interface TypeAnnotationTargetConverter extends TypeAnnotationTargetVisitor<TypeAnnotationTarget> {}
  
  public static TypeAnnotationTargetConverter targetConverterAtCode(final ClassLoaderReference clRef, final IBytecodeMethod method) {
    return new TypeAnnotationTargetConverter() {
      @Override
      public TypeAnnotationTarget visitTypeParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitSuperTypeTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.SuperTypeTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeParameterBoundTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterBoundTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitEmptyTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.EmptyTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitFormalParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.FormalParameterTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitThrowsTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.ThrowsTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitLocalVarTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.LocalVarTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        
        // TODO: is this even allowed? Should we have thrown an exception earlier?
        if (target.getNrOfRanges() == 0) return new LocalVarTarget(-1, null);
        
        final int varIndex = target.getIndex(0);
        final String name = method.getLocalVariableName(target.getStartPc(0), target.getIndex(0));
        for (int i = 0; i < target.getNrOfRanges(); i++) {
          if ( target.getIndex(i) != varIndex) throw new IllegalArgumentException();
          if (!method.getLocalVariableName(target.getStartPc(0), target.getIndex(0)).equals(name)) {
            throw new IllegalArgumentException();
          }
        }
        return new LocalVarTarget(varIndex, name);
      }

      @Override
      public TypeAnnotationTarget visitCatchTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.CatchTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        
        final TypeReference catchType;
        if (target.getCatchType() == com.ibm.wala.shrikeCT.TypeAnnotationsReader.CatchTarget.ALL_EXCEPTIONS) {
          catchType = CatchTarget.ALL_EXCEPTIONS;
        } else {
          catchType = fromString(clRef, target.getCatchType());
        }
        
        
        // well this is awkward..
        try {
          final int catchIIndex = method.getInstructionIndex(target.getCatchPC());
          return new CatchTarget(catchIIndex, catchType);
        } catch (InvalidClassFileException e) {
          return new CatchTarget(TypeAnnotationTarget.INSTRUCTION_INDEX_UNAVAILABLE, catchType);
        }
        
      }

      @Override
      public TypeAnnotationTarget visitOffsetTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.OffsetTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        try {
          final int iindex = method.getInstructionIndex(target.getOffset());
          return new OffsetTarget(iindex);
        } catch (InvalidClassFileException e) {
          return new OffsetTarget(TypeAnnotationTarget.INSTRUCTION_INDEX_UNAVAILABLE);
        }
      }

      @Override
      public TypeAnnotationTarget visitTypeArgumentTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeArgumentTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.Code);
        try {
          final int iindex = method.getInstructionIndex(target.getOffset());
          return new TypeArgumentTarget(iindex, target.getTypeArgumentIndex());
        } catch (InvalidClassFileException e) {
          return new TypeArgumentTarget(TypeAnnotationTarget.INSTRUCTION_INDEX_UNAVAILABLE, target.getTypeArgumentIndex());
        }
      }
    };
  }

  // TODO: method is currently unused, but we may want to use it if we decide to resolve generic signature indices here
  public static TypeAnnotationTargetConverter targetConverterAtMethodInfo(final ClassLoaderReference clRef, final IBytecodeMethod method) {
    return new TypeAnnotationTargetConverter() {
      @Override
      public TypeAnnotationTarget visitTypeParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        return new TypeParameterTarget(target.getIndex()); 
      }

      @Override
      public TypeAnnotationTarget visitSuperTypeTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.SuperTypeTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeParameterBoundTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterBoundTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        return new TypeParameterBoundTarget(target.getParameterIndex(), target.getBoundIndex());
      }

      @Override
      public TypeAnnotationTarget visitEmptyTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.EmptyTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        return new EmptyTarget();
      }

      @Override
      public TypeAnnotationTarget visitFormalParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.FormalParameterTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        return new FormalParameterTarget(target.getIndex());
      }

      @Override
      public TypeAnnotationTarget visitThrowsTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.ThrowsTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        return new ThrowsTarget(fromString(clRef, target.getThrowType()));
      }

      @Override
      public TypeAnnotationTarget visitLocalVarTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.LocalVarTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitCatchTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.CatchTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitOffsetTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.OffsetTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeArgumentTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeArgumentTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.method_info);
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public static TypeAnnotationTargetConverter targetConverterAtClassFile(final ClassLoaderReference clRef) {
    return new TypeAnnotationTargetConverter() {
      @Override
      public TypeAnnotationTarget visitTypeParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        return new TypeParameterTarget(target.getIndex()); 
      }

      @Override
      public TypeAnnotationTarget visitSuperTypeTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.SuperTypeTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        return new SuperTypeTarget(fromString(clRef, target.getSuperType()));
      }

      @Override
      public TypeAnnotationTarget visitTypeParameterBoundTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterBoundTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        return new TypeParameterBoundTarget(target.getParameterIndex(), target.getBoundIndex());
      }

      @Override
      public TypeAnnotationTarget visitEmptyTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.EmptyTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitFormalParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.FormalParameterTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitThrowsTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.ThrowsTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitLocalVarTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.LocalVarTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitCatchTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.CatchTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitOffsetTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.OffsetTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeArgumentTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeArgumentTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.ClassFile);
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public static TypeAnnotationTargetConverter targetConverterAtFieldInfo(final ClassLoaderReference clRef) {
    return new TypeAnnotationTargetConverter() {
      @Override
      public TypeAnnotationTarget visitTypeParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitSuperTypeTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.SuperTypeTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeParameterBoundTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeParameterBoundTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitEmptyTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.EmptyTarget target) {
        assert mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        return new EmptyTarget();
      }

      @Override
      public TypeAnnotationTarget visitFormalParameterTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.FormalParameterTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitThrowsTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.ThrowsTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitLocalVarTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.LocalVarTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitCatchTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.CatchTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitOffsetTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.OffsetTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }

      @Override
      public TypeAnnotationTarget visitTypeArgumentTarget(com.ibm.wala.shrikeCT.TypeAnnotationsReader.TypeArgumentTarget target) {
        assert !mayAppearIn(target.getTargetInfo(), TypeAnnotationLocation.field_info);
        throw new UnsupportedOperationException();
      }
    };
  }

  public Annotation getAnnotation() {
    return annotation;
  }

  public List<Pair<TypePathKind, Integer>> getTypePath() {
    return typePath;
  }

  public TypeAnnotationTarget getTypeAnnotationTarget() {
    return typeAnnotationTarget;
  }
}
