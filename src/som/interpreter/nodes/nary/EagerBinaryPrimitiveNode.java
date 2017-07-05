package som.interpreter.nodes.nary;

import som.interpreter.SArguments;
import som.interpreter.TruffleCompiler;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode;
import som.interpreter.nodes.MessageSendNode.GenericMessageSendNode;
import som.vm.Universe;
import som.vm.constants.ExecutionLevel;
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
      return makeGenericSend(SArguments.getExecutionLevel(frame)).doPreEvaluated(frame,
          new Object[] {receiver, argument});
    }
  }

  private GenericMessageSendNode makeGenericSend(final ExecutionLevel level) {
    Universe.getCurrent().insertInstrumentationWrapper(this);
    GenericMessageSendNode node = MessageSendNode.createGeneric(selector,
        new ExpressionNode[] {receiver, argument}, getSourceSection(), level, this.getFactory());
    if (argument.getParent() instanceof WrapperNode) {
      // Disable previous wrapping of receiver node
      Universe.getCurrent().insertInstrumentationWrapper(argument);
    }
    Universe.getCurrent().insertInstrumentationWrapper(argument);
    return replace(node);
  }

  public ExpressionNode getArgument() {
    return argument;
  }

  @Override
  public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
    return executeEvaluated(frame, args[0], args[1]);
  }

  @Override
  protected void setTags(final byte tagMark) {
    primitive.tagMark = tagMark;
  }

  @Override
  protected boolean isTaggedWith(final Class<?> tag) {
    assert !(primitive instanceof WrapperNode);
    boolean result = super.isTaggedWith(tag) ? super.isTaggedWith(tag) : primitive.isTaggedWith(tag);
    return result;
  }
}
