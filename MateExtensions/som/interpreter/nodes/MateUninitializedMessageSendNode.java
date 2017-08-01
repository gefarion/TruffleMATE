package som.interpreter.nodes;

import som.interpreter.MateNode;
import som.interpreter.nodes.MessageSendNode.UninitializedMessageSendNode;
import som.matenodes.IntercessionHandling;
import com.oracle.truffle.api.frame.VirtualFrame;

public class MateUninitializedMessageSendNode extends
    UninitializedMessageSendNode implements MateNode {
  @Child private IntercessionHandling ih;

  public MateUninitializedMessageSendNode(final UninitializedMessageSendNode somNode) {
    super(somNode.getSelector(), somNode.argumentNodes, somNode.getSourceSection());
    if (this.isSuperSend()) {
      ih = IntercessionHandling.createForSuperMessageLookup(this.getSelector(), (ISuperReadNode) this.argumentNodes[0]);
    } else {
      ih = IntercessionHandling.createForMessageLookup(this.getSelector());
    }
    this.adoptChildren();
  }

  @Override
  public final Object executeGeneric(final VirtualFrame frame) {
    Object[] arguments = evaluateArguments(frame);
    Object value = ih.doMateSemantics(frame, arguments);
    if (value == null) {
      value = doPreEvaluated(frame, arguments);
    }
    return value;
  }

  @Override
  public ExpressionNode asMateNode() {
    return null;
  }

  @Override
  protected AbstractMessageSpecializationsFactory getFactory() {
    return MessageSendNode.mateSpecializationFactory;
  }
}
