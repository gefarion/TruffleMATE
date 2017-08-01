package som.interpreter.nodes.specialized;

import som.interpreter.SArguments;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.primitives.Primitive;
import som.vm.constants.ExecutionLevel;
import som.vm.constants.Nil;
import som.vmobjects.SBlock;
import som.vmobjects.SInvokable;
import tools.dym.Tags.ControlFlowCondition;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;


@GenerateNodeFactory
public abstract class IfMessageNode extends BinaryExpressionNode {
  protected final ConditionProfile condProf = ConditionProfile.createCountingProfile();
  private final boolean expected;

  @GenerateNodeFactory
  @Primitive(selector = "ifTrue:",
             receiverType = Boolean.class, noWrapper = true)
  public abstract static class IfTrueMessageNode extends IfMessageNode {
    public IfTrueMessageNode(final boolean eagWrap, final SourceSection source) { super(eagWrap, source, true); assert !eagWrap; }
  }

  @GenerateNodeFactory
  @Primitive(selector = "ifFalse:",
             receiverType = Boolean.class, noWrapper = true)
  public abstract static class IfFalseMessageNode extends IfMessageNode {
    public IfFalseMessageNode(final boolean eagWrap, final SourceSection source) { super(eagWrap, source, false); assert !eagWrap; }
  }

  protected IfMessageNode(final boolean eagWrap, final SourceSection source, final boolean expected) {
    super(false, source);
    this.expected = expected;
  }

  protected static DirectCallNode createDirect(final DynamicObject method, final ExecutionLevel level) {
    return Truffle.getRuntime().createDirectCallNode(SInvokable.getCallTarget(method, level));
  }

  protected static IndirectCallNode createIndirect() {
    return Truffle.getRuntime().createIndirectCallNode();
  }

  protected static ExecutionLevel executionLevel(final VirtualFrame frame) {
    return SArguments.getExecutionLevel(frame);
  }


  @Specialization(guards = {"arg.getMethod() == method"})
  public final Object cachedBlock(final VirtualFrame frame, final boolean rcvr, final SBlock arg,
      @Cached("arg.getMethod()") final DynamicObject method,
      @Cached("createDirect(method, executionLevel(frame))") final DirectCallNode callTarget) {
    if (condProf.profile(rcvr == expected)) {
      return callTarget.call(new Object[] {SArguments.getEnvironment(frame), SArguments.getExecutionLevel(frame), arg});
    } else {
      return Nil.nilObject;
    }
  }

  @Specialization(replaces = "cachedBlock")
  public final Object fallback(final VirtualFrame frame, final boolean rcvr,
      final SBlock arg,
      @Cached("createIndirect()") final IndirectCallNode callNode) {
    if (condProf.profile(rcvr == expected)) {
      return callNode.call(SInvokable.getCallTarget(arg.getMethod(), executionLevel(frame)), new Object[] {SArguments.getEnvironment(frame), SArguments.getExecutionLevel(frame), arg});
    } else {
      return Nil.nilObject;
    }
  }

  protected final boolean notABlock(final Object arg) {
    return !(arg instanceof SBlock);
  }

  @Specialization(guards = {"notABlock(arg)"})
  public final Object literal(final boolean rcvr, final Object arg) {
    if (condProf.profile(rcvr == expected)) {
      return arg;
    } else {
      return Nil.nilObject;
    }
  }

  @Override
  protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
    if (tag == ControlFlowCondition.class) {
      return true;
    } else {
      return super.isTaggedWithIgnoringEagerness(tag);
    }
  }
}
