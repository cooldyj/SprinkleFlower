package jerry.sprinkleflower;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 撒花动画
 * Created by Jerry Yang on 2017/8/23.
 */

public class FlowerAnimation extends View implements ValueAnimator.AnimatorUpdateListener {

	/**
	 * 动画播放的时间
	 */
	private static final int PLAY_TIME = 4000;

	/**
	 * 动画间隔
	 */
	private static final int DELAY = 400;

	/**
	 * 花集合
	 */
	private List<Flower> flowersList1 = new ArrayList<>();
	private List<Flower> flowersList2 = new ArrayList<>();
	private List<Flower> flowersList3 = new ArrayList<>();

	/**
	 * y坐标的数组
	 */
	private int[] yLocations = {-100, -50, -25, 0};

	/**
	 * 屏幕宽度
	 */
	private int width = 0;

	/**
	 * 屏幕高度的1.5倍
	 */
	private int height = 0;

	/**
	 * 曲线高度个数分割
	 */
	private static final int QUAD_COUNT = 10;

	/**
	 * 曲度
	 */
	private static final float INTENSITY = 0.2f;

	/**
	 * 第一批个数
	 */
	private static final int FLOWER_COUNT = 6;

	/**
	 * 路径记录器
	 */
	private PathMeasure pathMeasure;

	/**
	 * 曲线摇摆的幅度 (70dp对应的像素数)
	 */
	private int range = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());

	/**
	 * 高度往上偏移量,把开始点移出屏幕顶部 (40dp对应的像素数)
	 */
	private float dy = TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

	public FlowerAnimation(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化宽高，并构建花朵集合
	 */
	private void init(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//屏幕宽度
		width = wm.getDefaultDisplay().getWidth();
		//屏幕高度的1.5倍
		height = (int) (wm.getDefaultDisplay().getHeight() * 3 / 2f);

		pathMeasure = new PathMeasure();

		buildFlower(flowersList1, R.mipmap.ic_launcher);
		buildFlower(flowersList2, R.mipmap.rose_yellow);
		buildFlower(flowersList3, R.mipmap.rose_rotate);
	}

	/**
	 * 创建花集合
	 */
	private void buildFlower(List<Flower> flowers, int resourceId) {
		int max = (int) (width * 3 / 4f);
		int min = (int) (width / 4f);
		Random random = new Random();
		for (int i = 0; i < FLOWER_COUNT; i++) {
			//构造一个Flower对象
			Flower flower = new Flower();

			Path path = new Path();
			int x = random.nextInt(max) % (max - min + 1) + min;
			CPoint CPoint = new CPoint(x, yLocations[random.nextInt(3)]);
			//生成路径坐标集合
			List<CPoint> points = buildPath(CPoint);
			//画曲线
			drawFlowerPath(path, points);
			flower.setPath(path);

			BitmapFactory.Options options = new BitmapFactory.Options();
			// 设置为true,表示解析Bitmap对象，该对象不占内存
			options.inJustDecodeBounds = true;
			// 设置缩放比例
			options.inSampleSize = Math.round((float) 100 / (float) 50);
			// 设置为false,解析Bitmap对象加入到内存中
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					resourceId, options);
			flower.setBitmap(bitmap);

			//把Flower对象加入集合中
			flowers.add(flower);
		}
	}

	/**
	 * 生成路径坐标集合
	 *
	 * @param point 坐标
	 * @return 坐标集合
	 */
	private List<CPoint> buildPath(CPoint point) {
		List<CPoint> points = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < QUAD_COUNT; i++) {
			if (i == 0) {
				points.add(point);
			} else {
				CPoint tmp = new CPoint(0, 0);
				if (random.nextInt(100) % 2 == 0) {
					tmp.x = point.x + random.nextInt(range);
				} else {
					tmp.x = point.x - random.nextInt(range);
				}
				tmp.y = (int) (height / (float) QUAD_COUNT * i);
				points.add(tmp);
			}
		}
		return points;
	}

	/**
	 * 画曲线
	 *
	 * @param path 传入的路径，用来记录生成路径值
	 * @param points 坐标集合
	 */
	private void drawFlowerPath(Path path, List<CPoint> points) {
		if (points.size() > 1) {
			for (int i = 0; i < points.size(); i++) {

				CPoint point = points.get(i);

				if (i == 0) { //第一个坐标
					CPoint next = points.get(i + 1);
					point.dx = ((next.x - point.x) * INTENSITY);
					point.dy = ((next.y - point.y) * INTENSITY);
				} else if (i == points.size() - 1) { //最后一个坐标
					CPoint prev = points.get(i - 1);
					point.dx = ((point.x - prev.x) * INTENSITY);
					point.dy = ((point.y - prev.y) * INTENSITY);
				} else { //中间的其他坐标
					CPoint next = points.get(i + 1);
					CPoint prev = points.get(i - 1);
					point.dx = ((next.x - prev.x) * INTENSITY);
					point.dy = ((next.y - prev.y) * INTENSITY);
				}

				// create the cubic-spline path
				if (i == 0) { //第一个坐标
					path.moveTo(point.x, point.y);
				} else { //其他坐标
					CPoint prev = points.get(i - 1);
					path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy), point.x
							- point.dx, (point.y - point.dy), point.x, point.y);
				}
			}
		}
	}

	ObjectAnimator mAnimator1;
	ObjectAnimator mAnimator2;
	ObjectAnimator mAnimator3;

	/**
	 * 开始执行动画
	 */
	public void startAnimation() {
		if (mAnimator1 != null && mAnimator1.isRunning()) {
			mAnimator1.cancel();
		}
		mAnimator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
		mAnimator1.setDuration(PLAY_TIME);
		mAnimator1.addUpdateListener(this);
//		mAnimator1.setRepeatCount(ValueAnimator.INFINITE);
//		mAnimator1.setRepeatMode(ValueAnimator.REVERSE);

		mAnimator1.start();
		mAnimator1.setInterpolator(new AccelerateInterpolator(1f));

		if (mAnimator2 != null && mAnimator2.isRunning()) {
			mAnimator2.cancel();
		}
		mAnimator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
		mAnimator2.setDuration(PLAY_TIME);
		mAnimator2.addUpdateListener(this);
//		mAnimator2.setRepeatCount(ValueAnimator.INFINITE);
//		mAnimator2.setRepeatMode(ValueAnimator.REVERSE);
		mAnimator2.start();
		mAnimator2.setInterpolator(new AccelerateInterpolator(1f));
		mAnimator2.setStartDelay(DELAY);

		if (mAnimator3 != null && mAnimator3.isRunning()) {
			mAnimator3.cancel();
		}
		mAnimator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
		mAnimator3.setDuration(PLAY_TIME);
		mAnimator3.addUpdateListener(this);
//		mAnimator3.setRepeatCount(ValueAnimator.INFINITE);
//		mAnimator3.setRepeatMode(ValueAnimator.REVERSE);
		mAnimator3.start();
		mAnimator3.setInterpolator(new AccelerateInterpolator(1f));
		mAnimator3.setStartDelay(DELAY * 2);
	}

	/**
	 * 动画执行过程中，更新后的回调方法
	 */
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		updateValue(getPhase1(), flowersList1);
		updateValue(getPhase2(), flowersList2);
		updateValue(getPhase3(), flowersList3);
		invalidate();
	}

	/**
	 * 跟新小球的位置
	 *
	 * @param phase 相位
	 * @param flowers 花集合
	 */
	private void updateValue(float phase, List<Flower> flowers) {
		for (Flower flower : flowers) {
			flower.setPhase(phase);
		}
	}

	/**
	 * 重写该方法，每次绘制都会调用该方法
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawFlower(canvas, flowersList1);
		drawFlower(canvas, flowersList2);
		drawFlower(canvas, flowersList3);
	}

	/**
	 * 画出花
	 * @param canvas 画布
	 * @param flowers 花的集合
	 */
	private void drawFlower(Canvas canvas, List<Flower> flowers) {
		for (Flower flower : flowers) {
			float[] pos = new float[2];
			// canvas.drawPath(flower.getPath(),mPaint);
			pathMeasure.setPath(flower.getPath(), false);
			pathMeasure.getPosTan(height * flower.getPhase(), pos, null);
			// canvas.drawCircle(pos[0], pos[1], 10, mPaint);
			canvas.drawBitmap(flower.getBitmap(), pos[0], pos[1] - dy, null);
		}
	}

	/**
	 * 记录x，y轴坐标
	 */
	private class CPoint {

		float x = 0f;
		float y = 0f;

		/**
		 * x-axis distance
		 */
		float dx = 0f;

		/**
		 * y-axis distance
		 */
		float dy = 0f;

		CPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 动画改变的属性值
	 */
	private float phase1 = 0f;
	private float phase2 = 0f;
	private float phase3 = 0f;

	public float getPhase1() {
		return phase1;
	}

	public void setPhase1(float phase1) {
		this.phase1 = phase1;
	}

	public float getPhase2() {
		return phase2;
	}

	public void setPhase2(float phase2) {
		this.phase2 = phase2;
	}

	public float getPhase3() {
		return phase3;
	}

	public void setPhase3(float phase3) {
		this.phase3 = phase3;
	}
}
