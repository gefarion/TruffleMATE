package tools.dym.profiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import som.vmobjects.SObject;
import tools.dym.profiles.AllocationProfileFactory.AllocProfileNodeGen;
import tools.dym.profiles.CallsiteProfile.Counter;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;


public class AllocationProfile extends tools.dym.profiles.Counter {

  protected final AllocProfileNode profile;

  public AllocationProfile(final SourceSection source) {
    super(source);
    this.profile = AllocProfileNodeGen.create();
  }

  public AllocProfileNode getProfile() {
    return profile;
  }

  public Map<DynamicObject, Integer> getAllocations() {
    return profile.getAllocations();
  }

  public abstract static class AllocProfileNode extends Node {
    protected Map<DynamicObject, Counter> allocationMap = new HashMap<>();

    public abstract void executeProfiling(DynamicObject obj);

    public Map<DynamicObject, Integer> getAllocations() {
      HashMap<DynamicObject, Integer> result = new HashMap<>();
      for (Entry<DynamicObject, Counter> e : allocationMap.entrySet()) {
        result.put(e.getKey(), e.getValue().val);
      }
      return result;
    }

    protected Counter createCounterFor(final DynamicObject klass) {
      Counter c = allocationMap.get(klass);
      if (c != null) {
        return c;
      }
      c = new Counter();
      allocationMap.put(klass, c);
      return c;
    }

    @Specialization(guards = "getClass(object) == cachedClass", limit = "100")
    public void doDynamicObject(final DynamicObject object,
        @Cached("getClass(object)") final DynamicObject cachedClass,
        @Cached("createCounterFor(cachedClass)") final Counter counter) {
      counter.inc();
    }

    public static DynamicObject getClass(final DynamicObject obj) {
      return SObject.getSOMClass(obj);
    }
  }
}
