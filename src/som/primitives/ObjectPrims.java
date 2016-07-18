package som.primitives;

import som.interpreter.Types;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.primitives.reflection.IndexDispatch;
import som.vm.Universe;
import som.vm.constants.Nil;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SClass;
import som.vmobjects.SObject;
import som.vmobjects.SReflectiveObject;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;


public final class ObjectPrims {

  @GenerateNodeFactory
  public abstract static class InstVarAtPrim extends BinaryExpressionNode {

    @Child private IndexDispatch dispatch;

    public InstVarAtPrim() {
      super();
      dispatch = IndexDispatch.create();
    }
    public InstVarAtPrim(final InstVarAtPrim node) { this(); }

    @Specialization
    public final Object doSObject(final DynamicObject receiver, final long idx) {
      return dispatch.executeDispatch(receiver, (int) idx - 1);
    }

    @Override
    public final Object executeEvaluated(final VirtualFrame frame,
      final Object receiver, final Object firstArg) {
      assert receiver instanceof DynamicObject;
      assert firstArg instanceof Long;

      DynamicObject rcvr = (DynamicObject) receiver;
      long idx     = (long) firstArg;
      return doSObject(rcvr, idx);
    }
  }

  @GenerateNodeFactory
  public abstract static class InstVarAtPutPrim extends TernaryExpressionNode {
    @Child private IndexDispatch dispatch;

    public InstVarAtPutPrim() {
      super();
      dispatch = IndexDispatch.create();
    }
    public InstVarAtPutPrim(final InstVarAtPutPrim node) { this(); }

    @Specialization
    public final Object doSObject(final DynamicObject receiver, final long idx, final Object val) {
      dispatch.executeDispatch(receiver, (int) idx - 1, val);
      return val;
    }

    @Override
    public final Object executeEvaluated(final VirtualFrame frame,
      final Object receiver, final Object firstArg, final Object secondArg) {
      assert receiver instanceof DynamicObject;
      assert firstArg instanceof Long;
      assert secondArg != null;

      DynamicObject rcvr = (DynamicObject) receiver;
      long idx     = (long) firstArg;
      return doSObject(rcvr, idx, secondArg);
    }
  }

  @GenerateNodeFactory
  public abstract static class InstVarNamedPrim extends BinaryExpressionNode {
    @TruffleBoundary
    @Specialization
    public final Object doSObject(final DynamicObject receiver, final SSymbol fieldName) {
      //CompilerAsserts.neverPartOfCompilation("InstVarNamedPrim");
      return receiver.get(SClass.lookupFieldIndex(SObject.getSOMClass(receiver), fieldName), Nil.nilObject);
    }
  }

  @GenerateNodeFactory
  public abstract static class HaltPrim extends UnaryExpressionNode {
    public HaltPrim() { super(null); }
    @Specialization
    public final Object doSAbstractObject(final Object receiver) {
      Universe.errorPrintln("BREAKPOINT");
      return receiver;
    }
  }

  @GenerateNodeFactory
  public abstract static class ClassPrim extends UnaryExpressionNode {
    @Specialization
    public final DynamicObject doSAbstractObject(final SAbstractObject receiver) {
      return receiver.getSOMClass();
    }

    @Specialization
    public final DynamicObject doDynamicObject(final DynamicObject receiver) {
      return SObject.getSOMClass(receiver);
    }

    @Specialization
    public final DynamicObject doObject(final Object receiver) {
      return Types.getClassOf(receiver);
    }
  }

  @GenerateNodeFactory
  public abstract static class installEnvironmentPrim extends BinaryExpressionNode {
    @Specialization
    public final Object doSObject(final DynamicObject receiver, final DynamicObject environment) {
      SReflectiveObject.setEnvironment(receiver, environment);
      return receiver;
    }
  }
  
  @GenerateNodeFactory
  public abstract static class ShallowCopyPrim extends UnaryExpressionNode {
    @Specialization
    public final Object doSObject(final DynamicObject receiver) {
      return receiver.copy(receiver.getShape());
    }
  }
  
  @GenerateNodeFactory
  public abstract static class HashPrim extends UnaryExpressionNode {
    @Specialization
    public final long doSObject(final DynamicObject receiver) {
      return receiver.hashCode();
    }
  }
}