package som.primitives;

import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.nary.UnaryBasicOperation;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.primitives.Primitives.Specializer;
import som.vm.constants.Classes;
import tools.debugger.Tags.LiteralTag;
import tools.dym.Tags.OpArithmetic;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;


public abstract class DoublePrims  {

  @GenerateNodeFactory
  @Primitive(klass = "Double", selector = "round", receiverType = Double.class)
  public abstract static class RoundPrim extends UnaryBasicOperation {
    public RoundPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doDouble(final double receiver) {
      return Math.round(receiver);
    }

    @Override
    protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
      if (tag == OpArithmetic.class) { // TODO: is this good enough?
        return true;
      } else {
        return super.isTaggedWithIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Double", selector = "asInteger", receiverType = Double.class)
  public abstract static class AsIntegerPrim extends UnaryBasicOperation {
    public AsIntegerPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doDouble(final double receiver) {
      return (long) receiver;
    }

    @Override
    protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
      if (tag == OpArithmetic.class) { // TODO: is this good enough?
        return true;
      } else {
        return super.isTaggedWithIgnoringEagerness(tag);
      }
    }
  }

  public static class IsDoubleClass extends Specializer<ExpressionNode> {
    public IsDoubleClass(final Primitive prim, final NodeFactory<ExpressionNode> fact) { super(prim, fact); }

    @Override
    public boolean matches(final Object[] args, final ExpressionNode[] argNodess) {
      return args[0] == Classes.doubleClass;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Double Class", selector = "PositiveInfinity",
             noWrapper = true, specializer = IsDoubleClass.class)
  public abstract static class PositiveInfinityPrim extends UnaryExpressionNode {
    public PositiveInfinityPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    protected final boolean receiverIsDoubleClass(final DynamicObject receiver) {
      return receiver == Classes.doubleClass;
    }

    @Specialization(guards = "receiverIsDoubleClass(receiver)")
    public final double doSClass(final DynamicObject receiver) {
      return Double.POSITIVE_INFINITY;
    }

    @Override
    protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
      if (tag == LiteralTag.class) {
        return true;
      } else {
        return super.isTaggedWithIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Double", selector = "floor", receiverType = Double.class)
  public abstract static class FloorPrim extends UnaryBasicOperation {
    public FloorPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doDouble(final double receiver) {
      return (long) Math.floor(receiver);
    }

    @Override
    protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
      if (tag == OpArithmetic.class) { // TODO: is this good enough?
        return true;
      } else {
        return super.isTaggedWithIgnoringEagerness(tag);
      }
    }
  }
}
