package som.primitives;

import java.util.ArrayList;
import som.interpreter.nodes.MessageSendNode.AbstractMessageSendNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.constants.MateClasses;
import som.vmobjects.MockJavaObject;
import som.vmobjects.SArray;
import som.vmobjects.SSymbol;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;
import com.oracle.truffle.api.source.SourceSection;


public class ASTNodePrims {
  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "selector",
             eagerSpecializable = false, mate = true)
  public abstract static class MateMessageSendNodesPrim extends UnaryExpressionNode {
    public MateMessageSendNodesPrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public final SSymbol doMock(final MockJavaObject receiver) {
      Node mockedNode = (Node) receiver.getMockedObject();
      assert mockedNode instanceof AbstractMessageSendNode;
      return ((AbstractMessageSendNode) mockedNode).getSelector();
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "ASTNode", selector = "filterNodesByClassname:",
      eagerSpecializable = false, mate = true)
  public abstract static class MateFilterNodesByClassPrim extends BinaryExpressionNode {
    public MateFilterNodesByClassPrim(final boolean eagWrap, final SourceSection source) {
    super(false, source);
    }
    
    @SuppressWarnings("unchecked")
    @Specialization
    public final SArray doMock(final MockJavaObject receiver, final String classname) {
      Node mockedNode = (Node) receiver.getMockedObject();
      Class<? extends Node> somNodeClass = null;
      try {
        somNodeClass = (Class<? extends Node>) Class.forName(classname);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      NodeTypeFilter filter = new NodeTypeFilter(somNodeClass);
      mockedNode.accept(filter);
      return SArray.create(filter.getResultingNodes().toArray());
    }
  }

  private static final class NodeTypeFilter implements NodeVisitor {
    
    Class<? extends Node> filter;
    private final ArrayList<MockJavaObject> selectedNodes = new ArrayList<MockJavaObject>();

    NodeTypeFilter(Class<? extends Node> filter) {
        this.filter = filter;
    }

    public boolean visit(Node node) {
        if (filter.isInstance(node)) {
          selectedNodes.add(new MockJavaObject(node, MateClasses.astNodeClass));
        }
        return true;
    }
    
    public ArrayList<MockJavaObject> getResultingNodes() {
      return selectedNodes;
    }
  }

}
