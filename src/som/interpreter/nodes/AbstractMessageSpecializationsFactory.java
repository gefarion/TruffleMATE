package som.interpreter.nodes;

import som.interpreter.nodes.MessageSendNode.GenericMessageSendNode;
import som.interpreter.nodes.dispatch.UninitializedDispatchNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.EagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.QuaternaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;


public abstract class AbstractMessageSpecializationsFactory {
  public abstract EagerUnaryPrimitiveNode unaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, UnaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerBinaryPrimitiveNode binaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, BinaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerTernaryPrimitiveNode ternaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, ExpressionNode argument2, TernaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, ExpressionNode argument2, ExpressionNode argument3, QuaternaryExpressionNode primitive, VirtualFrame frame);
  public abstract GenericMessageSendNode genericMessageFor(SSymbol selector, ExpressionNode[] argumentNodes, SourceSection source);

  public static class SOMMessageSpecializationsFactory extends AbstractMessageSpecializationsFactory {
    @Override
    public EagerUnaryPrimitiveNode unaryPrimitiveFor(final SSymbol selector,
        final ExpressionNode receiver, final UnaryExpressionNode primitive, final VirtualFrame frame) {
      return new EagerUnaryPrimitiveNode(selector, receiver, primitive);
    }

    @Override
    public EagerBinaryPrimitiveNode binaryPrimitiveFor(final SSymbol selector,
        final ExpressionNode receiver, final ExpressionNode argument,
        final BinaryExpressionNode primitive, final VirtualFrame frame) {
      return new EagerBinaryPrimitiveNode(selector, receiver, argument, primitive);
    }

    @Override
    public EagerTernaryPrimitiveNode ternaryPrimitiveFor(final SSymbol selector,
        final ExpressionNode receiver, final ExpressionNode argument,
        final ExpressionNode argument2, final TernaryExpressionNode primitive, final VirtualFrame frame) {
      return new EagerTernaryPrimitiveNode(selector, receiver, argument, argument2, primitive);
    }

    @Override
    public EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(final SSymbol selector,
        final ExpressionNode receiver, final ExpressionNode argument,
        final ExpressionNode argument2, final ExpressionNode argument3, final QuaternaryExpressionNode primitive, final VirtualFrame frame) {
      return new EagerQuaternaryPrimitiveNode(selector, receiver, argument, argument2, argument3, primitive);
    }

    @Override
    public GenericMessageSendNode genericMessageFor(final SSymbol selector,
        final ExpressionNode[] argumentNodes, final SourceSection source) {
      return new GenericMessageSendNode(selector, argumentNodes,
          new UninitializedDispatchNode(source, selector), source);
    }
  }
}
