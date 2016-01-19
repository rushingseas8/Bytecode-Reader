import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

public class FunctionPanel extends JPanel {
    private JPanel navigation;
    private JButton back, forward;

    private JTextArea text;
    private Grapher graph;

    private int curFunc;

    private Color background = Color.WHITE;
    private Color foreground = Color.BLACK;

    public FunctionPanel(Grapher g) {
        this.graph = g;
        this.background = graph.graphPanel.getBackground();
        this.foreground = graph.graphPanel.getForeground();

        setLayout(new BorderLayout());

        //Text box input

        text = new JTextArea();
        text.setBackground(background);
        text.setForeground(foreground);
        text.setCaretColor(foreground);
        text.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(foreground, 1), "public double evaluate(double x) {",
                TitledBorder.LEFT, TitledBorder.TOP, null, foreground)
        ); 

        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        text.getActionMap().put("enter", new AbstractAction() {
                public void actionPerformed(ActionEvent a){
                    graph.functions[graph.currentFunction] = FunctionCompiler.compile(text.getText());
                    graph.functionNames[graph.currentFunction] = FunctionCompiler.interpretedFunction;
                    graph.graphPanel.drawFunction(graph.functions[graph.currentFunction]);
                    //graph.setInterpretedFunctionText(FunctionCompiler.interpretedFunction);
                    graph.graphPanel.redraw(true);
                    graph.graphPanel.repaint();

                    //functions[curFunc] = latestFunction;

                }
            });

        add(text, BorderLayout.CENTER);

        //Navigation button to toggle between functions

        navigation = new JPanel();
        navigation.setLayout(new GridLayout(2, 1));

        back = new JButton("^");
        back.setEnabled(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setPreferredSize(new Dimension(16,16));
        back.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent a) {
                    backstep();
                }
            });

        forward = new JButton("v");
        forward.setEnabled(true);
        forward.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forward.setPreferredSize(new Dimension(16,16));
        forward.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent a) {
                    advance();
                }
            });

        navigation.add(back);
        navigation.add(forward);

        add(navigation, BorderLayout.EAST);
    }

    public void advance() {
        graph.currentFunction++;

        if(graph.currentFunction == graph.functions.length - 1) 
            forward.setEnabled(false);

        if(graph.currentFunction > 0)
            back.setEnabled(true);

        graph.graphPanel.drawFunction(graph.functions[graph.currentFunction]);
        text.setText(graph.functionNames[graph.currentFunction] == null ? "" : graph.functionNames[graph.currentFunction]);
        //graph.setInterpretedFunctionText(functionNames[curFunc] == null ? "" : functionNames[curFunc]);
    }

    public void backstep() {
        graph.currentFunction--;

        if(graph.currentFunction == 0) 
            back.setEnabled(false);

        if(graph.currentFunction < graph.functions.length)
            forward.setEnabled(true);

        graph.graphPanel.drawFunction(graph.functions[graph.currentFunction]);
        text.setText(graph.functionNames[graph.currentFunction] == null ? "" : graph.functionNames[graph.currentFunction]);
    }

    public void foreground(Color to) {
        this.foreground = to;
        text.setForeground(foreground);
        text.setCaretColor(foreground);
        text.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(foreground, 1), "public double evaluate(double x) {",
                TitledBorder.LEFT, TitledBorder.TOP, null, foreground)
        ); 
        text.repaint();
    }

    public void background(Color to) {
        this.background = to;
        text.setBackground(background);
        text.repaint();
    }

    public static void main() {
        JFrame frame = new JFrame("Test");
        frame.add(new FunctionPanel(null));
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
}