package som.instrumentation;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.InstrumentableFactory;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.NodeCost;

import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.PreevaluatedExpression;

public final class FixedSizeExpressionWrapperFactory implements
    InstrumentableFactory<ExpressionNode> {

  @Override
  public com.oracle.truffle.api.instrumentation.InstrumentableFactory.WrapperNode createWrapper(
      final ExpressionNode delegateNode, final ProbeNode probeNode) {
    return new FixedSizeWrapper(delegateNode, probeNode);
  }

  private static final class FixedSizeWrapper extends ExpressionNode
      implements WrapperNode {

    @Child private ExpressionNode delegateNode;
    @Child private ProbeNode      probeNode;

    FixedSizeWrapper(final ExpressionNode delegateNode,
        final ProbeNode probeNode) {
      super(delegateNode.getSourceSection());
      this.delegateNode = delegateNode;
      this.probeNode = probeNode;
    }

    @Override
    public ExpressionNode getDelegateNode() {
      return delegateNode;
    }

    @Override
    public ProbeNode getProbeNode() {
      return probeNode;
    }

    @Override
    public NodeCost getCost() {
      return NodeCost.NONE;
    }

    @Override
    public Object executeGeneric(final VirtualFrame frame) {
      Object returnValue;
      for (;;) {
        boolean wasOnReturnExecuted = false;
        try {
          probeNode.onEnter(null);
          returnValue = delegateNode.executeGeneric(frame);
          wasOnReturnExecuted = true;
          probeNode.onReturnValue(null, returnValue);
          return returnValue;
        } catch (Throwable t) {
          // TODO: is passing `null` here as virtual frame an issue?
          Object result = probeNode.onReturnExceptionalOrUnwind(null, t, wasOnReturnExecuted);
          if (result == ProbeNode.UNWIND_ACTION_REENTER) {
            continue;
          } else if (result != null) {
            returnValue = result;
            break;
          } else {
            throw t;
          }
        }
      }
      return returnValue;
    }

    @SuppressWarnings("unused")
    public Object doPreEvaluated(final VirtualFrame frame, final Object[] args) {
      Object returnValue;
      for (;;) {
        boolean wasOnReturnExecuted = false;
        try {
          probeNode.onEnter(null);
          returnValue = ((PreevaluatedExpression) delegateNode).doPreEvaluated(frame, args);
          wasOnReturnExecuted = true;
          probeNode.onReturnValue(null, returnValue);
          return returnValue;
        } catch (Throwable t) {
          // TODO: is passing `null` here as virtual frame an issue?
          Object result = probeNode.onReturnExceptionalOrUnwind(null, t, wasOnReturnExecuted);
          if (result == ProbeNode.UNWIND_ACTION_REENTER) {
            continue;
          } else if (result != null) {
            returnValue = result;
            break;
          } else {
            throw t;
          }
        }
      }
      return returnValue;
    }
  }
}
