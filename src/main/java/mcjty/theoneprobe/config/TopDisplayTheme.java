package mcjty.theoneprobe.config;

import javax.annotation.Nullable;

public enum TopDisplayTheme {

    NULL(0,0, 0, 0),
    JADE(0xFF383838,0xFF242424, 1, 0);

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
