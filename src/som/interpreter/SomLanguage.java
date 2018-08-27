package som.interpreter;

import java.io.IOException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags.CallTag;
import com.oracle.truffle.api.instrumentation.StandardTags.RootTag;
import com.oracle.truffle.api.instrumentation.StandardTags.StatementTag;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.Source;

import som.vm.NotYetImplementedException;
import som.vm.Universe;
import tools.debugger.Tags.ArgumentTag;
import tools.debugger.Tags.CommentTag;
import tools.debugger.Tags.DelimiterClosingTag;
import tools.debugger.Tags.DelimiterOpeningTag;
import tools.debugger.Tags.IdentifierTag;
import tools.debugger.Tags.KeywordTag;
import tools.debugger.Tags.LiteralTag;
import tools.debugger.Tags.LocalVariableTag;
import tools.debugger.Tags.StatementSeparatorTag;
import tools.dym.Tags.ArrayRead;
import tools.dym.Tags.ArrayWrite;
import tools.dym.Tags.BasicPrimitiveOperation;
import tools.dym.Tags.CachedClosureInvoke;
import tools.dym.Tags.CachedVirtualInvoke;
import tools.dym.Tags.ClassRead;
import tools.dym.Tags.ComplexPrimitiveOperation;
import tools.dym.Tags.ControlFlowCondition;
import tools.dym.Tags.FieldRead;
import tools.dym.Tags.FieldWrite;
import tools.dym.Tags.LocalArgRead;
import tools.dym.Tags.LocalVarRead;
import tools.dym.Tags.LocalVarWrite;
import tools.dym.Tags.LoopBody;
import tools.dym.Tags.LoopNode;
import tools.dym.Tags.NewArray;
import tools.dym.Tags.NewObject;
import tools.dym.Tags.OpArithmetic;
import tools.dym.Tags.OpClosureApplication;
import tools.dym.Tags.OpComparison;
import tools.dym.Tags.OpLength;
import tools.dym.Tags.PrimitiveArgument;
import tools.dym.Tags.StringAccess;
import tools.dym.Tags.UnspecifiedInvoke;
import tools.dym.Tags.VirtualInvoke;
import tools.dym.Tags.VirtualInvokeReceiver;

@TruffleLanguage.Registration(name = SomLanguage.LANG_NAME, id = SomLanguage.LANG_NAME, version = "0.1.0", characterMimeTypes = SomLanguage.MIME_TYPE)
 @ProvidedTags({RootTag.class, StatementTag.class, CallTag.class,
  KeywordTag.class, LiteralTag.class,
  CommentTag.class, IdentifierTag.class, ArgumentTag.class,
  LocalVariableTag.class, StatementSeparatorTag.class,
  DelimiterOpeningTag.class, DelimiterClosingTag.class,

  UnspecifiedInvoke.class, CachedVirtualInvoke.class,
  CachedClosureInvoke.class, VirtualInvoke.class,
  VirtualInvokeReceiver.class, NewObject.class, NewArray.class,
  ControlFlowCondition.class, FieldRead.class, FieldWrite.class, ClassRead.class,
  LocalVarRead.class, LocalVarWrite.class, LocalArgRead.class, ArrayRead.class,
  ArrayWrite.class, LoopNode.class, LoopBody.class, BasicPrimitiveOperation.class,
  ComplexPrimitiveOperation.class, PrimitiveArgument.class,
  StringAccess.class, OpClosureApplication.class, OpArithmetic.class,
  OpComparison.class, OpLength.class
})
public class SomLanguage extends TruffleLanguage<Universe> {

  public static final String LANG_NAME = "SomST";
  public static final String MIME_TYPE = "application/x-mate-som";
  public static final String CMD_ARGS  = "command-line-arguments";
  public static final String FILE_EXTENSION = "som";
  public static final String DOT_FILE_EXTENSION = "." + FILE_EXTENSION;

  public SomLanguage() {
    super();
  }

  public static final Source START = getSyntheticSource("", "START");

  public static Source getSyntheticSource(final String text, final String name) {
    return Source.newBuilder(text).internal().name(name).mimeType(SomLanguage.MIME_TYPE).build();
  }

  private static final class ParseResult extends RootNode {
    private final DynamicObject klass;

    ParseResult(final SomLanguage language, final DynamicObject klassArg) {
      super(language);
      this.klass = klassArg;
    }

    @Override
    public DynamicObject execute(final VirtualFrame frame) {
      return klass;
    }
  }

  @Override
  protected Universe createContext(final Env env) {
    try {
      return new Universe(env);
    } catch (IOException e) {
      throw new RuntimeException("Failed accessing kernel or platform code of SOMns.", e);
    }
  }

  @Override
  protected void initializeContext(final Universe context) throws Exception {
    context.initialize(this);
  }


  private static class StartInterpretation extends RootNode {

    protected StartInterpretation(final SomLanguage language) {
      super(language);
    }

    @Override
    public Object execute(final VirtualFrame frame) {
      return this.getLanguage(SomLanguage.class).getContextReference().get().execute();
    }
  }

  private CallTarget createStartCallTarget() {
    return Truffle.getRuntime().createCallTarget(new StartInterpretation(this));
  }

  @Override
  protected CallTarget parse(final ParsingRequest request) throws IOException {
    Source code = request.getSource();
    if (code == START || (code.getLength() == 0 && code.getName().equals("START"))) {
      return createStartCallTarget();
    }

    Universe vm = this.getContextReference().get();
    DynamicObject klass = vm.loadClass(code);
    ParseResult result = new ParseResult(this, klass);
    return Truffle.getRuntime().createCallTarget(result);
  }

  @Override
  protected Object findExportedSymbol(final Universe context,
      final String globalName, final boolean onlyExplicit) {
    return context.getExport(globalName);
  }

  @Override
  protected Object getLanguageGlobal(final Universe context) {
    return null;
  }

  @Override
  protected boolean isObjectOfLanguage(final Object object) {
    throw new NotYetImplementedException();
  }
}
