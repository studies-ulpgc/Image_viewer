package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.application.commands.*;
import software.ulpgc.imageviewer.architecture.Command;
import software.ulpgc.imageviewer.architecture.Image;
import software.ulpgc.imageviewer.architecture.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.CENTER;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final SwingImageDisplay imageDisplay;
    private ThumbnailBar thumbnailBar;
    private ImageTransformController controller;

    public static Desktop create(SwingImageDisplay imageDisplay, ImageProvider imageProvider) throws IOException {
        return new Desktop(imageDisplay, imageProvider);
    }

    private Desktop(SwingImageDisplay imageDisplay, ImageProvider imageProvider) throws HeadlessException {
        this.imageDisplay = imageDisplay;
        this.commands = new HashMap<>();
        this.setTitle("Image Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.controller = new ImageTransformController(imageDisplay, imageDisplay.getStateRepository());

        // 🆕 ImageTransformController
        ImageTransformController controller =
                new ImageTransformController(imageDisplay, imageDisplay.getStateRepository());

        // 🆕 Inicializar comandos
        initCommands(controller);

        this.createToolbar();

        this.getContentPane().add(imageDisplay, BorderLayout.CENTER);

        List<Image> allImages = imageProvider.allImages(Main::readImage);
        thumbnailBar = new ThumbnailBar(imageDisplay, allImages);
        JScrollPane scroll = new JScrollPane(thumbnailBar,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        this.getContentPane().add(scroll, SOUTH);

    }

    private void createToolbar() {
        this.getContentPane().add(toolbar(), NORTH);
    }

    private JSlider zoomSlider;

    private void initCommands(ImageTransformController controller) {
        commands.put("zoomIn", new ZoomInCommand(controller, zoomSlider));
        commands.put("zoomOut", new ZoomOutCommand(controller, zoomSlider));
        commands.put("resetZoom", new ResetZoomCommand(controller, zoomSlider));

        commands.put("rotate", new RotateCommand(controller));
        commands.put("resetRotation", new ResetRotationCommand(controller));
    }

    private JPanel toolbar() {
        JPanel panel = new JPanel(new FlowLayout(CENTER));
        panel.setBackground(new Color(237, 236, 250));

        // Prev/Next con flechas
        panel.add(button("prev", "←"));
        panel.add(button("next", "→"));

        panel.add(button("zoomIn", "+"));
        panel.add(button("zoomOut", "-"));

        zoomSlider = new JSlider(20, 500, 100);
        zoomSlider.setPreferredSize(new Dimension(150, 20));
        zoomSlider.setBackground(new Color(237, 236, 250));
        zoomSlider.setForeground(new Color(84, 100, 172));
        zoomSlider.setPaintTrack(true);
        zoomSlider.setPaintTicks(false);
        zoomSlider.setPaintLabels(false);
        zoomSlider.addChangeListener(e -> {
            // Solo actuamos si el usuario está moviendo el slider manualmente
            if (zoomSlider.getValueIsAdjusting()) {
                controller.zoomTo(zoomSlider.getValue() / 100.0);
            }
        });
        panel.add(zoomSlider);

        panel.add(button("resetZoom", "Reset Zoom"));
        panel.add(button("rotate", "⟳"));
        panel.add(button("resetRotation", "Reset Rotate"));
        return panel;
    }

    public void updateLabels() {
        Image image = imageDisplay.image();
        if (image != null) {
            double currentZoom = imageDisplay.getStateRepository()
                    .stateOf(image.id()).zoom();
            zoomSlider.setValue((int) (currentZoom * 100));
        }
    }

    private JButton button(String name, String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.addActionListener(e -> commands.get(name).execute());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public Desktop put(String name, Command command) {
        commands.put(name, command);
        return this;
    }
}


