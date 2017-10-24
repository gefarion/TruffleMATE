package som.interpreter.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.SArguments;
import som.interpreter.nodes.AbstractMessageSpecializationsFactory.SOMMessageSpecializationsFactory;
import som.interpreter.nodes.MessageSendNode.GenericMessageSendNode;
import som.interpreter.nodes.dispatch.MateUninitializedDispatchNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.EagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.QuaternaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.constants.ExecutionLevel;
import som.vmobjects.SSymbol;


public class MateMessageSpecializationsFactory extends
    SOMMessageSpecializationsFactory {
  @Override
  public EagerUnaryPrimitiveNode unaryPrimitiveFor(final SSymbol selector,
      final ExpressionNode receiver, final UnaryExpressionNode primitive, final VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta) {
      return super.unaryPrimitiveFor(selector, receiver, primitive, frame);
    }
    return new MateEagerUnaryPrimitiveNode(selector, receiver, primitive);
  }

  @Override
  public EagerBinaryPrimitiveNode binaryPrimitiveFor(final SSymbol selector,
      final ExpressionNode receiver, final ExpressionNode argument,
      final BinaryExpressionNode primitive, final VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta) {
      return super.binaryPrimitiveFor(selector, receiver, argument, primitive, frame);
    }
    return new MateEagerBinaryPrimitiveNode(selector, receiver, argument, primitive);
  }

  @Override
  public EagerTernaryPrimitiveNode ternaryPrimitiveFor(final SSymbol selector,
      final ExpressionNode receiver, final ExpressionNode argument,
      final ExpressionNode argument2, final TernaryExpressionNode primitive, final VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta) {
      return super.ternaryPrimitiveFor(selector, receiver, argument, argument2, primitive, frame);
    }
    return new MateEagerTernaryPrimitiveNode(selector, receiver, argument, argument2, primitive);
  }

  @Override
  public EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(final SSymbol selector,
      final ExpressionNode receiver, final ExpressionNode argument,
      final ExpressionNode argument2, final ExpressionNode argument3,
      final QuaternaryExpressionNode primitive, final VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta) {
      super.quaternaryPrimitiveFor(selector, receiver, argument, argument2, argument3, primitive, frame);
    }
    return new MateEagerQuaternaryPrimitiveNode(selector, receiver, argument, argument2, argument3, primitive);
  }

  @Override
  public GenericMessageSendNode genericMessageFor(final SSymbol selector,
      final ExpressionNode[] argumentNodes, final SourceSection source) {
    return new MateGenericMessageSendNode(selector, argumentNodes,
        new MateUninitializedDispatchNode(source, selector), source);
  }
}
