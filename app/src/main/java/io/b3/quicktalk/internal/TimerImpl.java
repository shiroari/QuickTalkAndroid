package io.b3.quicktalk.internal;

import android.os.Handler;

/**
 * @author shiroari
 * @since 6.08.2016
 */
public class TimerImpl implements Timer {

    private final Handler handler = new Handler();

    private final Runnable trigger = new Runnable() {
        @Override
        public void run() {
            TimerImpl.this.listener.timeout();
        }
    };

    private TimerListener listener;

    @Override
    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    @Override
    public void invalidate() {
        handler.removeCallbacks(trigger);
    }

    @Override
    public void timeout(double delay) {
        handler.postDelayed(trigger, (long) delay * 1000);
    }
}
