package som.interpreter.nodes.nary;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

import bd.primitives.nodes.EagerlySpecializable;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.PreevaluatedExpression;
import som.vm.Universe;
import som.vmobjects.SSymbol;

public abstract class EagerlySpecializableNode extends ExpressionWithTagsNode
  implements PreevaluatedExpression, EagerlySpecializable<ExpressionNode, SSymbol, Universe> {

  @CompilationFinal private boolean eagerlyWrapped;

  @Override
  public ExpressionNode initialize(final SourceSection sourceSection,
      final boolean eagerlyWrapped) {
    this.initialize(sourceSection);
    assert !this.eagerlyWrapped;
    this.eagerlyWrapped = eagerlyWrapped;
    return this;
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
}
