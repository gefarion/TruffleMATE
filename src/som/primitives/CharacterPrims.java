package som.primitives;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.primitives.Primitives.Specializer;
import som.primitives.arrays.NewPrim;
import som.vm.Universe;
import som.vm.constants.Classes;
import tools.dym.Tags.StringAccess;

public class CharacterPrims {

  @GenerateNodeFactory
  @Primitive(klass = "Character class", selector = "new:",
      specializer = NewCharPrim.IsCharacterClass.class, eagerSpecializable = false)
  // No specialization to avoid clash with new: from Arrays
  public abstract static class NewCharPrim extends BinaryExpressionNode {

    public NewCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final Character doCreate(final DynamicObject clazz, final long value) {
      return (char) value;
    }

    public static class IsCharacterClass extends Specializer<NewPrim> {
      public IsCharacterClass(final Primitive prim, final NodeFactory<NewPrim> fact, final Universe vm) { super(prim, fact, vm); }

      @Override
      public boolean matches(final Object[] args, final ExpressionNode[] argNodes) {
        return receiverIsCharacterClass((DynamicObject) args[0]);
      }

      protected static final boolean receiverIsCharacterClass(final DynamicObject receiver) {
        return receiver == Classes.characterClass;
      }
    }


    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "asInteger", eagerSpecializable = false)
  public abstract static class AsIntegerCharPrim extends UnaryExpressionNode {
    public AsIntegerCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doCharacter(final char subject) {
      return subject;
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "isDigit")
  public abstract static class IsDigitCharPrim extends UnaryExpressionNode {
    public IsDigitCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final boolean doCharacter(final char subject) {
      return Character.isDigit(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "asDigit")
  public abstract static class AsDigitCharPrim extends UnaryExpressionNode {
    public AsDigitCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doCharacter(final char subject) {
      return Character.getNumericValue(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "isLetter")
  public abstract static class IsLetterCharPrim extends UnaryExpressionNode {
    public IsLetterCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final boolean doCharacter(final char subject) {
      return Character.isLetter(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "isAlphaNumeric")
  public abstract static class IsAlphaNumericCharPrim extends UnaryExpressionNode {
    public IsAlphaNumericCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final boolean doCharacter(final char subject) {
      return Character.isLetterOrDigit(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "asUppercase")
  public abstract static class AsUppercaseCharPrim extends UnaryExpressionNode {
    public AsUppercaseCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final char doCharacter(final char subject) {
      return Character.toUpperCase(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "isUppercase")
  public abstract static class IsUppercaseCharPrim extends UnaryExpressionNode {
    public IsUppercaseCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final boolean doCharacter(final char subject) {
      return Character.isUpperCase(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "asLowercase")
  public abstract static class AsLowercaseCharPrim extends UnaryExpressionNode {
    public AsLowercaseCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final char doCharacter(final char subject) {
      return Character.toLowerCase(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "isLowercase")
  public abstract static class IsLowercaseCharPrim extends UnaryExpressionNode {
    public IsLowercaseCharPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final boolean doCharacter(final char subject) {
      return Character.isLowerCase(subject);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "Character", selector = "compareWith:", eagerSpecializable = false)
  public abstract static class CompareCharsPrim extends BinaryExpressionNode {
    public CompareCharsPrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public final long doCharacter(final char receiver, final char param) {
      return Character.compare(receiver, param);
    }

    @Override
    protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
      if (tag == StringAccess.class) {
        return true;
      } else {
        return super.hasTagIgnoringEagerness(tag);
      }
    }
  }
}
