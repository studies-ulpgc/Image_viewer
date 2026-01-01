package software.ulpgc.imageviewer.application.cli;

import software.ulpgc.imageviewer.architecture.Image;
import software.ulpgc.imageviewer.architecture.ImageDisplay;

public class ConsoleImageDisplay implements ImageDisplay {
    private Image image;
    @Override
    public Image image() {
        return image;
    }

    @Override
    public void show(Image image) {
        this.image = image;
        System.out.printf("IMAGE: " + image.id() + " " +  image.bitmap().length);
    }
}
