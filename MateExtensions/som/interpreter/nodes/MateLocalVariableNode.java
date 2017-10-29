package som.interpreter.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import som.interpreter.MateNode;
import som.interpreter.SArguments;
import som.interpreter.nodes.LocalVariableNode.LocalVariableReadNode;
import som.interpreter.nodes.LocalVariableNode.LocalVariableWriteNode;
import som.matenodes.IntercessionHandling;
import som.vm.constants.ReflectiveOp;

public abstract class MateLocalVariableNode {
  public static class MateLocalVariableReadNode extends LocalVariableReadNode
      implements MateNode {

    @Child private IntercessionHandling ih;
    @Child LocalVariableNode local;

    public MateLocalVariableReadNode(final LocalVariableReadNode node) {
      super(node);
      this.local = node;
      ih = IntercessionHandling.createForOperation(ReflectiveOp.ExecutorReadLocal);
      this.adoptChildren();
    }

    @Override
    public Object executeGeneric(final VirtualFrame frame) {
      Object value = ih.doMateSemantics(frame, new Object[] {SArguments.rcvr(frame), local.slot.getIdentifier()});
      if (value == null) {
       value = local.executeGeneric(frame);
      }
      return value;
    }

    @Override
    public Node asMateNode() {
      return null;
    }
  }

  public static class MateLocalVariableWriteNode extends LocalVariableWriteNode
      implements MateNode {

    @Child private IntercessionHandling ih;
    @Child LocalVariableWriteNode local;

    public MateLocalVariableWriteNode(final LocalVariableWriteNode node) {
      super(node);
      this.local = node;
      ih = IntercessionHandling.createForOperation(ReflectiveOp.ExecutorWriteLocal);
      this.adoptChildren();
    }

    @Override
    public Object executeGeneric(final VirtualFrame frame) {
      // Todo: Rewrite this node like in message sends to avoid executing the local expression twice
      Object value = ih.doMateSemantics(frame, new Object[] {SArguments.rcvr(frame),
          local.slot.getIdentifier(), (long) 1});
      if (value == null) {
       value = local.executeGeneric(frame);
      }
      return value;
    }

    @Override
    public ExpressionNode getExp() {
      return local.getExp();
    }

    @Override
    public Node asMateNode() {
      return null;
    }
  }
}
