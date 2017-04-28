package som.primitives;

import java.util.EnumSet;

import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.QuaternaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.constants.Nil;
import som.vmobjects.MockJavaObject;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;
import som.vmobjects.SShape;
import som.vmobjects.SSymbol;
import som.vmobjects.SReflectiveObjectLayoutImpl.SReflectiveObjectType;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.HiddenKey;
import com.oracle.truffle.api.object.LocationModifier;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.source.SourceSection;


public class ShapePrims {
  @GenerateNodeFactory
  @Primitive(klass = "Shape class", selector = "newWithFieldsCount:",
             eagerSpecializable = false, mate = true)
  public abstract static class MateNewShapePrim extends BinaryExpressionNode {
    public MateNewShapePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SAbstractObject doSClass(final DynamicObject receiver, final long fieldsCount) {
      return new SShape((int) fieldsCount);
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Shape", selector = "fieldsCount",
             eagerSpecializable = false, mate = true)
  public abstract static class MateShapeFieldsCountPrim extends UnaryExpressionNode {
    public MateShapeFieldsCountPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final long doSShape(SShape shape) {
      return shape.getShape().getPropertyCount();
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Shape", selector = "installEnvironment:",
             eagerSpecializable = false, mate = true)
  public abstract static class MateInstallEnvironmentInShapePrim extends BinaryExpressionNode {
    public MateInstallEnvironmentInShapePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SShape doSObject(SShape shape, DynamicObject environment) {
      return new SShape(
          shape.getShape().changeType(
              ((SReflectiveObjectType) shape.getShape().getObjectType()).setEnvironment(environment)));
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Shape", selector = "installClass:",
             eagerSpecializable = false, mate = true)
  public abstract static class MateInstallClassInShapePrim extends BinaryExpressionNode {
    public MateInstallClassInShapePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SShape doSObject(SShape shape, DynamicObject klass) {
      return new SShape(
          shape.getShape().changeType(
              ((SReflectiveObjectType) shape.getShape().getObjectType()).setKlass(klass)));
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Shape", selector = "define:final:hidden:",
             eagerSpecializable = false, mate = true)
  public abstract static class DefinePropertyInShapePrim extends QuaternaryExpressionNode {
    public DefinePropertyInShapePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SArray doSSymbol(SShape shape, SSymbol keyValue, boolean isFinal, boolean hidden) {
      Object key;
      if (hidden) {
        key = new HiddenKey(keyValue.getString());
      } else {
        key = keyValue.getString();
      }
      Property environment = Property.create(key,
          shape.getShape().allocator().locationForType(com.oracle.truffle.api.object.DynamicObject.class, EnumSet.of(LocationModifier.NonNull, LocationModifier.Final)),
          0);
      return SArray.create(new Object[]{new MockJavaObject(key, Nil.nilObject),
          new SShape(shape.getShape().addProperty(environment))});
    }
    
    @Specialization
    public final SArray doLong(SShape shape, long key, boolean isFinal, boolean hidden) {
      // If key is an instance variable index it is not hidden nor final
      Property environment = Property.create(key,
          shape.getShape().allocator().locationForType(com.oracle.truffle.api.object.DynamicObject.class,
              EnumSet.of(LocationModifier.NonNull)),
          0);
      return SArray.create(new Object[]{key,
          new SShape(shape.getShape().addProperty(environment))});
    }
  }
}
