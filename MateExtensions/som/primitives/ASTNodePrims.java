package som.primitives;

import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SShape;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;


public class ASTNodePrims {
  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "messageSend:",
             eagerSpecializable = false, mate = true)
  public abstract static class MateMessageSendNodesPrim extends BinaryExpressionNode {
    public MateMessageSendNodesPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SAbstractObject doSClass(final DynamicObject receiver, final long fieldsCount) {
      return new SShape((int) fieldsCount);
    }
  }
}
