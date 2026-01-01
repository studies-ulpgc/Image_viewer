package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.Canvas;
import software.ulpgc.imageviewer.architecture.Image;
import software.ulpgc.imageviewer.architecture.ImageDisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SwingImageDisplay extends JPanel implements ImageDisplay {
    private Image image;
    private BufferedImage bitmap;
    private int initShiftX;
    private int offsetX;

    public SwingImageDisplay() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initShiftX = e.getX();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                offsetX = 0;
                changeImageIfShowingMoreThanHalf(e.getX() - initShiftX);
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX = e.getX() - initShiftX;
                repaint();
            }
        });
    }

    private void changeImageIfShowingMoreThanHalf(int delta) {
        if (showingLessThanHalfofImage(delta)) return;
        image = delta > 0 ? image.previous() : image.next();
        bitmap = readBitmap();
    }

    private boolean showingLessThanHalfofImage(int delta) {
        return Math.abs(delta) <= getWidth() / 2;
    }

    @Override
    public Image image() {
        return image;
    }

    @Override
    public void show(Image image) {
        this.image = image;
        this.bitmap = readBitmap();
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.fillRect(0,0,this.getWidth(), this.getHeight());

        if (bitmap == null) return;
        Canvas canvas = Canvas.ofSize(this.getWidth(), this.getHeight())
                .fit(bitmap.getWidth(), bitmap.getHeight());

        int x = (this.getWidth() - canvas.width()) / 2 + offsetX;
        int y = (this.getHeight() - canvas.height()) / 2;
        g.drawImage(bitmap, x, y, canvas.width(), canvas.height(), null);

        if (offsetX == 0) return;
        BufferedImage nextBitmap = readBitmap(offsetX < 0 ? image.next() : image.previous());
        int x1 = x -  sign(offsetX) * canvas.width();
        g.drawImage(nextBitmap, x1, y, canvas.width(), canvas.height(), null);
    }

    private static int sign(int value) {
        return value < 0 ? -1 : 1;
    }

    private BufferedImage readBitmap() {
        return readBitmap(this.image);
    }

    private BufferedImage readBitmap(Image image) {
        try {
            return ImageIO.read(new ByteArrayInputStream(image.bitmap()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
