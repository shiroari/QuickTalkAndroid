package io.b3.quicktalk.internal;

/**
 * @author Stas Sukhanov
 * @since 6.08.2016
 */
public interface Timer {
    void invalidate();
    void timeout(double delay);
    void setListener(TimerListener listener);
}
