package som.primitives.arithmetic;

import som.interpreter.nodes.nary.UnaryBasicOperation;
import som.primitives.Primitive;
import tools.dym.Tags.OpArithmetic;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;


@GenerateNodeFactory
@Primitive(klass = "Double", selector = "sin", receiverType = Double.class)
public abstract class SinPrim extends UnaryBasicOperation {

  public SinPrim(final boolean eagWrap, final SourceSection source) {
    super(eagWrap, source);
  }

  @Specialization
  public final double doSin(final double rcvr) {
    return Math.sin(rcvr);
  }

  @Override
  protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
    if (tag == OpArithmetic.class) { // TODO: is this good enough?
      return true;
    } else {
      return super.isTaggedWithIgnoringEagerness(tag);
    }
  }
}
