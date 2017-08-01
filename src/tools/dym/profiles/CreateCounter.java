package tools.dym.profiles;

import tools.dym.profiles.ReadValueProfile.ProfileCounter;

import com.oracle.truffle.api.object.DynamicObject;


public interface CreateCounter {
  ProfileCounter createCounter(DynamicObject klass);
}
