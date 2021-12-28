package com.wuc.fish;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author : wuchao5
 * @date : 2021/12/21 14:58
 * @desciption :
 */
public class FishDrawable extends Drawable {

  private Paint mPaint;
  private Path mPath;
  /**
   * 鱼的重心（鱼身的中心，以鱼身的中心为圆点进行摆动）
   */
  private PointF mMiddlePoint;
  /**
   * 鱼头的重心（鱼头的中心）
   */
  private PointF mHeadPoint;
  /**
   * 鱼的主要朝向角度
   */
  private float mFishMainAngle = 90;

  /**
   * 身体之外的部分的透明度
   */
  private final static int OTHER_ALPHA = 110;
  /**
   * 身体的透明度
   */
  private final static int BODY_ALPHA = 160;

  // -------------鱼的长度值---------------
  /**
   * 绘制鱼头的半径
   */
  public final static float HEAD_RADIUS = 50;
  /**
   * 鱼身的长度
   */
  private final static float BODY_LENGTH = 3.2f * HEAD_RADIUS;
  /**
   * 寻找鱼鳍起始点坐标的线长
   */
  private final static float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;
  /**
   * 鱼鳍(二阶贝塞尔) 起始点 到 结束点 的长度
   * 鱼鳍的长度
   */
  private final static float FINS_LENGTH = 1.3f * HEAD_RADIUS;

  // -------------鱼尾---------------
  /**
   * 尾部大圆的半径(圆心就是身体底部的中点)
   */
  private final float BIG_CIRCLE_RADIUS = HEAD_RADIUS * 0.7f;
  /**
   * 尾部中圆的半径
   */
  private final float MIDDLE_CIRCLE_RADIUS = BIG_CIRCLE_RADIUS * 0.6f;
  /**
   * 尾部小圆的半径
   */
  private final float SMALL_CIRCLE_RADIUS = MIDDLE_CIRCLE_RADIUS * 0.4f;
  /**
   * --寻找尾部中圆圆心的线长
   */
  private final float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS;
  /**
   * --寻找尾部小圆圆心的线长
   */
  private final float FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f);
  /**
   * --寻找大三角形底边中心点的线长
   */
  private final float FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f;

  private float currentValue;
  /**
   * 鱼鳍的变化值
   */
  private float mFinsValue = 0;
  /**
   * 鱼摆动的频率
   */
  private float mFrequence = 1f;

  public FishDrawable() {
    init();
  }

  private void init() {
    // 路径
    mPath = new Path();
    // 画笔
    mPaint = new Paint();
    // 画笔类型，填充
    mPaint.setStyle(Paint.Style.FILL);
    // 设置颜色
    mPaint.setARGB(OTHER_ALPHA, 244, 92, 71);
    // 抗锯齿
    mPaint.setAntiAlias(true);
    // 防抖
    mPaint.setDither(true);

    mMiddlePoint = new PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS);

    // 下面乘与了 系数 1.2 和 1.5 ，鱼尾摆动一个周期需要计算 最小公倍数为 1200，否则鱼尾摆动时会抖动
    // 系数 1.2 和 1.5 改变的话对应的 最小公倍数 也需要改变
    // currentValue * 1.2 =  360 * 整数  currentValue * 1.5 = 360 * 整数
    // currentValue = 300 --- currentValue = 240
    // 300/4/5/3 = 5 和 240/4/5/3 = 4 的最小公倍数 ---》 5* 240 -- 4*300 == 1200
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1200f);
    valueAnimator.setDuration(5 * 1000);
    // 设置循环模式， 先从-1到1，再从1到-1
    // RESTART 表示从头开始，REVERSE 表示从末尾倒播
    valueAnimator.setRepeatMode(ValueAnimator.RESTART);
    // 循环次数，无限制
    valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
    valueAnimator.setInterpolator(new LinearInterpolator());
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        currentValue = (float) animation.getAnimatedValue();
        invalidateSelf();
      }
    });
    valueAnimator.start();

    // 摆动鱼鳍
    // ObjectAnimator finsAnimator = ObjectAnimator.ofFloat(this,
    //     "finsValue", 0, HEAD_RADIUS * 2, 0);
    // // finsAnimator.setRepeatCount(new Random().nextInt(4));
    // finsAnimator.setRepeatCount(ValueAnimator.INFINITE);
    // // finsAnimator.setDuration((new Random().nextInt(1) + 1) * 500);
    // finsAnimator.setDuration(1000);
    // // finsAnimator.setRepeatMode(ValueAnimator.RESTART);
    // finsAnimator.start();
  }

  /**
   * 绘制，类似自定义 view 中的 onDraw 方法
   *
   * @param canvas 画布
   */
  @Override public void draw(@NonNull Canvas canvas) {
    // float fishAngle = mFishMainAngle;
    float fishAngle = (float) (mFishMainAngle + Math.sin(Math.toRadians(currentValue * 1.2 * mFrequence)) * 4);
    // 绘制鱼头
    mHeadPoint = calculatePoint(mMiddlePoint, BODY_LENGTH / 2, fishAngle);
    canvas.drawCircle(mHeadPoint.x, mHeadPoint.y, HEAD_RADIUS, mPaint);
    // 绘制鱼右鳍
    PointF rightFinsPoint = calculatePoint(mHeadPoint, FIND_FINS_LENGTH, fishAngle - 110);
    makeFins(canvas, rightFinsPoint, fishAngle, true);
    // 绘制鱼左鳍
    PointF leftFinsPoint = calculatePoint(mHeadPoint, FIND_FINS_LENGTH, fishAngle + 110);
    makeFins(canvas, leftFinsPoint, fishAngle, false);
    // 身体的底部的中心点
    PointF bodyBottomCenterPoint = calculatePoint(mHeadPoint, BODY_LENGTH, fishAngle - 180);
    // 绘制节肢1
    PointF middleCircleCenterPoint = makeSegment(canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS,
        FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true);
    // 绘制节肢2
    // PointF middleCircleCenterPoint = calculatePoint(bodyBottomCenterPoint, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle - 180);
    makeSegment(canvas, middleCircleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS,
        FIND_SMALL_CIRCLE_LENGTH, fishAngle, false);

    // 改变 三角形 底部的长
    float findEdgeLength = (float) Math.abs(Math.sin(Math.toRadians(currentValue * 1.5 * mFrequence)) * BIG_CIRCLE_RADIUS);
    // 绘制大三角形
    makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH, findEdgeLength, fishAngle);
    // 绘制小三角形
    makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH - 10,
        findEdgeLength - 20, fishAngle);
    // 画身体
    makeBody(canvas, mHeadPoint, bodyBottomCenterPoint, fishAngle);

    // 画左眼睛
    PointF eyesLeft = calculatePoint(mHeadPoint, HEAD_RADIUS, fishAngle + 30);
    makeEyes(canvas, eyesLeft);
    // 画右眼睛
    PointF eyesRight = calculatePoint(mHeadPoint, HEAD_RADIUS, fishAngle - 30);
    makeEyes(canvas, eyesRight);
  }

  /**
   * 画鱼眼睛
   */
  private void makeEyes(Canvas canvas, PointF eyesPoint) {
    canvas.drawCircle(eyesPoint.x, eyesPoint.y, SMALL_CIRCLE_RADIUS, mPaint);
  }

  /**
   * 画鱼身
   *
   * @param headPoint
   * @param bodyBottomCenterPoint
   */
  private void makeBody(Canvas canvas, PointF headPoint, PointF bodyBottomCenterPoint, float fishAngle) {
    // 身体的四个点
    PointF topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90);
    PointF topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90);
    PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle + 90);
    PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, fishAngle - 90);
    // 二阶贝塞尔曲线的控制点 --- 决定鱼的胖瘦
    PointF controlLeft = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle + 130);
    PointF controlRight = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle - 130);
    // 画鱼身
    mPath.reset();
    mPath.moveTo(topLeftPoint.x, topLeftPoint.y);
    mPath.quadTo(controlLeft.x, controlLeft.y, bottomLeftPoint.x, bottomLeftPoint.y);
    mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
    mPath.quadTo(controlRight.x, controlRight.y, topRightPoint.x, topRightPoint.y);
    mPaint.setAlpha(BODY_ALPHA);
    canvas.drawPath(mPath, mPaint);
  }

  /**
   * 画三角形
   *
   * @param findCenterLength 顶点到底部的垂直线长
   * @param findEdgeLength 底部一半
   */
  private void makeTriangle(Canvas canvas, PointF startPoint,
      float findCenterLength, float findEdgeLength, float fishAngle) {
    // 三角形鱼尾的摆动角度需要跟着节肢2走，摆动幅度和节肢2一样
    float triangleAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5 * mFrequence)) * 35);
    // 三角形底边的中心坐标
    PointF centerPoint = calculatePoint(startPoint, findCenterLength, triangleAngle - 180);
    // 三角形底边两个点
    PointF leftPoint = calculatePoint(centerPoint, findEdgeLength, triangleAngle + 90);
    PointF rightPoint = calculatePoint(centerPoint, findEdgeLength, triangleAngle - 90);
    // 绘制三角形
    mPath.reset();
    mPath.moveTo(startPoint.x, startPoint.y);
    mPath.lineTo(leftPoint.x, leftPoint.y);
    mPath.lineTo(rightPoint.x, rightPoint.y);
    canvas.drawPath(mPath, mPaint);
  }

  /**
   * 画节肢
   *
   * @param bottomCenterPoint 梯形底部的中心点坐标（长边）
   * @param bigRadius 大圆的半径
   * @param smallRadius 小圆的半径
   * @param findSmallCircleLength 寻找梯形小圆的线长
   * @param hasBigCircle 是否有大圆
   */
  private PointF makeSegment(Canvas canvas, PointF bottomCenterPoint, float bigRadius, float smallRadius,
      float findSmallCircleLength, float fishAngle, boolean hasBigCircle) {
    // 节肢摆动的角度
    float segmentAngle;
    // 鱼尾摆动时，节肢1 先开始带动节肢2进行摆动 sin 和 cos 相差四分之一个周期
    if (hasBigCircle) {
      // 节肢1
      segmentAngle = (float) (fishAngle + Math.cos(Math.toRadians(currentValue * 1.5 * mFrequence)) * 15);
    } else {
      // 节肢2
      segmentAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5 * mFrequence)) * 35);
    }
    // 梯形上底圆的圆心（短边）
    PointF upperCenterPoint = calculatePoint(bottomCenterPoint, findSmallCircleLength, segmentAngle - 180);
    // 梯形的四个顶点
    PointF bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle + 90);
    PointF bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle - 90);
    PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle + 90);
    PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle - 90);
    if (hasBigCircle) {
      // 画大圆 --- 只在节肢1 上才绘画
      canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint);
    }
    // 绘制小圆
    canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint);
    // 绘制梯
    mPath.reset();
    mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y);
    mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y);
    mPath.lineTo(upperRightPoint.x, upperRightPoint.y);
    mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);

    canvas.drawPath(mPath, mPaint);
    return upperCenterPoint;
  }

  /**
   * 绘制鱼鳍
   *
   * @param startPoint 起始点的坐标
   * @param fishAngle 鱼头相对于x坐标的角度
   * @param isRightFins 是否是右鱼鳍
   */
  private void makeFins(Canvas canvas, PointF startPoint, float fishAngle, boolean isRightFins) {
    float controlAngle = 115;
    // 鱼鳍的终点 --- 二阶贝塞尔曲线的终点
    PointF endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle - 180);
    // 二阶贝塞尔 控制点   1.8f * FINS_LENGTH -> 调鱼鳍的大小
    // PointF controlPoint = calculatePoint(startPoint, 1.8f * FINS_LENGTH,
    //     isRightFins ? fishAngle - controlAngle : fishAngle + controlAngle);

    // 鱼鳍变化
    float controlFishCrossLength = (float) (FINS_LENGTH * 1.8f * Math.cos(Math.toRadians(65)));
    PointF controlFishCrossPoint = calculatePoint(startPoint, controlFishCrossLength, fishAngle - 180);
    float lineLength = (float) Math.abs(Math.tan(Math.toRadians(controlAngle)) * HEAD_RADIUS);
    float line = lineLength - mFinsValue;
    // 控制点
    PointF controlPoint = calculatePoint(controlFishCrossPoint, line,
        isRightFins ? (fishAngle - 90) : (fishAngle + 90));

    // 用 drawPath 绘制了后，Path 的路径还是存在的，所以如果需要绘制新的路径，需要先调用 Path 的 reset 方法
    mPath.reset();
    // 移动到起始点
    mPath.moveTo(startPoint.x, startPoint.y);
    // 二阶贝塞尔曲线
    mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
    canvas.drawPath(mPath, mPaint);
  }

  /**
   * 求对应点的坐标 -- 知道起始点，知道鱼头的角度，知道两点间的距离，就可以算出想要的点的坐标
   *
   * @param startPoint 起始点的坐标
   * @param length 两点间的长度
   * @param angle 鱼头相对于x坐标的角度
   * @return 对应点的坐标
   */
  public PointF calculatePoint(PointF startPoint, float length, float angle) {
    // angle 角度（0度~360度）  三角函数 -- 弧度
    // sinA = a/c --> sinA * c = a --> 得到B点的y坐标
    // cosA = b/c --> cosA * c = b --> 得到B点的x坐标
    // Math.sin()、Math.cos()的参数是弧度。坐标是按数学中的坐标。
    // Math.toRadians() 将角度转成弧度。
    // 圆是360度，也是2π弧度，即360°=2π
    float deltaX = (float) (Math.cos(Math.toRadians(angle)) * length);
    // 数学系 Y 坐标 和 Android系 Y 坐标相反，所以 Y 值要取反
    float deltaY = (float) (-Math.sin(Math.toRadians(angle)) * length);
    return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
  }

  /**
   * 设置透明度的方法
   *
   * @param alpha 透明度 在 0 - 255 之间
   */
  @Override public void setAlpha(int alpha) {
    // 设置 Drawable 的透明度，一般情况下将 alpha 设置给 Paint
    mPaint.setAlpha(alpha);
  }

  /**
   * 设置颜色过滤器，在绘制出来之前，被绘制内容的每一个像素都会被颜色过滤器改变
   *
   * @param colorFilter 过滤颜色
   */
  @Override public void setColorFilter(@Nullable ColorFilter colorFilter) {
    // 设置颜色滤镜，一般情况下将 alpha 设置给 Paint
    mPaint.setColorFilter(colorFilter);
  }

  /**
   * 这个值，可以根据 setAlpha 中设置的值进行调整。比如，alpha == 0 时设置为 PixelFormat.TRANSPARENT。
   * 在 alpha == 255 时设置为 PixelFormat.OPAQUE 。其它时候设置为 PixelFormat.TRANSLUCENT。
   * PixelFormat.OPAQUE：完全不透明，遮盖在它下面的所有内容
   * PixelFormat.TRANSPARENT：透明，完全不显示任何内容
   * PixelFormat.TRANSLUCENT：只有绘制的地方才覆盖底下的内容
   */
  @Override public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  /**
   * 如果ImageView的宽高为wrap_content,则获取这个值
   * 获取 Drawable 的内部高
   */
  @Override public int getIntrinsicHeight() {
    return (int) (8.38f * HEAD_RADIUS);
  }

  /**
   * 获取 Drawable 的内部宽
   */
  @Override public int getIntrinsicWidth() {
    return (int) (8.38f * HEAD_RADIUS);
  }

  /**
   * 鱼的重心点
   */
  public PointF getMiddlePoint() {
    return mMiddlePoint;
  }

  /**
   * 鱼头的重心点
   */
  public PointF getHeadPoint() {
    return mHeadPoint;
  }

  /**
   * 获取鱼头的半径
   */
  public float getHeadRadius() {
    return HEAD_RADIUS;
  }

  /**
   * 设置鱼的主要朝向角度
   */
  public void setFishMainAngle(float fishMainAngle) {
    this.mFishMainAngle = fishMainAngle;
  }

  /**
   * 设置鱼鳍的变化值
   */
  public void setFinsValue(float finsValue) {
    mFinsValue = finsValue;
  }

  public float getFinsValue() {
    return mFinsValue;
  }

  public float getFrequence() {
    return mFrequence;
  }

  /**
   * 设置鱼摆动的频率
   */
  public void setFrequence(float frequence) {
    mFrequence = frequence;
  }
}