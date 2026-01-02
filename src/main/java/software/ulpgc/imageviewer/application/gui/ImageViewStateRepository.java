package software.ulpgc.imageviewer.application.gui;

import java.util.HashMap;
import java.util.Map;

public class ImageViewStateRepository {

    private final Map<String, ImageViewState> states;

    public ImageViewStateRepository() {
        this.states = new HashMap<>();
    }

    public ImageViewState stateOf(String imageId) {
        return states.computeIfAbsent(imageId, id -> new ImageViewState());
    }
}
