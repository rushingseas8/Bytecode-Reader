import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OptionFrame extends JFrame {
    private static GraphPanel graph;
    private static FunctionPanel func;
    public static Color[] colorSettings = new Color[11];

    public OptionFrame(JFrame parent, Grapher g) {
        this.graph = g.graphPanel;
        this.func = g.functionPanel;
        colorSettings[0] = graph.getBackground();
        colorSettings[1] = graph.getForeground();

        JTabbedPane pane = new JTabbedPane();

        JPanel color = new JPanel();

        color.setLayout(new GridLayout(11, 2));

        for(int i = 0; i < 11; i++) {
            if(i == 0) color.add(new JLabel("Background"));
            else {
                color.add(new JLabel("Function " + i));
            }
            ColorComboBox c =  new ColorComboBox(i);
            c.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        colorSettings[c.id] = ColorComboBox.colors[c.getSelectedIndex()];
                        pushUpdate(c.id);
                    }
                }
            );
            color.add(c);
        }

        pane.add("Color", color);

        add(pane);
        setSize(320, 240);
        setVisible(true);
        setLocationRelativeTo(parent);
    }

    private void pushUpdate(int i) {
        if(graph == null) return;

        if(i == 0) { 
            graph.background(colorSettings[0]);
            graph.redraw(true);
            graph.repaint();
            
            func.background(colorSettings[0]);
            func.repaint();
            return;
        }
        if(i == 1) {
            graph.setForeground(colorSettings[1]);
            func.foreground(colorSettings[1]);
        }
        graph.functionColorOrder[i-1] = colorSettings[i];

        graph.redraw(true);
        graph.repaint();

        func.foreground(colorSettings[1]);
        func.repaint();
    }

    public static void main() {
        new OptionFrame(null, null);
    }

    private static class ColorComboBox extends JComboBox<String> {
        public static String[] names = new String[]
            {"White", "Black", "Dark Gray", "Gray", "Light Gray", "Red", "Orange", "Yellow",
                "Green", "Emerald", "Cyan", "Blue", "Dark Blue", "Purple", "Pink", "Brown"};

        public static Color[] colors = new Color[]
            {Color.WHITE, Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.RED,
                Color.ORANGE, Color.YELLOW, Color.GREEN, Color.GREEN.darker(), Color.CYAN, Color.BLUE,
                new Color(0,0,128), new Color(64,0,128), Color.MAGENTA, new Color(128,64,0)};

        public int id;

        public ColorComboBox(int id) {
            super(names);
            this.id = id;
            if(id == 0) setSelectedIndex(indexOf(graph.getBackground()));
            else try {setSelectedIndex(indexOf(graph.functionColorOrder[id-1]));} catch(Exception e) {}
        }

        private int indexOf(Color c) {
            for(int i = 0; i < colors.length; i++) {
                Color co = colors[i];
                if(co.equals(c))
                    return i;
            }
            return -1;
        }
    }
}