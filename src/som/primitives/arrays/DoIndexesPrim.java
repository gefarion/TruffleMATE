package som.primitives.arrays;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.Invokable;
import som.interpreter.nodes.dispatch.BlockDispatchNode;
import som.interpreter.nodes.dispatch.BlockDispatchNodeGen;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.primitives.LengthPrim;
import som.primitives.LengthPrimFactory;
import som.primitives.Primitive;
import som.vmobjects.SArray;
import som.vmobjects.SBlock;


@GenerateNodeFactory
@Primitive(klass = "Array", selector = "doIndexes:",
           receiverType = SArray.class, disabled = true)
public abstract class DoIndexesPrim extends BinaryExpressionNode {
  @Child private BlockDispatchNode block;
  @Child private LengthPrim length;

  public DoIndexesPrim(final boolean eagWrap, final SourceSection source) {
    super(eagWrap, source);
    block = BlockDispatchNodeGen.create();
    length = LengthPrimFactory.create(false, null, null);
  }

  @Specialization
  public final SArray doArray(final VirtualFrame frame,
      final SArray receiver, final SBlock block) {
    int length = (int) this.length.executeEvaluated(receiver);
    loop(frame, block, length);
    return receiver;
  }

  private void loop(final VirtualFrame frame, final SBlock block, final int length) {
    try {
      if (SArray.FIRST_IDX < length) {
        this.block.activateBlock(frame, new Object[] {
            block, (long) SArray.FIRST_IDX + 1}); // +1 because it is going to the smalltalk level
      }
      for (long i = 1; i < length; i++) {
        this.block.activateBlock(frame, new Object[] {
            block, i + 1}); // +1 because it is going to the smalltalk level
      }
    } finally {
      if (CompilerDirectives.inInterpreter()) {
        reportLoopCount(length);
      }
    }
  }

  protected final void reportLoopCount(final long count) {
    assert count >= 0;
    CompilerAsserts.neverPartOfCompilation("reportLoopCount");
    Node current = getParent();
    while (current != null && !(current instanceof RootNode)) {
      current = current.getParent();
    }
    if (current != null) {
      ((Invokable) current).propagateLoopCountThroughoutLexicalScope(count);
    }
  }
}
