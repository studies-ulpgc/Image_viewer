package software.ulpgc.imageviewer.application.commands;

import software.ulpgc.imageviewer.architecture.Command;
import software.ulpgc.imageviewer.application.gui.ImageTransformController;

import javax.swing.*;

public class ResetZoomCommand implements Command {

    private final ImageTransformController controller;
    private final JSlider slider;

    public ResetZoomCommand(ImageTransformController controller, JSlider slider) {
        this.controller = controller;
        this.slider = slider;
    }

    @Override
    public void execute() {
        controller.resetZoom();
        slider.setValue((int)(controller.getCurrentZoom() * 100));
    }
}
