package som.interpreter.nodes.dispatch;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.InvalidAssumptionException;
import com.oracle.truffle.api.object.DynamicObject;

import som.interpreter.SArguments;
import som.matenodes.IntercessionHandling;
import som.vm.constants.ExecutionLevel;
import som.vmobjects.SInvokable.SMethod;

public class MateCachedDispatchNode extends CachedDispatchNode {
  @Child IntercessionHandling ih;
  final DynamicObject method;

  public MateCachedDispatchNode(final DispatchGuard guard, final DynamicObject methodToCall,
      final AbstractDispatchNode nextInCache, final boolean shouldSplit, final ExecutionLevel level) {
    super(guard, methodToCall, nextInCache, shouldSplit, level);
    method = methodToCall;
    ih = IntercessionHandling.createForMethodActivation(SMethod.getSignature(method));
  }

  @Override
  public Object executeDispatch(final VirtualFrame frame,
      final DynamicObject environment, final ExecutionLevel exLevel, final Object[] arguments) {
    Object rcvr = arguments[0];
    try {
      if (morphicness.profile(guard.entryMatches(rcvr))) {
        Object[] realArgs = (Object[]) ih.doMateSemantics(frame, arguments);
        if (realArgs == null) {
          realArgs = SArguments.createSArguments(environment, exLevel, arguments);
        }
        return cachedMethod.call(realArgs);
      } else {
        return nextInCache.executeDispatch(frame, environment, exLevel, arguments);
      }
    } catch (InvalidAssumptionException e) {
      CompilerDirectives.transferToInterpreter();
      return replace(nextInCache).
          executeDispatch(frame, environment, exLevel, arguments);
    }
  }

  /*public Object executeBasicDispatch(final VirtualFrame frame,
      final DynamicObject environment, final ExecutionLevel exLevel, final Object[] arguments) {
    return super.executeDispatch(frame, environment, exLevel, arguments);
  }*/

}
