package uwb;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;
 
public class Example extends JPanel {
    private BufferedImage image;
 
    public Example() throws IOException {
        image = ImageIO.read(new URL("http://web.mit.edu/18.06/www/PIX/mitlogo3.gif"));
    }
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Color light = new Color(0xee, 0xee, 0xff);
        Color dark = new Color(0x80, 0x80, 0xff);
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top -insets.bottom;
        int x0 = insets.left;
        int y0 = insets.top;
        g2.setPaint(new GradientPaint(0, 0, dark, w, 0, light, true));
        g2.fillRect(x0,y0,w, h);
        g2.setPaint(Color.BLUE);
        for(int xx=x0, ub=x0+w; xx<ub; xx+=10)
            g2.drawLine(xx, y0, xx, y0+h);
        for(int yy=y0, ub=y0+h; yy<ub; yy+=10)
            g2.drawLine(x0, yy, x0+w, yy);
 
        int x = x0 + (w-image.getWidth())/2, y = y0 + (h-image.getHeight())/2;
        g2.drawRenderedImage(image, AffineTransform.getTranslateInstance(x,y));
    }
 
    //The 100's are to show off the grid pattern
    public Dimension getPreferredSize() {
        Dimension sz = getMinimumSize();
        sz.width += 100;
        sz.height += 100;
        return sz;
    }
 
    public Dimension getMinimumSize() {
        Insets insets = getInsets();
        int w = insets.left + insets.right + image.getWidth();
        int h = insets.top + insets.bottom + image.getHeight();
        return new Dimension(w, h);
    }
 
    public static void main(String[] args) throws IOException {
        JComponent comp = new Example();
        comp.setBorder(BorderFactory.createTitledBorder("Example of rendering"));
        JFrame f = new JFrame("Example");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(comp);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
