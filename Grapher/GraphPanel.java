import javax.swing.*;
import javax.swing.border.*;
import java.text.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
    //Boundaries of the viewport.
    private static double xMin, xMax, yMin, yMax;

    //The centermost point of the viewport.
    private static double xCenter, yCenter;

    //The width and height of the viewport; these may be different from the 'width' and 'height' values.
    private static double displayWidth, displayHeight;

    //How many units we have per pixel; defined as displayWidth / width and displayHeight / height, respectively.
    private static double xResolution, yResolution;

    //The drawing location of the x and y axis.
    private static int xAxisPosition, yAxisPosition;

    //How many ticks we have on the mouse for zoom.
    private static int zoomAmount = 0;

    //The color of the background.
    private static Color background = Color.WHITE;

    //The color of the foreground drawing.
    private static Color foreground = Color.BLACK;

    //The current position of the mouse.
    private static Point mousePos;

    //The previous position of the mouse.
    private static Point lastPoint;

    //The function we're going to draw.
    //private static Function drawingFunction = null;

    //Text showing the interpreted Function.
    private static String interpretedFunction = "";

    //A formatting object used for displaying coordinates.
    private static DecimalFormat low = new DecimalFormat("#.##");
    private static DecimalFormat med = new DecimalFormat("#.#####");
    private static DecimalFormat high = new DecimalFormat("#.##########");

    //We store the drawn function on here until we redraw to save computation time.
    private static BufferedImage functionImage;

    //The accuracy of the points. 0 is low, 1 is med, 2 is high.
    private static int accuracy = 1;

    //Whether or not we're supposed to draw all functions layered on each other.
    private static boolean drawAll = true;

    //Whether or not we should redraw the function onto 'functionImage'.
    private static boolean redraw = true;

    //Whether or not the mouse pointer should be locked to the function.
    private static boolean funcLock = false;

    //Whether or not we should draw thick axes. True is 2 wide, false is 1.
    private static boolean boldAxes = true;

    private static Grapher graph;

    public static Color[] functionColorOrder;

    /**
     * Create a new GraphPanel with the default starting viewport size.
     */
    public GraphPanel(Grapher g) {
        this.graph = g;
        functionColorOrder = new Color[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.ORANGE, new Color(0,0,128)};

        setBackground(background);
        setForeground(foreground);

        xMin = yMin = -10;
        xMax = yMax = 10;

        update(); //Sets up the initial variables

        //Resize listener
        addComponentListener(
            new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent c) {
                    update();
                    redraw = true; //No need to repaint, as resizing repaints by default.
                }
            });

        //Mouse movement
        addMouseMotionListener(
            new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent m) {
                    mousePos = m.getPoint();
                    lastPoint = mousePos;
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent m) {
                    if(lastPoint != null) {
                        double x = (m.getPoint().x - lastPoint.x) * xResolution;
                        double y = (m.getPoint().y - lastPoint.y) * yResolution;

                        xMin-=x; xMax-=x;
                        yMin-=y; yMax-=y; 

                        xAxisPosition = (int)(-yMin / yResolution);
                        yAxisPosition = (int)(-xMin / xResolution);

                        lastPoint = m.getPoint();
                        mousePos = lastPoint;

                        update();
                        redraw = true;
                        repaint();
                    }
                }
            });

        //Mouse button listener (used for mouse release for now)
        addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent m) {
                    lastPoint = null;
                }
            });

        //Mouse wheel
        addMouseWheelListener(
            new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent m) {
                    //Approximately 30 ticks should mean a doubling in zoom factor.
                    //System.out.println(zoomAmount);
                    zoomAmount+=m.getWheelRotation();

                    //System.out.println("Drawing from x= " + xMin + " to x= " + xMax + " and y= " + yMin + " to y= " + yMax);
                    //System.out.println("Center is " + xCenter + ", " + yCenter);

                    xMin = xCenter - (Math.pow(2, zoomAmount/30.0) * 10);
                    xMax = xCenter + (Math.pow(2, zoomAmount/30.0) * 10);
                    yMin = yCenter - (Math.pow(2, zoomAmount/30.0) * 10);
                    yMax = yCenter + (Math.pow(2, zoomAmount/30.0) * 10);

                    update();
                    redraw = true;
                    repaint();
                }
            });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK, false), "lockon");
        getActionMap().put("lockon", new AbstractAction() { public void actionPerformed(ActionEvent a) { funcLock = true; repaint(); } } );

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), "lockoff");
        getActionMap().put("lockoff", new AbstractAction() { public void actionPerformed(ActionEvent a) { funcLock = false; repaint(); } } );
    }

    //Draws the given function.
    public void drawFunction(Function f) {
        graph.functions[graph.currentFunction] = f;
        redraw = true;
        repaint();
    }

    public void drawAll() {
        drawAll = true;
        redraw = true;
        repaint();
    }

    public void redraw(boolean to) {
        redraw = to;
    }
    
    public void background(Color to) {
        this.background = to;
    }

    public void foreground(Color to) {
        this.foreground = to;
    }

    public void accuracy(int to) {
        this.accuracy = to;
    }

    public void setInterpretedFunctionText(String to) {
        this.interpretedFunction = "y =" + to;
    }

    //Updates all variables to their needed values.
    public void update() {
        //Viewports are left alone; xMin, xMax, yMin, yMax.

        xCenter = (xMin + xMax) / 2;
        yCenter = (yMin + yMax) / 2;

        displayWidth = xMax - xMin;
        displayHeight = yMax - yMin;

        xResolution = displayWidth / getWidth();
        yResolution = displayHeight / getHeight();

        xAxisPosition = (int)(-yMin / yResolution);
        yAxisPosition = (int)(-xMin / xResolution);
    }

    @Override
    public void paintComponent(Graphics g2) {
        //System.out.println("Drawing from x= " + xMin + " to x= " + xMax + " and y= " + yMin + " to y= " + yMax);
        //System.out.println("X resolution is " + xResolution + " and Y resolution is " + yResolution);

        if(redraw) {
            //Double buffering; here is the image we draw to first.
            functionImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = functionImage.createGraphics();

            //Draw the background.
            g.setColor(background);
            g.fillRect(0, 0, getWidth(), getHeight());

            //Draw the axes.
            g.setColor(functionColorOrder[0]);
            g.drawLine(0, xAxisPosition, getWidth(), xAxisPosition); //X axis
            g.drawLine(yAxisPosition, 0, yAxisPosition, getHeight()); //Y axis

            //Make the axes darker.
            if(boldAxes) {
                g.drawLine(0, xAxisPosition - 1, getWidth(), xAxisPosition - 1); //X axis
                g.drawLine(yAxisPosition - 1, 0, yAxisPosition - 1, getHeight()); //Y axis
            }

            //Draw the interpreted function text, if possible.
            if(graph.functionNames[graph.currentFunction] != null)
                g.drawString("y=" + graph.functionNames[graph.currentFunction], 0, 20);

            //Draw all functions if requested..
            if(drawAll) {
                for(int j = 0; j < functionColorOrder.length; j++) {
                    Function f = graph.functions[j];
                    g.setColor(functionColorOrder[j]);
                    if(f != null) {
                        for(int i = 0; i < getWidth()-1; i++) {
                            double y1 = (yMin + (f.evaluate(xMin + (i * xResolution)))) / yResolution;
                            double y2 = (yMin + (f.evaluate(xMin + ((i + 1) * xResolution)))) / yResolution;
                            g.drawLine(i, -(int)(y1), i+1, -(int)(y2));
                        }
                    }
                }
            } else { //Else draw just one.
                if(graph.functions[graph.currentFunction] != null) {
                    g.setColor(functionColorOrder[graph.currentFunction]);
                    for(int i = 0; i < getWidth()-1; i++) {
                        double y1 = (yMin + (graph.functions[graph.currentFunction].evaluate(xMin + (i * xResolution)))) / yResolution;
                        double y2 = (yMin + (graph.functions[graph.currentFunction].evaluate(xMin + ((i + 1) * xResolution)))) / yResolution;
                        g.drawLine(i, -(int)(y1), i+1, -(int)(y2));
                    }
                }
            }

            redraw = false;
        }

        //Draw the function as a background.
        g2.drawImage(functionImage, 0, 0, null);

        //Render the mouse
        if(mousePos != null) {
            g2.setColor(functionColorOrder[0]);

            //If funcLock is enabled, lock the pointer to the function.
            double y = mousePos.y;
            if(funcLock) 
                if(graph.functions[graph.currentFunction] != null)
                    y = -((yMin + (graph.functions[graph.currentFunction].evaluate(xMin + (mousePos.x * xResolution)))) / yResolution);

            //A crosshair
            g2.drawLine((int)mousePos.x - 3, (int)y, (int)mousePos.x + 3, (int)y);
            g2.drawLine((int)mousePos.x, (int)y - 3, (int)mousePos.x, (int)y + 3);

            //Text
            switch(accuracy) {
                case 0: g2.drawString(low.format(xMin+(mousePos.x*xResolution)) + ", " + low.format(-(yMin + (y * yResolution))), (int)mousePos.x + 3, (int)y - 3); break;
                case 1: g2.drawString(med.format(xMin+(mousePos.x*xResolution)) + ", " + med.format(-(yMin + (y * yResolution))), (int)mousePos.x + 3, (int)y - 3); break;
                case 2: g2.drawString(high.format(xMin+(mousePos.x*xResolution)) + ", " + high.format(-(yMin + (y * yResolution))), (int)mousePos.x + 3, (int)y - 3); break;
            }
        }
    }
}