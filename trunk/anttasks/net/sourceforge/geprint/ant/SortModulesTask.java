/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.geprint.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;

/**
 *
 * @author nigjo
 */
public class SortModulesTask extends Task
{

  private String xclasspath;
  private File suitedir;
  private String modulesList;
  private Path modulesPaths;
  private String xbuildpath;
  //
  Properties suitedata;
  Hashtable<String, Dep> modules;

  public void setModules(String list)
  {
    this.modulesList = list;
  }

  private void updateModulesList()
  {
    if (modulesList == null)
      return;
    this.modules = new Hashtable<String, Dep>();
    if (modulesList.length() == 0)
      return;

    for (String module : modulesList.split(":"))
    {
      if (this.modules.containsKey(module))
      {
        continue;
      }
      this.modules.put(module, new Dep(module));
    }

  }

  public void setClassPath(String propvalue)
  {
    this.xclasspath = propvalue;
  }

  public void setBuildPath(String propvalue)
  {
    this.xbuildpath = propvalue;
  }

  public void setSuite(File suitedir)
  {
    this.suitedir = suitedir;
    suitedata = loadProperties("suite.properties", suitedir, null);
  }

  private static Properties loadProperties(String name, File location, Properties defaults)
  {
    File modprops = new File(location, name);
    Properties data = new Properties(defaults);
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

  public void setModuleRef(Reference ref)
  {
    this.modulesPaths = (Path) ref.getReferencedObject();
  }

  void updateModulesPath()
  {
    if (modulesPaths == null)
      return;
    modules = new Hashtable<String, Dep>();
    File basedir = getProject().getBaseDir();

    Iterator iterator = modulesPaths.iterator();
    while (iterator.hasNext())
    {
      Object next = iterator.next();
      Resource res = (Resource) next;

      File resname = new File(basedir, res.getName());
      File moduledir = resname.getParentFile().getAbsoluteFile();

      Dep dep = new Dep(moduledir);
      //System.out.println("next=" + dep.name);

      modules.put(dep.name, dep);
    }
  }

  @Override
  public void execute() throws BuildException
  {
    String resultProperty;
    if (xclasspath != null)
      resultProperty = xclasspath;
    else if (xbuildpath != null)
      resultProperty = xbuildpath;
    else
      throw new BuildException("no resulting property defined");

    updateModulesPath();
    updateModulesList();
    if (modules == null)
    {
      throw new BuildException("no modules defined");
    }
    else if (modules.size() == 0)
    {
//      System.out.println("no dependency");
      getProject().setProperty(resultProperty, "");
      return;
    }
    for (String key : modules.keySet())
    {
      Dep dep = modules.get(key);
      dep.resolve(modules);
    }
//    System.out.println("liste=" + modules.toString());
    Vector<Dep> deps = new Vector<Dep>(modules.values());
    Collections.sort(deps, new Comparator<Dep>()
    {

      public int compare(Dep o1, Dep o2)
      {
        if (o1 == o2)
        {
          return 0;
        }
        if (o1.dependsOn(o2))
        {
          return 1;
        }
        else
        {
          return -1;
        }
      }
    });
//    System.out.println("deps=" + deps.toString());
    if (deps.size() == 0)
    {
      getProject().setProperty(resultProperty, "");
    }
    else
    {
//      System.out.println("building deps");
      StringBuilder builder = null;
      if (xclasspath != null)
      {
        for (Dep dep : deps)
        {
          String classes = dep.getClasses();
          if (classes == null)
            continue;
          if (builder == null)
            builder = new StringBuilder();
          else
            builder.append(System.getProperty("path.separator"));
          builder.append(new File(dep.location, classes).toString());
        }
      }
      else
      {
        for (Dep dep : deps)
        {
          if (builder == null)
            builder = new StringBuilder();
          else
            builder.append(System.getProperty("path.separator"));

          File buildxml = new File(dep.location, "build.xml");
          builder.append(buildxml.toString());
        }
      }
      getProject().setProperty(resultProperty, builder.toString());
    }
  }

  private class Dep
  {

    private File location;
    private String name;
    private Vector<Dep> deps;
    private Properties data;
    Project subprj;

    public Dep(File location)
    {
      if (location.isAbsolute())
        this.location = location;
      else
        this.location = new File(suitedir, location.toString());
      updateData();
      this.name = getProperty("module.title");
      if (name == null)
        name = location.getName();
//      System.err.println("DBG: new Dependency '" + name + "' in " + this.location.toString());
    }

    public Dep(String name)
    {
      this.name = name;
      String dir =
          getProject().getProperty("module." + name + ".dir");
      if (dir == null)
      {
        throw new BuildException("unknown module '" + name + "'");
      }
      location = new File(dir);
      if (!location.isAbsolute())
        location = new File(suitedir, dir);
      updateData();
    }

    private boolean dependsOn(Dep dep)
    {
      if (!isResolved())
        throw new IllegalStateException();

      return deps.contains(dep);
    }

    private String getClasses()
    {
      String classesdir = getProperty("build.classes.dir");
      if (classesdir == null)
        throw new BuildException("no classes defined for dep " + name);
      return classesdir;
    }

    private String getProperty(String key)
    {
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

    private boolean isResolved()
    {
      return deps != null;
    }

    private void resolve(Hashtable<String, Dep> harvester)
    {
      if (isResolved())
        return;
      deps = new Vector<Dep>();
      String modulelist = getProperty("modules");
      if (modulelist == null)
        return;
      String[] modules = modulelist.split(":");
      for (String module : modules)
      {
        if (harvester.containsKey(module))
        {
          continue;
        }
        Dep dep = new Dep(module);
        harvester.put(module, dep);
        dep.resolve(harvester);
      }
    }

    private void updateData()
    {
      data = loadProperties("module.properties", location, suitedata);
    }
  }
}
