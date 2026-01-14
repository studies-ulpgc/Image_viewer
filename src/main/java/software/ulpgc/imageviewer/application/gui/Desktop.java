package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.application.commands.*;
import software.ulpgc.imageviewer.architecture.Command;
import software.ulpgc.imageviewer.architecture.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
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

        initCommands(new ImageTransformController(imageDisplay, imageDisplay.getStateRepository()));

        this.createToolbar();
        this.getContentPane().add(imageDisplay, BorderLayout.CENTER);
        
        thumbnailBar = new ThumbnailBar(imageDisplay, imageProvider.allImages(Main::readImage));
        JScrollPane scroll = getJScrollPane();
        this.getContentPane().add(scroll, SOUTH);

    }

    private JScrollPane getJScrollPane() {
        JScrollPane scroll = new JScrollPane(thumbnailBar,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        return scroll;
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

        panel.add(button("prev", "←"));
        panel.add(button("next", "→"));

        panel.add(button("zoomIn", "+"));
        panel.add(button("zoomOut", "-"));

        setZoomSlider(panel);

        panel.add(button("resetZoom", "Reset Zoom"));
        panel.add(button("rotate", "⟳"));
        panel.add(button("resetRotation", "Reset Rotate"));
        return panel;
    }

    private void setZoomSlider(JPanel panel) {
        zoomSlider = new JSlider(20, 500, 100);
        zoomSlider.setPreferredSize(new Dimension(150, 20));
        zoomSlider.setBackground(new Color(237, 236, 250));
        zoomSlider.setForeground(new Color(84, 100, 172));
        zoomSlider.setPaintTrack(true);
        zoomSlider.setPaintTicks(false);
        zoomSlider.setPaintLabels(false);
        zoomSlider.addChangeListener(e -> {
            if (zoomSlider.getValueIsAdjusting()) {
                controller.zoomTo(zoomSlider.getValue() / 100.0);
            }
        });
        panel.add(zoomSlider);
    }

    private JButton button(String name, String label) {
        JButton button = new JButton(label);
        extractButtonColors(button);
        button.addActionListener(e -> commands.get(name).execute());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void extractButtonColors(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
    }

    public Desktop put(String name, Command command) {
        commands.put(name, command);
        return this;
    }
}


