package net.sourceforge.gedprint.gui.cmdline;

import java.text.MessageFormat;
import java.util.MissingResourceException;

import net.sourceforge.gedprint.gui.core.Bundle;

public enum CommandlineArgument
{
  _DEFAULT,
  indi('i'),
  family('f'),
  type,
  debug(false);

  // <editor-fold defaultstate="collapsed" desc="parsing">
  /**
   *
   * @param args
   */
  public static void parseCommandline(String[] args)
      throws CommandlineArgumentException
  {
    CommandlineArgument lastOption = null;
    for(String arg : args)
    {
      if(arg == null)
        continue;
      if(arg.charAt(0) == '-')
      {
        if(lastOption != null)
        {
          throw new CommandlineArgumentException("err.missingargument", arg);
        }
        if(arg.length() == 1)
        {
          throw new CommandlineArgumentException("err.invalidarg", arg);
        }
        CommandlineArgument option = null;
        if(arg.charAt(1) == '-')
        {
          try
          {
            // Langversion
            option = CommandlineArgument.valueOf(arg.substring(2));
          }
          catch(IllegalArgumentException e)
          {
            throw new CommandlineArgumentException("err.invalidarg", arg);
          }
        }
        else
        {
          // Kurzversion
          char optionChar = arg.charAt(1);
          for(CommandlineArgument commandlineArg :
              CommandlineArgument.values())
          {
            Character shortOption =
                commandlineArg.getShortOption();
            if(shortOption != null
                && shortOption == optionChar)
            {
              option = commandlineArg;
              break;
            }
          }
          if(option == null)
          {
            throw new CommandlineArgumentException("err.unknownoption", arg);
          }
        }

        //
        // OPTIONS
        //
        lastOption = null;
        if(!option.isExpectingArgument())
          option.setArgument(null);
        else
          lastOption = option;
      }
      else
      {
        if(lastOption == null)
        {
          //
          // Parameter ohne Option.
          //
          try
          {
            CommandlineArgument defArgument =
                CommandlineArgument.valueOf("_DEFAULT");
            if(defArgument.isDefined())
            {
              throw new CommandlineArgumentException("err.unknownargument", arg);
            }
            defArgument.setArgument(arg);
          }
          catch(IllegalArgumentException e)
          {
            throw new CommandlineArgumentException("err.unknownargument", arg);
          }
        }
        else
        {
          lastOption.setArgument(arg);
        }
        lastOption = null;
      }
    }
    if(lastOption != null)
    {
      throw new CommandlineArgumentException("err.missingargument",
          args[args.length - 1]);
    }

  }
  // </editor-fold>
  //<editor-fold defaultstate="collapsed" desc="implementation details">
  private Character shortSymbol;
  private String argument;
  private boolean expectingArgument;
  private boolean defined;

  private CommandlineArgument()
  {
    this(null, true);
  }

  private CommandlineArgument(Character shortSymbol)
  {
    this(shortSymbol, true);
  }

  private CommandlineArgument(boolean expectingArgument)
  {
    this(null, expectingArgument);
  }

  private CommandlineArgument(Character shortSymbol, boolean expectingArgument)
  {
    this.shortSymbol = shortSymbol;
    this.expectingArgument = expectingArgument;
  }

  public Character getShortOption()
  {
    return shortSymbol;
  }

  public boolean isExpectingArgument()
  {
    return expectingArgument;
  }

  public void setArgument(String argument)
  {
    this.argument = argument;
    this.defined = true;
  }

  public String getArgument()
  {
    return argument;
  }

  public boolean isDefined()
  {
    return defined;
  }
  //</editor-fold>

  public static class CommandlineArgumentException extends Exception
  {
    private static final long serialVersionUID = 1L;
    private final Object[] args;

    public CommandlineArgumentException(String pattern, Object... args)
    {
      super(pattern);
      this.args = args;
    }

    // <editor-fold defaultstate="collapsed" desc="implementation details">
    @Override
    public String getLocalizedMessage()
    {
      try
      {
        String key = getMessage();
        String pattern = Bundle.getString(key, CommandlineArgument.class);
        if(args != null && args.length > 0)
          return MessageFormat.format(pattern, args);
        else
          return pattern;
      }
      catch(MissingResourceException mre)
      {
        return super.getLocalizedMessage();
      }
    }// </editor-fold>
  }
}
