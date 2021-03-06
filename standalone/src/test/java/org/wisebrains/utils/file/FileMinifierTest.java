package org.wisebrains.utils.file;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by eXo Platform MEA on 16/09/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class FileMinifierTest
{

  private static File file;
  private static File minified;

  @BeforeClass
  public static final void setUp()
  {
    String fileContent = "I'm looking for\n"
        + "a KEY that\n"
        + "may be endorsed within\n"
        + "another similar key";

    File tempDir = new File(System.getProperty("java.io.tmpdir"));

    file = new File(tempDir, "mock.log");

    /* Initialize file and deleteOnExit */
    try
    {
      FileUtils.writeStringToFile(file, fileContent, false);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static final void tearDown()
  {
    file.delete();
  }

  @After
  public final void tearMinifiedFile()
  {
    minified.delete();
  }

  @Test
  public void testExtractFileWithLinesCount()
  {

    String expectedContent = "a KEY that\n"
        + "another similar key\n";

    FileMinifier fileMinifier = new FileMinifier(file.getPath(), "KEY", 1);
    minified = fileMinifier.extractToFileWithLineCount();
    String finalContent = null;

    try
    {
      finalContent = FileUtils.readFileToString(minified);
    } catch (IOException e)
    {
      e.printStackTrace();
    }

    Assert.assertEquals("There should be only two lines containing the \"KEY\": ", expectedContent, finalContent);

  }

  @Test
  public void testExtractFileWithKeyStop()
  {

    String expectedContent = "a KEY that\n"
        + "may be endorsed within\n"
        + "another similar key\n";

    FileMinifier fileMinifier = new FileMinifier(file.getPath(), "KEY", "endorsed");
    minified = fileMinifier.extractToFileWithKeyStop();
    String finalContent = null;

    try
    {
      finalContent = FileUtils.readFileToString(minified);
    } catch (IOException e)
    {
      e.printStackTrace();
    }

    Assert.assertEquals("There should be only three lines containing the \"KEY\" and \"endorsed\" words: ", expectedContent, finalContent);

  }

  @Test
  public void testRemoveFromFileWithLinesCount()
  {

    String expectedContent = "I'm looking for\n"
        + "may be endorsed within\n";

    FileMinifier fileMinifier = new FileMinifier(file.getPath(), "KEY", 1);
    minified = fileMinifier.removeFromFileWithLineCount();
    String finalContent = null;

    try
    {
      finalContent = FileUtils.readFileToString(minified);
    } catch (IOException e)
    {
      e.printStackTrace();
    }

    Assert.assertEquals("There should be only two lines without the \"KEY\" word: ", expectedContent, finalContent);

  }

  @Test
  public void testRemoveFromFileWithKeyStop()
  {

    String expectedContent = "I'm looking for\n";

    FileMinifier fileMinifier = new FileMinifier(file.getPath(), "KEY", "endorsed");
    minified = fileMinifier.removeFromFileWithKeyStop();
    String finalContent = null;

    try
    {
      finalContent = FileUtils.readFileToString(minified);
    } catch (IOException e)
    {
      e.printStackTrace();
    }

    Assert.assertEquals("There should be only one line in final file: ", expectedContent, finalContent);

  }
}
