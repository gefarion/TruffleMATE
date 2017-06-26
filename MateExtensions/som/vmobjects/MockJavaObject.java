package som.vmobjects;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.object.DynamicObject;

public class MockJavaObject extends SAbstractObject {
  private final Object mockedObject;
  private final DynamicObject somClass;

  @Override
  public DynamicObject getSOMClass() {
    return somClass;
  }

  public MockJavaObject(final Object object, final DynamicObject klass) {
    mockedObject = object;
    somClass = klass;
  }

  public Object getMockedObject() {
    return mockedObject;
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return (ForeignAccess) this.getMockedObject();
  }
}
