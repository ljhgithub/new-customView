package com.saysay.ljh.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.saysay.ljh.customview.R;
import com.saysay.ljh.customview.widgets.PullOutLayout;

/**
 * Created by ljh on 2015/9/14 0014.
 */
public class GuideDialog extends Dialog implements View.OnClickListener {
    private View optionView;
    private PullOutLayout mPullOut;
    public static final int PADDING = 10;
    private ImageView ivGuide;
    private OptionListener optionListener;

    public GuideDialog(Context context) {
        super(context, R.style.GuideDialog);
        setContentView(R.layout.guide_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPullOut = (PullOutLayout) findViewById(R.id.pol_root);
        mPullOut.setOnClickListener(this);
        optionView = new View(context);
        optionView.setId(R.id.option);
        ivGuide = new ImageView(context);
        ivGuide.setId(R.id.i_known);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("tag", "onDismiss");
            }
        });

    }

    public void setOptionListener(OptionListener listener) {
        optionListener = listener;
    }

    public void setGuide(int left, int top, int width, int height, int id) {
        ivGuide.setImageResource(id);
        mPullOut.addView(ivGuide);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.setMargins(left, top, 0, 0);
        ivGuide.setLayoutParams(lp);
        ivGuide.setScaleType(ImageView.ScaleType.FIT_XY);
        ivGuide.setOnClickListener(this);
    }

    public void setRectLocation(float left, float top, float right, float bottom) {
        mPullOut.setRectLocation(left, top, right, bottom);
        addOptionView((int) left, (int) top, (int) (right - left), (int) (bottom - top));
    }

    public void setCircleLocation(int x, int y, int radius) {
        mPullOut.setCircleLocation(x, y, radius);
        addOptionView(x-radius-PADDING,y-radius-PADDING, 2*radius+PADDING, 2*radius+PADDING);
    }

    public void setRoundRectLocation(float left, float top, float right, float bottom, int radius) {
        mPullOut.setRoundRectLocation(left, top, right, bottom, radius);
        addOptionView((int) left, (int) top, (int) (right - left), (int) (bottom - top));
    }

    private void addOptionView(int left, int top, int width, int height) {
        mPullOut.addView(optionView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width + PADDING, height + PADDING);
        lp.setMargins(left - PADDING, top - PADDING, 0, 0);
        optionView.setLayoutParams(lp);
        optionView.setBackgroundColor(Color.TRANSPARENT);
        optionView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.option:
                if (null != optionListener) {
                    optionListener.onOption();
                }
                Log.d("tag", "option");
                break;
            case R.id.i_known:
                Log.d("tag", "i_known");
                if (null != optionListener) {
                    optionListener.onKnown();
                }
                break;
            case R.id.pol_root:
                if (null != optionListener) {
                    optionListener.onEmpty();
                }
                Log.d("tag", "empty");
                break;
        }
        this.dismiss();
    }

    public interface OptionListener {
        void onOption();

        void onKnown();

        void onEmpty();
    }
}
