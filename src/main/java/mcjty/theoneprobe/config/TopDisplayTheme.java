package mcjty.theoneprobe.config;

public enum TopDisplayTheme {

    VANILLA(0,0, 0, 0),
    JADE(0x88383838,0x88242424, 1, 0);

    TopDisplayTheme(int boxBorderColor, int boxFillColor, int boxThickness, int boxOffset) {
        this.displayBoxBorderColor = boxBorderColor;
        this.displayBoxFillColor = boxFillColor;
        this.displayBoxThickness = boxThickness;
        this.displayBoxOffset = boxOffset;
    }

    public final int displayBoxBorderColor;
    public final int displayBoxFillColor;
    public final int displayBoxThickness;
    public final int displayBoxOffset;
}
