package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.Image;
import software.ulpgc.imageviewer.architecture.ImageDisplay;

public class ImageTransformController {

    private final ImageDisplay display;
    private final ImageViewStateRepository stateRepository;

    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_STEP = 0.4;

    public ImageTransformController(ImageDisplay display,
                                    ImageViewStateRepository stateRepository) {
        this.display = display;
        this.stateRepository = stateRepository;
    }

    public void zoomIn() {
        applyZoom(ZOOM_STEP);
    }

    public void zoomOut() {
        applyZoom(-ZOOM_STEP);
    }

    public void resetZoom() {
        state().resetZoom();
        refresh();
    }

    public void rotate() {
        state().setRotation(state().rotation() + 90);
        refresh();
    }

    public void resetRotation() {
        state().resetRotation();
        refresh();
    }

    private void applyZoom(double delta) {
        double newZoom = state().zoom() + delta;
        newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));
        state().setZoom(newZoom);
        refresh();
    }

    private ImageViewState state() {
        Image image = display.image();
        if (image == null) return new ImageViewState();
        return stateRepository.stateOf(image.id());
    }

    private void refresh() {
        display.repaint();
    }

    public double getCurrentZoom() {
        Image image = display.image();
        if (image == null) return 1.0;
        return stateRepository.stateOf(image.id()).zoom();
    }

    public void zoomTo(double factor) {
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, factor));
        state().setZoom(newZoom);
        display.repaint();
    }
}
