package net.sourceforge.geprint.ant;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

class Module extends AntProject
{

  private String name;
  private Vector<Module> deps;
  private Suite suite;

  public Module(Suite suite, File location)
  {
    super(location);
    this.suite = suite;
    loadProperties("module.properties");
    this.name = getProperty("module.title");
    if (name == null)
      name = location.getName();
    addProperties(suite.getData());
  }

  public Module(Suite suite, String name)
  {
    super(null);
    this.suite = suite;
    this.name = name;
    String dir = suite.getProperty("module." + name + ".dir");
    if (dir == null)
      throw new BuildException("unknown module \'" + name + "\'");
    File location = new File(dir);
    if (!location.isAbsolute())
      location = new File(suite.getLocation(), dir);
    setLocation(location);
    loadProperties("module.properties");
    addProperties(suite.getData());
  }

  public String getName()
  {
    return name;
  }

  public boolean dependsOn(Module dep)
  {
    if (!isResolved())
      throw new IllegalStateException();
    return deps.contains(dep);
  }

  private boolean isResolved()
  {
    return deps != null;
  }

  public void resolve()
  {
    if (isResolved())
      return;
    deps = new Vector<Module>();
    String modulelist = getProperty("modules");
    if (modulelist == null)
      return;
    String[] modules = modulelist.split(":");
    for (String module : modules)
    {
      if (suite.containsModule(module))
        continue;
      deps.add(suite.addModule(module));
    }
  }
}
