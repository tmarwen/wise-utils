package org.wisebrains.utils.file;

import org.apache.commons.io.FileUtils;
import org.wisebrains.utils.operation.OperationType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by eXo Platform MEA on 26/05/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 *
 * This utility artifact can be used either to shrink a file by removing repeatable lines or to
 * extract some lines to another file.
 * The counter is launched once the search key is found. It limits to either a stop token if specified
 * Or when reaching some range of lines when the flag "withLinesCount" is specified.
 *
 * Use below commands
 *
 */
public class FileMinifier
{
  private String fileFromPath;
  private String fileToPath;
  private String searchKey;
  private int linesToCount;
  private String stopKey;
  private static final String REMOVE = "remove";
  private static final String EXTRACT = "extract";

  public FileMinifier(String fileFromPath, String fileToPath, String searchKey, int linesToCount)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileToPath;
    this.searchKey = searchKey;
    this.linesToCount = linesToCount;
  }

  public FileMinifier(String fileFromPath, String fileToPath, String searchKey, String stopKey)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileToPath;
    this.searchKey = searchKey;
    this.stopKey = stopKey;
  }


  public FileMinifier(String fileFromPath, String searchKey, int linesToCount)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileFromPath + "-minified";
    this.searchKey = searchKey;
    this.linesToCount = linesToCount;
  }

  public FileMinifier(String fileFromPath, String searchKey, String stopKey)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileFromPath + "-minified";
    this.searchKey = searchKey;
    this.stopKey = stopKey;
  }

  public static void main(String[] args)
  {
    args = new String[5];
    args[0] = "remove";
    args[1] = "withtoutLinesCount";
    args[2] = "/home/exo/Work/working/customers-issues/SEE/SEE-20/tdump/dump";
    args[3] = "by \"TP-Processor39\"";
    args[4] = "at java.lang.Thread.run";

    FileMinifier fileMinifier;
    OperationType operation = OperationType.valueOf(args[0].toUpperCase());

    switch (operation)
    {
      case REMOVE:
      {
        if (args.length > 5)
        {
          if (args[1].equals("withLinesCount"))
          {
            fileMinifier = new FileMinifier(args[2], args[3], args[4], Integer.parseInt(args[5]));
            fileMinifier.removeFromFileWithLineCount();
          } else
          {
            fileMinifier = new FileMinifier(args[2], args[3], args[4], args[5]);
            fileMinifier.removeFromFileWithKeyStop();
          }
        } else
        {
          if (args[1].equals("withLinesCount"))
          {
            fileMinifier = new FileMinifier(args[2], args[3], Integer.parseInt(args[4]));
            fileMinifier.removeFromFileWithLineCount();
          } else
          {
            fileMinifier = new FileMinifier(args[2], args[3], args[4]);
            fileMinifier.removeFromFileWithKeyStop();
          }
        }
      }
      case EXTRACT:
      {
        if (args.length > 5)
        {
          if (args[1].equals("withLinesCount"))
          {
            fileMinifier = new FileMinifier(args[2], args[3], args[4], Integer.parseInt(args[5]));
            fileMinifier.extractToFileWithLineCount();
          } else
          {
            fileMinifier = new FileMinifier(args[2], args[3], args[4], args[5]);
            fileMinifier.removeFromFileWithKeyStop();
          }
        } else
        {
          if (args[1].equals("withLinesCount"))
          {
            fileMinifier = new FileMinifier(args[2], args[3].concat("-EXTRACTED"), args[3], Integer.parseInt(args[4]));
            fileMinifier.removeFromFileWithLineCount();
          } else
          {
            fileMinifier = new FileMinifier(args[2], args[2].concat("-EXTRACTED"), args[3], args[4]);
            fileMinifier.extractToFileWithKeyStop();
          }
        }
      }
    }
  }

  public void removeFromFileWithLineCount()
  {
    File sourceFile = new File(fileFromPath);
    File destinationFile = new File(fileToPath);
    BufferedReader br = null;
    BufferedWriter bw = null;

    try
    {
      br = new BufferedReader(new FileReader(sourceFile));
      bw = new BufferedWriter(new FileWriter(destinationFile));
      String currentLine;
      while ((currentLine = br.readLine()) != null)
      {
        if (currentLine.contains(searchKey))
        {
          for (int i = 0; i < linesToCount; i++)
          {
            br.readLine();
          }
        } else
        {
          bw.write(currentLine.concat("\n"));
        }
      }
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        br.close();
        bw.close();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  public void extractToFileWithLineCount()
  {
    File sourceFile = new File(fileFromPath);
    File destinationFile = new File(fileToPath);
    BufferedReader br = null;

    try
    {
      br = new BufferedReader(new FileReader(sourceFile));
      String currentLine;
      while ((currentLine = br.readLine()) != null)
      {
        if (currentLine.contains(searchKey))
        {
          for (int i = 0; i < linesToCount; i++)
          {
            FileUtils.writeStringToFile(destinationFile, currentLine.concat("\n"), "UTF-8", true);
            currentLine = br.readLine();
          }
        }
      }
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        br.close();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  public void removeFromFileWithKeyStop()
  {
    File sourceFile = new File(fileFromPath);
    File destinationFile = new File(fileToPath);
    BufferedReader br = null;
    BufferedWriter bw = null;

    try
    {
      br = new BufferedReader(new FileReader(sourceFile));
      bw = new BufferedWriter(new FileWriter(destinationFile));
      String currentLine;
      while ((currentLine = br.readLine()) != null)
      {
        if (currentLine.contains(searchKey))
        {
          do
          {
            currentLine = br.readLine();
          }
          while (!(currentLine.contains(stopKey)) && (currentLine != null));
        } else
        {
          bw.write(currentLine.concat("\n"));
        }
      }
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        br.close();
        bw.close();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  public void extractToFileWithKeyStop()
  {
    File sourceFile = new File(fileFromPath);
    File destinationFile = new File(fileToPath);
    BufferedReader br = null;
    boolean quitIteration;

    try
    {
      br = new BufferedReader(new FileReader(sourceFile));
      String currentLine;
      while ((currentLine = br.readLine()) != null)
      {
        if ((currentLine.contains(searchKey)) && (currentLine != null))
        {
          quitIteration = false;
          do
          {
            FileUtils.writeStringToFile(destinationFile, currentLine.concat("\n"), "UTF-8", true);
            currentLine = br.readLine();
            if (currentLine.contains(stopKey) && (currentLine != null))
            {
              FileUtils.writeStringToFile(destinationFile, currentLine.concat("\n"), "UTF-8", true);
              quitIteration = true;
            }
          }
          while (!quitIteration);
        }
      }
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    } finally
    {
      try
      {
        br.close();
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
