/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.geprint.ant;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
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
  private String modulesList;
  private Path modulesPaths;
  private String xbuildpath;
  //
  private Suite suite;

  public void setModules(String list)
  {
    this.modulesList = list;
  }

  private void sort(Vector<Module> deps)
  {
    Collections.sort(deps, new Comparator<Module>()
    {

      public int compare(Module o1, Module o2)
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
  }

  private void updateModulesList()
  {
    if (modulesList == null)
      return;

    suite.initialize();
    if (modulesList.length() == 0)
      return;

    for (String module : modulesList.split(":"))
    {
      suite.addModule(module);
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
    this.suite = new Suite(suitedir);
  }

  public void setModuleRef(Reference ref)
  {
    this.modulesPaths = (Path) ref.getReferencedObject();
  }

  void updateModulesPath()
  {
    if (modulesPaths == null)
      return;
    suite.initialize();
    File basedir = getProject().getBaseDir();
    
    @SuppressWarnings("rawtypes")
    Iterator iterator = modulesPaths.iterator();
    while (iterator.hasNext())
    {
      Object next = iterator.next();
      Resource res = (Resource) next;

      File resname = new File(basedir, res.getName());
      File moduledir = resname.getParentFile().getAbsoluteFile();

      suite.addModule(moduledir);
    //Module dep = new Module(suite, moduledir);
    //System.out.println("next=" + dep.name);

    //modules.put(dep.getName(), dep);
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

    Hashtable<String, Module> modules = suite.getModules();
    if (modules == null)
    {
      throw new BuildException("no modules defined");
    }
    else if (modules.size() == 0)
    {
      getProject().setProperty(resultProperty, "");
      return;
    }
    suite.resolveModules();
    Vector<Module> deps = new Vector<Module>(modules.values());
    sort(deps);
    if (deps.size() == 0)
    {
      getProject().setProperty(resultProperty, "");
    }
    else
    {
      StringBuilder builder = null;
      if (xclasspath != null)
      {
        for (Module dep : deps)
        {
          String classes = dep.getProperty("build.classes.dir");
          if (classes == null)
            throw new BuildException("no classes defined for dep " + dep.getName());
          if (builder == null)
            builder = new StringBuilder();
          else
            builder.append(System.getProperty("path.separator"));
          builder.append(new File(dep.getLocation(), classes).toString());
        }
      }
      else
      {
        for (Module dep : deps)
        {
          if (builder == null)
            builder = new StringBuilder();
          else
            builder.append(System.getProperty("path.separator"));

          File buildxml = new File(dep.getLocation(), "build.xml");
          builder.append(buildxml.toString());
        }
      }
      getProject().setProperty(resultProperty, builder.toString());
    }
  }
}
