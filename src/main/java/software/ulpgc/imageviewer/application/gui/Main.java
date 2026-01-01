package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.application.FileImageStore;
import software.ulpgc.imageviewer.architecture.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static File root;

    public static void main(String[] args) throws IOException {
        root = new File("images");
        ImageStore store = new FileImageStore(root);
        ImageProvider imageProvider = ImageProvider.with(store.images());
        SwingImageDisplay imageDisplay = new SwingImageDisplay();
        imageDisplay.show(imageProvider.first(Main::readImage));
        Desktop.create(imageDisplay)
                .put("next", new NextCommand(imageDisplay))
                .put("prev", new PrevCommand(imageDisplay))
                .setVisible(true);
    }

    private static byte[] readImage(String id) {
        try {
            return Files.readAllBytes(new File(root, id).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
