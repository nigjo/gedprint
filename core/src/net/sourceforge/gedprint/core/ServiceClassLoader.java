package net.sourceforge.gedprint.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Klassenlader fuer
 *
 * @todo Hier fehlt die Beschreibung der Klasse.
 *
 * @author Hofschr&ouml;er, Jens
 */
class ServiceClassLoader extends URLClassLoader
{
  public static final String CLASS_EXT = ".class";

  public ServiceClassLoader(ClassLoader parent)
  {
    super(getServiceJars(), parent);
  }

  private static URL[] getServiceJars()
  {
    File appdir = findAppDir(ServiceClassLoader.class);
    if(appdir == null)
      return new URL[0];
    List<URL> harvester = new ArrayList<URL>();
    harvester.addAll(findServiceJars(new File(appdir, "lib")));
    harvester.addAll(findServiceJars(new File(appdir, "services")));
    harvester.addAll(findServiceJars(new File(System.getProperty("user.dir"))));

    return harvester.toArray(new URL[harvester.size()]);
  }

  private static File findAppDir(Class<?> aClass)
  {
    String simpleName = aClass.getSimpleName();
    URL clazzLocation = aClass.getResource(simpleName + CLASS_EXT);
    String proto = clazzLocation.getProtocol();
    if(proto.equals("file"))
    {
      // das file protokoll bedeutet normalerweise, dass sich die Anwendung im
      // Debug-Modus befindet. Hier werden keine weiteren Jars gesucht, da sich
      // die services bereits im Klassenpfad befinden sollten.
      return null;
    }

    File appdir = getJarDir(aClass, clazzLocation);
    // Normalerweise ist die Jar-Datei im Unterverzeichnis "lib" zu finden.
    if(appdir.getName().equals("lib"))
      appdir = appdir.getParentFile();

    return appdir;
  }

  private static File getJarDir(Class<?> aClass, URL clazzLocation)
  {
    //File file = new File(clazzLocation.toURI());
    String path = clazzLocation.getPath();
    String parentpath = path.substring(0, path.length() - (aClass.getName().
        length() + CLASS_EXT.length()));
    // sollte parentpath nicht auf dem "Trennzeichen" enden, ist irgendetwas
    // an dieser Stelle falsch. Lieber Protokollieren und abbrechen.
    if(!parentpath.endsWith("!/"))
    {
      Logger.getLogger(ServiceClassLoader.class.getName()).log(
          Level.WARNING, "invalid jar locator: {0}", clazzLocation.toString());
      return null;
    }
    try
    {
      URL jarpath = new URL(parentpath.substring(0, parentpath.length() - 2));
      if(!"file".equals(jarpath.getProtocol()))
      {
        Logger.getLogger(ServiceClassLoader.class.getName()).log(
            Level.WARNING, "not a local jar file: {0}", jarpath.toString());
        return null;
      }
      File jar = new File(jarpath.toURI());
      return jar.getParentFile();
    }
    catch(URISyntaxException ex)
    {
      // sollte an dieser Stelle eigentlich nicht mehr auftreten
      Logger.getLogger(ServiceClassLoader.class.getName()).
          log(Level.SEVERE, null, ex);
    }
    catch(MalformedURLException ex)
    {
      // sollte an dieser Stelle eigentlich nicht mehr auftreten
      Logger.getLogger(ServiceClassLoader.class.getName()).
          log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static List<URL> findServiceJars(File servicedir)
  {
    if(!servicedir.exists())
      return Collections.emptyList();
    File[] serviceFiles = servicedir.listFiles(new JarFilter());

    List<URL> services = new ArrayList<URL>();
    for(File servicefile : serviceFiles)
    {
      try
      {
        URL serviceLocator = servicefile.toURI().toURL();
        services.add(serviceLocator);
        Logger.getLogger(ServiceClassLoader.class.getName()).log(
            Level.CONFIG, "added {0}", servicefile.getName());
      }
      catch(MalformedURLException ex)
      {
        Logger.getLogger(ServiceClassLoader.class.getName()).log(Level.WARNING,
            servicefile.toString(), ex);
      }
    }
    return services;
  }

  private static class JarFilter implements FileFilter
  {
    @Override
    public boolean accept(File pathname)
    {
      try
      {
        if(!pathname.isFile())
          return false;
        ZipFile zip = new ZipFile(pathname, ZipFile.OPEN_READ);
        try
        {
          ZipEntry entry = zip.getEntry("META-INF/services/");
          if(entry == null)
            return false;
          boolean isdir = entry.isDirectory();
          return isdir;
        }
        finally
        {
          zip.close();
        }
      }
      catch(IOException ex)
      {
        return false;
      }

    }
  }
}
