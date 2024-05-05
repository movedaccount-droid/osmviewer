package ac.uk.hope.osmviewer.MultiCompassOverlay;

public enum CompassMode {
    SHOW_NORTH(0),
    COMPASS_FOLLOWS_MAP(1),
    MAP_FOLLOWS_COMPASS(2);

    private int id;
    private CompassMode(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public static CompassMode fromId(int id) {
        for (CompassMode type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

}