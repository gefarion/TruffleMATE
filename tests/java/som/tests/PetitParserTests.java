package som.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import som.mateTests.MateTests;

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
  protected List<URL> getCP() throws MalformedURLException {
    List<URL> urls = super.getCP();
    urls.addAll(Arrays.asList(
        new File("TestSuite").toURI().toURL(),
        new File("Smalltalk/Mate").toURI().toURL(),
        new File("Smalltalk/Mate/MOP").toURI().toURL(),
        new File("Smalltalk/Collections/Streams").toURI().toURL(),
        new File("TestSuite/PetitParser").toURI().toURL(),
        new File("TestSuite/PetitParser/PetitSmalltalk").toURI().toURL(),
        new File("Smalltalk/PetitParser").toURI().toURL(),
        new File("Smalltalk/PetitParser/PetitSmalltalk").toURI().toURL(),
        new File("Smalltalk/AST-Core").toURI().toURL(),
        new File("Smalltalk/AST-Core/Parser").toURI().toURL()
        ));
    return urls;
  }
}
