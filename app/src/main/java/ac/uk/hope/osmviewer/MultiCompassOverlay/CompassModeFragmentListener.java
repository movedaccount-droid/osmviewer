package ac.uk.hope.osmviewer.MultiCompassOverlay;

public interface CompassModeFragmentListener {
    void onPreviewCompassMode(CompassMode mode);
    void onCommitCompassModes(CompassMode newMode, CompassMode oldMode);
}
