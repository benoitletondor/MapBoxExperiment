package com.benoitletondor.mapboxexperiment.common.map;

/**
 * Immutable object that contains data about the current camera center location
 *
 * @author Benoit LETONDOR
 */
public final class CameraCenterLocation
{
    /**
     * The latitude of the camera center
     */
    private final double mLatitude;
    /**
     * The longitude of the camera center
     */
    private final double mLongitude;

// ------------------------------------>

    public CameraCenterLocation(double latitude, double longitude)
    {
        mLatitude = latitude;
        mLongitude = longitude;
    }

// ------------------------------------>

    /**
     * Get the latitude of the camera center location
     *
     * @return The latitude of the camera center
     */
    public double getLatitude()
    {
        return mLatitude;
    }

    /**
     * Get the longitude of the camera center location
     *
     * @return The longitude of the camera center
     */
    public double getLongitude()
    {
        return mLongitude;
    }

// ------------------------------------>

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CameraCenterLocation that = (CameraCenterLocation) o;

        return Double.compare(that.mLatitude, mLatitude) == 0 && Double.compare(that.mLongitude, mLongitude) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mLatitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
