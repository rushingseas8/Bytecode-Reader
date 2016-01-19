import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Grapher {
    public static GraphPanel graphPanel;
    public static FunctionPanel functionPanel;
    public static GrapherMenu menu;

    public static Function[] functions;
    public static String[] functionNames;
    public static int currentFunction;

    public Grapher() {
        functions = new Function[10];
        functionNames = new String[functions.length];
        currentFunction = 0;
        
        //Puts the menu bar on the top if this is a mac.
        if (System.getProperty("os.name").contains("Mac")) 
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        //Initialise the main JFrame.
        JFrame frame = new JFrame("Drawing");
        frame.setSize(640, 480);

        //Set up the compiler.
        FunctionCompiler.init();

        //Set up the main drawing panel.
        graphPanel = new GraphPanel(this);
        frame.add(graphPanel);

        //Set up the main input panel.
        functionPanel = new FunctionPanel(this);
        frame.add(functionPanel, BorderLayout.SOUTH);

        //Add the toolbar.
        menu = new GrapherMenu(graphPanel);
        frame.setJMenuBar(menu);

        //We're live!
        frame.setVisible(true);

        //g.drawFunction(new Function() { @Override public double evaluate(double x) { return x * x; }});

        //This deletes all temporary files that the compiler generates.
        frame.addWindowListener(new WindowAdapter() { 
                public void windowClosing(WindowEvent w) {
                    FunctionCompiler.cleanup();
                }
            });

        //Option menu setup.
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, KeyEvent.META_DOWN_MASK), "options");
                
        Grapher g = this; //Used for the anonymous class.
        frame.getRootPane().getActionMap().put("options", 
            new AbstractAction() {
                public void actionPerformed(ActionEvent a) {
                    new OptionFrame(frame, g);
                }
            });

        //Other keybindings.
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
                KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK), "advance");
        frame.getRootPane().getActionMap().put("advance", new AbstractAction() {public void actionPerformed(ActionEvent a) {functionPanel.advance();}});

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
                KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK), "backstep");
        frame.getRootPane().getActionMap().put("backstep", new AbstractAction() {public void actionPerformed(ActionEvent a) {functionPanel.backstep();}});
    }

    public static void main(String[] args) {
        new Grapher();
    }
}