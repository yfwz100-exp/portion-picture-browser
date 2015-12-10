package io.github.yfwz100.portionpicture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The picture portion selection dialog.
 *
 * @author yfwz100
 */
public class PicturePortionSelectionDialog extends JDialog {

    private BufferedImage image;
    private JPanel contentPane;
    private Set<Rectangle2D.Float> portions = new HashSet<>();

    public PicturePortionSelectionDialog(Frame owner) {
        super(owner, "Portion Selection");
        setModal(true);

        contentPane = new JPanel() {

            {
                setPreferredSize(new Dimension(600, 400));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                super.paintComponent(g2d);

                if (image != null) {
                    g2d.drawImage(image, 0, 0, this);
                    g2d.setColor(Color.YELLOW);
                    portions.forEach(g2d::draw);
                }
                g2d.dispose();
            }
        };
        contentPane.addMouseListener(new MouseAdapter() {

            private float x, y;

            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                portions.add(new Rectangle2D.Float(x, y, e.getX() - x, e.getY() - y));
                SwingUtilities.invokeLater(PicturePortionSelectionDialog.this::repaint);
            }
        });
        contentPane.setFocusable(true);

        add(new JScrollPane(contentPane), BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.PAGE_END);
        toolBar.add(new AbstractAction("Import portions") {

            private JFileChooser fileChooser = new JFileChooser();

            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showOpenDialog(PicturePortionSelectionDialog.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                        Set<Rectangle2D.Float> portions = (Set<Rectangle2D.Float>) ois.readObject();
                        setPortions(portions);
                    } catch (IOException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });
        toolBar.add(new AbstractAction("Export portions") {

            private JFileChooser fileChooser = new JFileChooser();

            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showSaveDialog(PicturePortionSelectionDialog.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
                        oos.writeObject(portions);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        pack();
    }

    public void setImage(BufferedImage image) {
        if (image != null) {
            this.image = image;
            SwingUtilities.invokeLater(() -> {
                contentPane.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                contentPane.validate();
                contentPane.invalidate();
                contentPane.repaint();
                repaint();
            });
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean edit(File file) throws IOException {
        getPortions().clear();
        setImage(ImageIO.read(file));
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return true; // TODO no discard option yet.
    }

    public void setPortions(Set<Rectangle2D.Float> portions) {
        this.portions = portions;
    }

    public Set<Rectangle2D.Float> getPortions() {
        return portions;
    }
}
