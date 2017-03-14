package som.interpreter;

import com.oracle.truffle.api.nodes.Node;

public interface ReflectiveNode {
  /**
   * If necessary, this method wraps the node, and replaces it in the AST with
   * the wrapping node.
   * @return 
   */
  default void wrapIntoMateNode() {
    Node replacement = this.asMateNode();
    if (replacement != null) {
      ((Node) this).replace(replacement);
    }
  }

  default Node asMateNode() {
    // do nothing!
    // only a small subset of nodes needs to implement this method.
    return null;
  }
}
