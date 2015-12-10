package io.github.yfwz100.portionpicture;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * The picture panel.
 *
 * @author yfwz100
 */
public class PortionPicturePanel extends JPanel {

    private BufferedImage image;
    private Set<Rectangle2D.Float> portions;

    public PortionPicturePanel() {
        setPreferredSize(new Dimension(600, 400));
    }

    public PortionPicturePanel(BufferedImage image, Set<Rectangle2D.Float> portions) {
        setImagePortion(image, portions);
        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (image != null && portions != null) {
            int x = 0, y = 0, ymax = 0, gap = 10;
            for (Rectangle2D.Float portion : portions) {
                if (x + portion.getWidth()> getWidth()) {
                    y += ymax;
                    ymax = 0;
                    x = 0;
                }
                Graphics2D g3 = (Graphics2D) g2d.create();
                g3.translate(x-portion.getX(), y-portion.getY());
                g3.setClip(portion);
                g3.drawImage(
                        image,
                        0, 0,
                        this
                );
                g3.dispose();
                x += (int) portion.getWidth() + gap;
                if (ymax < portion.getHeight()) {
                    ymax = (int) portion.getHeight();
                }
            }
        }
        g2d.dispose();
    }

    public void setImagePortion(BufferedImage image, Set<Rectangle2D.Float> portions) {
        this.image = image;
        this.portions = portions;
        SwingUtilities.invokeLater(this::repaint);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        SwingUtilities.invokeLater(this::repaint);
    }

    public void setPortions(Set<Rectangle2D.Float> portions) {
        this.portions = portions;
        SwingUtilities.invokeLater(this::repaint);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Set<Rectangle2D.Float> getPortions() {
        return portions;
    }
}
