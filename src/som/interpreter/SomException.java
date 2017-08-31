package som.interpreter;

import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.object.DynamicObject;


public final class SomException extends ControlFlowException {

  private static final long serialVersionUID = -639789248178270606L;
  private final DynamicObject somObj;

  public SomException(final DynamicObject somObj) {
      super();
      this.somObj = somObj;
  }

  public DynamicObject getSomObject() {
    return somObj;
  }
}
