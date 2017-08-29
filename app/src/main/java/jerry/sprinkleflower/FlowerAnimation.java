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
	 * 动画改变的属性值
	 */
	private float phase1 = 0f;
	private float phase2 = 0f;
	private float phase3 = 0f;

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
	 * 曲线摇摆的幅度
	 */
	private int range = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources()
					.getDisplayMetrics());

	/**
	 * 高度往上偏移量,把开始点移出屏幕顶部
	 */
	private float dy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
			40, getResources().getDisplayMetrics());

	/**
	 * 测量路径的坐标位置
	 */
	private PathMeasure pathMeasure;

	public FlowerAnimation(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = (int) (wm.getDefaultDisplay().getHeight() * 3 / 2f);

		pathMeasure = new PathMeasure();

		buildFlower(FLOWER_COUNT, flowersList1, R.mipmap.ic_launcher);
		buildFlower(FLOWER_COUNT, flowersList2, R.mipmap.rose_yellow);
		buildFlower(FLOWER_COUNT, flowersList3, R.mipmap.rose_rotate);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawFlower(canvas, flowersList1);
		drawFlower(canvas, flowersList2);
		drawFlower(canvas, flowersList3);
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		updateValue(getPhase1(), flowersList1);
		updateValue(getPhase2(), flowersList2);
		updateValue(getPhase3(), flowersList3);
		invalidate();
	}

	/**
	 * 创建花
	 */
	private void buildFlower(int count, List<Flower> flowers, int resourceId) {
		int max = (int) (width * 3 / 4f);
		int min = (int) (width / 4f);
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			int s = random.nextInt(max) % (max - min + 1) + min;
			Path path = new Path();
			CPoint CPoint = new CPoint(s, yLocations[random.nextInt(3)]);
			List<CPoint> points = builderPath(CPoint);
			drawFlowerPath(path, points);
			Flower flower = new Flower();
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
			flowers.add(flower);
		}
	}

	/**
	 * 画曲线
	 *
	 * @param path
	 * @param points
	 */
	private void drawFlowerPath(Path path, List<CPoint> points) {
		if (points.size() > 1) {
			for (int j = 0; j < points.size(); j++) {

				CPoint point = points.get(j);

				if (j == 0) {
					CPoint next = points.get(j + 1);
					point.dx = ((next.x - point.x) * INTENSITY);
					point.dy = ((next.y - point.y) * INTENSITY);
				} else if (j == points.size() - 1) {
					CPoint prev = points.get(j - 1);
					point.dx = ((point.x - prev.x) * INTENSITY);
					point.dy = ((point.y - prev.y) * INTENSITY);
				} else {
					CPoint next = points.get(j + 1);
					CPoint prev = points.get(j - 1);
					point.dx = ((next.x - prev.x) * INTENSITY);
					point.dy = ((next.y - prev.y) * INTENSITY);
				}

				// create the cubic-spline path
				if (j == 0) {
					path.moveTo(point.x, point.y);
				} else {
					CPoint prev = points.get(j - 1);
					path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy), point.x
							- point.dx, (point.y - point.dy), point.x, point.y);
				}
			}
		}
	}

	/**
	 * 画路径
	 *
	 * @param point
	 * @return
	 */
	private List<CPoint> builderPath(CPoint point) {
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
	 * @param canvas
	 * @param flowers
	 */
	private void drawFlower(Canvas canvas, List<Flower> flowers) {
		for (Flower flower : flowers) {
			float[] pos = new float[2];
			// canvas.drawPath(flower.getPath(),mPaint);
			pathMeasure.setPath(flower.getPath(), false);
			pathMeasure.getPosTan(height * flower.getValue(), pos, null);
			// canvas.drawCircle(pos[0], pos[1], 10, mPaint);
			canvas.drawBitmap(flower.getBitmap(), pos[0], pos[1] - dy, null);
		}
	}

	ObjectAnimator mAnimator1;
	ObjectAnimator mAnimator2;
	ObjectAnimator mAnimator3;

	public void startAnimation() {
		if (mAnimator1 != null && mAnimator1.isRunning()) {
			mAnimator1.cancel();
		}
		mAnimator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
		mAnimator1.setDuration(PLAY_TIME);
		mAnimator1.addUpdateListener(this);

		mAnimator1.start();
		mAnimator1.setInterpolator(new AccelerateInterpolator(1f));

		if (mAnimator2 != null && mAnimator2.isRunning()) {
			mAnimator2.cancel();
		}
		mAnimator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
		mAnimator2.setDuration(PLAY_TIME);
		mAnimator2.addUpdateListener(this);
		mAnimator2.start();
		mAnimator2.setInterpolator(new AccelerateInterpolator(1f));
		mAnimator2.setStartDelay(DELAY);

		if (mAnimator3 != null && mAnimator3.isRunning()) {
			mAnimator3.cancel();
		}
		mAnimator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
		mAnimator3.setDuration(PLAY_TIME);
		mAnimator3.addUpdateListener(this);
		mAnimator3.start();
		mAnimator3.setInterpolator(new AccelerateInterpolator(1f));
		mAnimator3.setStartDelay(DELAY * 2);
	}

	/**
	 * 跟新小球的位置
	 *
	 * @param value
	 * @param flowers
	 */
	private void updateValue(float value, List<Flower> flowers) {
		for (Flower flower : flowers) {
			flower.setValue(value);
		}
	}

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
