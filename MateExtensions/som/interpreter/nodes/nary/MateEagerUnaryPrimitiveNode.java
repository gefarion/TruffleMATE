package som.interpreter.nodes.nary;

import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode;
import som.matenodes.IntercessionHandling;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.frame.VirtualFrame;


public class MateEagerUnaryPrimitiveNode extends EagerUnaryPrimitiveNode {
  @Child private IntercessionHandling messageSend;
  @Child private IntercessionHandling primitiveActivation;

  public MateEagerUnaryPrimitiveNode(final SSymbol selector, final ExpressionNode receiver,
      final UnaryExpressionNode primitive) {
    super(selector, receiver, primitive);
    messageSend = IntercessionHandling.createForMessageLookup(this.getSelector());
    primitiveActivation = IntercessionHandling.createForOperation(this.getPrimitive().reflectiveOperation());
    this.adoptChildren();
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    Object rcvr = this.getReceiver().executeGeneric(frame);
    return this.doPreEvaluated(frame, new Object[] {rcvr});
  }

  @Override
  public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
    Object value = messageSend.doMateSemantics(frame, args);
    if (value == null) {
     value = executeEvaluated(frame, args[0]);
    }
    return value;
  }

  @Override
  public Object executeEvaluated(final VirtualFrame frame, final Object receiver) {
    Object value = primitiveActivation.doMateSemantics(frame, new Object[]{receiver});
    if (value == null) {
     value = super.executeEvaluated(frame, receiver);
    }
    return value;
  }

  @Override
  protected AbstractMessageSpecializationsFactory getFactory() {
    return MessageSendNode.mateSpecializationFactory;
  }
}
