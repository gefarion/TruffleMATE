package som.interpreter.nodes.nary;

import som.interpreter.SArguments;
import som.interpreter.TruffleCompiler;
import som.interpreter.nodes.ExpressionNode;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.InstrumentableFactory.WrapperNode;

public class EagerBinaryPrimitiveNode extends EagerPrimitive {

  @Child private ExpressionNode receiver;
  @Child private ExpressionNode argument;
  @Child private BinaryExpressionNode primitive;

  public EagerBinaryPrimitiveNode(
      final SSymbol selector,
      final ExpressionNode receiver,
      final ExpressionNode argument,
      final BinaryExpressionNode primitive) {
    super(primitive.getSourceSection(), selector);
    this.receiver  = receiver;
    this.argument  = argument;
    this.primitive = primitive;
    this.adoptChildren();
  }

  @Override
  public ExpressionNode getReceiver() {
    return receiver;
  }
  protected BinaryExpressionNode getPrimitive() { return primitive; }

  @Override
  public Object executeGenericWithReceiver(final VirtualFrame frame, final Object receiver) {
    Object arg  = argument.executeGeneric(frame);
    return executeEvaluated(frame, receiver, arg);
  }

  public Object executeEvaluated(final VirtualFrame frame,
    final Object receiver, final Object argument) {
    try {
      return primitive.executeEvaluated(frame, receiver, argument);
    } catch (UnsupportedSpecializationException e) {
      TruffleCompiler.transferToInterpreterAndInvalidate("Eager Primitive with unsupported specialization.");
      return replaceWithGenericSend(SArguments.getExecutionLevel(frame)).doPreEvaluated(frame,
          new Object[] {receiver, argument});
    }
  }

  public ExpressionNode getArgument() {
    return argument;
  }

  @Override
  public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
    return executeEvaluated(frame, args[0], args[1]);
  }

  @Override
  protected void tagWith(final byte mask) {
    primitive.tagWith(mask);
  }

  @Override
  protected boolean isTaggedWith(final Class<?> tag) {
    assert !(primitive instanceof WrapperNode);
    return primitive.isTaggedWithIgnoringEagerness(tag);
  }

  @Override
  protected ExpressionNode[] getArgumentNodes() {
    return new ExpressionNode[] {receiver, argument};
  }
}
