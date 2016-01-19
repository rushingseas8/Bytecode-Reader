import javax.swing.*;

public class ValueDialog extends JDialog {
    public ValueDialog(JFrame owner) {
        setLocationRelativeTo(owner);
        setSize(320, 180);
        setVisible(true);
    }
    
    public static void main() {
        JFrame frame = new JFrame();
        frame.setSize(640, 480);
        frame.setVisible(true);
        
        new ValueDialog(frame);
    }
}