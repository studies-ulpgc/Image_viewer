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

public class ThumbnailBar extends JPanel {

    private final SwingImageDisplay mainDisplay;
    private final List<Image> images;

    public ThumbnailBar(SwingImageDisplay display, List<Image> images) {
        this.mainDisplay = display;
        this.images = images;

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(0xD5, 0xD2, 0xF5));
        updateThumbnails();
    }

    private void updateThumbnails() {
        removeAll();
        for (Image img : images) {
            try {
                byte[] data = img.bitmap();
                if (data == null || data.length == 0) continue; // ignorar imagen inválida
                BufferedImage thumb = ImageIO.read(new ByteArrayInputStream(data));
                if (thumb == null) continue; // ignorar imagen que no se pudo leer

                ImageIcon icon = new ImageIcon(
                        thumb.getScaledInstance(80, 60, java.awt.Image.SCALE_SMOOTH)
                );

                JLabel label = new JLabel(icon);
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Borde según estado
                label.setBorder(BorderFactory.createLineBorder(
                        img == mainDisplay.image() ? new Color(84, 100, 172) : Color.LIGHT_GRAY,
                        img == mainDisplay.image() ? 4 : 1
                ));

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (img != mainDisplay.image())
                            label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (img != mainDisplay.image())
                            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        mainDisplay.show(img);
                        updateThumbnails();
                    }
                });

                add(label);
            } catch (IOException e) {
                e.printStackTrace(); // si falla la lectura de esta imagen, seguimos con las demás
            }
        }
        revalidate();
        repaint();
    }

}
