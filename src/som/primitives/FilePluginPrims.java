package som.primitives;

import java.io.File;
import java.io.IOException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.profiles.ValueProfile;
import com.oracle.truffle.api.source.SourceSection;

import som.interpreter.nodes.ExpressionNode;
import som.interpreter.nodes.nary.BinaryExpressionNode;
import som.interpreter.nodes.nary.ExpressionWithTagsNode;
import som.interpreter.nodes.nary.TernaryExpressionNode;
import som.interpreter.nodes.nary.UnaryExpressionNode;
import som.vm.Universe;
import som.vm.constants.Nil;
import som.vmobjects.SArray;
import som.vmobjects.SArray.ArrayType;
import som.vmobjects.SFile;


public abstract class FilePluginPrims {

  @GenerateNodeFactory
  @Primitive(klass = "FilePluginPrims", selector = "imageFile")
  public abstract static class ImageFilePrim extends UnaryExpressionNode {
    public ImageFilePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public String doGeneric(final DynamicObject receiver) {
      return System.getProperty("user.dir") + "/" + Universe.getCurrent().imageName();
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primOpen:writable:")
  public abstract static class OpenFilePrim extends TernaryExpressionNode {
    public OpenFilePrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    public Object doGeneric(final DynamicObject receiver, final String filename, final Boolean writable) {
      SFile file = new SFile(new File(filename), writable);
      if (!file.getFile().exists()) {
        return Nil.nilObject;
      }
      return file;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primGetPosition:")
  public abstract static class GetPositionFilePrim extends BinaryExpressionNode {

    public GetPositionFilePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public long doGeneric(final DynamicObject receiver, final SFile file) {
      return file.getPosition();
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primSetPosition:to:")
  public abstract static class SetPositionFilePrim extends TernaryExpressionNode {
    public SetPositionFilePrim(final boolean eagWrap, final SourceSection source) {
      super(eagWrap, source);
    }

    @Specialization
    @TruffleBoundary
    public long doGeneric(final DynamicObject receiver, final SFile file, final long position) {
      file.setPosition(position);
      return position;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primSize:")
  public abstract static class SizeFilePrim extends BinaryExpressionNode {
    public SizeFilePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    public long doGeneric(final DynamicObject receiver, final SFile file) {
      return file.getFile().length();
    }
  }

  @GenerateNodeFactory
  @NodeChildren({
    @NodeChild(value = "receiver", type = ExpressionNode.class),
    @NodeChild(value = "sfile", type = ExpressionNode.class),
    @NodeChild(value = "vector", type = ExpressionNode.class),
    @NodeChild(value = "starting", type = ExpressionNode.class),
    @NodeChild(value = "count", type = ExpressionNode.class),
  })
  @Primitive(klass = "StandardFileStream", selector = "primRead:into:startingAt:count:", eagerSpecializable = false)
  @ImportStatic(ArrayType.class)
  public abstract static class ReadIntoFilePrim extends ExpressionWithTagsNode {
    public ReadIntoFilePrim(final boolean eagWrap, final SourceSection source) {
      super(source);
    }

    private final ValueProfile storageType = ValueProfile.createClassProfile();

    @Specialization(guards = {"isByteType(collection)"})
    public long doEmptyBytes(final DynamicObject receiver, final SFile file, final SArray collection, final long startingAt, final long count) {
      if (ArrayType.isEmptyType(collection)) {
        collection.transitionTo(ArrayType.BYTE, new byte[(int) count]);
      }
      byte[] buffer = collection.getByteStorage(storageType);
      return read(file, buffer, (int) startingAt - 1, (int) count);
    }

    @TruffleBoundary
    @Specialization(guards = {"!isByteType(collection)"})
    public long doEmpty(final DynamicObject receiver, final SFile file, final SArray collection, final long startingAt, final long count) {
      byte[] buffer = new byte[(int) count];
      long countRead = read(file, buffer, (int) startingAt - 1, (int) count);
      /*TODO: Workaround this so in case the read is in a subpart of the array we do not lose the rest*/
      collection.transitionTo(ArrayType.CHAR, (new String(buffer)).toCharArray());
      return countRead;
    }

    @TruffleBoundary
    private static long read(final SFile file, final byte[] buffer, final int start, final int count) {
      try {
        return file.getInputStream().read(buffer, start, count);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return 0;
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primAtEnd:")
  public abstract static class AtEndFilePrim extends BinaryExpressionNode {
    public AtEndFilePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    @TruffleBoundary
    public boolean doGeneric(final DynamicObject receiver, final SFile file) {
      try {
        return file.getInputStream().available() == 0;
      } catch (IOException e) {
        Universe.errorExit("Error when trying to set file to eof");
        return false;
      }
    }
  }

  @GenerateNodeFactory
  @Primitive(klass = "StandardFileStream", selector = "primClose:")
  public abstract static class CloseFilePrim extends BinaryExpressionNode {
    public CloseFilePrim(final boolean eagWrap, final SourceSection source) {
      super(false, source);
    }

    @Specialization
    @TruffleBoundary
    public boolean doGeneric(final DynamicObject receiver, final SFile file) {
      try {
        file.close();
        return true;
      } catch (IOException e) {
        Universe.errorExit("Error when closing file");
        return false;
      }
    }
  }
}
