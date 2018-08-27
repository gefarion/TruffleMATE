package som.vmobjects;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;

import som.interop.DynamicObjectInteropMessageResolutionForeign;
import som.vm.constants.Nil;


public class MateObjectType extends ObjectType {
  @Override
  public ForeignAccess getForeignAccessFactory(final DynamicObject object) {
    return DynamicObjectInteropMessageResolutionForeign.ACCESS;
  }

  @Override
  @TruffleBoundary
  public String toString(final DynamicObject object) {
    return super.toString(object) +
    "\nclass:" + SClass.getName(SObject.getSOMClass(object));
  }

  public static class MateReflectiveObjectType extends MateObjectType {
    @Override
    @TruffleBoundary
    public String toString(final DynamicObject object) {
      return super.toString(object) +
      "\nhas metaobject:" + String.valueOf(SReflectiveObject.getEnvironment(object) != Nil.nilObject);
    }
  }
}
