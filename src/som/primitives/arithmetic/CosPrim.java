package som.primitives.arithmetic;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.nary.UnaryBasicOperation;
import som.primitives.Primitive;
import tools.dym.Tags.OpArithmetic;


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
  protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
    if (tag == OpArithmetic.class) { // TODO: is this good enough?
      return true;
    } else {
      return super.hasTagIgnoringEagerness(tag);
    }
  }
}
