package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;

public class ThumbnailBar extends JPanel {

    private final SwingImageDisplay mainDisplay;
    private final List<Image> images;

    public ThumbnailBar(SwingImageDisplay display, List<Image> images) {
        this.mainDisplay = display;
        this.images = images;

        setLayout();
    }

    private void setLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(237, 236, 250));
        updateThumbnails();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void updateThumbnails() {
        removeAll();
        for (Image img : images) {
            try {
                BufferedImage thumb = ImageIO.read(new ByteArrayInputStream(img.bitmap()));
                ImageIcon icon = new ImageIcon(
                        thumb.getScaledInstance(80, 60, SCALE_SMOOTH)
                );
                stablishingColors(img, new JLabel(icon));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        revalidate();
        repaint();
    }

    private void stablishingColors(Image img, JLabel label) {
        label.setBorder(BorderFactory.createLineBorder(
                img == mainDisplay.image() ? Color.BLUE : Color.LIGHT_GRAY, 2));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainDisplay.show(img);
                updateThumbnails();
            }
        });
        add(label);
    }
}