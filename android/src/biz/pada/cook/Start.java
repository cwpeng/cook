package biz.pada.cook;
import biz.pada.cook.ui.ShareUI;
import biz.pada.cook.service.Network;
import biz.pada.cook.service.Login;
import biz.pada.cook.loader.*;
import biz.pada.cook.core.Player;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
public class Start extends Activity{
	private int localResourcesVersion=-1;
	private int serverResourcesVersion=-1;
	private String password;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		FrameLayout frame=(FrameLayout)this.findViewById(R.id.start);
		this.hideSystemUI(frame);
		// Login player with IMEI number
		this.login();
		// Get local resources version
		SharedPreferences config=this.getPreferences(Context.MODE_PRIVATE);
		this.localResourcesVersion=config.getInt("resources-version", -1);
		// Check server resources version
		this.checkResourcesVersion();
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
	// Ready to game
	private void readyToGame(){
		FrameLayout frame=(FrameLayout)this.findViewById(R.id.start);
		frame.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Start.this.startActivity(new Intent(Start.this, Main.class));
			}
		});
	}
	// Login player with IMEI number(Player ID)
	private void login(){
		if(Network.isOnline(this)){
			String imei=((TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE)).getDeviceId();
			StringBuilder passwordBuilder=new StringBuilder((int)Math.floor(Math.random()*899+100)+((char)Math.floor(Math.random()*26+97))+(int)Math.floor(Math.random()*89+10)+((char)Math.floor(Math.random()*26+65))+(int)Math.floor(Math.random()*89+10));
			for(int i=0;i<5;i++){
				passwordBuilder.append((char)Math.floor(Math.random()*26+65));
			}
			this.password=passwordBuilder.toString();
			(new Login(this)).execute("http://big-cook.appspot.com/exe/api/Login", imei, this.password);
		}else{
			Network.showNetworkUnavailable(this);
		}
	}
		public void loginCallback(Player player){
			ShareUI.alert(this, player.id+","+player.name+","+player.token);
		}
	// Check resources version from server
	private void checkResourcesVersion(){
		if(Network.isOnline(this)){
			(new ResourcesVersionLoader(this)).execute("http://big-cook.appspot.com/exe/api/GetResourcesVersion");
		}else{
			Network.showNetworkUnavailable(this);
		}
	}
		public void checkResourcesVersionCallback(int serverVersion){
			if(serverVersion<0){
				ShareUI.alert(this, "Load Failed");
			}else{
				if(serverVersion>this.localResourcesVersion){
					this.serverResourcesVersion=serverVersion;
					this.loadResources();
				}else{
					ShareUI.alert(this, "Up-to-Date");
					// Ready to game
					this.readyToGame();
				}
			}
		}
	// Load updated resources from server
	private void loadResources(){
		if(Network.isOnline(this)){
			(new MaterialBasesLoader(this)).execute("http://big-cook.appspot.com/exe/api/GetMaterialBases");
		}else{
			Network.showNetworkUnavailable(this);
		}
	}
		public void loadResourcesCallback(boolean result){
			if(result){
				ShareUI.alert(this, "Loaded");
				// Update local resources version
				SharedPreferences config=this.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor=config.edit();
				editor.putInt("resources-version", this.serverResourcesVersion);
				editor.commit();
				this.localResourcesVersion=this.serverResourcesVersion;
				// Ready to game
				this.readyToGame();
			}else{
				ShareUI.alert(this, "Load Failed");
			}
		}
}