package som.vmobjects;

import som.interop.DynamicObjectInteropMessageResolutionForeign;
import som.vm.constants.Nil;
import som.vmobjects.SObjectLayoutImpl.SObjectType;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.ObjectType;


public class MateObjectType extends ObjectType {
  @Override
  public ForeignAccess getForeignAccessFactory(DynamicObject object) {
    return DynamicObjectInteropMessageResolutionForeign.createAccess();
  }
  
  @Override
  @TruffleBoundary
  public String toString(DynamicObject object) {
    return super.toString(object) +
    "\nclass:" + SClass.getName(SObject.getSOMClass(object));
  }
  
  public static class MateReflectiveObjectType extends SObjectLayoutImpl.SObjectType {
    public MateReflectiveObjectType(DynamicObject klass) {
      super(klass);
    }

    @Override
    @TruffleBoundary
    public String toString(DynamicObject object) {
      return super.toString(object) +
      "\nhas metaobject:" + String.valueOf(SReflectiveObject.getEnvironment(object) != Nil.nilObject);
    }
  }
}
