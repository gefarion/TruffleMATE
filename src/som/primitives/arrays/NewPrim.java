package som.primitives.arrays;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.object.DynamicObject;

import bd.primitives.Primitive;
import bd.primitives.Specializer;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.vm.Universe;
import som.vm.constants.Classes;
import som.vmobjects.SArray;
import som.vmobjects.SClass;
import som.vmobjects.SSymbol;
import tools.dym.Tags.NewArray;

@GenerateNodeFactory
@Primitive(className = "Array class", primitive = "new:", selector = "new:",
           specializer = NewPrim.IsArrayClass.class)
public abstract class NewPrim extends BinaryExpressionNode {

  public static class IsArrayClass extends Specializer<Universe, ExpressionNode, SSymbol> {
    public IsArrayClass(final Primitive prim, final NodeFactory<ExpressionNode> fact) {
      super(prim, fact);
    }

    @Override
    public boolean matches(final Object[] args, final ExpressionNode[] argNodes) {
      return receiverIsArrayClass((DynamicObject) args[0]);
    }
  }

  protected static final boolean receiverIsArrayClass(final DynamicObject receiver) {
    return receiver == Classes.arrayClass;
  }

  protected static final boolean receiverIsByteArrayClass(final DynamicObject receiver) {
    return (SClass.getName(receiver).getString().equals("ByteArray"));
  }

  @Specialization(guards = "receiverIsArrayClass(receiver)")
  public final SArray doSClass(final DynamicObject receiver, final long length) {
    return new SArray(length);
  }

  @Specialization(guards = "receiverIsByteArrayClass(receiver)")
  public final SArray doByteSClass(final DynamicObject receiver, final long length) {
    return SArray.create(new byte[(int) length]);
  }

  @Override
  protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
    if (tag == NewArray.class) {
      return true;
    } else {
      return super.hasTagIgnoringEagerness(tag);
    }
  }
}
