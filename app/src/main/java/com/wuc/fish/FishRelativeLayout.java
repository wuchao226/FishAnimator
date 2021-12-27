package com.wuc.fish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatImageView;
import java.util.Random;

/**
 * @author : wuchao5
 * @date : 2021/12/23 14:42
 * @desciption :
 */
public class FishRelativeLayout extends RelativeLayout {
  private AppCompatImageView mIvFish;
  private FishDrawable mFishDrawable;
  /**
   * 绘制水波纹的 Paint
   */
  private Paint mPaint;
  /**
   * 水波纹半径的变化系数
   */
  private float ripple;
  /**
   * 水波纹透明度
   */
  private int alpha;
  /**
   * 触摸点 x y 坐标
   */
  private float mTouchX = -1;
  private float mTouchY = -1;

  public FishRelativeLayout(Context context) {
    this(context, null);
  }

  public FishRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FishRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    // 创建 ImageView 并添加到 FishRelativeLayout
    mIvFish = new AppCompatImageView(context);
    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    mIvFish.setLayoutParams(layoutParams);
    // 创建 fish 并添加到 ImageView
    mFishDrawable = new FishDrawable();
    mIvFish.setImageDrawable(mFishDrawable);
    addView(mIvFish);

    mPaint = new Paint();
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(8);

    // 因为是容器，不会执行 onDraw，设置标志位，让 onDraw 执行
    setWillNotDraw(false);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // 获取手指按下的 x，y坐标，作为圆心的point
        mTouchX = event.getX();
        mTouchY = event.getY();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "ripple", 0, 1f)
            .setDuration(1000);
        objectAnimator.start();

        makeTrail();
        break;
      default:
        break;
    }
    return super.onTouchEvent(event);
  }

  private void makeTrail() {
    // 鱼的重心：相对 ImageView 坐标
    PointF fishRelativeMiddle = mFishDrawable.getMiddlePoint();
    /**  -----直接跳转到手指按下的位置-----*/
    // mIvFish.setTranslationX(mTouchX - mIvFish.getLeft() - fishRelativeMiddle.x);
    // mIvFish.setTranslationY(mTouchY - mIvFish.getTop() - fishRelativeMiddle.y);

    /** -----路径上慢慢移动到手指按下的位置 -- 使用 ValueAnimator 方式实现-----*/
    // 属性动画 -- ValueAnimator(值的改变，与属性无关), ObjectAnimator(属性动画)
    // 小鱼的起始点 上一次平移后的位置
    // x方向的平移
    /*ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(mIvFish.getTranslationX(),
        mTouchX - mIvFish.getLeft() - fishRelativeMiddle.x);
    valueAnimatorX.setDuration(2000);
    valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float currentValue = (float) animation.getAnimatedValue();
        mIvFish.setTranslationX(currentValue);
      }
    });
    valueAnimatorX.start();
    // y方向的平移
    ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(mIvFish.getTranslationY(),
        mTouchY - mIvFish.getTop() - fishRelativeMiddle.y);
    valueAnimatorY.setDuration(2000);
    valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float currentValue = (float) animation.getAnimatedValue();
        mIvFish.setTranslationY(currentValue);
      }
    });
    valueAnimatorY.start();*/
    /** ------- 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式1实现 -- setTranslationX -------**/
    /*ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(mIvFish, "translationX", mIvFish.getTranslationX(),
        mTouchX - mIvFish.getLeft() - fishRelativeMiddle.x);
    objectAnimatorX.setDuration(2000);
    objectAnimatorX.start();
    ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(mIvFish, "translationY", mIvFish.getTranslationY(),
        mTouchY - mIvFish.getTop() - fishRelativeMiddle.y);
    objectAnimatorY.setDuration(2000);
    objectAnimatorY.start();*/
    /** ------- 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式2实现 -- setX,setY -------**/
   /* ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(mIvFish, "X", mIvFish.getX(),
        mTouchX - fishRelativeMiddle.x);
    objectAnimatorX.setDuration(2000);
    objectAnimatorX.start();
    ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(mIvFish, "Y", mIvFish.getY(),
        mTouchY - fishRelativeMiddle.y);
    objectAnimatorY.setDuration(2000);
    objectAnimatorY.start();*/
    /** ------- 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式3实现 -- setX,setY -- 通过Path -------**/
    // 鱼的重心：绝对坐标 --- 起始点
    PointF fishMiddle = new PointF(mIvFish.getX() + fishRelativeMiddle.x, mIvFish.getY() + fishRelativeMiddle.y);
    // 鱼头圆心的坐标 -- 控制点1
    final PointF fishHead = new PointF(mIvFish.getX() + mFishDrawable.getHeadPoint().x,
        mIvFish.getY() + mFishDrawable.getHeadPoint().y);

    // 点击坐标 -- 结束点
    PointF touch = new PointF(mTouchX, mTouchY);
    // AOB 夹角
    float angle = includedAngle(fishMiddle, fishHead, touch);
    float delta = includedAngle(fishMiddle, new PointF(fishMiddle.x + 1, fishMiddle.y), fishHead);
    // 控制点2
    PointF controlPoint = mFishDrawable.calculatePoint(fishMiddle,
        FishDrawable.HEAD_RADIUS * 1.6f, angle / 2 + delta);

    Path path = new Path();
    path.moveTo(mIvFish.getX(), mIvFish.getY());
    // path.moveTo(fishMiddle.x - fishRelativeMiddle.x, fishMiddle.y - fishRelativeMiddle.y);
    path.cubicTo(fishHead.x - fishRelativeMiddle.x, fishHead.y - fishRelativeMiddle.y,
        controlPoint.x - fishRelativeMiddle.x, controlPoint.y - fishRelativeMiddle.y,
        mTouchX - fishRelativeMiddle.x, mTouchY - fishRelativeMiddle.y);
    // path.lineTo(mTouchX - fishRelativeMiddle.x, mTouchY - fishRelativeMiddle.y);
    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mIvFish, "x", "y", path);
    objectAnimator.setDuration(2000);
    objectAnimator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        // 设置鱼摆动的频率
        mFishDrawable.setFrequence(1f);
      }

      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        // 设置鱼摆动的频率
        mFishDrawable.setFrequence(3f);

        // 摆动鱼鳍
        ObjectAnimator finsAnimator = ObjectAnimator.ofFloat(mFishDrawable,
            "finsValue", 0, mFishDrawable.getHeadRadius() * 2, 0);
        finsAnimator.setRepeatCount(new Random().nextInt(4));
        finsAnimator.setDuration((new Random().nextInt(1) + 1) * 500);
        finsAnimator.start();
      }
    });

    final PathMeasure pathMeasure = new PathMeasure(path, false);
    final float[] tan = new float[2];
    objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animator) {
        float fraction = animator.getAnimatedFraction();
        // 计算 tan 切线
        pathMeasure.getPosTan(pathMeasure.getLength() * fraction, null, tan);
        // y轴与实际坐标相反，tan[1] 需要取反
        float angle = (float) Math.toDegrees(Math.atan2(-tan[1], tan[0]));
        mFishDrawable.setFishMainAngle(angle);
      }
    });
    objectAnimator.start();

  }

  public float includedAngle(PointF O, PointF A, PointF B) {
    // OA的长度
    float OALength = (float) Math.sqrt((A.x - O.x) * (A.x - O.x) + (A.y - O.y) * (A.y - O.y));
    // OB的长度
    float OBLength = (float) Math.sqrt((B.x - O.x) * (B.x - O.x) + (B.y - O.y) * (B.y - O.y));
    // 向量的数量积 OA*OB=(Ax-Ox)*(Bx-Ox)+(Ay-Oy)*(By-Oy)
    float AOB = (A.x - O.x) * (B.x - O.x) + (A.y - O.y) * (B.y - O.y);
    // cosAOB = (OA*OB)/(|OA|*|OB|) 其中OA*OB是向量的数量积，|OA|表示线段OA的模即OA的长度
    float cosAOB = AOB / (OALength * OBLength);
    // 角度 -- 反余弦
    float angleAOB = (float) Math.toDegrees(Math.acos(cosAOB));
    // 鱼是向左转弯，还是向右转弯
    // AB连线和 X轴的夹角 tan 值 - OB连线与X轴的夹角 tan值  --> 负数 说明点击在鱼的右侧，正数在左侧
    float direction = (A.y - B.y) / (A.x - B.x) - (O.y - B.y) / (O.x - B.x);
    // 点击在鱼头延长线上 -- angleAOB == 0 ---点击在鱼尾延长线上 angleAOB 180
    if (direction == 0) {
      if (AOB >= 0) {
        return 0;
      } else {
        return 180;
      }
    } else {
      if (direction > 0) {
        return -angleAOB;
      } else {
        return angleAOB;
      }
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mTouchX >= 0 && mTouchY >= 0) {
      mPaint.setAlpha(alpha);
      canvas.drawCircle(mTouchX, mTouchY, ripple * 100, mPaint);
    }
  }

  public void setRipple(float ripple) {
    this.ripple = ripple;
    // 透明度的变化 100 - 0
    alpha = (int) (100 * (1 - ripple));
    invalidate();
  }
}