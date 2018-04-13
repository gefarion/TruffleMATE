package mate.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import som.tests.SomTests;

public class MateSOMTests extends SomTests {

  public MateSOMTests(final String testName) {
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
}
