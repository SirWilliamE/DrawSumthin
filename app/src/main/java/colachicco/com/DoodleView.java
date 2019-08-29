package colachicco.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
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

    // DoodleView constructor initializes DoodleView
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs); // pass context to View's constructor
        paintScreen = new Paint(); // used to display bitmap on screen

        // set initial display settings for painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // set black as default color
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(5); // default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
    }

    // creates Bitmap and Canvas based on View's size
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    // erase the drawing
    public void clear() {
        pathMap.clear(); // remove all paths
        previousPointMap.clear(); // remove all points
        bitmap.eraseColor(Color.WHITE); // clear the bitmap
        invalidate(); // refresh the screen
    }

    // set the line color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the line color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    // set the line width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the line width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    // perform custom drawing when DoodleView is refreshed on the screen
    @Override
    protected void onDraw(Canvas canvas) {
        // draw the background screen
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        // for each path currently being drawn
        for (Integer key : pathMap.keySet())
            canvas.drawPath(pathMap.get(key), paintLine); // draw line
    }

    // handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer ie: finger

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            touchMoved(event);
        }

        invalidate(); // redraw
        return true;
    }

    // called when user touches the screen
    private void touchStarted(float x, float y, int lineID) {
        Path path; // the path for the given touch ID
        Point point; // the last point in the path

        // if there is already a path for the lineID
        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID); // get the path
            path.reset(); // reset the path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point
        }
        else {
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
        }

        // move the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    // called when user drags finger on screen
    private void touchMoved(MotionEvent event) {
        // for each of the pointers in the given MotionEvent
        for (int i = 0; i< event.getPointerCount(); i++) {
            // get the pointer ID and pointer index
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                //get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point of this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the touch moved from last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }



}
