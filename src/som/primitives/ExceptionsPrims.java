package som.primitives;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.SomException;
import som.interpreter.nodes.nary.UnaryExpressionNode;


public abstract class ExceptionsPrims {
  @GenerateNodeFactory
  @Primitive(klass = "Exception", selector = "signal", eagerSpecializable = false)
  public abstract static class SignalPrim extends UnaryExpressionNode {
    public SignalPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final Object doSignal(final DynamicObject exceptionObject) {
      throw new SomException(exceptionObject);
    }
  }
}
