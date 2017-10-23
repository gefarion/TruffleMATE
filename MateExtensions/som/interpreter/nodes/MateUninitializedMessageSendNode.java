package som.interpreter.nodes;

import som.interpreter.nodes.MessageSendNode.UninitializedMessageSendNode;

public class MateUninitializedMessageSendNode extends
    UninitializedMessageSendNode {

  public MateUninitializedMessageSendNode(final UninitializedMessageSendNode somNode) {
    super(somNode.getSelector(), somNode.argumentNodes, somNode.getSourceSection());
    this.adoptChildren();
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
