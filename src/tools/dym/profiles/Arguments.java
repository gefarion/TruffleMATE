package tools.dym.profiles;

import java.util.Arrays;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.utilities.JSONHelper;
import com.oracle.truffle.api.utilities.JSONHelper.JSONArrayBuilder;
import com.oracle.truffle.api.utilities.JSONHelper.JSONObjectBuilder;

import som.interpreter.Types;
import som.vmobjects.SClass;

public final class Arguments {

  private final Class<?>[] argJavaTypes;

  // TODO: do we need this, or is the first sufficient?
  //       this makes it language specific...
  private final DynamicObject[]   argSomTypes;

  Arguments(final Object[] arguments) {
    this.argJavaTypes = getJavaTypes(arguments);
    this.argSomTypes  = getSomTypes(arguments);
  }

  private static Class<?>[] getJavaTypes(final Object[] args) {
    return Arrays.stream(args).
        map(e -> e.getClass()).
        toArray(Class[]::new);  // remove the <?> because of checkstyle issue
  }

  private static DynamicObject[] getSomTypes(final Object[] args) {
    return Arrays.stream(args).
        map(e -> Types.getClassOf(e)).
        toArray(DynamicObject[]::new);
  }

  @Override
  public boolean equals(final Object obj) {
    if (super.equals(obj)) {
      return true;
    }
    if (!(obj instanceof Arguments)) {
      return false;
    }

    Arguments o = (Arguments) obj;

    return Arrays.equals(argJavaTypes, o.argJavaTypes)
        || Arrays.equals(argSomTypes,  o.argSomTypes);
  }

  public boolean argTypeIs(final int idx, final String name) {
    return getArgType(idx).equals(name);
  }

  public String getArgType(final int idx) {
    return getArgType(argSomTypes[idx]);
  }

  public String getArgType(final DynamicObject o) {
    return SClass.getName(o).getString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(argJavaTypes);
    result = prime * result + Arrays.hashCode(argSomTypes);
    return result;
  }

  public JSONObjectBuilder toJson() {
    JSONObjectBuilder result = JSONHelper.object();

    JSONArrayBuilder javaTypes = JSONHelper.array();
    for (Class<?> c : argJavaTypes) {
      javaTypes.add(c.getSimpleName());
    }

    result.add("javaTypes", javaTypes);

    JSONArrayBuilder somTypes = JSONHelper.array();
    for (DynamicObject c : argSomTypes) {
      somTypes.add(getArgType(c).toString());
    }
    result.add("somTypes", somTypes);
    return result;
  }

  @Override
  public String toString() {
    String result = "";
    for (DynamicObject c : argSomTypes) {
      if (result.equals("")) {
        result = getArgType(c);
      } else {
        result += ", " + getArgType(c);
      }
    }
    return result;
  }
}
