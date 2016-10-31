package gr.roropoulos.opensubtool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple OpenSubTool.
 */
public class OpenSubToolTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OpenSubToolTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(OpenSubToolTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }
}
