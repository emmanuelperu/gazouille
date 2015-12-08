package fr.manumehdi.gazouille.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import fr.manumehdi.gazouille.GazouilleApplication;
import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.di.DataComponent;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * Created by mehdi on 19/11/2015.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponent(GazouilleApplication.dataComponent);
    }

    public void startActivityWithRevealAnimation(final Intent intent, @ColorInt int color, final View view) {
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View decorChild = decor.getChildAt(0);

        if (decorChild != null) {
            //decor.removeView(decorChild)
        }

        final RevealFrameLayout revealContainer = new RevealFrameLayout(this);
        final View revealColorView = new View(this);
        revealColorView.setBackground(new ColorDrawable(color));
        revealColorView.setAlpha(0f);
        revealContainer.addView(revealColorView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //revealContainer.addView(decorChild)
        decor.addView(revealContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        revealColorView.post(new Runnable() {
            @Override
            public void run() {
                Integer cx, cy;
                if (view != null) {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    cx = rect.left + view.getWidth() / 2;
                    cy = rect.top + view.getHeight() / 2;
                } else {
                    cx = revealContainer.getWidth() / 2;
                    cy = revealContainer.getHeight() / 2;
                }

                revealContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                SupportAnimator animator = ViewAnimationUtils.createCircularReveal(revealColorView, cx, cy, 0f,
                        Math.max(revealContainer.getHeight(),
                                revealContainer.getWidth()));
                // FIXME extraire
                animator.setDuration(600);
                animator.setInterpolator(new FastOutSlowInInterpolator());
                animator.addListener(new SupportAnimator.SimpleAnimatorListener() {

                    @Override
                    public void onAnimationStart() {
                        revealColorView.setAlpha(1f);
                    }

                    @Override
                    public void onAnimationEnd() {
                        revealContainer.setLayerType(View.LAYER_TYPE_NONE, null);
                        revealColorView.animate().setStartDelay(400).alpha(0f).start();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        // FIXME
                        /*if (onFinish != null) {
                            onFinish();
                        }*/
                    }
                });
                animator.start();
            }
        });

    }


    public void hypo(Integer a, Integer b) {
        // Float f = (Math.pow(a.toDouble(), 2.toDouble()) + Math.pow(b.toDouble(), 2.toDouble())).toFloat()
    }

    public abstract void initComponent(DataComponent dataComponent);
}
