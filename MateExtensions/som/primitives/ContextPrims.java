package som.primitives;

import som.interpreter.FrameOnStackMarker;
import som.interpreter.Invokable;
import som.interpreter.MateVisitors;
import som.interpreter.SArguments;
import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.Universe;
import som.vmobjects.MockJavaObject;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleRuntime;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameInstance.FrameAccess;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;


public class ContextPrims {
  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "method", receiverType = {FrameInstance.class})
  public abstract static class GetMethodPrim extends UnaryExpressionNode {
    public GetMethodPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final DynamicObject doMaterializedFrame(final FrameInstance frame) {
      RootCallTarget target = ((RootCallTarget) frame.getCallTarget());
      return ((Invokable) target.getRootNode()).getBelongsToMethod();
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "sender", receiverType = {FrameInstance.class})
  public abstract static class SenderPrim extends UnaryExpressionNode {
    public SenderPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final FrameInstance doMaterializedFrame(final FrameInstance frame) {
      TruffleRuntime runtime = ((Universe) ((ExpressionNode) this).getRootNode().getExecutionContext()).getTruffleRuntime();
      FrameInstance sender;
      if (runtime.getCurrentFrame() == frame) {
        sender = runtime.getCallerFrame();
      } else {
        sender = runtime.iterateFrames(new MateVisitors.FindSenderFrame(frame.getFrame(FrameAccess.MATERIALIZE)));
      }
      Frame senderFrame = sender.getFrame(FrameAccess.MATERIALIZE);
      if (senderFrame.getFrameDescriptor().findFrameSlot(Universe.frameOnStackSlotName()) == null) {
        senderFrame.setObject(
            senderFrame.getFrameDescriptor().addFrameSlot(Universe.frameOnStackSlotName(), FrameSlotKind.Object),
            new FrameOnStackMarker());
      }
      return sender;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "receiver", receiverType = {FrameInstance.class})
  public abstract static class GetReceiverFromContextPrim extends UnaryExpressionNode {
    public GetReceiverFromContextPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final DynamicObject doMaterializedFrame(final FrameInstance frame) {
      Frame virtualFrame = frame.getFrame(FrameAccess.READ_ONLY);
      return (DynamicObject) SArguments.rcvr(virtualFrame);
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "localAt:", receiverType = { MockJavaObject.class })
  public abstract static class GetLocalVarAtPrim extends BinaryExpressionNode {
    public GetLocalVarAtPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    //Todo: dispatch chain index->slot
    @Specialization
    public final Object doVirtualFrame(final MockJavaObject mockedFrame,
        String identifier) {
      FrameInstance frameInstance = (FrameInstance) mockedFrame.getMockedObject();
      Frame frame = frameInstance.getFrame(FrameAccess.READ_ONLY);
      FrameSlot slot = frame.getFrameDescriptor().findFrameSlot(identifier);
      return frame.getValue(slot);
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "localAt:put:", receiverType = { MockJavaObject.class })
  public abstract static class LocalVarAtPutPrim extends TernaryExpressionNode {
    public LocalVarAtPutPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    //Todo: dispatch chain index->slot
    @Specialization
    public final Object doVirtualFrame(final MockJavaObject mockedFrame,
        String identifier, Object value) {
      FrameInstance frameInstance = (FrameInstance) mockedFrame.getMockedObject();
      Frame frame = frameInstance.getFrame(FrameAccess.READ_WRITE);
      FrameSlot slot = frame.getFrameDescriptor().findFrameSlot(identifier);
      slot.setKind(FrameSlotKind.Object); //This probably needs a proper type specialization for performance
      frame.setObject(slot, value);
      return value;
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "Context", selector = "argAt:", receiverType = { MockJavaObject.class })
  public abstract static class GetArgAtPrim extends BinaryExpressionNode {
    public GetArgAtPrim(final boolean eagWrap, SourceSection source) {
      super(eagWrap, source);
    }

    //Todo: dispatch chain index->slot
    @Specialization
    public final Object doVirtualFrame(final MockJavaObject mockedFrame,
        long index) {
      FrameInstance frameInstance = (FrameInstance) mockedFrame.getMockedObject();
      Frame frame = frameInstance.getFrame(FrameAccess.READ_ONLY);
      return SArguments.arg(frame, (int) index);
    }
  }

}
