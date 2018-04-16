package petitparser.tests;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import mate.tests.MateTests;


@RunWith(Parameterized.class)
public class PetitParserTests extends MateTests {

  public PetitParserTests(final String testName) {
    super(testName);
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"PPArithmeticParserTest"  },
        {"PPConditionalParserTest" },
        {"PPContextMementoTest"    },
        {"PPContextTest"           },
        {"PPExpressionParserTest"  },
        {"PPExtensionTest"         },
        {"PPLambdaParserTest"      },
        {"PPMappingTest"           },
        {"PPObjectTest"            },
        {"PPParserTest"            },
        {"PPPredicateTest"         },
        {"PPScriptingTest"         },
        {"PPTokenTest"             },
        {"PPSmalltalkClassesTest"  },
        {"PPSmalltalkGrammarTest"  },
        {"PPSmalltalkParserTest"   }
      });
  }

  @Override
  protected String[] getArguments() {
    String[] arg = {
        "--mate",
        "-activateMate",
        "-cp",
        "Smalltalk:Smalltalk/Mate:Smalltalk/Mate/MOP:Smalltalk/Mate/Compiler:Smalltalk/Collections/Streams:" +
        "Smalltalk/PetitParser:Smalltalk/PetitParser/PetitSmalltalk:Smalltalk/AST-Core:Smalltalk/AST-Core/Parser:" +
        "Smalltalk/Exceptions:" +
        "TestSuite:TestSuite/PetitParser:TestSuite/PetitParser/PetitSmalltalk:" +
        " ",
        "TestHarness",
        testName};
    return arg;
  }
}
