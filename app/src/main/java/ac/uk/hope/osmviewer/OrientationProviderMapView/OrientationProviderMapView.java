package ac.uk.hope.osmviewer.OrientationProviderMapView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;

public class OrientationProviderMapView extends MapView implements IOrientationProvider {

    // we do not want to take control of the mapview through the interface,
    // so we perform read-only operations
    private IOrientationConsumer mOrientationConsumer;
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
    public OrientationProviderMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public OrientationProviderMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs, boolean hardwareAccelerated) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs, hardwareAccelerated);
    }

    public OrientationProviderMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrientationProviderMapView(Context context) {
        super(context);
    }

    public OrientationProviderMapView(Context context, MapTileProviderBase aTileProvider) {
        super(context, aTileProvider);
    }

    public OrientationProviderMapView(Context context, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, aTileProvider, tileRequestCompleteHandler);
    }


}
