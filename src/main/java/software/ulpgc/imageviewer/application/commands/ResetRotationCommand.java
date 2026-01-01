package software.ulpgc.imageviewer.application.commands;

import software.ulpgc.imageviewer.architecture.Command;
import software.ulpgc.imageviewer.application.gui.ImageTransformController;

public class ResetRotationCommand implements Command {

    private final ImageTransformController controller;

    public ResetRotationCommand(ImageTransformController controller) {
        this.controller = controller;
    }

    @Override
    public void execute() {
        controller.resetRotation();
    }
}
