package jerry.sprinkleflower;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * PathPainterActivity
 * Created by Jerry Yang on 2017/9/4.
 */

public class PathPainterActivity extends Activity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		PathPainterView pathPainterView = (PathPainterView) findViewById(R.id.path_painter);
	}
}
