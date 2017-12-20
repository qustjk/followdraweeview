package com.axeboya.followdraweeview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by JiangKe on 2017/9/11.
 */

public class FlowDraweeView extends SimpleDraweeView {

    private int lastX;
    private int lastY;

    private boolean isMoved = false;

    private float width = 0;

    private float height = 0;

    private float mParentWidth;

    private float mParentHeight;

    private ValueAnimator animToLeft;

    private ValueAnimator animToRight;

    public FlowDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FlowDraweeView(Context context) {
        super(context);
    }

    public FlowDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        ViewGroup mViewGroup = (ViewGroup) getParent();
        if (null != mViewGroup) {
            mParentWidth = mViewGroup.getWidth();
            mParentHeight = mViewGroup.getHeight();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isMoved = false;
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (!isMoved) {
                    callOnClick();
                } else {
                    int animWidth = (int) (mParentWidth / 2);
                    int leftX = (int) getX();
                    int rightX = (int) (leftX + width);
                    if ((leftX + rightX) / 2 >= animWidth) {
                        toRight();
                    } else {
                        toLeft();
                    }
                }
                isMoved = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isMoved = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = (int) (getLeft() + dx);
                if (left < 0) {
                    left = 0;
                }
                if (left + width > mParentWidth) {
                    left = (int) (mParentWidth - width);
                }
                int top = (int) (getTop() + dy);
                if (top < 0) {
                    top = 0;
                }
                if (top + height > mParentHeight) {
                    top = (int) (mParentHeight - height);
                }

                layout(left, top, (int) (left + width), (int) (top + height));
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
        }
        invalidate();
        return true;
    }

    private void toRight() {
        final int[] left = {getLeft()};
        final int top = getTop();
        int rightX = (int) (getLeft() + width);
        float distance = mParentWidth - rightX;

        float maxDistance = (mParentWidth - width) / 2;

        int time = (int) (distance / maxDistance * 1000);

        animToRight = ValueAnimator.ofFloat(0, distance);
        animToRight.setDuration(time);
        animToRight.setRepeatCount(1);
        animToRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                left[0] = (int) (left[0] + value);
                if (left[0] + width >= mParentWidth) {
                    left[0] = (int) (mParentWidth - width);
                    animToRight.cancel();
                }
                layout(left[0], top, (int) (left[0] + width), (int) (top + height));
            }
        });
        animToRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animToRight.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animToRight.start();
    }

    private void toLeft() {
        final int[] left = {getLeft()};
        final int top = getTop();
        float distance = getLeft();
        float maxDistance = (mParentWidth - width) / 2;
        int time = (int) (distance / maxDistance * 1200);
        animToLeft = ValueAnimator.ofFloat(0, distance);
        animToLeft.setDuration(time);
        animToLeft.setRepeatCount(1);
        animToLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                left[0] = (int) (left[0] - value);
                if (left[0] <= 0) {
                    left[0] = 0;
                    animToLeft.cancel();
                }
                layout(left[0], top, (int) (left[0] + width), (int) (top + height));
            }
        });
        animToLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animToLeft.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animToLeft.start();
    }

}
