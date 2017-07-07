package som.interpreter.nodes.nary;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MessageSendNode;
import som.interpreter.nodes.MessageSendNode.GenericMessageSendNode;
import som.interpreter.nodes.OperationNode;
import som.interpreter.nodes.PreevaluatedExpression;
import som.vm.Universe;
import som.vm.constants.ExecutionLevel;
import som.vm.constants.ReflectiveOp;
import som.vmobjects.SSymbol;


public abstract class EagerPrimitive extends ExpressionWithTagsNode
    implements OperationNode, ExpressionWithReceiver, PreevaluatedExpression {
  protected final SSymbol selector;

  protected EagerPrimitive(final SourceSection source, final SSymbol sel) {
    super(source);
    selector = sel;
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    return executeGenericWithReceiver(frame, this.getReceiver().executeGeneric(frame));
  }

  protected SSymbol getSelector() {
    return selector;
  }

   @Override
  public String getOperation() {
    return selector.getString();
  }

  @Override
  public ReflectiveOp reflectiveOperation() {
    return ReflectiveOp.MessageLookup;
  }

  protected AbstractMessageSpecializationsFactory getFactory() {
    return MessageSendNode.specializationFactory;
  }

  protected final GenericMessageSendNode replaceWithGenericSend(final ExecutionLevel level) {
    Universe.getCurrent().insertInstrumentationWrapper(this);
    ExpressionNode[] arguments = this.getArgumentNodes();
    GenericMessageSendNode node = MessageSendNode.createGeneric(selector,
        arguments, getSourceSection(), level, this.getFactory());
    replace(node);
    Universe.getCurrent().insertInstrumentationWrapper(node);
    Universe.getCurrent().insertInstrumentationWrapper(arguments[0]);
    return node;
  }

  protected abstract ExpressionNode[] getArgumentNodes();

}
