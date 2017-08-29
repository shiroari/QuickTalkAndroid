package io.b3.quicktalk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import io.b3.quicktalk.AppContext;
import io.b3.quicktalk.AppModule;
import io.b3.quicktalk.R;
import io.b3.quicktalk.config.ConfigService;
import io.b3.quicktalk.engine.CardManager;
import io.b3.quicktalk.engine.Tutor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * TODO:
 * - Butterknife
 */
public class CardViewActivity extends AppCompatActivity implements Observer {

    @Inject
    Tutor tutor;

    @Inject
    CardManager manager;

    @Inject
    ConfigService config;

    private boolean firstStart = true;
    private boolean hold;

    private View mContentView;

    private TextView output;
    private TextView status;
    private TextView header;
    private ImageView playIcon;
    private ImageView soundIcon;

    private GestureDetectorCompat mDetector;

    private static final String[] MENU_ITEMS = new String[]{
            "Select", "Random", "Restart", "Settings"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getDelegate().requestWindowFeature(AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY);
        getDelegate().requestWindowFeature(AppCompatDelegate.FEATURE_ACTION_MODE_OVERLAY);

        super.onCreate(savedInstanceState);

        Log.d("Main", "onCreate");

        AppContext.initContext(getApplicationContext());
        AppContext.addModule(new AppModule());
        AppContext.inject(this);

        config.addObserver(this);
        manager.addObserver(this);

        setContentView(R.layout.activity_card_view);

        mContentView = findViewById(R.id.content);

        output = (TextView) findViewById(R.id.output);
        header = (TextView) findViewById(R.id.header);
        status = (TextView) findViewById(R.id.status);
        playIcon = (ImageView) findViewById(R.id.playIcon);
        soundIcon = (ImageView) findViewById(R.id.soundIcon);

        mDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                replayStep();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("g", "confirm");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent event) {
                showInfo();
            }

            @Override
            public boolean onFling(MotionEvent startEvent, MotionEvent endEvent, float velocityX, float velocityY) {
                float dx = endEvent.getX() - startEvent.getX();
                float dy = endEvent.getY() - startEvent.getY();
                if (Math.abs(dx) > 50.0f && Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        previousStep();
                    } else {
                        nextStep();
                    }
                } else if (dy < -50.0f && Math.abs(dx) < Math.abs(dy)) {
                    openMenu();
                }
                return true;
            }

        });

        if (firstStart) {
            firstStart = false;
            tutor.start();
        } else {
            tutor.resume();
        }

        enterFullScreen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        enterFullScreen();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            hideInfo();
        }
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void openMenu() {

        tutor.stop();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Menu");

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterFullScreen();
            }
        });

        builder.setItems(MENU_ITEMS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                dispatchMenuAction(index);
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        int index = 0;
        for (String item: MENU_ITEMS) {
            menu.add(0, 0, index++, item);
        }
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        Log.d("menu", "closed");
        enterFullScreen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        dispatchMenuAction(item.getOrder());
        return true;
    }

    private void enterFullScreen() {
        mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_VISIBLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void dispatchMenuAction(int index) {
        switch (index) {
            case 0:
                selectDialog();
                break;
            case 1:
                randomDialog();
                enterFullScreen();
                break;
            case 2:
                restartDialog();
                enterFullScreen();
                break;
            case 3:
                openSettings();
                break;
        }
    }

    private void selectDialog() {
        tutor.stop();
        startActivity(new Intent(this, SelectActivity.class));
    }

    private void restartDialog() {
        tutor.restart();
    }

    private void randomDialog() {
        tutor.startRandom();
    }

    private void previousStep() {
        tutor.previousStep();
    }

    private void nextStep() {
        tutor.nextStep();
    }

    private void replayStep() {
        if (hold) {
            hold = false;
        }
        if (config.isEnabledAutoPlay()) {
            if (config.isPaused()) {
                tutor.resume();
            } else {
                tutor.stop();
            }
        } else {
            tutor.replayStep();
        }
    }

    private void openSettings() {
        tutor.stop();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void showInfo() {
        hold = true;
        update();
    }

    private void hideInfo() {
        hold = false;
        update();
    }

    private void updateView() {
        switch (manager.getState()) {
            case Title:
            case Front:
                output.setTextColor(Color.BLACK);
                output.setText(manager.getText());
                break;
            case Back:
                output.setTextColor(Color.argb(120, 0, 0, 0));
                output.setText(manager.getText());
                break;
        }
    }

    private void updateOverlay() {
        status.setText(String.format("%s/%s", manager.getCurrent() + 1, manager.getCount()));
        if (hold) {
            if (manager.getState() != CardManager.CardState.Title) {
                header.setText(manager.getTitle());
            }
        } else {
            header.setText("");
        }
    }

    private void updateSettings() {
        playIcon.setImageResource(
                config.isPaused()
                        ? R.drawable.paused
                        : R.drawable.play);
        playIcon.setVisibility(
                config.isEnabledAutoPlay()
                        ? ImageView.VISIBLE
                        : ImageView.INVISIBLE);
        soundIcon.setVisibility(
                config.isEnabledTts()
                        ? ImageView.VISIBLE
                        : ImageView.INVISIBLE);
    }

    @Override
    public void update(Observable observable, Object o) {
        update();
    }

    private void update() {
        getSupportActionBar().setTitle(manager.getTitle());
        disableAutoLock(config.isPaused()
                && config.isEnabledAutoPlay()
                && config.isDisabledAutoLock());
        updateSettings();
        updateOverlay();
        updateView();
    }

    public void disableAutoLock(boolean disabled) {
        // TODO: disable autolock
    }


//    /**
//     * Some older devices needs a small delay between UI widget updates
//     * and a change of the status and navigation bar.
//     */
//    private static final int UI_ANIMATION_DELAY = 300;
//    private final Handler mHideHandler = new Handler();

//    private void delayedAction(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }

}
