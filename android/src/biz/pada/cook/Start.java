package biz.pada.cook;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
public class Start extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		FrameLayout frame=(FrameLayout)this.findViewById(R.id.start);
		frame.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Start.this.startActivity(new Intent(Start.this, Main.class));
			}
		});
		this.hideSystemUI(frame);
		String imei=((TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE)).getDeviceId();
	}
	// Hides the system bars.
	private void hideSystemUI(View view){
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		view.setSystemUiVisibility(
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
			| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}