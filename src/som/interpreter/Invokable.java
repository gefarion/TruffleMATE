package som.interpreter;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.RootTag;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import som.compiler.MethodGenerationContext;
import som.compiler.Variable.Local;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.MateReturnNode;
import som.vmobjects.SInvokable;

public abstract class Invokable extends RootNode implements ReflectiveNode {

  @Child protected ExpressionNode expressionOrSequence;

  @CompilationFinal protected ExpressionNode uninitializedBody;
  @CompilationFinal protected DynamicObject belongsToMethod;

  public Invokable(final SourceSection sourceSection,
      final FrameDescriptor frameDescriptor,
      final ExpressionNode expressionOrSequence,
      final ExpressionNode uninitialized,
      final DynamicObject method, final SomLanguage language) {
    super(language, frameDescriptor);
    this.uninitializedBody = uninitialized;
    this.expressionOrSequence = expressionOrSequence;
    this.belongsToMethod = method;
  }

  @Override
  public final Object execute(final VirtualFrame frame) {
    return expressionOrSequence.executeGeneric(frame);
  }

  public abstract Invokable cloneWithNewLexicalContext(LexicalScope outerContext);

  public ExpressionNode inline(final MethodGenerationContext mgenc,
      final Local[] locals) {
    return InlinerForLexicallyEmbeddedMethods.doInline(uninitializedBody, mgenc,
        locals, 0); //getSourceSection().getCharIndex()
  }

  @Override
  public final boolean isCloningAllowed() {
    return true;
  }

  public DynamicObject getBelongsToMethod() {
    return this.belongsToMethod;
  }

  public final RootCallTarget createCallTarget() {
    return Truffle.getRuntime().createCallTarget(this);
  }

  public abstract void propagateLoopCountThroughoutLexicalScope(long count);

  public void setMethod(final DynamicObject method) {
    this.belongsToMethod = method;
  }

  @Override
  protected boolean isTaggedWith(final Class<?> tag) {
    if (tag == RootTag.class) {
      return true;
    } else {
      return super.isTaggedWith(tag);
    }
  }

  @Override
  public Node asMateNode() {
    expressionOrSequence = new MateReturnNode(expressionOrSequence);
    this.adoptChildren();
    uninitializedBody = NodeVisitorUtil.applyVisitor(uninitializedBody, new MateifyVisitor());
    return null;
  }

  @Override
  public String getName() {
    if (this.belongsToMethod == null) {
      return "";
    }
    return SInvokable.toString(this.belongsToMethod);
  }
}
