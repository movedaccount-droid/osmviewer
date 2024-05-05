package ac.uk.hope.osmviewer;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import ac.uk.hope.osmviewer.MultiCompassOverlay.CompassMode;
import ac.uk.hope.osmviewer.MultiCompassOverlay.CompassModeFragment;
import ac.uk.hope.osmviewer.MultiCompassOverlay.CompassModeFragmentListener;
import ac.uk.hope.osmviewer.MultiCompassOverlay.TappableCompassListener;
import ac.uk.hope.osmviewer.MultiCompassOverlay.TappableCompassOverlay;
import ac.uk.hope.osmviewer.OrientableMapView.OrientableMapView;
import ac.uk.hope.osmviewer.databinding.FragmentFirstBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class FirstFragment
        extends Fragment
        implements CompassModeFragmentListener, TappableCompassListener {

    private FragmentFirstBinding binding;
    private static final String TAG = FirstFragment.class.getSimpleName();

    // permissions launchers
    private ActivityResultLauncher<String[]> mLocationPermissionLauncher;
    private ActivityResultLauncher<String[]> mNetworkPermissionLauncher;
    private ActivityResultLauncher<String> mStoragePermissionLauncher;

    // map components
    private OrientableMapView mMap;
    private CompassOverlay mCompassOverlay;
    private MyLocationNewOverlay mLocationOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private ScaleBarOverlay mScaleBarOverlay;

    // map parameters
    CompassMode mCompassMode;
    CompassMode mPreviousCompassMode;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        loadPreferences();

        // register permissions callbacks
        mLocationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    Log.d(TAG, "fine location granted");
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    Log.d(TAG, "coarse location granted");
                } else {
                    Log.d(TAG, "no location granted");
                }
            }
        );

        mNetworkPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean networkGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_NETWORK_STATE, false);
                Boolean internetGranted = result.getOrDefault(
                        Manifest.permission.INTERNET,false);
                if (networkGranted != null && networkGranted
                    && internetGranted != null && internetGranted) {
                    Log.d(TAG, "internet granted");
                } else {
                    Log.d(TAG, "no internet granted");
                }
            }
        );

        mStoragePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            allowed -> {
                if (allowed) {
                    Log.d(TAG, "storage granted");
                } else {
                    Log.d(TAG, "no storage granted");
                }
            }
        );

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // setup permissions for the map
        // TODO: follow best guidance in https://developer.android.com/training/permissions/requesting#java
        mLocationPermissionLauncher.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        mNetworkPermissionLauncher.launch(new String[] {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        });

        mStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // show map
        mMap = (OrientableMapView) view.findViewById(R.id.map);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        IMapController mapController = mMap.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        // add current location
        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireActivity()),mMap
        );
        mLocationOverlay.enableMyLocation();
        mMap.getOverlays().add(this.mLocationOverlay);

        // allow multi-touch rotation
        mRotationGestureOverlay = new RotationGestureOverlay(mMap);
        mRotationGestureOverlay.setEnabled(true);
        mMap.getOverlays().add(mRotationGestureOverlay);

        // add compass
        // TODO: should this be named as in 7 creating?
        mCompassOverlay = new TappableCompassOverlay(
                requireActivity(),
                new InternalCompassOrientationProvider(requireActivity()),
                this,
                mMap
        );
        mCompassOverlay.enableCompass();
        mMap.getOverlays().add(this.mCompassOverlay);

        // show a scale bar
        final DisplayMetrics dm = requireActivity().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mMap);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMap.getOverlays().add(this.mScaleBarOverlay);

        // update everything we just made to our saved values
        useCompassMode(mCompassMode);
    }

    // === COMPASS PRESS HANDLING ===

    public void onCompassTap() {
        mMap.setMapOrientation(0);
    }

    public void onCompassDoubleTap() {
        flipCompassModes();
    }

    public void onCompassLongPress() {
        selectCompassMode();
    }

    // === COMPASS MODE SELECTION ===

    private void flipCompassModes() {
        CompassMode tempCompassMode = mCompassMode;
        mCompassMode = mPreviousCompassMode;
        mPreviousCompassMode = tempCompassMode;
        useCompassMode(mCompassMode);
    }

    private void selectCompassMode() {
        CompassModeFragment.newInstance(mCompassMode, mPreviousCompassMode).show(
                getChildFragmentManager(), "something"
        );
    }

    public void onPreviewCompassMode(CompassMode mode) {
        useCompassMode(mode);
    }

    public void onCommitCompassModes(CompassMode currentMode, CompassMode previousMode) {
        mCompassMode = currentMode;
        mPreviousCompassMode = previousMode;
        useCompassMode(mCompassMode);
    }

    private void useCompassMode(CompassMode mode) {
        // TODO: compass mode should be saved in settings
        // enable or disable orientation producer/consumer pairing based on mode
        if (mode == CompassMode.MAP_FOLLOWS_COMPASS) {
            mMap.enableOrientation(
                    new InternalCompassOrientationProvider(requireActivity())
            );
            mRotationGestureOverlay.setEnabled(false);
        } else {
            mMap.disableOrientation();
            mRotationGestureOverlay.setEnabled(true);
        }

        if (mode == CompassMode.COMPASS_FOLLOWS_MAP) {
            mCompassOverlay.enableCompass(mMap);
        } else {
            mCompassOverlay.enableCompass(
                    new InternalCompassOrientationProvider(requireActivity())
            );
        }
    }

    // === PREFERENCES AND RESUME HANDLING ===

    private final String SAVED_MODE = "savedMode";
    private final String PREVIOUS_SAVED_MODE = "previousSavedMode";

    private void loadPreferences() {
        // TODO: should this be named as in 7 creating?
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        Configuration.getInstance().load(requireActivity(), prefs);
        mCompassMode = CompassMode.fromId(prefs.getInt(SAVED_MODE, 1));
        mPreviousCompassMode = CompassMode.fromId(prefs.getInt(PREVIOUS_SAVED_MODE, 2));
    }

    private void savePreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putInt(SAVED_MODE, mCompassMode.getId());
            prefsEditor.putInt(PREVIOUS_SAVED_MODE, mPreviousCompassMode.getId());
            prefsEditor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
        loadPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
        savePreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePreferences();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}