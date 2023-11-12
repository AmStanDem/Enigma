import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RoundLabel extends JLabel {
    Color borderColor;
    public RoundLabel(String text) {
        super(text);

        setBackground(Color.lightGray);
        serBorderColor(Color.lightGray);
    /*
     These statements enlarge the button so that it
     becomes a circle rather than an oval.
    */
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);

    /*
     This call causes the JButton not to paint the background.
     This allows us to paint a round background.
    */
    }

    public void serBorderColor(Color color)
    {
        borderColor = color;

    }
    public Color getBorderColor()
    {
        return borderColor;

    }
    protected void paintComponent(Graphics g)
    {
        g.setColor(getBackground());
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g)
    {
        g.setColor(borderColor);
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    // Hit detection.
    Shape shape;

    public boolean contains(int x, int y) {
        // If the button has changed size,  make a new shape object.
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }

}
