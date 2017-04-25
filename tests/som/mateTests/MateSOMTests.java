package som.mateTests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import som.tests.SomTests;

public class MateSOMTests extends SomTests {

  public MateSOMTests(String testName) {
    super(testName);
  }

  @Parameters
  public static Iterable<Object[]> data() {
    List<Object[]> somTests = ((List<Object[]>) SomTests.data());
    List<Object[]> mateTests = new ArrayList<Object[]>();
    mateTests.addAll(somTests);
    mateTests.add(new String[]{"Files"});
    mateTests.add(new String[]{"BasicOperations"});
    return mateTests;
  }

  @Override
  protected String[] getArguments() {
    String[] args = {
        "--mate",
        "-activateMate",
        "TestHarness",
        testName};
    return args;
  }
  
  @Override
  protected List<URL> getCP() throws MalformedURLException {
    List<URL> urls = super.getCP();
    urls.addAll(Arrays.asList(
        new File("TestSuite").toURI().toURL(),
        new File("Smalltalk/Mate").toURI().toURL(),
        new File("Smalltalk/Mate/MOP").toURI().toURL(),
        new File("Smalltalk/Mate/Compiler").toURI().toURL(),
        new File("Smalltalk/Collections/Streams").toURI().toURL(),
        new File("Smalltalk/FileSystem/Core").toURI().toURL(),
        new File("Smalltalk/FileSystem/Disk").toURI().toURL(),
        new File("Smalltalk/FileSystem/Directories").toURI().toURL(),
        new File("Smalltalk/FileSystem/Streams").toURI().toURL(),
        new File("TestSuite/FileSystem").toURI().toURL(),
        new File("TestSuite/Mate").toURI().toURL()
        ));
    return urls;
  }
}
