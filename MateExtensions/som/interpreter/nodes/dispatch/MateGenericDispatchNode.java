package som.interpreter.nodes.dispatch;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import som.matenodes.IntercessionHandling;
import som.vm.constants.ExecutionLevel;
import som.vmobjects.SSymbol;

public class MateGenericDispatchNode extends GenericDispatchNode {
  @Child IntercessionHandling ih;

  public MateGenericDispatchNode(final SourceSection source, final SSymbol selector) {
    super(source, selector);
    ih = IntercessionHandling.createForMethodActivation(selector);
  }

  @Override
  protected Object[] getArguments(final VirtualFrame frame, final DynamicObject environment, final ExecutionLevel exLevel, final Object[] arguments) {
    Object[] realArgs = (Object[]) ih.doMateSemantics(frame, arguments);
    if (realArgs == null) {
      return super.getArguments(frame, environment, exLevel, arguments);
    }
    return realArgs;
  }

}
