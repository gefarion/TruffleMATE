package som.vm.constants;

import com.oracle.truffle.api.object.DynamicObject;

import som.vm.ObjectMemory;

public final class MateClasses extends Classes {
  public static final DynamicObject environmentMO;
  public static final DynamicObject operationalSemanticsMO;
  public static final DynamicObject messageMO;
  public static final DynamicObject shapeClass;
  public static final DynamicObject contextClass;
  public static final DynamicObject STANDARD_ENVIRONMENT;
  public static final DynamicObject astNodeClass;

  static {
    // Allocate the Metalevel classes
    environmentMO          = ObjectMemory.newSystemClass(objectClass);
    operationalSemanticsMO = ObjectMemory.newSystemClass(objectClass);
    messageMO              = ObjectMemory.newSystemClass(objectClass);
    shapeClass             = ObjectMemory.newSystemClass(objectClass);
    contextClass           = ObjectMemory.newSystemClass(objectClass);
    astNodeClass           = ObjectMemory.newSystemClass(objectClass);
    STANDARD_ENVIRONMENT   = Nil.nilObject;
    // environment = Universe.newSystemClass();
  }
}
