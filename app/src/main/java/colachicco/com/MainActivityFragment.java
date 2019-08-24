package colachicco.com;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivityFragment extends Fragment {
    private DoodleView doodleView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;

    private static final int ACCELERATION_THRESHOLD = 100000;

    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    // listen for sensor events
    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening(); // listen for device shake
    }

    private void enableAccelerometerListening() {
        // get SensorManager
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // register to listen for accelerometer events
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    // stop listening for accelerometer events
    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    private void disableAccelerometerListening() {
        // get SensorManager
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // stop listening for accelerometer events
        sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    // event handler for accelerometer events
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        // user accelerometer to check if user shook the device
        @Override
        public void onSensorChanged(SensorEvent event) {
            // make sure other dialogs are not displayed
            if (!dialogOnScreen) {
                // get X, Y, and Z values for the SensorEvent
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // save previous acceleration value
                lastAcceleration = currentAcceleration;

                // calculate current acceleration
                currentAcceleration = x * x + y * y + z * z;

                // calculate the change in acceleration
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                // if the acceleration is above the threshold, erase
                if (acceleration > ACCELERATION_THRESHOLD)
                    confirmErase();
            }
        }

        // method required by interface SensorEventListener
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    // confirm with user whether image should be erased
    private void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }

    // show the menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    // handle menu choice
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch for menu choice
        switch (item.getItemId()) {
            case R.id.color:
                ColorDialogFragment colorDialog = new ColorDialogFragment();
                colorDialog.show(getFragmentManager(), "color dialog");
                return true; // consume menu event
            case R.id.line_width:
                LineWidthDialogFragment widthDialog = new LineWidthDialogFragment();
                widthDialog.show(getFragmentManager(), "line width dialog");
                return true; // consume menu event
            case R.id.delete_drawing:
                confirmErase();
                return true; // consume menu event
            case R.id.save:
                saveImage();
                return true; // consume menu event
            case R.id.print:
                doodleView.printImage();
                return true; // consume menu event
        }

        return super.onOptionsItemSelected(item);
    }

}
