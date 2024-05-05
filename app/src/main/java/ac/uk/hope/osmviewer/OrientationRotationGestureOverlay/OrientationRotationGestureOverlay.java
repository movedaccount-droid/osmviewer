package ac.uk.hope.osmviewer.OrientationRotationGestureOverlay;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class OrientationRotationGestureOverlay extends RotationGestureOverlay
        implements IOrientationProvider {

    private IOrientationConsumer mOrientationConsumer;
    private final IMapOrientationCallback mMapOrientationCallback;

    public OrientationRotationGestureOverlay(MapView mapView,
                                             IMapOrientationCallback callback) {
        super(mapView);
        mMapOrientationCallback = callback;
    }

    @Override
    public boolean startOrientationProvider(IOrientationConsumer orientationConsumer) {
        mOrientationConsumer = orientationConsumer;
        return true;
    }

    @Override
    public void stopOrientationProvider() {}

    @Override
    public float getLastKnownOrientation() {
        return mMapOrientationCallback.getMapOrientation();
    }

    @Override
    public void destroy() {}

    @Override
    public void onRotate(float deltaAngle) {
        super.onRotate(deltaAngle);
        float orientation = 0 - mMapOrientationCallback.getMapOrientation();
        mOrientationConsumer.onOrientationChanged(orientation, this);
    }
}
