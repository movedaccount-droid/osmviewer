package ac.uk.hope.osmviewer.MultiCompassOverlay;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ac.uk.hope.osmviewer.R;

public class CompassModeFragment extends DialogFragment {

    private static final String SAVED_MODE = "savedMode";
    private static final String PREVIOUS_SAVED_MODE = "previousSavedMode";
    private CompassMode mPreviewedMode;
    private CompassMode mSavedMode;
    private CompassMode mPreviousSavedMode;
    private CompassModeFragmentListener mListener;

    public CompassModeFragment() {
        // Required empty public constructor
    }

    public static CompassModeFragment newInstance(CompassMode savedMode,
                                                  CompassMode previousSavedMode) {
        CompassModeFragment fragment = new CompassModeFragment();

        // we take control of the old modes just to separate the logic from the main fragment
        Bundle args = new Bundle();
        args.putSerializable(SAVED_MODE, savedMode);
        args.putSerializable(PREVIOUS_SAVED_MODE, previousSavedMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (CompassModeFragmentListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("provided context to CompassModeFragment was not valid listener");
        }
        if (getArguments() != null) {
            mSavedMode = (CompassMode) getArguments().get(SAVED_MODE);
            mPreviousSavedMode = (CompassMode) getArguments().get(PREVIOUS_SAVED_MODE);
        } else {
            mSavedMode = CompassMode.SHOW_NORTH;
            mPreviousSavedMode = CompassMode.COMPASS_FOLLOWS_MAP;
        }
        mPreviewedMode = mSavedMode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] modes = {"Show north", "Compass follows map", "Map follows compass"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Compass Mode")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // only change the saved modes if we've made a change
                    if (CompassMode.fromId(which) != mSavedMode) {
                        mListener.onCommitCompassModes(mPreviewedMode, mSavedMode);
                    } else {
                        mListener.onCommitCompassModes(mSavedMode, mPreviousSavedMode);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    mListener.onCommitCompassModes(mSavedMode, mPreviousSavedMode);
                })
                .setSingleChoiceItems(modes, mSavedMode.getId(), (dialog, which) -> {
                    mPreviewedMode = CompassMode.fromId(which);
                    mListener.onPreviewCompassMode(mPreviewedMode);
                });
        return builder.create();
    }
}

