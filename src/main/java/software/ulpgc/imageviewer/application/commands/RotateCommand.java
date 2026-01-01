package software.ulpgc.imageviewer.application.commands;

import software.ulpgc.imageviewer.architecture.Command;
import software.ulpgc.imageviewer.application.gui.ImageTransformController;

public class RotateCommand implements Command {

    private final ImageTransformController controller;

    public RotateCommand(ImageTransformController controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.rotate();
    }
}
