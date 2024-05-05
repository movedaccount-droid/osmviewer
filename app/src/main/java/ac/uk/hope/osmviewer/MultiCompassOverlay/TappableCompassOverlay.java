package ac.uk.hope.osmviewer.MultiCompassOverlay;

import android.content.Context;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationProvider;

import ac.uk.hope.osmviewer.MultiCompassOverlay.ITappableCompassListener;

public class TappableCompassOverlay extends CompassOverlay {

    private ITappableCompassListener mCompassListener;

    public TappableCompassOverlay(Context context, IOrientationProvider orientationProvider,
                               ITappableCompassListener compassListener, MapView mapView) {
        super(context, orientationProvider, mapView);
        mCompassListener = compassListener;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
        mCompassListener.onTap();
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {
        mCompassListener.onLongPress();
        return true;
    }

}
