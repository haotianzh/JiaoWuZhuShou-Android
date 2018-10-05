package com.haotian.myview;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.haotian.loginapp.R;

/**
 * Created by HAOTIAN on 2016/3/13.
 */
public class LoadingDialog {
    private Context context;
    private PopupWindow popupDialog;
    private LayoutInflater layoutInflater;
    private RelativeLayout layout;
    private final ThreadLocal<RelativeLayout> layout_bg = new ThreadLocal<>();
    private View circleView;
    private RotateAnimation rotateAnim;
    private AlphaAnimation alphaAnim_in;
    private AlphaAnimation alphaAnim_out;
    public LoadingDialog(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }
    private void initAnim() {
        rotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(2000);
        rotateAnim.setRepeatMode(Animation.RESTART);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setInterpolator(new LinearInterpolator());
        alphaAnim_in = new AlphaAnimation(0f, 1f);
        alphaAnim_in.setFillAfter(true);
        alphaAnim_in.setDuration(200);
        alphaAnim_in.setInterpolator(new LinearInterpolator());
        alphaAnim_out = new AlphaAnimation(1f, 0f);
        alphaAnim_out.setFillAfter(true);
        alphaAnim_out.setDuration(100);
        alphaAnim_out.setInterpolator(new LinearInterpolator());
        alphaAnim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                dismiss();
            }
        });
    }
    /**
     * 判断是否显示
     * @return
     */
    public boolean isShowing() {
        if (popupDialog != null && popupDialog.isShowing()) {
            return true;
        }
        return false;
    }
    /**
     * 显示
     */
    public void show() {
        dismiss();
        initAnim();
        layout = (RelativeLayout) layoutInflater.inflate(R.layout.mypopwindow, null);
        circleView = (View) layout.findViewById(R.id.loading_dialog);
        layout_bg.set((RelativeLayout) layout.findViewById(R.id.relativelayout2));
        popupDialog = new PopupWindow(layout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        View parentView = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        popupDialog.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        layout_bg.get().startAnimation(alphaAnim_in);
        circleView.startAnimation(rotateAnim);
    }
    /**
     * 隐藏
     */
    public void dismiss() {
        if (popupDialog != null && popupDialog.isShowing()) {
            layout_bg.get().clearAnimation();
            circleView.clearAnimation();
            popupDialog.dismiss();
        }
    }
}