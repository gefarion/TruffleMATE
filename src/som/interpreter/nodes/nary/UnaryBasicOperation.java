package som.interpreter.nodes.nary;

import tools.dym.Tags.BasicPrimitiveOperation;

import com.oracle.truffle.api.source.SourceSection;


/**
 * Nodes of this type represent basic operations such as arithmetics and
 * comparisons. Basic means here, that these nodes are mapping to one or only
 * a few basic operations in an ideal native code mapping.
 */
public abstract class UnaryBasicOperation extends UnaryExpressionNode {
  protected UnaryBasicOperation(final boolean eagerlyWrapped, final SourceSection source) {
    super(eagerlyWrapped, source);
  }

  @Override
  protected boolean isTaggedWithIgnoringEagerness(final Class<?> tag) {
    if (tag == BasicPrimitiveOperation.class) {
      return true;
    } else {
      return super.isTaggedWithIgnoringEagerness(tag);
    }
  }
}
