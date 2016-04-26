package sendingprogressview.com.sendingprogressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    boolean showLoadingView;
    private int loadingViewSize = Utils.dpToPx(200);
    SendingProgressView sendingProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);

        final View bgView = new View(this);
        bgView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        bgView.setBackgroundColor(0x77ffffff);
        frame.addView(bgView);
        bgView.setAlpha(0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(loadingViewSize, loadingViewSize);
        params.gravity = Gravity.CENTER;
        sendingProgressView = new SendingProgressView(this);
        sendingProgressView.setLayoutParams(params);
        frame.addView(sendingProgressView);

        sendingProgressView.setOnLoadingFinishedListener(new SendingProgressView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                sendingProgressView.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                bgView.animate().alpha(0.f).setDuration(200).setStartDelay(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Timber.e("animation ended");

                                showLoadingView = false;
                            }
                        })
                        .start();
            }
        });



        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("animation start");

                sendingProgressView.changeState(SendingProgressView.STATE_PROGRESS_STARTED);
                runThread();
                sendingProgressView.setScaleX(1);
                sendingProgressView.setScaleY(1);
                bgView.setAlpha(1);
            }
        });
    }

    int i = 0;

    private void runThread() {

        new Thread() {
            public void run() {
                while (i < 100) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendingProgressView.setCurrentProgress(i);
                            }
                        });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i += 20;
                }
                i = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendingProgressView.changeState(SendingProgressView.STATE_DONE_STARTED);

                    }
                });
            }
        }.start();
    }
}
