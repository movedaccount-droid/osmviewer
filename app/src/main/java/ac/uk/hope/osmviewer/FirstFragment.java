package ac.uk.hope.osmviewer;

import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.content.Context;
import android.content.DialogInterface;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import ac.uk.hope.osmviewer.databinding.FragmentFirstBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private static final String TAG = FirstFragment.class.getSimpleName();
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private ActivityResultLauncher<String[]> networkPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;
    private MapView map = null;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // register permissions callbacks
        locationPermissionLauncher = registerForActivityResult(
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

        networkPermissionLauncher = registerForActivityResult(
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

        storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            allowed -> {
                if (allowed) {
                    Log.d(TAG, "storage granted");
                } else {
                    Log.d(TAG, "no storage granted");
                }
            }
        );

        // load settings from sharedpreferences
        // TODO: should this be named as in 7 creating?
        Configuration.getInstance().load(requireActivity(),
                PreferenceManager.getDefaultSharedPreferences(requireActivity()));

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
        locationPermissionLauncher.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        networkPermissionLauncher.launch(new String[] {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        });

        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // show map
        map = (MapView) view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}