# FishAnimator
Android 自定义鱼游动
### 效果图
![](https://github.com/wuchao226/FishAnimator/blob/main/images/preview.gif)

### 锦鲤动画

**实现步骤：**

1. 实现小鱼的绘制
2. 实现小鱼的原地摆动
3. 实现小鱼点击游动

![image](https://user-images.githubusercontent.com/11853088/147464980-7ff6e72c-4b21-4012-8e30-2d7a41cc7496.png)

### 分解图
![](https://github.com/wuchao226/FishAnimator/blob/main/images/%E9%B1%BC%E6%95%B4%E4%BD%93%E5%88%86%E8%A7%A3%E5%9B%BE.png)

### Drawable

**Drawable是什么？**

1. 一种可以在 Canvas 上进行绘制的抽象的概念
2. 颜色、图片等都可以是一个 Drawable
3. Drawable 可以通过XML定义，或者通过代码创建
4. Android 中 Drawable 是一个抽象类，每个具体的 Drawable 都是其子类

**Drawable的优点:**

1. 使用简单，比自定义View成本低
2. 非图片类的Drawable所占空间小，能减小apk大小

#### Drawable 重写方法

![image](https://user-images.githubusercontent.com/11853088/147465246-2541bc0e-a2ef-4b4d-bc7a-ead0dd909609.png)

![image](https://user-images.githubusercontent.com/11853088/147465256-6d95ef6c-2f81-4036-b28d-4d9da596fe38.png)

![image](https://user-images.githubusercontent.com/11853088/147465316-6a23a6d9-c884-432a-8550-412f8cb0c51b.png)

![image](https://user-images.githubusercontent.com/11853088/147465327-c22b4969-a65b-4902-ba01-0979ff4d3490.png)

![image](https://user-images.githubusercontent.com/11853088/147465695-2e78d71e-439a-4291-b077-3465e31ca1c4.png)


### Paint
![image](https://user-images.githubusercontent.com/11853088/147465353-b5379f81-e1b8-4bef-81d7-2611acbefdbc.png)

### Path

![image](https://github.com/wuchao226/FishAnimator/blob/main/images/path.png)

### Canvas
![](https://github.com/wuchao226/FishAnimator/blob/main/images/canvas.png)

### 鱼各个部分比例计算宽高
![](https://github.com/wuchao226/FishAnimator/blob/main/images/%E9%B1%BC%E5%90%84%E4%B8%AA%E9%83%A8%E5%88%86%E6%AF%94%E4%BE%8B%E8%AE%A1%E7%AE%97%E5%AE%BD%E9%AB%98.png)

### 三角函数计算坐标
![](https://github.com/wuchao226/FishAnimator/blob/main/images/%E4%B8%89%E8%A7%92%E5%87%BD%E6%95%B0%E8%AE%A1%E7%AE%97%E5%9D%90%E6%A0%87.png)

### 三角函数图
![image](https://user-images.githubusercontent.com/11853088/147467365-67be5267-f61e-4993-bf97-f19dbb575715.png)



### 鱼鳍坐标计算方式
![](https://github.com/wuchao226/FishAnimator/blob/main/images/%E9%B1%BC%E9%B3%8D%E5%9D%90%E6%A0%87%E8%AE%A1%E7%AE%97%E6%96%B9%E5%BC%8F.png)

### 属性动画(ValueAnimator)
1. ValueAnimator 没有重绘，所以需要自己调用addUpdateListener方法，需要结合AnimatorUpdateListener使用。

2. 操作的对象的属性不一定要有getter和setter方法。

3. 默认插值器为AccelerateDecelerateInterpolator

![image](https://user-images.githubusercontent.com/11853088/147466885-4130c164-eb42-423c-bd6a-8a317b12f728.png)

### 属性动画(ObjectAnimator)
继承自 ValueAnimator，相对于 ValueAnimatior，可以直接操作控件。

**原理**：通过改变 View 的属性值来改变控件的形态，说白了就是通过反射技术来获取控件的一些属性如 alpha、scaleY 等的 get 和 set 方法，从而实现所谓的动画效果。所以，这就需要我们的 View （如自定义 View 中）具有 set 和 get 方法，如果没有则会导致程序的 Clash 。

**具体步骤:**
1. 首先，系统通过 get 方法获得属性值
2. 系统在时间插值器的作用下，变更属性值
3. 系统调用 set 方法，将属性值重新赋予控件

```
// 透明度动画 --- 需要有 setAlpha,getAlpha 方法
ObjectAnimator.ofFloat(ivFish, "alpha", 1, 0, 1)
            .setDuration(4000)
            .start();

```
### 鱼的游动路线
![](https://github.com/wuchao226/FishAnimator/blob/main/images/%E9%B1%BC%E7%9A%84%E6%B8%B8%E5%8A%A8%E8%B7%AF%E7%BA%BF.png)
1. 利用头部圆心、鱼身的重心以及点击点坐标来唯一确定一个特征三角形。
2. 确定鱼身需要向左还是向右转弯，知道三角形内角 AOB 的大小，就知道转动的方向了

### 向量夹角计算
向量的夹角公式计算夹角cosAOB = (OA*OB)/(|OA|*|OB|)其中OA*OB是向量的数量积,计算过程如下：

OA=(Ax-Ox,Ay-Oy)
OB=(Bx-Ox,By-Oy)
OA*OB=(Ax-Ox)(Bx-Ox)+(Ay-Oy)*(By-Oy)
|OA|表示线段OA的模即OA的长度

![image](https://user-images.githubusercontent.com/11853088/147467391-4f5affed-d3a3-48aa-97fa-953d8316ea0b.png)

![image](https://user-images.githubusercontent.com/11853088/147467400-f040508d-3133-4c55-afb0-3b02e1888e57.png)

![image](https://user-images.githubusercontent.com/11853088/147467410-da11dcfd-4742-4ece-9832-ae4750b3a3f4.png)

### PathMeasure

mPathMeasure.getPosTan(float distance, float pos[], float tan[])，参数信息：

- distance : 这个参数就是确定要获取路径上哪个位置的点

- pos[] ：根据distance返回点的坐标信息并保存在传入的pos[]内， X保存在pos[0], Y则在pos[1]

- tan[] : 根据distance返回点的角度信息并保存传入tan[]内 ，主要结合float degree = (float) (Math.atan2(mTan[1], mTan[0]) * 180 / Math.PI);












