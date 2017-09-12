package jerry.sprinkleflower;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

/**
 * 路径动画 PathMeasure
 * Created by Jerry Yang on 2017/9/4.
 */

public class PathPainterView extends View {

	/**
	 * 画笔
	 */
	private Paint mPaint;
	/**
	 * 路基记录器
	 */
	private PathMeasure mPathMeasure;
	/**
	 * 已运行动画的数值率
	 */
	private float mAnimatorValue;
	/**
	 * 已画的路径
	 */
	private Path mDst;
	/**
	 * 总路径长度
	 */
	private float mLength;

	public PathPainterView(Context context) {
		this(context, null);
	}

	public PathPainterView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PathPainterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPathMeasure = new PathMeasure();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(5);
		Path mPath = new Path();
		mPath.addCircle(400, 400 , 100, Path.Direction.CW);
		mPathMeasure.setPath(mPath, true);
		mLength = mPathMeasure.getLength();
		mDst = new Path();

		final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mAnimatorValue = (float) valueAnimator.getAnimatedValue();
				invalidate();
			}
		});
		valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		valueAnimator.setDuration(2000);
		valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		valueAnimator.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mDst.reset();
		//硬件加速BUG
		mDst.lineTo(0, 0);

		float stop = mLength * mAnimatorValue;

		float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mLength));

		mPathMeasure.getSegment(start, stop, mDst, true);
		canvas.drawPath(mDst, mPaint);
	}
}
