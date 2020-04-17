package de.dennisguse.opentracks.content.sensor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import de.dennisguse.opentracks.util.UintUtils;
import de.dennisguse.opentracks.util.UnitConversions;

/**
 * Provides cadence in rpm and speed in milliseconds from Bluetooth LE Cycling Cadence and Speed sensors.
 * <p>
 * https://www.bluetooth.org/docman/handlers/downloaddoc.ashx?doc_id=261449
 */
public final class SensorDataCycling {

    private static final String TAG = SensorDataCycling.class.getSimpleName();

    private static final int INVALID_VALUE_INT = -1;
    private static final float INVALID_VALUE_FLOAT = Float.NaN;

    private SensorDataCycling() {
    }

    public static class Cadence extends SensorData {

        private long crankRevolutionsCount; // UINT32
        private int crankRevolutionsTime; // UINT16; 1/1024s
        private float cadence_rpm = INVALID_VALUE_FLOAT;

        public Cadence(String sensorAddress, String sensorName, long crankRevolutionsCount, int crankRevolutionsTime) {
            super(sensorAddress, sensorName);
            this.crankRevolutionsCount = crankRevolutionsCount;
            this.crankRevolutionsTime = crankRevolutionsTime;
        }

        /**
         * Workaround for Wahoo CADENCE: provides speed instead of cadence
         */
        public Cadence(@NonNull SensorDataCycling.Speed speed) {
            this(speed.getSensorAddress(), speed.getSensorName(), speed.getWheelRevolutionsCount(), speed.getWheelRevolutionsTime());
        }

        @VisibleForTesting
        public Cadence(long crankRevolutionsCount, int crankRevolutionsTime) {
            super("sensorAddress", "sensorName");
            this.crankRevolutionsCount = crankRevolutionsCount;
            this.crankRevolutionsTime = crankRevolutionsTime;
        }

        public boolean hasData() {
            return crankRevolutionsCount != INVALID_VALUE_INT && crankRevolutionsTime != INVALID_VALUE_INT;
        }

        public long getCrankRevolutionsCount() {
            return crankRevolutionsCount;
        }

        public int getCrankRevolutionsTime() {
            return crankRevolutionsTime;
        }

        public boolean hasCadence_rpm() {
            return !Float.isNaN(cadence_rpm);
        }

        public float getCadence_rpm() {
            return cadence_rpm;
        }

        public void compute(Cadence previous) {
            if (hasData() && previous != null && previous.hasData()) {
                Log.e(TAG, previous.getCrankRevolutionsTime() + " " + previous.getCrankRevolutionsCount() + " - " + this.getCrankRevolutionsTime() + " " + getCrankRevolutionsCount()); //TODO REMOVE
                long timeDiff_ms = UintUtils.diff(crankRevolutionsTime, previous.crankRevolutionsTime, UintUtils.UINT16_MAX) * 1024 / UnitConversions.S_TO_MS;
                if (timeDiff_ms <= 0) {
                    Log.e(TAG, "Timestamps difference is invalid: cannot compute cadence.");
                    cadence_rpm = INVALID_VALUE_FLOAT;
                } else {
                    long crankDiff = UintUtils.diff(crankRevolutionsCount, previous.crankRevolutionsCount, UintUtils.UINT32_MAX);
                    float cadence_ms = crankDiff / (float) timeDiff_ms;
                    cadence_rpm = (float) (cadence_ms / UnitConversions.MS_TO_S / UnitConversions.S_TO_MIN);
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "cadence=" + getCadence_rpm() + "_" + getCrankRevolutionsTime();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Cadence)) return false;

            Cadence comp = (Cadence) obj;
            return getCrankRevolutionsCount() == comp.getCrankRevolutionsCount() && getCrankRevolutionsTime() == comp.getCrankRevolutionsTime();
        }
    }

    //TODO Speed computation; needs wheel diameter / circumference
    public static class Speed extends SensorData {

        private int wheelRevolutionsCount; // UINT16
        private int wheelRevolutionsTime; // UINT16; 1/1024s
        private float speed_ms = INVALID_VALUE_FLOAT;

        public Speed(String sensorAddress, String sensorName, int wheelRevolutionsCount, int wheelRevolutionsTime) {
            super(sensorAddress, sensorName);
            this.wheelRevolutionsCount = wheelRevolutionsCount;
            this.wheelRevolutionsTime = wheelRevolutionsTime;
        }

        public boolean hasData() {
            return wheelRevolutionsCount != INVALID_VALUE_INT && wheelRevolutionsTime != INVALID_VALUE_INT;
        }

        public int getWheelRevolutionsCount() {
            return wheelRevolutionsCount;
        }

        public int getWheelRevolutionsTime() {
            return wheelRevolutionsTime;
        }

        public boolean hasSpeed() {
            return !Float.isNaN(speed_ms);
        }

        public float getSpeed_ms() {
            return speed_ms;
        }

        public void compute(Speed previous) {
            if (hasData() && previous != null && previous.hasData()) {
                long timeDiff_ms = UintUtils.diff(wheelRevolutionsTime, previous.wheelRevolutionsTime, UintUtils.UINT16_MAX) * 1024 / UnitConversions.S_TO_MS;
                if (timeDiff_ms <= 0) {
                    Log.e(TAG, "Timestamps difference is invalid: cannot compute cadence.");
                    speed_ms = INVALID_VALUE_FLOAT;
                } else {
                    long crankDiff = UintUtils.diff(wheelRevolutionsCount, previous.wheelRevolutionsCount, UintUtils.UINT32_MAX);
                    speed_ms = crankDiff / (float) timeDiff_ms;
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "speed=" + getSpeed_ms() + "_" + getWheelRevolutionsTime();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Speed)) return false;

            Speed comp = (Speed) obj;
            return getWheelRevolutionsCount() == comp.getWheelRevolutionsCount() && getWheelRevolutionsTime() == comp.getWheelRevolutionsTime();
        }
    }

    public static class CadenceAndSpeed extends SensorData {

        private Cadence cadence;
        private Speed speed;

        public CadenceAndSpeed(String sensorAddress, String sensorName, @NonNull Cadence cadence, @NonNull Speed speed) {
            super(sensorAddress, sensorName);
            this.cadence = cadence;
            this.speed = speed;
        }

        public Cadence getCadence() {
            return cadence;
        }

        public Speed getSpeed() {
            return speed;
        }
    }
}

