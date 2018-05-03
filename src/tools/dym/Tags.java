package tools.dym;

import com.oracle.truffle.api.instrumentation.Tag;

public abstract class Tags extends Tag {
  protected Tags() {
    /* No instances */
  }

  // this is some form of invoke in the source, unclear what it is during program execution
  @Tag.Identifier("Unknown Kind Of Invoke")
  public final class UnspecifiedInvoke extends Tags {
    private UnspecifiedInvoke() { }
  }

  // a virtual invoke where the lookup was cached
  @Tag.Identifier("Cached Invoke")
  public final class CachedVirtualInvoke extends Tags {
    private CachedVirtualInvoke() { }
  }

  // a closure invoke where the closure method was cached
  @Tag.Identifier("Cached Closure")
  public final class CachedClosureInvoke extends Tags {
    private CachedClosureInvoke() { }
  }

  // the lexical site of a virtual invoke
  @Tag.Identifier("Vritual Dispatch")
  public final class VirtualInvoke extends Tags {
    private VirtualInvoke() { }
  }

  // the lexical site of a virtual invoke
  @Tag.Identifier("Virtual Dispatch Receiver")
  public final class VirtualInvokeReceiver extends Tags {
    private VirtualInvokeReceiver() { }
  }

  @Tag.Identifier("Object Creation")
  public final class NewObject extends Tags {
    private NewObject() { }
  }

  @Tag.Identifier("Array Creation")
  public final class NewArray extends Tags {
    private NewArray() { }
  }

  // a condition expression that results in a control-flow change
  @Tag.Identifier("Control Flow")
  public final class ControlFlowCondition extends Tags {
    private ControlFlowCondition() { }
  }

  @Tag.Identifier("Field Read")
  public final class FieldRead extends Tags {
    private FieldRead() { }
  }

  @Tag.Identifier("Field Write")
  public final class FieldWrite extends Tags {
    private FieldWrite() { }
  }

  // lexical access/reference to a class
  @Tag.Identifier("Class Read")
  public final class ClassRead extends Tags {
    private ClassRead() { }
  }

  @Tag.Identifier("Local Variable Read")
  public final class LocalVarRead extends Tags {
    private LocalVarRead() { }
  }

  @Tag.Identifier("Local Variable Write")
  public final class LocalVarWrite extends Tags {
    private LocalVarWrite() { }
  }

  @Tag.Identifier("Local Argument Read")
  public final class LocalArgRead extends Tags {
    private LocalArgRead() { }
  }

  @Tag.Identifier("Array Read")
  public final class ArrayRead extends Tags {
    private ArrayRead() { }
  }

  @Tag.Identifier("Array Write")
  public final class ArrayWrite extends Tags {
    private ArrayWrite() { }
  }

  @Tag.Identifier("Loop Node")
  public final class LoopNode extends Tags {
    private LoopNode() { }
  }

  @Tag.Identifier("Loop Body")
  public static final class LoopBody extends Tags {
    private LoopBody() { }
  }

  @Tag.Identifier("Basic Primitive")
  public final class BasicPrimitiveOperation extends Tags {
    private BasicPrimitiveOperation() { }
  }

  @Tag.Identifier("Complex Primitive")
  public final class ComplexPrimitiveOperation extends Tags {
    private ComplexPrimitiveOperation() { }
  }

  @Tag.Identifier("Primitive Argument")
  public final class PrimitiveArgument extends Tags {
    private PrimitiveArgument() { }
  }

  // some operation that somehow accesses a string
  @Tag.Identifier("String Access")
  public final class StringAccess extends Tags {
    private StringAccess() { }
  }

  @Tag.Identifier("Closure Application")
  public final class OpClosureApplication extends Tags {
    private OpClosureApplication() { }
  }

  @Tag.Identifier("Arithmetic")
  public final class OpArithmetic extends Tags {
    private OpArithmetic() { }
  }

  @Tag.Identifier("Comparison")
  public final class OpComparison extends Tags {
    private OpComparison() { }
  }

  @Tag.Identifier("Length")
  public final class OpLength extends Tags {
    private OpLength() { }
  }
}
