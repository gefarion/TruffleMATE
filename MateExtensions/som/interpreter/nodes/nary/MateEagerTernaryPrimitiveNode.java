package som.interpreter.nodes.nary;

import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode;
import som.matenodes.IntercessionHandling;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.frame.VirtualFrame;


public class MateEagerTernaryPrimitiveNode extends EagerTernaryPrimitiveNode {
  @Child private IntercessionHandling messageSend;
  @Child private IntercessionHandling primitiveActivation;

  public MateEagerTernaryPrimitiveNode(final SSymbol selector, final ExpressionNode receiver, final ExpressionNode argument1, final ExpressionNode argument2,
      final TernaryExpressionNode primitive) {
    super(selector, receiver, argument1, argument2, primitive);
    messageSend = IntercessionHandling.createForMessageLookup(this.getSelector());
    primitiveActivation = IntercessionHandling.createForOperation(this.getPrimitive().reflectiveOperation());
    this.adoptChildren();
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    Object rcvr = this.getReceiver().executeGeneric(frame);
    Object arg1 = this.getFirstArg().executeGeneric(frame);
    Object arg2 = this.getSecondArg().executeGeneric(frame);
    return this.doPreEvaluated(frame, new Object[] {rcvr, arg1, arg2});
  }

  @Override
  public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
    Object value = messageSend.doMateSemantics(frame, args);
    if (value == null) {
     value = executeEvaluated(frame, args[0], args[1], args[2]);
    }
    return value;
  }

  @Override
  public Object executeEvaluated(final VirtualFrame frame,
      final Object receiver, final Object argument1, final Object argument2) {
    Object value = primitiveActivation.doMateSemantics(frame, new Object[]{receiver, argument1, argument2});
    if (value == null) {
     value = super.executeEvaluated(frame, receiver, argument1, argument2);
    }
    return value;
  }

  @Override
  protected AbstractMessageSpecializationsFactory getFactory() {
    return MessageSendNode.mateSpecializationFactory;
  }
}
