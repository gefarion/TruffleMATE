/**
 * Copyright (c) 2015 Guido Chari, gchari@dc.uba.ar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package som.mateTests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import som.tests.SomTests;

@RunWith(Parameterized.class)
public class MateTests extends SomTests {

  public MateTests(final String testName) {
    super(testName);
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"BasicOperations"},
        /* Immutability and Layout use heavility the environment in the shape for optimizations.
         * In case it is necessary we can provide alternatives for this functionality with this
         * setting too
         */
        // {"Immutability"},
        // {"Layout"},
        {"Compiler"},
      });
  }

  @Override
  protected String[] getArguments() {
    String[] arg = {
        "--mate",
        "-activateMate",
        "TestHarness",
        testName};
    return arg;
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
        new File("TestSuite/Mate").toURI().toURL(),
        new File("Examples/Benchmarks").toURI().toURL(),
        new File("Examples/Benchmarks/Mate/Immutability/Handles").toURI().toURL(),
        new File("Examples/Benchmarks/Mate/Immutability/DelegationProxies").toURI().toURL(),
        new File("Examples/Benchmarks/Mate/Layout").toURI().toURL()
        ));
    return urls;
  }
}
