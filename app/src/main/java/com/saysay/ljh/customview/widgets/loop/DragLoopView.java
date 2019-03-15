package com.saysay.ljh.customview.widgets.loop;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 左右循环翻页自定义控件
 *
 * @author moujunfeng
 * @date 2018/6/13 11:04
 */
public class DragLoopView extends ViewGroup implements View.OnTouchListener {
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int ANIMATE_TO_START_DURATION = 200;
    //设置每个视图的距离
    private int SPACE = 0;
    //最上面的view
    private View topView;
    //最上面view的矩阵
    private Rect topRectF;
    private Rect curTopRect = new Rect();
    //设置子控件添加删除布局的动画
    LayoutTransition mLayoutTransition;
    //设置显示的个数
    private int showNum = 3;
    private AdapterDataSetObserver adapterDataSetObserver;
    private List<View> listData = new ArrayList<View>();

    private List<ViewTag> viewTags = new ArrayList<>();
    private int firstChildWidth = 0;
    private int firstChildHeight = 0;

    private boolean mIsBeingDragged;
    private float mTotalDragDistance = -1;

    public DragLoopView(Context context) {
        this(context, null);

    }

    private BaseDragLoopAdapter mAdapter;

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setAdapter(BaseDragLoopAdapter adapter) {
        if (null != mAdapter) {
            mAdapter.registerDataSetObserver(adapterDataSetObserver);
        }
        this.mAdapter = adapter;
        adapterDataSetObserver = new AdapterDataSetObserver();
        mAdapter.registerDataSetObserver(adapterDataSetObserver);
        initAdapterView();
    }

    public BaseDragLoopAdapter getAdapter() {
        return mAdapter;
    }


    public int getShowNum() {
        return showNum;
    }

    public DragLoopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        init();
    }

    private void init() {
        mLayoutTransition = new LayoutTransition();
        setLayoutTransition(mLayoutTransition);
        setOnTouchListener(this);

    }


    public void initAdapterView() {
        removeAllViews();
        if (null == mAdapter || mAdapter.isEmpty()) return;
        listData.clear();
        viewTags.clear();
        int count = mAdapter.getCount();
        View view;
        int showIndex;
        int minCount = Math.min(count, showNum);
        for (int i = 0; i < minCount; i++) {
            viewTags.add(i, new ViewTag(i, i));
            view = mAdapter.onCreateView(i, this);
            listData.add(view);
        }


        LayoutParams layoutParams;
        for (int i = 0; i < minCount; i++) {
            showIndex = minCount - 1 - i;
            view = listData.get(showIndex);
            layoutParams = view.getLayoutParams();
            addViewInLayout(view, i, layoutParams == null ? generateDefaultLayoutParams() : layoutParams);

        }
        requestLayout();
    }

    private View getRecycler(int position) {

        if (listData.size() > position) {
            return listData.get(position);
        }
        return null;
    }

    private void setData(List<View> views) {
        if (null == views || views.isEmpty()) return;
        listData.clear();
        listData.addAll(views);

        viewTags = new ArrayList<>();
        int count = listData.size();
        View view;
        int showIndex;
        for (int i = 0; i < count; i++) {
            view = listData.get(i);
            viewTags.add(i, new ViewTag(view, i));


        }
        for (int i = 0; i < count && i < showNum; i++) {
            showIndex = count - 1 - i;
            view = listData.get(showIndex);
            addView(view, i);

        }
    }


    private void moveToOffsetLeftAndRight(int position) {
        int offset = position - topView.getLeft();
        ViewCompat.offsetLeftAndRight(topView, offset);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("LoopPagerView", "onLayout:");
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ViewTag viewTag = viewTags.get(i);
            View view = viewTag.getView();
            mAdapter.onBindView(viewTag.getPosition(), view, this);
            viewTag.getRect().set(l + viewTag.getOffsetWidth(), t + viewTag.getOffsetHeight(), l + viewTag.getWidth() + viewTag.getOffsetWidth(), t + viewTag.getHeight() + viewTag.getOffsetHeight());
            view.layout(viewTag.getRect().left, viewTag.getRect().top, viewTag.getRect().right, viewTag.getRect().bottom);

            Log.e("LoopPagerView", i + " = " + viewTag.getRect().toString() + view.toString() + view.getLeft());
//            Log.e("onLayout", i + " = " + viewTag.getRect().toString());
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("LoopPagerView", "onMeasure" + getChildCount());
        //获得控件的宽高
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        int firstChildWidth = 0;
        int firstChildHeight = 0;
        int flexibleWidth = 0;
        ViewTag viewTag;
        for (int i = 0; i < count; i++) {
            viewTag = viewTags.get(i);
            View childView = viewTag.getView();//最后一个view,做为第一个看板

            if (i == 0) {
                childMeasure(childView, 0, widthMeasureSpec, heightMeasureSpec);
                firstChildWidth = childView.getMeasuredWidth();
                firstChildHeight = childView.getMeasuredHeight();

                flexibleWidth = (width - firstChildWidth) / 3;
                mTotalDragDistance = firstChildWidth * 2 / 5;
                viewTag.setTag(0);
                viewTag.setSize(firstChildWidth, firstChildHeight);
                viewTag.setOffset(0, 0);
//                Log.e("LooperPagerView", i + ",width otherWidth =" + firstChildWidth + " height =" + firstChildHeight);
            } else {
                int otherWidth = (int) (firstChildWidth * Math.pow(0.8, i));
                int otherHeight = (int) (firstChildHeight * Math.pow(0.8, i));
                childMeasure(childView, 0, MeasureSpec.makeMeasureSpec(otherWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(otherHeight, MeasureSpec.EXACTLY));


//                Log.e("LooperPagerView", i + ",width otherWidth =" + otherWidth + " height =" + otherHeight);

                viewTag.setTag(i);
                viewTag.setSize(otherWidth, otherWidth);
                viewTag.setOffset(firstChildWidth - otherWidth + flexibleWidth * i, (firstChildHeight - otherWidth) / 2);
            }
        }

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                firstChildHeight);


    }

    private void childMeasure(View childView, int usedSize, int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams = childView.getLayoutParams();
        childView.measure(resolveSize(usedSize, widthMeasureSpec, layoutParams.width), resolveSize(usedSize, heightMeasureSpec, layoutParams.height));
//        Log.e("tag", "childMeasure" + childView.getMeasuredWidth() + "  " + childView.getMeasuredHeight());

    }

    /**
     * @param usedSize    已经被使用的空间
     * @param measureSpec
     * @param lpSize      子view LayoutParams获取的height或者width
     * @return
     */
    private int resolveSize(int usedSize, int measureSpec, int lpSize) {

        int selfSpecMode = MeasureSpec.getMode(measureSpec);
        int selfSpecSize = MeasureSpec.getSize(measureSpec);
        int childMeasureSpec;

        switch (lpSize) {
            case LayoutParams.MATCH_PARENT:
                //子类view要求填充父类view空间,父类mode为EXACTLY或AT_MOST
                if (selfSpecMode == MeasureSpec.EXACTLY || selfSpecMode == MeasureSpec.AT_MOST) {
                    //此时子view的可用空间是父类view measureSpec中的size
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(selfSpecSize, MeasureSpec.EXACTLY);

                } else {
                    //父类mode为UNSPECIFIED，无法满足子view的MATCH_PARENT，子view mode 设置UNSPECIFIED，size写0即可
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }

                break;
            case LayoutParams.WRAP_CONTENT:
                //父类mode为EXACTLY或AT_MOST
                if (selfSpecMode == MeasureSpec.EXACTLY || selfSpecMode == MeasureSpec.AT_MOST) {
                    //给子view 的mode为AT_MOST，给size设置一个最大值让其自己测量
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(selfSpecSize - usedSize, MeasureSpec.AT_MOST);
                } else {
                    //父类mode为UNSPECIFIED 对子view 不做限制
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }
                break;

            default: //子view size有具体值

                childMeasureSpec = MeasureSpec.makeMeasureSpec(lpSize, MeasureSpec.EXACTLY);

                break;
        }

        return childMeasureSpec;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    int beforeX = 0;
    int beforeY = 0;

    int differx;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e("tag","MotionEvent"+isLayout);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下y
                beforeX = (int) ev.getX();
                Log.e("MotionEvent", "onInterceptTouchEvent ACTION_DOWN" + beforeX);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE://移动
                mIsBeingDragged = true;
                Log.e("MotionEvent", "onInterceptTouchEvent ACTION_MOVE");
//                Log.e("MotionEvent", "ACTION_MOVE");
//                int new_x = (int) ev.getX();
//                int new_y = (int) ev.getY();
//                //判断有水平滑动的意向
//                int move_x = Math.abs(new_x - beforeX);//x轴滑动的距离
//                int move_y = Math.abs(new_y - beforeY);//y轴滑动的距离
//                //10的偏移量
//                if (move_x > move_y) {
//                    //请求父类不要拦截
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    return true;//传递给字View
//                } else {//下面这句 应该不会执行 但是保险起见还是放在这里
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                    return false;
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e("MotionEvent", "onInterceptTouchEvent ACTION_UP");
                mIsBeingDragged = false;
//                if (topRectF.contains((int) ev.getX(), (int) ev.getY())) {
//                    return false;
//                }
                break;
        }
        return mIsBeingDragged;

    }

    ValueAnimator offsetOriginPositionAnimator;

    private void animateOffsetToStartPosition(Animator.AnimatorListener listener) {


        int originPosition = topRectF.left;
        final int currentPosition = curTopRect.left;

        final int originOffset = originPosition - curTopRect.left;

        offsetOriginPositionAnimator = new ValueAnimator();
        offsetOriginPositionAnimator.setIntValues(currentPosition, originPosition);

        if (null != listener) {
            offsetOriginPositionAnimator.addListener(listener);
        }

        offsetOriginPositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                moveToOffsetLeftAndRight((int) (originOffset * interpolatedTime + currentPosition));
            }
        });
        offsetOriginPositionAnimator.setDuration(ANIMATE_TO_START_DURATION);
        offsetOriginPositionAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOriginPositionAnimator.start();

    }

    ValueAnimator offsetOutLeftAnimator;

    private void animateOffsetToOutLeft(Animator.AnimatorListener listener) {
        int outLeftPosition = -topRectF.width();
        final int currentPosition = curTopRect.left;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.left;
        offsetOutLeftAnimator = new ValueAnimator();
        offsetOutLeftAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutLeftAnimator.addListener(listener);
        }

        offsetOutLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
//                Log.e("LooperPagerView", "interpolatedTime:" + interpolatedTime);
                moveToOffsetLeftAndRight((int) (outLeftOriginOffset * interpolatedTime + currentPosition));
            }
        });
        offsetOutLeftAnimator.setDuration(ANIMATE_TO_START_DURATION);
        offsetOutLeftAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutLeftAnimator.start();

    }


    ValueAnimator offsetOutRightAnimator;

    private void animateOffsetToOutRight(Animator.AnimatorListener listener) {
        int outLeftPosition = getWidth() + topRectF.width();
        final int currentPosition = curTopRect.left;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.left;
        offsetOutRightAnimator = new ValueAnimator();
        offsetOutRightAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutRightAnimator.addListener(listener);
        }

        offsetOutRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                moveToOffsetLeftAndRight((int) (outLeftOriginOffset * interpolatedTime + currentPosition));
            }
        });
        offsetOutRightAnimator.setDuration(ANIMATE_TO_START_DURATION);
        offsetOutRightAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutRightAnimator.start();

    }

    private void finishDrag(float overscrollLeft) {
        if (Math.abs(overscrollLeft) < mTotalDragDistance) {
            animateOffsetToStartPosition(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsBeingDragged = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsBeingDragged = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsBeingDragged = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {

            if (overscrollLeft < 0) {//左
                Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsBeingDragged = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.e("LoopPagerView", "onAnimationEnd:");
//                        ViewTag viewTag = viewTags.get(0);
//                        viewTags.remove(0);
//                        viewTags.add(viewTag);
//                        removeView(viewTag.getView());
//                        addView(viewTag.getView(), 0);

                        int newPosition;
                        for (ViewTag viewTag : viewTags) {
                            newPosition = viewTag.getPosition() + 1;
                            newPosition = newPosition % mAdapter.getCount();
                            viewTag.setPosition(newPosition);
                        }
                        View view = listData.get(0);
                        listData.remove(0);
                        listData.add(view);
                        removeViewInLayout(view);
                        addView(view, 0);
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.e("LoopPagerView", "onAnimationCancel:");
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                animateOffsetToOutLeft(listener);
            } else {
                Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsBeingDragged = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.e("LoopPagerView", "onAnimationEnd:");
//                        ViewTag viewTag = viewTags.get(0);
//                        viewTags.remove(0);
//                        viewTags.add(1, viewTag);
//                        removeView(viewTag.getView());
//                        addView(viewTag.getView(), 1);


                        viewTags.get(0).setPosition(1);
                        viewTags.get(1).setPosition(0);


                        View view = listData.get(0);
                        listData.remove(0);
                        listData.add(1, view);

                        removeViewInLayout(view);
                        addView(view, 1);
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.e("LoopPagerView", "onAnimationCancel:");
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                animateOffsetToOutRight(listener);

            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                beforeX = (int) event.getX();
                mIsBeingDragged = false;
                topView = viewTags.get(0).getView();
                topRectF = viewTags.get(0).getRect();
                curTopRect.set(topRectF);
                Log.e("MotionEvent", "onTouch ACTION_DOWN" + event.getX());
                break;
            case MotionEvent.ACTION_MOVE: {
                Log.e("MotionEvent", "onTouch ACTION_MOVE" + event.getX());
                int cusX = (int) event.getX();
                differx = cusX - beforeX;
                beforeX = cusX;
                int temp = Math.abs(differx);
                if (temp <= 10) {
                    return false;
                }
                mIsBeingDragged = true;

                topView = viewTags.get(0).getView();
                topRectF = viewTags.get(0).getRect();
                curTopRect.offset(differx, 0);
//                topView.layout(beofreRect.left, beofreRect.top, beofreRect.right, beofreRect.bottom);
                ViewCompat.offsetLeftAndRight(topView, differx);

            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                Log.e("MotionEvent", " onTouch ACTION_UP differx " + topView.getLeft() + "  " + viewTags.get(0).getRect().toString());
                if (mIsBeingDragged) {
                    ViewTag viewTag = viewTags.get(0);
                    int offsetX = topView.getLeft() - viewTag.getRect().left;
                    finishDrag(offsetX);
                    mIsBeingDragged = false;

                }
                return false;

            }

        }
        return true;
    }


    /**
     * 获得缩放值
     *
     * @param curValue
     * @return
     */
    private float getScale(float min, float max, float curValue) {
        float temp = (float) curValue / SPACE;
        //区间值
        float diff = max - min;
        float resultDiff = temp * diff;
        float result = min + resultDiff;
        return result;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(adapterDataSetObserver);
        }
    }

    public class ViewTag {
        private Integer tag;
        private View view;
        private Integer position;
        private Rect rect = new Rect();
        private int offsetWidth, offsetHeight;
        private int width, height;

        public ViewTag(Integer tag, Integer position) {
            this.tag = tag;
            this.position = position;
        }

        public ViewTag(View view, Integer tag) {
            this.view = view;
            this.tag = tag;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public Rect getRect() {
            return rect;
        }

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public void setTag(Integer tag) {
            this.tag = tag;
        }

        public Integer getTag() {
            return tag;
        }

        public View getView() {
            return listData.get(tag);
        }

        public void setOffset(int offsetWidth, int offsetHeight) {
            this.offsetWidth = offsetWidth;
            this.offsetHeight = offsetHeight;
        }

        public void setOffsetHeight(int offsetHeight) {
            this.offsetHeight = offsetHeight;
        }

        public void setOffsetWidth(int offsetWidth) {
            this.offsetWidth = offsetWidth;
        }

        public int getOffsetHeight() {
            return offsetHeight;
        }

        public int getOffsetWidth() {
            return offsetWidth;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }

        public Integer getPosition() {
            return position;
        }
    }

    public class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            initAdapterView();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }
}

