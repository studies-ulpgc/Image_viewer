package software.ulpgc.imageviewer.application.gui;

public class ImageViewState {

    private double zoom;
    private double rotation;

    public ImageViewState() {
        this.zoom = 1.0;
        this.rotation = 0.0;
    }

    public double zoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double rotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void resetZoom() {
        this.zoom = 1.0;
    }

    public void resetRotation() {
        this.rotation = 0.0;
    }
}
