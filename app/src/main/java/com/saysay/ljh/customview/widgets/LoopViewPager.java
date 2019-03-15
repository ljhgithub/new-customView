package com.saysay.ljh.customview.widgets;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by ljh on 2015/9/10 0010.
 */
public class LoopViewPager extends ViewPager {
    private PointIndicator mPointIndicator;
    private OnLoopPageChangeListener mOnLoopPageChangeListener;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnLoopPageChangeListener(OnLoopPageChangeListener onLoopPageChangeListener) {
        this.mOnLoopPageChangeListener = onLoopPageChangeListener;
    }

    public void setIndicator(PointIndicator pointIndicator) {
        mPointIndicator = pointIndicator;


    }

    public void setIndicatorCount(int count) {
        if (null != mPointIndicator) {
            mPointIndicator.setIndicatorCount(count);
        }
    }

    private void init() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (null != mOnLoopPageChangeListener) {
                    mOnLoopPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (null != mPointIndicator) {
                    mPointIndicator.translationPoint(position);
                }
                if (null != mOnLoopPageChangeListener) {
                    mOnLoopPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (null != mOnLoopPageChangeListener) {
                    mOnLoopPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    public void startLoop(long delay, long period) {
        Timer timer = new Timer();
        WeakLoopTask task = new WeakLoopTask(timer, this);
        timer.schedule(task, delay, period);
    }


    protected static class WeakLoopTask extends TimerTask {

        private final Timer mTimer;
        private final WeakReference<ViewPager> mVp;

        public WeakLoopTask(Timer timer, ViewPager vp) {
            this.mTimer = timer;
            this.mVp = new WeakReference<>(vp);
        }

        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager vp = mVp.get();
                if (null != vp) {
                    PagerAdapter adapter = vp.getAdapter();
                    int count = adapter.getCount();
                    if (null != adapter && count > 0) {
                        vp.setCurrentItem((vp.getCurrentItem() + 1) % count, true);

                    }

                } else {
                    mTimer.cancel();
                }
            }
        };

        @Override
        public void run() {

            ViewPager vp = mVp.get();
            if (null != vp) {
                vp.post(mRunnable);
            } else {
                mTimer.cancel();
            }
        }
    }

    public interface OnLoopPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
}
