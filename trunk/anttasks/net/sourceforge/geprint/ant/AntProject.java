/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.geprint.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author nigjo
 */
class AntProject
{

  private Properties data;
  private File location;

  public AntProject(File location)
  {
    this.location = location;
  }

  protected void setLocation(File location)
  {
    this.location = location;
  }

  public Properties getData()
  {
    return data;
  }

  public String getProperty(String key)
  {
    if (data == null)
      return null;
    String value = data.getProperty(key);
    if (value == null)
      return value;
    final Pattern regex = Pattern.compile("(.*)\\$\\{(.*)\\}(.*)");
    Matcher matcher = regex.matcher(value);
    while (matcher.matches())
    {
      int anz = matcher.groupCount();
      value = matcher.group(1);
      value += getProperty(matcher.group(2));
      value += matcher.group(3);
      matcher.reset(value);
    }
    if (value.contains("${"))
      throw new BuildException(value);
    return value;
  }

  public File getLocation()
  {
    return location;
  }

  protected void addProperties(Properties data)
  {
    if (this.data == null)
      this.data = new AntProperties();
    this.data.putAll(data);
  }

  public Properties loadProperties(String name)
  {
    File modprops = new File(location, name);
    if (data == null)
      data = new AntProperties();
    try
    {
      FileInputStream in = new FileInputStream(modprops);
      try
      {
        data.load(in);
      }
      finally
      {
        in.close();
      }
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
    return data;
  }

  private static class AntProperties extends Properties
  {

    @Override
    public synchronized Object put(Object key, Object value)
    {
      if (containsKey(key))
        return get(key);
      return super.put(key, value);
    }
  }
}
