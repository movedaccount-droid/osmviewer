package ac.uk.hope.osmviewer;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static final String TAG = FirstFragment.class.getSimpleName();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // handle shared element transition
        setSharedElementEnterTransition(
            TransitionInflater
                .from(requireActivity())
                .inflateTransition(R.transition.change_bounds)
        );
        setSharedElementReturnTransition(
            TransitionInflater
                .from(requireActivity())
                .inflateTransition(R.transition.change_bounds)
        );

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrientableMapView innerMap = (OrientableMapView) view.findViewById(R.id.inner_map);

        binding.buttonFirst.setOnClickListener(v -> {
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(innerMap, ViewCompat.getTransitionName(innerMap))
                    .build();
            NavHostFragment.findNavController(FirstFragment.this).navigate(
                    R.id.action_FirstFragment_to_SecondFragment,
                    null,
                    null,
                    extras);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}