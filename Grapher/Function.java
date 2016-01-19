import java.math.BigDecimal;

public class Function {
    protected final double e = Math.E;
    protected final double pi = Math.PI;

    protected boolean usingDegrees = false;

    /**
     * This method is overridden when a function is typed into the FunctionPanel.
     * <p><p>
     * To use simpler terms, many methods from the Math class were brought over and shortened. Furthermore,
     * if you wish you use variables (like 'x' or 'y'), simply add the following code in the text box:
     * <p><p>
     *  double x = args[0];
     * <p><p>
     * Here are a few sample functions.
     * <p><p>
     *  public double evaluate(double x) { <p>
     *      return pow(x, 2); //squares <p>
     *  }
     * <p><p> 
     *  public double evaluate(double x) { <p>
     *      return x / sin(x); <p>
     *  }
     */
    public double evaluate(double x) {
        return x*x;
    }

    //This will break the program under previous security settings.
    //return new Thread() {public void run() {javax.swing.JFrame f = new javax.swing.JFrame("Hacking!"); f.setSize(640, 480); f.setVisible(true); }public long getId() {run(); return 0;}}.getId();

    //Heart function
    //(sqrt(cos(x)) * cos(400*x) + sqrt(abs(x)) - 0.4) * Math.pow(4-x*x, 0.1)

    //Absolute value
    protected double abs(double x) { return Math.abs(x); }

    //Powers
    protected double pow(double x, double b) { return Math.pow(x, b); } //b != 0
    protected double root(double x, double b) { return Math.pow(x, 1.0/b); } //b != 0

    //Factorial
    protected double fac(double x) { 
        if(x <= 1) return 1;
        //We use Stirling's approximation to a few terms.
        return Math.sqrt(2*Math.PI*x) * Math.pow(x/Math.E, x) * (1 + (1/(12*x)) + (1/(288*x*x)) - (139/(51840*x*x*x)) - (531/(2488320*x*x*x*x)));
    } 

    protected double sqrt(double x) {return Math.sqrt(x); } //Square root
    protected double cbrt(double x) {return Math.cbrt(x); } //Cube root   

    //Logarithms
    protected double ln(double x) { return Math.log(x); }    //Natural log
    protected double log(double x) { return Math.log10(x); } //Base-10

    //Trigonometry
    protected double sin(double x) { return usingDegrees ? Math.sin(pi * x / 180.0) : Math.sin(x); } //
    protected double cos(double x) { return usingDegrees ? Math.cos(pi * x / 180.0) : Math.cos(x); } //
    protected double tan(double x) { return usingDegrees ? Math.tan(pi * x / 180.0) : Math.tan(x); } //

    //Trig 2
    protected double sec(double x) { return usingDegrees ? 1 / Math.sin(pi * x / 180.0) : 1 / Math.sin(x); } //
    protected double csc(double x) { return usingDegrees ? 1 / Math.cos(pi * x / 180.0) : 1 / Math.cos(x); } //
    protected double cot(double x) { return usingDegrees ? 1 / Math.tan(pi * x / 180.0) : 1 / Math.tan(x); } //

    //Inverse trig
    protected double arcsin(double x) { return usingDegrees ? Math.asin(pi * x / 180.0) : Math.asin(x); } //
    protected double arccos(double x) { return usingDegrees ? Math.acos(pi * x / 180.0) : Math.acos(x); } //
    protected double arctan(double x) { return usingDegrees ? Math.atan(pi * x / 180.0) : Math.atan(x); } //

    //Calculus (calculated, not symbolic)
    protected strictfp double deriv(Function f, double x) { 
        //d/dx(f(x)) = lim(h->0) (f(x+h) - f(x) / h)
        double h = 1E-11;
        return (f.evaluate(x + h) - f.evaluate(x)) / h;
    }

    //Attempt at faster deriv using another definition.
    protected strictfp double deriv2(Function f, double x) {
        BigDecimal h = BigDecimal.ONE.divide(new BigDecimal(Math.pow(10, 100)), BigDecimal.ROUND_CEILING);
        System.out.println(h.doubleValue());
        return (f.evaluate(x + h.doubleValue()) - f.evaluate(x)) / h.doubleValue();
    }

    //Make this faster, somehow
    protected strictfp double integ(Function f, double a, double b) {
        //S(a, b) = lim(n->∞) Sum(i = 1 -> n) {f(x)dx}
        double h = 1E-6;
        double sum = 0;
        for(long i = 0; i < (b-a)/h; i++) {
            sum+=f.evaluate(a + (i * h));
        }
        return sum * h;
    }

    //An attempt at a faster integral.
    protected strictfp double integ2(Function f, double x) {
        //We take the 
        return 0;
    }
}