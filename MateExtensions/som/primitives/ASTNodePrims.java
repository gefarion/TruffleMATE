package som.primitives;

import java.util.ArrayList;
import java.util.List;

import som.interpreter.MateNode;
import som.interpreter.nodes.MessageSendNode.AbstractMessageSendNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.interpreter.objectstorage.FieldAccessorNode.ReadFieldNode;
import som.vm.ObjectMemory;
import som.vm.Universe;
import som.vmobjects.MockJavaObject;
import som.vmobjects.SArray;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Introspection;
import com.oracle.truffle.api.dsl.Introspection.Provider;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Introspection.SpecializationInfo;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;


public class ASTNodePrims {
  @GenerateNodeFactory
  @Primitive(klass = "MessageSendNode", selector = "selector",
             eagerSpecializable = false, mate = true)
  public abstract static class MateMessageSendNodesPrim extends UnaryExpressionNode {
    public MateMessageSendNodesPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SSymbol doMock(final MockJavaObject receiver) {
      AbstractMessageSendNode mockedNode = (AbstractMessageSendNode) receiver.getMockedObject();
      return mockedNode.getSelector();
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "FieldReadNode", selector = "fieldIndex",
             eagerSpecializable = false, mate = true)
  public abstract static class MateFieldReadFieldIndexPrim extends UnaryExpressionNode {
    public MateFieldReadFieldIndexPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final long doMock(final MockJavaObject receiver) {
      ReadFieldNode mockedNode = (ReadFieldNode) receiver.getMockedObject();
      return mockedNode.getFieldIndex();
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "isMateNode",
             eagerSpecializable = false, mate = true)
  public abstract static class IsMateNodePrim extends UnaryExpressionNode {
    public IsMateNodePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final boolean doMock(final MockJavaObject receiver) {
      return receiver.getMockedObject() instanceof MateNode;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "filterNodesByClassname:as:",
      eagerSpecializable = false, mate = true)
  public abstract static class MateFilterNodesByClassPrim extends TernaryExpressionNode {
    public MateFilterNodesByClassPrim(final boolean eagWrap, final SourceSection source) {
    super(false, source);
    }
    
    @SuppressWarnings("unchecked")
    @Specialization
    public final SArray doMock(final MockJavaObject receiver, final String classname,
        DynamicObject klass) {
      Node mockedNode = (Node) receiver.getMockedObject();
      Class<? extends Node> somNodeClass = null;
      try {
        somNodeClass = (Class<? extends Node>) Class.forName(classname);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      
      ArrayList<Node> nodes = filterChildrenByClass(mockedNode, somNodeClass);
      MockJavaObject[] stNodes = new MockJavaObject[nodes.size()]; 
      int i = 0;
      for (Node node: nodes){
        stNodes[i] = new MockJavaObject(node, klass);
        i++;
      }
      return SArray.create(stNodes);
    }
    
    public static ArrayList<Node> filterChildrenByClass(Node node, Class<? extends Node> somClass){
      NodeTypeFilter filter = new NodeTypeFilter(somClass);
      node.accept(filter);
      return filter.getResultingNodes();
    }
  }

  private static final class NodeTypeFilter implements NodeVisitor {
    
    Class<? extends Node> filter;
    private final ArrayList<Node> selectedNodes = new ArrayList<Node>();

    NodeTypeFilter(Class<? extends Node> filter) {
        this.filter = filter;
    }

    public boolean visit(Node node) {
      if (filter.isInstance(node)) {
        selectedNodes.add(node);
      }
      return true;
    }
    
    public ArrayList<Node> getResultingNodes() {
      return selectedNodes;
    }
  }
  
  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "dispatchChain",
      eagerSpecializable = false, mate = true)
  public abstract static class GetDispatchChainPrim extends UnaryExpressionNode {
    public GetDispatchChainPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }
    
    @Specialization(guards = "isIntrospectable(receiver)")
    public final DynamicObject doInstrospectable(final MockJavaObject receiver) {
      Node mockedNode = (Node) receiver.getMockedObject();
      assert Introspection.isIntrospectable(mockedNode);
      List<SpecializationInfo> specializations = Introspection.getSpecializations(mockedNode);
      DynamicObject[] stSpecializations = new DynamicObject[specializations.size()];
      int i = 0;
      ObjectMemory engine = Universe.getCurrent().getObjectMemory();
      
      for (SpecializationInfo specialization : specializations){
        DynamicObject stSpecialization = engine.newObject(
            engine.getGlobal(engine.symbolFor("Specialization")));
        stSpecialization.define(0, specialization.getMethodName());
        stSpecialization.define(1, specialization.isActive());
        stSpecialization.define(2, (long) specialization.getInstances());
        stSpecializations[i] = stSpecialization; 
        i++;
      }
      return this.createChain(stSpecializations);
    }
    
    @Specialization(guards = "isMessageSendNode(receiver)")
    public final DynamicObject doMessage(final MockJavaObject receiver) {
      AbstractMessageSendNode mockedNode = (AbstractMessageSendNode) receiver.getMockedObject();
      DynamicObject[] specializations = mockedNode.getSpecializations();
      return this.createChain(specializations);
    }
    
    protected DynamicObject createChain(DynamicObject[] specializations){
      ObjectMemory engine = Universe.getCurrent().getObjectMemory();
      DynamicObject chain = engine.newObject(engine.getGlobal(
          engine.symbolFor("DispatchChain")));
      chain.define(0, SArray.create(specializations));
      return chain;
    }
    
    protected static boolean isIntrospectable(MockJavaObject object){
      return object.getMockedObject() instanceof Provider;
    }
    
    protected static boolean isMessageSendNode(MockJavaObject object){
      return object.getMockedObject() instanceof AbstractMessageSendNode;
    }
  }


}
