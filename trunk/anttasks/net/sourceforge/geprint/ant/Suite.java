/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.geprint.ant;

import java.io.File;
import java.util.Hashtable;

/**
 *
 * @author nigjo
 */
class Suite extends AntProject
{

  Hashtable<String, Module> modules;

  public Suite()
  {
    super(null);
  }

  public Suite(File suitedir)
  {
    super(suitedir);
  }

  @Override
  protected void setLocation(File location)
  {
    super.setLocation(location);
  }

  public boolean containsModule(String module)
  {
    if (modules == null)
      return false;
    return modules.containsKey(module);
  }

  public Hashtable<String, Module> getModules()
  {
    return modules;
  }

  public Module addModule(String moduleName)
  {
    if (containsModule(moduleName))
      return null;
    Module m = new Module(this, moduleName);
    add(m);
    return m;
  }

  public Module addModule(File moduledir)
  {
    Module m = new Module(this, moduledir);
    String name = m.getName();
    if (containsModule(name))
      return null;
    add(m);
    return m;
  }

  public void initialize()
  {
    loadProperties("suite.properties");
    modules = new Hashtable<String, Module>();
  }

  private void add(Module m)
  {
    if (modules == null)
      modules = new Hashtable<String, Module>();
    modules.put(m.getName(), m);
  }

  public void resolveModules()
  {
    for (String key : modules.keySet())
    {
      Module dep = modules.get(key);
      dep.resolve();
    }
  }
}
