package jerry.sprinkleflower;

import android.graphics.Bitmap;
import android.graphics.Path;

/**
 * 花 模型
 * Created by Jerry Yang on 2017/8/23.
 */

class Flower {

	private Bitmap bitmap;
	private Path path;
	private float phase;

	Bitmap getBitmap() {
		return bitmap;
	}

	void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}


	Path getPath() {
		return path;
	}

	void setPath(Path path) {
		this.path = path;
	}

	float getPhase() {
		return phase;
	}

	void setPhase(float phase) {
		this.phase = phase;
	}
}
