import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GrapherMenu extends JMenuBar {
    private GraphPanel graph;
    
    public JMenu add;
    public JMenuItem addPoint, addLine;
    
    public JMenu calculus;
    public JMenuItem calcValue, calcLimit, calcDeriv, calcInteg, calcMin, calcMax, calcInfl;
    
    public JMenu mode;
    public ButtonGroup degreeMode;
    public JRadioButtonMenuItem modeDeg, modeRad;
    public ButtonGroup graphMode;
    public JRadioButtonMenuItem modeFunc, modePara, modePol;
    public ButtonGroup accurMode;
    public JRadioButtonMenuItem modeLow, modeMed, modeHigh;

    public GrapherMenu(GraphPanel g) {
        this.graph = g;
        
        //Instantiation
        add = new JMenu("Add");
        addPoint = new JMenuItem("Point");
        addLine = new JMenuItem("Line");
        
        calculus = new JMenu("Calculus");
        calcValue = new JMenuItem("Value");
        calcLimit = new JMenuItem("Limit");
        calcDeriv = new JMenuItem("Derivative");
        calcInteg = new JMenuItem("Integral");
        calcMin = new JMenuItem("Minimum");
        calcMax = new JMenuItem("Maximum");
        calcInfl = new JMenuItem("Inflection");

        mode = new JMenu("Mode");
        degreeMode = new ButtonGroup();
        modeDeg = new JRadioButtonMenuItem("Degree");
        modeRad = new JRadioButtonMenuItem("Radian");
        graphMode = new ButtonGroup();
        modeFunc = new JRadioButtonMenuItem("Function");
        modePara = new JRadioButtonMenuItem("Parametric");
        modePol  = new JRadioButtonMenuItem("Polar");
        accurMode = new ButtonGroup();
        modeLow = new JRadioButtonMenuItem("Low Accur.");
        modeMed = new JRadioButtonMenuItem("Med Accur.");
        modeHigh = new JRadioButtonMenuItem("High Accur.");
        
        modeLow.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent a) {graph.accuracy(0);}});
        modeMed.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent a) {graph.accuracy(1);}});
        modeHigh.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent a) {graph.accuracy(2);}});
        
        //Assembly
        add.add(addPoint);
        add.add(addLine);
        add(add);
        
        add(calculus);
        calculus.add(calcValue);
        calculus.add(calcLimit);
        calculus.add(calcDeriv);
        calculus.add(calcInteg);
        calculus.add(new JSeparator(0));
        calculus.add(calcMin);
        calculus.add(calcMax);
        calculus.add(calcInfl);
        
        add(mode);
        mode.add(modeDeg);
        mode.add(modeRad);
        degreeMode.add(modeDeg);
        degreeMode.add(modeRad);
        mode.add(new JSeparator(0));
        mode.add(modeFunc);
        mode.add(modePara);
        mode.add(modePol);      
        graphMode.add(modeFunc);
        graphMode.add(modePara);
        graphMode.add(modePol);
        mode.add(new JSeparator(0));
        mode.add(modeLow);
        mode.add(modeMed);
        mode.add(modeHigh);
        accurMode.add(modeLow);
        accurMode.add(modeMed);
        accurMode.add(modeHigh);
        
        
        modeRad.setSelected(true);
        modeFunc.setSelected(true);
        modeMed.setSelected(true);
    }
}