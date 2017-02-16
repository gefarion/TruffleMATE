package som.primitives;

import som.interpreter.SArguments;
import som.interpreter.nodes.dispatch.AbstractDispatchNode;
import som.interpreter.nodes.dispatch.UninitializedValuePrimDispatchNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.primitives.arrays.ToHostArrayNode.ToArgumentsArrayNode;
import som.primitives.arrays.ToHostArrayNodeFactory.ToArgumentsArrayNodeFactory;
import som.vm.Universe;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;
import som.vmobjects.SBlock;
import tools.dym.Tags.OpClosureApplication;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.source.SourceSection;


public abstract class BlockPrims {

  public interface ValuePrimitiveNode {
    void adoptNewDispatchListHead(AbstractDispatchNode node);
  }

  @GenerateNodeFactory
  @Primitive(klass = "Block", selector = "restart", eagerSpecializable = false)
  public abstract static class RestartPrim extends UnaryExpressionNode {
    public RestartPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public SAbstractObject doSBlock(final SBlock receiver) {
      CompilerDirectives.transferToInterpreter();
      // TruffleSOM intrinsifies #whileTrue: and #whileFalse:
      throw new RuntimeException("This primitive is not supported anymore! "
          + "Something went wrong with the intrinsification of "
          + "#whileTrue:/#whileFalse:?");
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Block", selector = "valueWithArguments:",
             receiverType = {SBlock.class}, extraChild = ToArgumentsArrayNodeFactory.class)
  @NodeChild(value = "argConversion", type = ToArgumentsArrayNode.class,
      executeWith = {"argument", "receiver"})
  public abstract static class ValueWithArgumentsPrim extends BinaryExpressionNode
    implements ValuePrimitiveNode {
    @Child private AbstractDispatchNode dispatchNode;

    public ValueWithArgumentsPrim(final boolean eagWrap) {
      super(eagWrap, Universe.emptySource.createUnavailableSection());
    }

    public ValueWithArgumentsPrim(final boolean eagerlyWrapped, SourceSection source) {
      super(eagerlyWrapped, source);
      dispatchNode = new UninitializedValuePrimDispatchNode(this.sourceSection);
    }

    @Specialization
    public final Object doSBlock(final VirtualFrame frame, final SBlock receiver, final SArray arguments,
        Object[] convertedArgs) {
      return dispatchNode.executeDispatch(frame, SArguments.getEnvironment(frame),
          SArguments.getExecutionLevel(frame), convertedArgs);
    }

    @Override
    public final void adoptNewDispatchListHead(final AbstractDispatchNode node) {
      dispatchNode = insert(node);
    }

    @Override
    public NodeCost getCost() {
      int dispatchChain = dispatchNode.lengthOfDispatchChain();
      if (dispatchChain == 0) {
        return NodeCost.UNINITIALIZED;
      } else if (dispatchChain == 1) {
        return NodeCost.MONOMORPHIC;
      } else if (dispatchChain <= AbstractDispatchNode.INLINE_CACHE_SIZE) {
        return NodeCost.POLYMORPHIC;
      } else {
        return NodeCost.MEGAMORPHIC;
      }
    }

    @Override
    protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
      if (tag == OpClosureApplication.class) {
        return true;
      } else {
        return super.isTaggedWith(tag);
      }
    }
  }
}
