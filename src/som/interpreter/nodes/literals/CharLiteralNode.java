package som.interpreter.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;


public final class CharLiteralNode extends LiteralNode {

  private final char value;

  public CharLiteralNode(final char value, final SourceSection source) {
    super(source);
    this.value = value;
  }

  @Override
  public Character executeCharacter(final VirtualFrame frame) {
    return value;
  }

  @Override
  public Object executeGeneric(final VirtualFrame frame) {
    return value;
  }
}
