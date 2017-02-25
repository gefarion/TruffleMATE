package som.vmobjects;

import som.vm.constants.MateClasses;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.object.DynamicObject;

public class MockJavaObject extends SAbstractObject {
  private final Object mockedObject;

  @Override
  public DynamicObject getSOMClass() {
    // TODO: Create a proper ST class for mockedObjects
    return MateClasses.shapeClass;
  }

  public MockJavaObject(Object object) {
    mockedObject = object;
  }

  public Object getMockedObject() {
    return mockedObject;
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return (ForeignAccess) this.getMockedObject();
  }
}
