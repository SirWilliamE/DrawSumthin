package colachicco.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class DoodleView extends View {

    // used to determine if user moved finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap; // drawing area
    private Canvas bitmapCanvas; // used to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap on screen
    private final Paint paintLine; // used to draw lines on bitmap

    // Maps of current Paths being drawn and Points in those Paths
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();


    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
