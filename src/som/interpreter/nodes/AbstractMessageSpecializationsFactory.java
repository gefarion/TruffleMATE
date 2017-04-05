package som.interpreter.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.EagerBinaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerQuaternaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerTernaryPrimitiveNode;
import som.interpreter.nodes.nary.EagerUnaryPrimitiveNode;
import som.interpreter.nodes.nary.QuaternaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vmobjects.SSymbol;


public abstract class AbstractMessageSpecializationsFactory {
  public abstract EagerUnaryPrimitiveNode unaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, UnaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerBinaryPrimitiveNode binaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, BinaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerTernaryPrimitiveNode ternaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, ExpressionNode argument2, TernaryExpressionNode primitive, VirtualFrame frame);
  public abstract EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(SSymbol selector, ExpressionNode receiver, ExpressionNode argument, ExpressionNode argument2, ExpressionNode argument3, QuaternaryExpressionNode primitive, VirtualFrame frame);

  public static class SOMMessageSpecializationsFactory extends AbstractMessageSpecializationsFactory {
    @Override
    public EagerUnaryPrimitiveNode unaryPrimitiveFor(SSymbol selector,
        ExpressionNode receiver, UnaryExpressionNode primitive, VirtualFrame frame) {
      return new EagerUnaryPrimitiveNode(selector, receiver, primitive);
    }

    @Override
    public EagerBinaryPrimitiveNode binaryPrimitiveFor(SSymbol selector,
        ExpressionNode receiver, ExpressionNode argument,
        BinaryExpressionNode primitive, VirtualFrame frame) {
      return new EagerBinaryPrimitiveNode(selector, receiver, argument, primitive);
    }

    @Override
    public EagerTernaryPrimitiveNode ternaryPrimitiveFor(SSymbol selector,
        ExpressionNode receiver, ExpressionNode argument,
        ExpressionNode argument2, TernaryExpressionNode primitive, VirtualFrame frame) {
      return new EagerTernaryPrimitiveNode(selector, receiver, argument, argument2, primitive);
    }

    @Override
    public EagerQuaternaryPrimitiveNode quaternaryPrimitiveFor(SSymbol selector,
        ExpressionNode receiver, ExpressionNode argument,
        ExpressionNode argument2, ExpressionNode argument3, QuaternaryExpressionNode primitive, VirtualFrame frame) {
      return new EagerQuaternaryPrimitiveNode(selector, receiver, argument, argument2, argument3, primitive);
    }
  }
}
