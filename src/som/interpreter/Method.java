package som.interpreter;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.SOMNode;


public final class Method extends Invokable {

  private final LexicalScope currentLexicalScope;

  public Method(final SourceSection sourceSection,
                final ExpressionNode expressions,
                final LexicalScope currentLexicalScope,
                final ExpressionNode uninitialized,
                final DynamicObject method,
                final SomLanguage language) {
    super(sourceSection, currentLexicalScope.getFrameDescriptor(),
        expressions, uninitialized, method, language);
    this.currentLexicalScope = currentLexicalScope;
    currentLexicalScope.setMethod(this);
    expressions.markAsRootExpression();
  }

  @Override
  public String toString() {
    SourceSection ss = getSourceSection();
    final String id = String.format("%s:%d", ss.getSource().getName(),
        ss.getStartLine());
    return "Method " + id + "\t@" + Integer.toHexString(hashCode());
  }

  @Override
  public Invokable cloneWithNewLexicalContext(final LexicalScope outerScope) {
    FrameDescriptor inlinedFrameDescriptor = getFrameDescriptor().copy();
    LexicalScope    inlinedCurrentScope = new LexicalScope(
        inlinedFrameDescriptor, outerScope);
    ExpressionNode inlinedBody = SplitterForLexicallyEmbeddedCode.doInline(
        uninitializedBody, inlinedCurrentScope);
    return new Method(getSourceSection(), inlinedBody,
        inlinedCurrentScope, uninitializedBody, this.belongsToMethod, getLanguage(SomLanguage.class));
  }

  public Invokable cloneAndAdaptToEmbeddedOuterContext(
      final InlinerForLexicallyEmbeddedMethods inliner) {
    LexicalScope currentAdaptedScope = new LexicalScope(
        getFrameDescriptor().copy(), inliner.getCurrentLexicalScope());
    ExpressionNode adaptedBody = InlinerAdaptToEmbeddedOuterContext.doInline(
        uninitializedBody, inliner, currentAdaptedScope);
    ExpressionNode uninitAdaptedBody = NodeUtil.cloneNode(adaptedBody);

    Method clone = new Method(getSourceSection(), adaptedBody,
        currentAdaptedScope, uninitAdaptedBody, this.belongsToMethod, getLanguage(SomLanguage.class));
    return clone;
  }

  public Invokable cloneAndAdaptToSomeOuterContextBeingEmbedded(
      final InlinerAdaptToEmbeddedOuterContext inliner) {
    LexicalScope currentAdaptedScope = new LexicalScope(
        getFrameDescriptor().copy(), inliner.getCurrentLexicalScope());
    ExpressionNode adaptedBody = InlinerAdaptToEmbeddedOuterContext.doInline(
        uninitializedBody, inliner, currentAdaptedScope);
    ExpressionNode uninitAdaptedBody = NodeUtil.cloneNode(adaptedBody);

    Method clone = new Method(getSourceSection(),
        adaptedBody, currentAdaptedScope, uninitAdaptedBody, this.belongsToMethod, getLanguage(SomLanguage.class));
    return clone;
  }

  @Override
  public void propagateLoopCountThroughoutLexicalScope(final long count) {
    assert count >= 0;
    currentLexicalScope.propagateLoopCountThroughoutLexicalScope(count);
    LoopNode.reportLoopCount(this, (count > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) count);
  }

  public SourceSection[] getDefinition() {
    // Should we include an special array of sourceSections for the method definition as in SOMns?
    return new SourceSection[]{this.getSourceSection()};
  }

  public SourceSection getRootNodeSource() {
    ExpressionNode root = SOMNode.unwrapIfNecessary(expressionOrSequence);
    assert root.isMarkedAsRootExpression();
    return root.getSourceSection();
  }

  @Override
  public Node deepCopy() {
    Node copy = cloneWithNewLexicalContext(currentLexicalScope.getOuterScopeOrNull());
    ((Invokable) copy).uninitializedBody = (ExpressionNode) uninitializedBody.deepCopy();
    return copy;
  }

  public boolean isBlock() {
    // TODO: analyze the best way to implement this method properly
    return false;
  }

  public LexicalScope getLexicalScope() {
    return currentLexicalScope;
  }
}
