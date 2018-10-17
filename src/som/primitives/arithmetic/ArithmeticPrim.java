package som.primitives.arithmetic;

import java.math.BigInteger;

import com.oracle.truffle.api.instrumentation.Tag;

import som.interpreter.nodes.nary.BinaryExpressionNode;
import tools.dym.Tags.BasicPrimitiveOperation;
import tools.dym.Tags.OpArithmetic;


public abstract class ArithmeticPrim extends BinaryExpressionNode {
  protected final Number reduceToLongIfPossible(final BigInteger result) {
    if (result.bitLength() > Long.SIZE - 1) {
      return result;
    } else {
      return result.longValue();
    }
  }

  @Override
  protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
    if (tag == OpArithmetic.class || tag == BasicPrimitiveOperation.class) {
      return true;
    } else {
      return super.hasTagIgnoringEagerness(tag);
    }
  }
}
