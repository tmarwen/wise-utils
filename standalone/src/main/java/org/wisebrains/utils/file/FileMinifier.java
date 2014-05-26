package org.wisebrains.utils.file;

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
 */
public class FileMinifier
{
  private String fileFromPath;
  private String fileToPath;
  private String searchKey;
  private int linesToCount;

  public FileMinifier(String fileFromPath, String fileToPath, String searchKey, int linesToCount)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileToPath;
    this.searchKey = searchKey;
    this.linesToCount = linesToCount;
  }

  public FileMinifier(String fileFromPath, String searchKey, int linesToCount)
  {
    this.fileFromPath = fileFromPath;
    this.fileToPath = fileFromPath + "-minified";
    this.searchKey = searchKey;
    this.linesToCount = linesToCount;
  }

  public static void main(String[] args)
  {
    FileMinifier fileMinifier;

    if (args.length >= 4)
    {
      fileMinifier = new FileMinifier(args[0], args[1], args[2], Integer.parseInt(args[3]));
    }
    else
    {
      fileMinifier = new FileMinifier(args[0], args[1], Integer.parseInt(args[2]));
    }
    fileMinifier.removeFromFile();
  }

  public void removeFromFile()
  {
    File sourceFile = new File(fileFromPath);
    File destinationFile = new File(fileToPath);
    BufferedReader br = null;
    BufferedWriter bw =  null;

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
        }
        else
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
    }
    finally
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
}
