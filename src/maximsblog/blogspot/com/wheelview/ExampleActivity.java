package maximsblog.blogspot.com.wheelview;

import maximsblog.blogspot.com.wheelview.WheelView.IRing;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ExampleActivity extends Activity implements OnCheckedChangeListener, IRing {

	private CheckBox mEnable;
	private WheelView mWheel1;
	private WheelView mWheel2;
	private WheelView mWheel3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_example);
		mEnable = (CheckBox)findViewById(R.id.enable);
		mWheel1 = (WheelView)findViewById(R.id.wheel1);
		mWheel2 = (WheelView)findViewById(R.id.wheel2);
		mWheel3 = (WheelView)findViewById(R.id.wheel3);
		mEnable.setChecked(true);
		mEnable.setOnCheckedChangeListener(this);
		mWheel1.setOnTouchRingListener(this);
		mWheel2.setOnTouchRingListener(this);
		mWheel3.setOnTouchRingListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.example, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mWheel1.setEnableRingWithAnim(isChecked);
		mWheel2.setEnableRingWithAnim(isChecked);
		mWheel3.setEnableRingWithAnim(isChecked);
	}

	@Override
	public void onCurrentValueChanged(int currentValue) {
		Toast.makeText(this, "current value: " + currentValue, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStartClick(int selectedValue) {
		Toast.makeText(this, "on click event! Selected value:" + selectedValue, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRotateChangeState() {
		//Toast.makeText(this, "rotation sector changed!", Toast.LENGTH_SHORT).show();
	}
}
