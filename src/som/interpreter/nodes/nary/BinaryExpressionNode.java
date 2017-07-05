package som.interpreter.nodes.nary;

import som.instrumentation.FixedSizeExpressionWrapperFactory;
import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.PreevaluatedExpression;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Instrumentable;
import com.oracle.truffle.api.source.SourceSection;


@NodeChildren({
  @NodeChild(value = "receiver", type = ExpressionNode.class),
  @NodeChild(value = "argument", type = ExpressionNode.class)})
@Instrumentable(factory = FixedSizeExpressionWrapperFactory.class)
public abstract class BinaryExpressionNode extends EagerlySpecializableNode
    implements ExpressionWithReceiver, PreevaluatedExpression {

  public abstract ExpressionNode getArgument();

  public BinaryExpressionNode(final boolean eagerlyWrapped,
      final SourceSection source) {
    super(eagerlyWrapped, source);
  }

  public abstract Object executeEvaluated(VirtualFrame frame,
      Object receiver, Object argument);

  @Override
  public Object doPreEvaluated(final VirtualFrame frame,
      final Object[] arguments) {
    return executeEvaluated(frame, arguments[0], arguments[1]);
  }

  @Override
  public EagerPrimitive wrapInEagerWrapper(
      final EagerlySpecializableNode prim, final SSymbol selector,
      final ExpressionNode[] arguments, final VirtualFrame frame, final AbstractMessageSpecializationsFactory factory) {
    return factory.binaryPrimitiveFor(selector,
        arguments[0], arguments[1], this, frame);
  }
}
