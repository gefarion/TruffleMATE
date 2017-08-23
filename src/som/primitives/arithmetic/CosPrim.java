package som.primitives.arithmetic;

import som.interpreter.nodes.nary.UnaryBasicOperation;
import som.primitives.Primitive;
import tools.dym.Tags.OpArithmetic;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;


@GenerateNodeFactory
@Primitive(klass = "Double", selector = "cos", receiverType = Double.class)
public abstract class CosPrim extends UnaryBasicOperation {

  public CosPrim(final boolean eagWrap, final SourceSection source) {
    super(eagWrap, source);
  }

  @Specialization
  public final double doCos(final double rcvr) {
    return Math.cos(rcvr);
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
