package som.interpreter.nodes.nary;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.AbstractMessageSpecializationsFactory;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.PreevaluatedExpression;
import som.vmobjects.SSymbol;

public abstract class EagerlySpecializableNode extends ExpressionWithTagsNode
  implements PreevaluatedExpression {

  @CompilationFinal private boolean eagerlyWrapped;

  protected EagerlySpecializableNode(final EagerlySpecializableNode wrappedNode) {
    super(wrappedNode);
    assert !wrappedNode.eagerlyWrapped : "I think this should be true.";
    this.eagerlyWrapped = false;
  }

  public EagerlySpecializableNode(final boolean eagerlyWrapped,
      final SourceSection source) {
    super(source);
    this.eagerlyWrapped = eagerlyWrapped;
  }

  /**
   * This method is used by eager wrapper or if this node is not eagerly
   * wrapped.
   */
  protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
    return super.hasTag(tag);
  }

  @Override
  public boolean hasTag(final Class<? extends Tag> tag) {
    if (eagerlyWrapped) {
      return false;
    } else {
      return hasTagIgnoringEagerness(tag);
    }
  }

  @Override
  protected void onReplace(final Node newNode, final CharSequence reason) {
    if (newNode instanceof WrapperNode ||
        !(newNode instanceof EagerlySpecializableNode)) { return; }

    EagerlySpecializableNode n = (EagerlySpecializableNode) newNode;
    n.eagerlyWrapped = eagerlyWrapped;
    super.onReplace(newNode, reason);
  }

  /**
   * Create an eager primitive wrapper, which wraps this node.
   */
  public abstract EagerPrimitive wrapInEagerWrapper(EagerlySpecializableNode prim,
      SSymbol selector, ExpressionNode[] arguments, VirtualFrame frame, AbstractMessageSpecializationsFactory factory);
}
