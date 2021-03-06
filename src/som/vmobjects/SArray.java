package som.vmobjects;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.profiles.ValueProfile;

import som.vm.Universe;
import som.vm.constants.Classes;
import som.vm.constants.Nil;

/**
 * SArrays are implemented using a Strategy-like approach.
 * The SArray objects are 'tagged' with a type, and the strategy behavior
 * is implemented directly in the AST nodes.
 *
 * @author smarr
 */
public final class SArray extends SAbstractObject {
  public static final int FIRST_IDX = 0;

  public static SArray create(final Object[] values) {
    return new SArray(values);
  }

  public static SArray create(final long[] values) {
    return new SArray(values);
  }

  public static SArray create(final double[] values) {
    return new SArray(values);
  }

  public static SArray create(final boolean[] values) {
    return new SArray(values);
  }

  public static SArray create(final byte[] values) {
    return new SArray(values);
  }

  public static SArray create(final char[] values) {
    return new SArray(values);
  }

  public static SArray create(final int length) {
    return new SArray(length);
  }

  private ArrayType type;
  private Object    storage;

  public ArrayType getType() {
    return type;
  }

  public int getEmptyStorage(final ValueProfile storageType) {
    assert type == ArrayType.EMPTY;
    return (int) storageType.profile(storage);
  }

  public PartiallyEmptyArray getPartiallyEmptyStorage(final ValueProfile storageType) {
    assert type == ArrayType.PARTIAL_EMPTY;
    return (PartiallyEmptyArray) storageType.profile(storage);
  }

  public Object[] getObjectStorage(final ValueProfile storageType) {
    assert type == ArrayType.OBJECT;
    return (Object[]) storageType.profile(storage);
  }

  public long[] getLongStorage(final ValueProfile storageType) {
    assert type == ArrayType.LONG;
    return (long[]) storageType.profile(storage);
  }

  public double[] getDoubleStorage(final ValueProfile storageType) {
    assert type == ArrayType.DOUBLE;
    return (double[]) storageType.profile(storage);
  }

  public boolean[] getBooleanStorage(final ValueProfile storageType) {
    assert type == ArrayType.BOOLEAN;
    return (boolean[]) storageType.profile(storage);
  }

  public byte[] getByteStorage(final ValueProfile storageType) {
    assert type == ArrayType.BYTE;
    return (byte[]) storageType.profile(storage);
  }

  public char[] getCharStorage(final ValueProfile storageType) {
    assert type == ArrayType.CHAR;
    return (char[]) storageType.profile(storage);
  }

  public Object getStoragePlain() {
    CompilerAsserts.neverPartOfCompilation();
    return storage;
  }

  public Object[] toJavaArray(final ValueProfile profile) {
    if (ArrayType.isEmptyType(this)) {
      this.transitionToObjectWithAll((int) this.storage, Nil.nilObject);
    }
    if (this.getType() == ArrayType.PARTIAL_EMPTY) {
      return this.
          getPartiallyEmptyStorage(profile).
          getStorage();
    } else {
      return this.getObjectStorage(profile);
    }
  }

  /**
   * Creates and empty array, using the EMPTY strategy.
   * @param length
   */
  public SArray(final long length) {
    type = ArrayType.EMPTY;
    storage = (int) length;
  }

  private SArray(final Object[] val) {
    type = ArrayType.OBJECT;
    storage = val;
  }

  private SArray(final long[] val) {
    type = ArrayType.LONG;
    storage = val;
  }

  private SArray(final double[] val) {
    type = ArrayType.DOUBLE;
    storage = val;
  }

  private SArray(final boolean[] val) {
    type = ArrayType.BOOLEAN;
    storage = val;
  }

  private SArray(final byte[] val) {
    type = ArrayType.BYTE;
    storage = val;
  }

  private SArray(final char[] val) {
    type = ArrayType.CHAR;
    storage = val;
  }

  public SArray(final ArrayType type, final Object storage) {
    this.type    = type;
    this.storage = storage;
  }

  private void fromEmptyToParticalWithType(final ArrayType type, final long idx, final Object val) {
    assert this.type == ArrayType.EMPTY;
    int length = (int) storage;
    storage   = new PartiallyEmptyArray(type, length, idx, val);
    this.type = ArrayType.PARTIAL_EMPTY;
  }

  /**
   * Transition from the Empty, to the PartiallyEmpty state/strategy.
   */
  public void transitionFromEmptyToPartiallyEmptyWith(final long idx, final Object val) {
    fromEmptyToParticalWithType(ArrayType.OBJECT, idx, val);
  }

  public void transitionFromEmptyToPartiallyEmptyWith(final long idx, final long val) {
    fromEmptyToParticalWithType(ArrayType.LONG, idx, val);
  }

  public void transitionFromEmptyToPartiallyEmptyWith(final long idx, final double val) {
    fromEmptyToParticalWithType(ArrayType.DOUBLE, idx, val);
  }

  public void transitionFromEmptyToPartiallyEmptyWith(final long idx, final boolean val) {
    fromEmptyToParticalWithType(ArrayType.BOOLEAN, idx, val);
  }

  public void transitionFromEmptyToPartiallyEmptyWith(final long idx, final byte val) {
    fromEmptyToParticalWithType(ArrayType.BYTE, idx, val);
  }

  public void transitionToEmpty(final long length) {
    type = ArrayType.EMPTY;
    storage = (int) length;
  }

  public void transitionTo(final ArrayType newType, final Object newStorage) {
    type = newType;
    storage = newStorage;
  }

  public void transitionToObjectWithAll(final long length, final Object val) {
    type = ArrayType.OBJECT;
    Object[] arr = new Object[(int) length];
    Arrays.fill(arr, val);
    storage = arr;
  }

  public void transitionToLongWithAll(final long length, final long val) {
    type = ArrayType.LONG;
    long[] arr = new long[(int) length];
    Arrays.fill(arr, val);
    storage = arr;
  }

  public void transitionToDoubleWithAll(final long length, final double val) {
    type = ArrayType.DOUBLE;
    double[] arr = new double[(int) length];
    Arrays.fill(arr, val);
    storage = arr;
  }

  public void transitionToBooleanWithAll(final long length, final boolean val) {
    type = ArrayType.BOOLEAN;
    boolean[] arr = new boolean[(int) length];
    if (val) {
      Arrays.fill(arr, true);
    }
    storage = arr;
  }

  public void transitionToByteWithAll(final long length, final byte val) {
    type = ArrayType.BYTE;
    byte[] arr = new byte[(int) length];
    Arrays.fill(arr, val);
    storage = arr;
  }

  public enum ArrayType {
    EMPTY, PARTIAL_EMPTY, LONG, DOUBLE, BOOLEAN, BYTE, CHAR, OBJECT;

    public static boolean isEmptyType(final SArray receiver) {
      return receiver.getType() == ArrayType.EMPTY;
    }

    public static boolean isPartiallyEmptyType(final SArray receiver) {
      return receiver.getType() == ArrayType.PARTIAL_EMPTY;
    }

    public static boolean isObjectType(final SArray receiver) {
      return receiver.getType() == ArrayType.OBJECT;
    }

    public static boolean isLongType(final SArray receiver) {
      return receiver.getType() == ArrayType.LONG;
    }

    public static boolean isDoubleType(final SArray receiver) {
      return receiver.getType() == ArrayType.DOUBLE;
    }

    public static boolean isBooleanType(final SArray receiver) {
      return receiver.getType() == BOOLEAN;
    }

    public static boolean isByteType(final SArray receiver) {
      return receiver.getType() == BYTE;
    }

    public static boolean isCharType(final SArray receiver) {
      return receiver.getType() == CHAR;
    }
  }

  private static long[] createLong(final Object[] arr) {
    long[] storage = new long[arr.length];
    for (int i = 0; i < arr.length; i++) {
      storage[i] = (long) arr[i];
    }
    return storage;
  }

  private static double[] createDouble(final Object[] arr) {
    double[] storage = new double[arr.length];
    for (int i = 0; i < arr.length; i++) {
      storage[i] = (double) arr[i];
    }
    return storage;
  }

  private static boolean[] createBoolean(final Object[] arr) {
    boolean[] storage = new boolean[arr.length];
    for (int i = 0; i < arr.length; i++) {
      storage[i] = (boolean) arr[i];
    }
    return storage;
  }

  private static byte[] createByte(final Object[] arr) {
    byte[] storage = new byte[arr.length];
    for (int i = 0; i < arr.length; i++) {
      storage[i] = (byte) arr[i];
    }
    return storage;
  }

  public static final ValueProfile partialStorageType = ValueProfile.createClassProfile();

  public void ifFullTransitionPartiallyEmpty() {
    PartiallyEmptyArray arr = getPartiallyEmptyStorage(partialStorageType);

    if (arr.isFull()) {
      if (arr.getType() == ArrayType.LONG) {
        type = ArrayType.LONG;
        storage = createLong(arr.getStorage());
      } else if (arr.getType() == ArrayType.DOUBLE) {
        type = ArrayType.DOUBLE;
        storage = createDouble(arr.getStorage());
      } else if (arr.getType() == ArrayType.BOOLEAN) {
        type = ArrayType.BOOLEAN;
        storage = createBoolean(arr.getStorage());
      } else if (arr.getType() == ArrayType.BYTE) {
        type = ArrayType.BYTE;
        storage = createByte(arr.getStorage());
      } else {
        type = ArrayType.OBJECT;
        storage = arr.getStorage();
      }
    }
  }

  public static final class PartiallyEmptyArray {
    private final Object[] arr;
    private int emptyElements;
    private ArrayType type;

    public PartiallyEmptyArray(final ArrayType type, final int length,
        final long idx, final Object val) {
      // can't specialize this here already,
      // because keeping track for nils would be to expensive
      arr = new Object[length];
      Arrays.fill(arr, Nil.nilObject);
      emptyElements = length - 1;
      arr[(int) idx] = val;
      this.type = type;
    }

    private PartiallyEmptyArray(final PartiallyEmptyArray old) {
      arr = old.arr.clone();
      emptyElements = old.emptyElements;
      type = old.type;
    }

    public ArrayType getType() {
      return type;
    }

    public Object[] getStorage() {
      return arr;
    }

    public void setType(final ArrayType type) {
      this.type = type;
    }

    public int getLength() {
      return arr.length;
    }

    public Object get(final long idx) {
      return arr[(int) idx];
    }

    public void set(final long idx, final Object val) {
      arr[(int) idx] = val;
    }

    public void incEmptyElements() { emptyElements++; }
    public void decEmptyElements() { emptyElements--; }
    public boolean isFull() { return emptyElements == 0; }

    public PartiallyEmptyArray copy() {
      return new PartiallyEmptyArray(this);
    }
  }

  private static final ValueProfile objectStorageType = ValueProfile.createClassProfile();

  /**
   * For internal use only, specifically, for SClass.
   * There we now, it is either empty, or of OBJECT type.
   * @param value
   * @return
   */
  public SArray copyAndExtendWith(final Object value) {
    Object[] newArr;
    if (type == ArrayType.EMPTY) {
      newArr = new Object[] {value};
    } else {
      // if this is not true, this method is used in a wrong context
      assert type == ArrayType.OBJECT;
      Object[] s = getObjectStorage(objectStorageType);
      newArr = Arrays.copyOf(s, s.length + 1);
      newArr[s.length] = value;
    }
    return new SArray(newArr);
  }


  @Override
  public DynamicObject getSOMClass() {
    if (this.type != ArrayType.BYTE) {
      return Classes.arrayClass;
    } else {
      Universe current = Universe.getCurrent();
      return current.getGlobal(current.symbolFor("ByteArray"));
    }
  }

  @Override
  public ForeignAccess getForeignAccess() {
    // TODO Auto-generated method stub
    return null;
  }
}
