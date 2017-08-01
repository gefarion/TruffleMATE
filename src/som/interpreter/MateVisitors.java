package som.interpreter;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameInstance.FrameAccess;
import com.oracle.truffle.api.frame.FrameInstanceVisitor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;

import som.vm.Universe;
import som.vm.constants.ExecutionLevel;


public class MateVisitors {

  public static class FindFirstBaseLevelFrame implements FrameInstanceVisitor<FrameInstance>{

    @Override
    public FrameInstance visitFrame(final FrameInstance frameInstance) {
      if (SArguments.getExecutionLevel(frameInstance.getFrame(FrameAccess.MATERIALIZE)) == ExecutionLevel.Base) {
        return frameInstance;
      }
      return null;
    }
  }

  public static class FindSenderFrame implements FrameInstanceVisitor<FrameInstance>{
    private final FrameOnStackMarker toFind;
    private Boolean currentFound;

    public FindSenderFrame(final Frame frame) {
      FrameOnStackMarker marker;
      try {
        marker = (FrameOnStackMarker) frame.getObject(frame.getFrameDescriptor().findFrameSlot(Universe.frameOnStackSlotName()));
      } catch (FrameSlotTypeException e) {
        marker = null;
        e.printStackTrace();
      }
      toFind = marker;
      currentFound = false;
    }

    @Override
    public FrameInstance visitFrame(final FrameInstance frameInstance) {
      if (currentFound) { return frameInstance; }
      Frame materialized = frameInstance.getFrame(FrameAccess.MATERIALIZE);
      FrameSlot slot = materialized.getFrameDescriptor().findFrameSlot(Universe.frameOnStackSlotName());
      if (slot != null) {
        try {
          FrameOnStackMarker marker = (FrameOnStackMarker) (materialized.getObject(slot));
          if (marker == toFind) {
            currentFound = true;
          }
        } catch (FrameSlotTypeException e) {
          e.printStackTrace();
        }
      }
      return null;
    }
  }

  public static class FindFirstMateNode implements NodeVisitor {
    MateNode matenode;

    public MateNode mateNode() {
      return matenode;
    }

    @Override
    public boolean visit(final Node node) {
      if (node instanceof MateNode) {
        matenode = (MateNode) node;
        return false;
      }
      return true;
    }
  }
}
