package ac.uk.hope.osmviewer.MultiCompassOverlay;

import android.content.Context;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationProvider;

public class TappableCompassOverlay extends CompassOverlay {

    private TappableCompassListener mCompassListener;

    public TappableCompassOverlay(Context context, IOrientationProvider orientationProvider,
                                  TappableCompassListener compassListener, MapView mapView) {
        super(context, orientationProvider, mapView);
        mCompassListener = compassListener;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        mCompassListener.onCompassTap();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e, MapView mapView) {
        mCompassListener.onCompassDoubleTap();
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {
        mCompassListener.onCompassLongPress();
        return true;
    }

}
