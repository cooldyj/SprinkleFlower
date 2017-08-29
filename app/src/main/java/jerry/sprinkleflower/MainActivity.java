package jerry.sprinkleflower;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private TextView qiTV;
	private TextView xiTV;
	private TextView kuaiTV;
	private TextView leTV;
	private View center;

	private FlowerAnimation flowerAnimation;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler();

		FrameLayout animationLayout = (FrameLayout) findViewById(R.id.flower_zone);
		qiTV = (TextView) findViewById(R.id.qi);
		xiTV = (TextView) findViewById(R.id.xi);
		kuaiTV = (TextView) findViewById(R.id.kuai);
		leTV = (TextView) findViewById(R.id.le);
		center = findViewById(R.id.center);

		flowerAnimation = new FlowerAnimation(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		flowerAnimation.setLayoutParams(params);
		animationLayout.addView(flowerAnimation);

		loadText();
	}

	/**
	 * 文字显示动画
	 */
	private void loadText(){

		float qiTVCurX = qiTV.getPivotX();
		float qiTVCurY = qiTV.getPivotY();

		float xiTVCurX = xiTV.getPivotX();
		float xiTVCurY = xiTV.getPivotY();

		float kuaiTVCurX = kuaiTV.getPivotX();
		float kuaiTVCurY = kuaiTV.getPivotY();

		float leTVCurX = leTV.getPivotX();
		float leTVCurY = leTV.getPivotY();

		ObjectAnimator qiTranX = ObjectAnimator.ofFloat(qiTV, "translationX", 300f, qiTVCurX);
		ObjectAnimator qiTranY = ObjectAnimator.ofFloat(qiTV, "translationY", 300f, qiTVCurY);
		ObjectAnimator qiAlpha = ObjectAnimator.ofFloat(qiTV, "alpha", 0f, 1f);
		ObjectAnimator qiScaleX = ObjectAnimator.ofFloat(qiTV, "scaleX", 0.3f, 1f);
		ObjectAnimator qiScaleY = ObjectAnimator.ofFloat(qiTV, "scaleY", 0.3f, 1f);

		ObjectAnimator xiTranX = ObjectAnimator.ofFloat(xiTV, "translationX", -300f, xiTVCurX);
		ObjectAnimator xiTranY = ObjectAnimator.ofFloat(xiTV, "translationY", 300f, xiTVCurY);
		ObjectAnimator xiAlpha = ObjectAnimator.ofFloat(xiTV, "alpha", 0f, 1f);
		ObjectAnimator xiScaleX = ObjectAnimator.ofFloat(xiTV, "scaleX", 0.3f, 1f);
		ObjectAnimator xiScaleY = ObjectAnimator.ofFloat(xiTV, "scaleY", 0.3f, 1f);

		ObjectAnimator kuaiTranX = ObjectAnimator.ofFloat(kuaiTV, "translationX", 300f, kuaiTVCurX);
		ObjectAnimator kuaiTranY = ObjectAnimator.ofFloat(kuaiTV, "translationY", -300f, kuaiTVCurY);
		ObjectAnimator kuaiAlpha = ObjectAnimator.ofFloat(kuaiTV, "alpha", 0f, 1f);
		ObjectAnimator kuaiScaleX = ObjectAnimator.ofFloat(kuaiTV, "scaleX", 0.3f, 1f);
		ObjectAnimator kuaiScaleY = ObjectAnimator.ofFloat(kuaiTV, "scaleY", 0.3f, 1f);

		ObjectAnimator leTranX = ObjectAnimator.ofFloat(leTV, "translationX", -300f, leTVCurX);
		ObjectAnimator leTranY = ObjectAnimator.ofFloat(leTV, "translationY", -300f, leTVCurY);
		ObjectAnimator leAlpha = ObjectAnimator.ofFloat(leTV, "alpha", 0f, 1f);
		ObjectAnimator leScaleX = ObjectAnimator.ofFloat(leTV, "scaleX", 0.3f, 1f);
		ObjectAnimator leScaleY = ObjectAnimator.ofFloat(leTV, "scaleY", 0.3f, 1f);

		AnimatorSet qiSet = new AnimatorSet();
		qiSet.play(qiTranX).with(qiTranY).with(qiAlpha).with(qiScaleX).with(qiScaleY);
		qiSet.setDuration(1000);

		AnimatorSet kuaiSet = new AnimatorSet();
		kuaiSet.play(kuaiTranX).with(kuaiTranY).with(kuaiAlpha).with(kuaiScaleX).with(kuaiScaleY);
		kuaiSet.setDuration(1000);

		AnimatorSet leSet = new AnimatorSet();
		leSet.play(leTranX).with(leTranY).with(leAlpha).with(leScaleX).with(leScaleY).after(kuaiSet);
		leSet.setDuration(1000);

		AnimatorSet xiSet = new AnimatorSet();
		xiSet.play(xiTranX).with(xiTranY).with(xiAlpha).with(xiScaleX).with(xiScaleY).after(qiSet).before(leSet);
		xiSet.setDuration(1000);
		xiSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				loadFlower();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		xiSet.start();

	}

	/**
	 * 撒花动画
	 */
	private void loadFlower(){
		flowerAnimation.startAnimation();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				flowerAnimation.startAnimation();
				loadFlower();
			}
		}, 5000);
	}
}
