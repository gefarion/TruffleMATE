package som.primitives.arrays;

import java.lang.reflect.Array;
import som.interpreter.SArguments;
import som.interpreter.nodes.ExpressionNode;
import som.vm.Universe;
import som.vmobjects.SArray;
import som.vmobjects.SArray.ArrayType;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ValueProfile;

@ImportStatic(ArrayType.class)
@NodeChildren({
  @NodeChild("somArray"),
  @NodeChild("receiver")})
@GenerateNodeFactory
public abstract class ToHostArrayNode extends ExpressionNode {
  private final ValueProfile storageType = ValueProfile.createClassProfile();

  // to have uniform create() for @Primitive
  public ToHostArrayNode(final boolean eagWrap) {
    super(Universe.emptySource.createUnavailableSection());
  }

  public static final boolean isNull(final Object somArray) {
    return somArray == null;
  }

  public abstract Object[] executedEvaluated(SArray somArray, Object rcvr);

  @Specialization(guards = "isNull(somArray)")
  public Object[] doNoArray(final Object somArray, final Object rcvr) {
    return new Object[] {};
  }

  @Specialization(guards = "isEmptyType(somArray)")
  public Object[] doEmptyArray(final SArray somArray, final Object rcvr) {
    return new Object[] {};
  }

  @Specialization(guards = "isPartiallyEmptyType(somArray)")
  public Object[] doPartiallyEmptyArray(final SArray somArray, final Object rcvr) {
    return somArray.getPartiallyEmptyStorage(storageType).getStorage();
  }

  @Specialization(guards = "isObjectType(somArray)")
  public Object[] doObjectArray(final SArray somArray,
      final Object rcvr) {
    return somArray.getObjectStorage(storageType);
  }

  @Specialization(guards = "isSomePrimitiveType(somArray)")
  public Object[] doPrimitiveArray(final SArray somArray,
      final Object rcvr) {
    storageType.profile(somArray);
    int arrlength = Array.getLength(somArray.getStoragePlain()) + this.extraLength();
    Object[] outputArray = new Object[arrlength];
    for (int i = this.extraLength(); i < arrlength; ++i) {
       outputArray[i] = Array.get(somArray.getStoragePlain(), i - this.extraLength());
    }
    return outputArray;
  }

  protected int extraLength() {
    return 0;
  }

  public abstract static class ToArgumentsArrayNode extends ToHostArrayNode {

    public ToArgumentsArrayNode(boolean eagWrap) {
      super(eagWrap);
    }

    public final Object[] executedEvaluated(final Object somArray, final Object rcvr) {
      return executedEvaluated((SArray) somArray, rcvr);
    }

    @Override
    @Specialization(guards = "isNull(somArray)")
    public final Object[] doNoArray(final Object somArray, final Object rcvr) {
      return new Object[] {rcvr};
    }

    @Override
    @Specialization(guards = "isEmptyType(somArray)")
    public Object[] doEmptyArray(final SArray somArray, final Object rcvr) {
      return new Object[] {rcvr};
    }

    private Object[] addRcvrToObjectArray(final Object rcvr, final Object[] storage) {
      Object[] argsArray = new Object[storage.length + 1];
      argsArray[SArguments.RCVR_ARGUMENTS_OFFSET] = rcvr;
      System.arraycopy(storage, 0, argsArray, 1, storage.length);
      return argsArray;
    }

    @Override
    @Specialization(guards = "isPartiallyEmptyType(somArray)")
    public final Object[] doPartiallyEmptyArray(final SArray somArray,
        final Object rcvr) {
      return addRcvrToObjectArray(
          rcvr, super.doPartiallyEmptyArray(somArray, rcvr));
    }

    @Override
    @Specialization(guards = "isObjectType(somArray)")
    public final Object[] doObjectArray(final SArray somArray,
        final Object rcvr) {
      return addRcvrToObjectArray(rcvr, super.doObjectArray(somArray, rcvr));
    }

    @Override
    @Specialization(guards = "isSomePrimitiveType(somArray)")
    public final Object[] doPrimitiveArray(final SArray somArray,
        final Object rcvr) {
      Object[] args = super.doPrimitiveArray(somArray, rcvr);
      args[0] = rcvr;
      return args;
    }

    @Override
    protected int extraLength() {
      return 1;
    }
  }
}
