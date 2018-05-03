package som.interpreter.nodes.nary;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.source.SourceSection;

import som.instrumentation.FixedSizeExpressionWrapper;
import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.PreevaluatedExpression;
import som.vmobjects.SSymbol;


@NodeChild(value = "receiver", type = ExpressionNode.class)
public abstract class UnaryExpressionNode extends EagerlySpecializableNode
    implements ExpressionWithReceiver, PreevaluatedExpression {

  public UnaryExpressionNode(final boolean eagerlyWrapped,
      final SourceSection source) {
    super(eagerlyWrapped, source);
  }

  @Override
  public WrapperNode createWrapper(final ProbeNode probeNode) {
    return new FixedSizeExpressionWrapper(this, probeNode);
  }

  public abstract Object executeEvaluated(VirtualFrame frame,
      Object receiver);

  @Override
  public final Object doPreEvaluated(final VirtualFrame frame,
      final Object[] arguments) {
    return executeEvaluated(frame, arguments[0]);
  }

  public Object[] evaluateArguments(final VirtualFrame frame) {
    Object[] arguments = new Object[1];
    arguments[0] = this.getReceiver().executeGeneric(frame);
    return arguments;
  }

  @Override
  public EagerPrimitive wrapInEagerWrapper(
      final EagerlySpecializableNode prim, final SSymbol selector,
      final ExpressionNode[] arguments, final VirtualFrame frame, final AbstractMessageSpecializationsFactory factory) {
    return factory.unaryPrimitiveFor(selector,
        arguments[0], this, frame);
  }
}
