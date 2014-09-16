package org.wisebrains.utils.model;

/**
 * Created by eXo Platform MEA on 15/09/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class FileWithExtention
{
  private String filePath;

  public FileWithExtention(String pathname, String ramification)
  {
    int dotIndex = pathname.lastIndexOf('.');
    if (pathname.lastIndexOf('.') > -1)
    {
      String fileName = pathname.substring(0, dotIndex);
      String extension = pathname.substring(dotIndex, pathname.length());
      filePath = new StringBuilder(fileName)
          .append(ramification)
          .append(extension)
          .toString();
    }
    else
    {
      filePath = new StringBuilder(pathname)
          .append(ramification)
          .toString();
    }
  }

  public String getFilePath()
  {
    return filePath;
  }
}
