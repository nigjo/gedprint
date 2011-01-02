/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.gedprint.gui.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Basisklasse fuer eine Action, die einen Modalen Dialog anzeigt.
 *
 * @author nigjo
 */
public abstract class DialogAction<C extends JComponent> extends BasicAction
{
  @Override
  public void actionPerformed(ActionEvent e)
  {
    ActionDialog<C> dlg = new ActionDialog<C>(this);
    C content = dlg.getDialogContent();
    load(content);
    dlg.setVisible(true);
    if(dlg.isApproveAction())
      store(content);
  }

  public String getApproveButtonText()
  {
    return "OK";
  }

  public String getCancelButtonText()
  {
    return "Abbrechen";
  }

  /**
   * Liefert den Inhalt des Dialogs.
   *
   * @return Componente, die vom Dialog angezeigt werden soll.
   */
  abstract public C getContentPane();

  /**
   * Laed die Standardwerte fuer den Dialog. Die Methode wird aufgerufen,
   * bevor der Dialog angezeigt wird. Es koennen hier die Daten in die Elemente
   * des ContentPane gespeichert werden.
   *
   * @see #store()
   * @see #getContentPane()
   */
  abstract protected void load(C content);

  /**
   * Speichert die Werte der Dialogelemente in die Datenstruktur. Die Methode
   * wird aufgerufen, wenn der Benutzer den OK Button des Dialog betaetigt
   * hat. Wird der Dialog auf andere Weise geschlossen, wird diese Methode
   * nicht aufgerufen.
   *
   * @see #load()
   * @see #getContentPane()
   */
  abstract protected void store(C content);

  /**
   * Hilfsklasse fuer eine DialogAction.
   *
   * @author nigjo
   */
  private static class ActionDialog<C extends JComponent> extends JDialog
  {
    private Action selected;
    private Action approveAction;
    private C content;

    public ActionDialog(DialogAction<C> invoker)
    {
      super(findOwner(invoker));
      setTitle((String)invoker.getValue(DialogAction.NAME));
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setModal(true);

      JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      String text = invoker.getApproveButtonText();
      approveAction = new DialogButton(text);
      if(text != null)
        wrapper.add(new JButton(approveAction));
      text = invoker.getCancelButtonText();
      if(text != null)
        wrapper.add(new JButton(new DialogButton(text)));
      add(wrapper, BorderLayout.SOUTH);

      content = invoker.getContentPane();
      add(content, BorderLayout.CENTER);
    }

    public C getDialogContent()
    {
      return content;
    }

    private static Frame findOwner(DialogAction<?> action)
    {
      Frame[] frames = Frame.getFrames();
      if(frames.length == 0)
        return null;
      for(Frame frame : frames)
      {
        if(frame instanceof JFrame)
          return frame;
      }
      return frames[0];
    }

    @Override
    public void addNotify()
    {
      super.addNotify();
      pack();
      setLocationRelativeTo(getParent());
    }

    public boolean isApproveAction()
    {
      return selected == approveAction;
    }

    private class DialogButton extends AbstractAction
    {
      public DialogButton(String title)
      {
        super(title);
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
        selected = this;
        dispose();
      }
    }
  }
}
