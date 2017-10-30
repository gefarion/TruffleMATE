package som.interpreter.nodes.nary;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;

import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode;
import som.matenodes.IntercessionHandling;
import som.vmobjects.SSymbol;


public class MateEagerBinaryPrimitiveNode extends EagerBinaryPrimitiveNode {
  @Child private IntercessionHandling messageSend;
  @Child private IntercessionHandling primitiveActivation;

  public MateEagerBinaryPrimitiveNode(final SSymbol selector, final ExpressionNode receiver, final ExpressionNode argument,
      final BinaryExpressionNode primitive) {
    super(selector, receiver, argument, primitive);
    messageSend = IntercessionHandling.createForMessageLookup(this.getSelector());
    primitiveActivation = IntercessionHandling.createForOperation(this.getPrimitive().reflectiveOperation());
    this.adoptChildren();
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    Object rcvr = this.getReceiver().executeGeneric(frame);
    Object arg  = this.getArgument().executeGeneric(frame);
    return this.doPreEvaluated(frame, new Object[] {rcvr, arg});
  }

  @Override
  public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
    Object value = messageSend.doMateSemantics(frame, args);
    if (value == null) {
     value = executeEvaluated(frame, args[0], args[1]);
    }
    return value;
  }

  @Override
  public Object executeEvaluated(final VirtualFrame frame,
      final Object receiver, final Object argument1) {
    Object[] realArgs = (Object[]) primitiveActivation.doMateSemantics(frame, new Object[]{receiver, argument1});
    if (realArgs == null) {
      return super.executeEvaluated(frame, receiver, argument1);
    } else {
      return super.executeEvaluated(frame, realArgs[2], realArgs[3]);
    }
  }

  @Override
  protected AbstractMessageSpecializationsFactory getFactory() {
    return MessageSendNode.mateSpecializationFactory;
  }

  @Override
  public NodeCost getCost() {
    return NodeCost.NONE;
  }
}
