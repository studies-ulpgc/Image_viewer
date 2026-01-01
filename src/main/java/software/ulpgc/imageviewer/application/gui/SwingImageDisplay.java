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
    private Image image;
    private BufferedImage bitmap;
    private int initShiftX;
    private int offsetX;

    public SwingImageDisplay() {
        this.stateRepository = new ImageViewStateRepository();

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

        this.addMouseWheelListener(e -> {
            ImageTransformController controller = new ImageTransformController(this, stateRepository);
            if (e.getPreciseWheelRotation() < 0) controller.zoomIn();
            else controller.zoomOut();
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo cian claro
        g.setColor(new Color(0xD5, 0xD2, 0xF5));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (bitmap == null || image == null) return;

        // Estado visual de la imagen actual
        ImageViewState state = stateRepository.stateOf(image.id());

        Canvas canvas = Canvas.ofSize(getWidth(), getHeight())
                .fit(bitmap.getWidth(), bitmap.getHeight());

        double zoom = state.zoom();
        double rotation = Math.toRadians(state.rotation());

        double scaledWidth = canvas.width() * zoom;
        double scaledHeight = canvas.height() * zoom;

        // Calculamos el centro del panel
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;

        // Coordenadas para que la imagen esté centrada
        double x = centerX - scaledWidth / 2 + offsetX;
        double y = centerY - scaledHeight / 2;

        Graphics2D g2 = (Graphics2D) g.create();

        // Aplicar transformaciones centradas
        g2.translate(x + scaledWidth / 2, y + scaledHeight / 2); // mover al centro de la imagen
        g2.rotate(rotation);                                     // aplicar rotación
        g2.translate(-scaledWidth / 2, -scaledHeight / 2);      // volver al origen superior izquierdo

        g2.drawImage(bitmap, 0, 0, (int) scaledWidth, (int) scaledHeight, null);
        g2.dispose();

        // Imagen siguiente/anterior durante arrastre
        if (offsetX != 0) {
            BufferedImage nextBitmap = readBitmap(offsetX < 0 ? image.next() : image.previous());
            double x1 = x - sign(offsetX) * canvas.width();
            g.drawImage(nextBitmap, (int) x1, (int) y, canvas.width(), canvas.height(), null);
        }
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
    // dentro de SwingImageDisplay
    public ImageViewStateRepository getStateRepository() {
        return stateRepository;
    }

}
