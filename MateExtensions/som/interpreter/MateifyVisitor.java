package som.interpreter;

import som.vmobjects.SInvokable.SMethod;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;


public class MateifyVisitor implements NodeVisitor {

  @Override
  public boolean visit(final Node node) {
    /*Some methods should not be mateify. At the moment, those methods must include the SPECIAL string in the selector. 
     A proper implementation should use an annotation at the Smalltalk method to define it as only base-level method!*/
    if (node instanceof Invokable) {
      if (SMethod.getSignature(((Invokable) (node)).getBelongsToMethod()).getString().matches("(.*)SPECIAL(.*)")) {
        return false;
      }
    }
    if (node instanceof ReflectiveNode) {
      Node replacement = ((ReflectiveNode) node).asMateNode();
      if (replacement != null) {
        node.replace(replacement);
      }
    }
    return true;
  }
}
