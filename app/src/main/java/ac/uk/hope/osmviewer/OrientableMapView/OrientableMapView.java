package ac.uk.hope.osmviewer.OrientableMapView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;

public class OrientableMapView extends MapView implements IOrientationProvider, IOrientationConsumer {

    // === AS ORIENTATION PROVIDER ===
    // provide the map's rotation as an orientation

    private IOrientationConsumer mOrientationConsumer;
    private IOrientationProvider mOrientationProvider;
    private boolean orientationEnabled;
    @Override
    public boolean startOrientationProvider(IOrientationConsumer orientationConsumer) {
        if (super.isEnabled()) {
            mOrientationConsumer = orientationConsumer;
            mOrientationConsumer.onOrientationChanged(getLastKnownOrientation(), this);
        }
        return super.isEnabled();
    }

    @Override
    public void stopOrientationProvider() {
        mOrientationConsumer = null;
    }

    @Override
    public float getLastKnownOrientation() {
        return 0 - super.getMapOrientation();
    }

    @Override
    public void setMapOrientation(float degrees) {
        super.setMapOrientation(degrees);
        if (mOrientationConsumer != null) {
            mOrientationConsumer.onOrientationChanged(getLastKnownOrientation(), this);
        }
    }

    @Override
    public void destroy() {
        mOrientationConsumer = null;
    }

    // === AS ORIENTATION CONSUMER ===
    // rotate the map to match the provided orientation

    public boolean enableOrientation(IOrientationProvider orientationProvider) {
        if (orientationEnabled) {
            disableOrientation();
        }
        mOrientationProvider = orientationProvider;
        orientationEnabled = mOrientationProvider.startOrientationProvider(this);
        return orientationEnabled;
    }

    public void disableOrientation() {
        if (orientationEnabled) {
            mOrientationProvider.stopOrientationProvider();
            mOrientationProvider = null;
            orientationEnabled = false;
        }
    }

    @Override
    public void onOrientationChanged(float orientation, IOrientationProvider source) {
        super.setMapOrientation(0 - orientation);
    }

    // === CONSTRUCTOR BOILERPLATE ===

    public OrientableMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public OrientableMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs, boolean hardwareAccelerated) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs, hardwareAccelerated);
    }

    public OrientableMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrientableMapView(Context context) {
        super(context);
    }

    public OrientableMapView(Context context, MapTileProviderBase aTileProvider) {
        super(context, aTileProvider);
    }

    public OrientableMapView(Context context, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, aTileProvider, tileRequestCompleteHandler);
    }
}
