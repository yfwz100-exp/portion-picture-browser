package io.github.yfwz100.portionpicture;

import sun.applet.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * The main app.
 *
 * @author yfwz100
 */
public class MainAppFrame extends JFrame {

    private File[] files;
    private int fileIndex;
    private PortionPicturePanel portionPicturePanel;
    private PicturePortionSelectionDialog picturePortionSelectionDialog = new PicturePortionSelectionDialog(MainAppFrame.this);

    public MainAppFrame() {
        super("Portion Picture Browser");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);

            JMenuBar menuBar = new JMenuBar();
            setJMenuBar(menuBar);

            {
                JMenu fileMenu = new JMenu("File");
                menuBar.add(fileMenu);

                JMenuItem openItem = new JMenuItem("Open Pictures");
                fileMenu.add(openItem);
                openItem.addActionListener((evt) -> {
                    int ret = fileChooser.showOpenDialog(MainAppFrame.this);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        files = fileChooser.getSelectedFiles();
                        fileIndex = 0;
                        try {
                            picturePortionSelectionDialog.edit(files[0]);
                            portionPicturePanel.setImagePortion(
                                    picturePortionSelectionDialog.getImage(),
                                    picturePortionSelectionDialog.getPortions()
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                JMenu playMenu = new JMenu("Play");
                menuBar.add(playMenu);
                playMenu.add(new AbstractAction("Start auto-play") {

                    private Timer timer = new Timer(1000, (evt) -> {
                        nextPicture();
                    });
                    {
                        timer.setRepeats(true);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!timer.isRunning()) {
                            int interval = Integer.parseInt(JOptionPane.showInputDialog(MainAppFrame.this, "Interval: "));
                            timer.setDelay(interval);
                            timer.start();
                            this.putValue(AbstractAction.NAME, "Stop auto-play");
                        } else {
                            timer.stop();
                            this.putValue(AbstractAction.NAME, "Start auto-play");
                        }
                    }
                });
                playMenu.add(new AbstractAction("Next") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nextPicture();
                    }
                });

                JMenu helpMenu = new JMenu("Help");
                menuBar.add(helpMenu);
                helpMenu.add(new AbstractAction("Usage") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(MainAppFrame.this,
                                "1. Open pictures and select several portions. \n" +
                                "2. Then press any key to navigate the pictures of selected portions.");
                    }
                });
                helpMenu.add(new AbstractAction("About") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(MainAppFrame.this, "https://github.com/yfwz100");
                    }
                });
            }
        }

        portionPicturePanel = new PortionPicturePanel();
        add(portionPicturePanel, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                nextPicture();
            }
        });

        pack();
    }

    public void nextPicture() {
        try {
            if (files != null) {
                fileIndex += 1;
                if (fileIndex >= files.length) {
                    fileIndex = 0;
                }
                portionPicturePanel.setImage(ImageIO.read(files[fileIndex]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ... args) {
        JFrame frame = new MainAppFrame();
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
