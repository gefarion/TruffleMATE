package som.interpreter;

import com.oracle.truffle.api.nodes.Node;

public interface ReflectiveNode {
  /**
   * If necessary, this method returns the mate node that replace the current base AST node.
   * Mate needed preprocessing can also be implemented by reimplementing this node
   * @return 
   */
  default Node asMateNode() {
    // do nothing!
    // only a small subset of nodes needs to implement this method.
    return null;
  }
}
