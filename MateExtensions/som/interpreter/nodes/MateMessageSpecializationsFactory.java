package som.interpreter.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import som.interpreter.SArguments;
import som.interpreter.nodes.AbstractMessageSpecializationsFactory.SOMMessageSpecializationsFactory;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.EagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.MateEagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.QuaternaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.constants.ExecutionLevel;
import som.vmobjects.SSymbol;


public class MateMessageSpecializationsFactory extends
    SOMMessageSpecializationsFactory {
  @Override
  public EagerUnaryPrimitiveNode unaryPrimitiveFor(SSymbol selector,
      ExpressionNode receiver, UnaryExpressionNode primitive, VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta){
      return super.unaryPrimitiveFor(selector, receiver, primitive, frame);
    }
    return new MateEagerUnaryPrimitiveNode(selector, receiver, primitive);
  }

  @Override
  public EagerBinaryPrimitiveNode binaryPrimitiveFor(SSymbol selector,
      ExpressionNode receiver, ExpressionNode argument,
      BinaryExpressionNode primitive, VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta){
      return super.binaryPrimitiveFor(selector, receiver, argument, primitive, frame);
    }
    return new MateEagerBinaryPrimitiveNode(selector, receiver, argument, primitive);
  }

  @Override
  public EagerTernaryPrimitiveNode ternaryPrimitiveFor(SSymbol selector,
      ExpressionNode receiver, ExpressionNode argument,
      ExpressionNode argument2, TernaryExpressionNode primitive, VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta){
      return super.ternaryPrimitiveFor(selector, receiver, argument, argument2, primitive, frame);
    }
    return new MateEagerTernaryPrimitiveNode(selector, receiver, argument, argument2, primitive);
  }

  @Override
  public EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(SSymbol selector,
      ExpressionNode receiver, ExpressionNode argument,
      ExpressionNode argument2, ExpressionNode argument3,
      QuaternaryExpressionNode primitive, VirtualFrame frame) {
    if (SArguments.getExecutionLevel(frame) == ExecutionLevel.Meta){
      super.quaternaryPrimitiveFor(selector, receiver, argument, argument2, argument3, primitive, frame);
    }
    return new MateEagerQuaternaryPrimitiveNode(selector, receiver, argument, argument2, argument3, primitive);
  }
}
