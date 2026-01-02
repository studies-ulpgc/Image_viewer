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

    private final ImageViewStateRepository stateRepository;
    private final ImageTransformController controller;
    private Image image;
    private BufferedImage bitmap;

    private int initShiftX;
    private int offsetX;

    public SwingImageDisplay() {
        this.stateRepository = new ImageViewStateRepository();
        this.controller = new ImageTransformController(this, stateRepository);

        addMouseListener(new MouseAdapter() {
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

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX = e.getX() - initShiftX;
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            double amount = -e.getPreciseWheelRotation() * 0.1;
            ImageViewState state = stateRepository.stateOf(image.id());
            state.setZoom(Math.max(0.1, state.zoom() + amount));
            repaint();
        });
    }

    private void changeImageIfShowingMoreThanHalf(int delta) {
        if (Math.abs(delta) <= getWidth() / 2) return;
        image = delta > 0 ? image.previous() : image.next();
        bitmap = readBitmap();
    }

    @Override
    public Image image() {
        return image;
    }

    @Override
    public void show(Image image) {
        if (this.image != null && this.image.id().equals(image.id())) {
            repaint(); 
            return;
        }
        this.image = image;
        this.bitmap = readBitmap();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(237, 236, 250));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (bitmap == null || image == null) return;

        ImageViewState state = stateRepository.stateOf(image.id());

        Canvas canvas = Canvas.ofSize(getWidth(), getHeight())
                .fit(bitmap.getWidth(), bitmap.getHeight());

        Result result = getResult(state, canvas);

        draw_transformed_image((Graphics2D) g.create(), result.x(), result.scaledWidth(), result.y(), result.scaledHeight(), result.rotation());

        if (offsetX == 0) return;
        BufferedImage nextBitmap =
                readBitmap(offsetX < 0 ? image.next() : image.previous());
        int x1 = (int) (result.x() - Math.signum(offsetX) * canvas.width());
        g.drawImage(nextBitmap, x1, (int) result.y(),
                canvas.width(), canvas.height(), null);
    }

    private Result getResult(ImageViewState state, Canvas canvas) {
        double zoom = state.zoom();
        double rotation = Math.toRadians(state.rotation());

        double scaledWidth = canvas.width() * zoom;
        double scaledHeight = canvas.height() * zoom;

        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;

        double x = centerX - scaledWidth / 2 + offsetX;
        double y = centerY - scaledHeight / 2;
        Result result = new Result(rotation, scaledWidth, scaledHeight, x, y);
        return result;
    }

    private record Result(double rotation, double scaledWidth, double scaledHeight, double x, double y) {
    }

    private void draw_transformed_image(Graphics2D g2, double x, double scaledWidth, double y, double scaledHeight, double rotation) {
        g2.translate(x + scaledWidth / 2, y + scaledHeight / 2);
        g2.rotate(rotation);
        g2.translate(-scaledWidth / 2, -scaledHeight / 2);

        g2.drawImage(bitmap, 0, 0,
                (int) scaledWidth, (int) scaledHeight, null);
        g2.dispose();
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

    public ImageViewStateRepository getStateRepository() {
        return stateRepository;
    }
}
