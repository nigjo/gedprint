package net.sourceforge.gedprint.gui.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.sourceforge.gedprint.gui.action.BasicAction;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class MenuGenerator
{
  private JMenuBar menubar;
  private URL resource;
  private static final String DEFAULT_XML_READER = "org.apache.crimson.parser.XMLReaderImpl"; //$NON-NLS-1$

  private MenuGenerator()
  {
    // nothing to do here
  }

  public static JMenuBar generate()
  {
    return generate(BasicAction.class.getResource("menu.xml")); //$NON-NLS-1$
  }

  public static JMenuBar generate(URL resource)
  {
    Logger.getLogger(MenuGenerator.class.getName()).fine(">>> generate"); //$NON-NLS-1$
    try
    {
      MenuGenerator generator = new MenuGenerator();
      generator.setResource(resource);
      return generator.getMenuBar();
    }
    finally
    {
      Logger.getLogger(MenuGenerator.class.getName()).fine("<<< generate"); //$NON-NLS-1$
    }
  }

  private JMenuBar getMenuBar()
  {
    if(menubar == null)
    {
      try
      {
        menubar = buildMenuBar(getResource());
      }
      catch(IOException e)
      {
        throw new IllegalStateException(e);
      }
    }
    return menubar;
  }

  private JMenuBar buildMenuBar(URL menures) throws IOException
  {
    XMLReader reader;
    try
    {
      reader = XMLReaderFactory.createXMLReader(DEFAULT_XML_READER);
    }
    catch(SAXException e)
    {
      try
      {
        // Fallback zum Standard Reader. Wird man wohl mit leben muessen.
        reader = XMLReaderFactory.createXMLReader();
      }
      catch(SAXException e1)
      {
        throw new IOException(e.toString());
      }
    }

    MenuContentHandler handler = new MenuContentHandler();
    reader.setContentHandler(handler);
    MenuEntityResolver resolver = new MenuEntityResolver();
    reader.setEntityResolver(resolver);

    InputStream is = menures.openStream();
    try
    {
      try
      {
        reader.parse(new InputSource(is));
      }
      catch(SAXException e)
      {
        throw new IOException(e.toString());
      }
    }
    finally
    {
      is.close();
    }

    return handler.getMenuBar();
  }

  private void setResource(URL resource)
  {
    this.resource = resource;
  }

  private URL getResource()
  {
    return resource;
  }

  private static class MenuContentHandler implements ContentHandler
  {
    private JMenuBar menubar;
    private JMenu current;
    private Locator locator;
    private String lastopen;

    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
      String text = new String(ch, start, length);
      if(text.trim().length() > 0)
        throw new SAXException(text);
    }

    public JMenuBar getMenuBar()
    {
      return menubar;
    }

    public void startDocument() throws SAXException
    {
      menubar = null;
    }

    public void endDocument() throws SAXException
    {
      if(menubar == null)
      {
        throw new SAXException(
            "missing oder wrong root element in line " + locator.getLineNumber()); //$NON-NLS-1$
      }
      // nothing else to be done here
    }

    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException
    {
      lastopen = qName;
      try
      {
        if("menubar".equals(qName)) //$NON-NLS-1$
        {
          if(menubar != null)
            throwSAXException("element 'menubar' already used"); //$NON-NLS-1$
          menubar = new JMenuBar();
        }
        else if("submenu".equals(qName)) //$NON-NLS-1$
        {
          if(menubar == null)
            throwSAXException("missing 'menubar' element"); //$NON-NLS-1$
          current = createSubMenu(atts.getValue("action")); //$NON-NLS-1$
        }
        else if("item".equals(qName)) //$NON-NLS-1$
        {
          if(current == null)
            throwSAXException("missing 'submenu' element"); //$NON-NLS-1$
          BasicAction action = getAction(atts.getValue("action")); //$NON-NLS-1$
          String name = (String)action.getValue(BasicAction.NAME);
          if(name == null || name.trim().length() == 0)
          {
            // Sicherstellen, dass ein Name angezeigt wird.
            action.putValue(BasicAction.NAME,
                '(' + action.getClass().getSimpleName() + ')');
          }
          current.add(action);
        }
        else if("separator".equals(qName)) //$NON-NLS-1$
        {
          current.addSeparator();
        }
        else
        {
          throwSAXException("unknown element '" + qName + '\''); //$NON-NLS-1$
        }
      }
      catch(IllegalStateException e)
      {
        // es ist etwas beim Erstellen des Menues schief gelaufen
        throwSAXException((Exception)e.getCause());
      }
    }

    private void throwSAXException(Exception e) throws SAXException
    {
      throw new SAXException(e.getMessage()
          + " in line " + locator.getLineNumber(), e); //$NON-NLS-1$
    }

    private void throwSAXException(String message) throws SAXException
    {
      throw new SAXException(message + " in line " + locator.getLineNumber()); //$NON-NLS-1$
    }

    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
      if("menubar".equals(qName)) //$NON-NLS-1$
      {
        // should be marked as done.
      }
      else if("submenu".equals(qName)) //$NON-NLS-1$
      {
        menubar.add(current);
        current = null;
      }
      else
      {
        if(!lastopen.equals(qName))
          throwSAXException("expected '" + lastopen + "' to be closed"); //$NON-NLS-1$//$NON-NLS-2$
      }
    }

    public void processingInstruction(String target, String data)
        throws SAXException
    {
      Logger.getLogger(getClass().getName()).fine("processingInstruction"); //$NON-NLS-1$
    }

    public void setDocumentLocator(Locator locator)
    {
      this.locator = locator;
    }

    public void skippedEntity(String name) throws SAXException
    {
      Logger.getLogger(getClass().getName()).fine("skippedEntity"); //$NON-NLS-1$
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
      // as the name says. ignore.
    }

    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
      // We don't care about this.
    }

    public void endPrefixMapping(String prefix) throws SAXException
    {
      // We don't care about this.
    }
  }

  private static class MenuEntityResolver implements EntityResolver
  {
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
      return new InputSource(new ByteArrayInputStream(new byte[0]));
    }
  }

  private static JMenu createSubMenu(String actionName)
  {
    Logger.getLogger(MenuGenerator.class.getName()).log(
        Level.FINE, "submenu {0}", actionName); //$NON-NLS-1$
    JMenu current = new JMenu();
    BasicAction action = getAction(actionName);
    current.setAction(action);
    return current;
  }

  private static BasicAction getAction(String actionName)
  {
    return ActionManager.getAction(actionName);
  }
}
