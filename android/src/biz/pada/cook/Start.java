package biz.pada.cook;
import biz.pada.cook.ui.ShareUI;
import biz.pada.cook.service.Network;
import biz.pada.cook.service.Signup;
import biz.pada.cook.service.Signin;
import biz.pada.cook.loader.*;
import biz.pada.cook.core.Player;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
public class Start extends Activity{
	private int localResourcesVersion=-1;
	private int serverResourcesVersion=-1;
	// Player data
	private long id;
	private String password;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		FrameLayout frame=(FrameLayout)this.findViewById(R.id.start);
		this.hideSystemUI(frame);
		// Signup player with IMEI number and password
		SharedPreferences config=this.getPreferences(Context.MODE_PRIVATE);
		this.id=config.getLong("id", -1);
		this.password=config.getString("password", null);
		if(this.id==-1||this.password==null){
			this.findViewById(R.id.signup).setVisibility(View.VISIBLE);
		}else{
			this.signin();
		}
		// Get local resources version
		this.localResourcesVersion=config.getInt("resources-version", -1);
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
	// Signup player with imei/password
	public void clickSignup(View view){
		this.findViewById(R.id.signup).setVisibility(View.INVISIBLE);
		this.signup(((TextView)this.findViewById(R.id.signup_name)).getText().toString());
	}
	private void signup(String name){
		if(Network.isOnline(this)){
			String imei=((TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE)).getDeviceId();
			StringBuilder passwordBuilder=new StringBuilder((int)Math.floor(Math.random()*899+100)+""+((char)Math.floor(Math.random()*26+97))+""+(int)Math.floor(Math.random()*89+10)+""+((char)Math.floor(Math.random()*26+65))+""+(int)Math.floor(Math.random()*89+10));
			for(int i=0;i<5;i++){
				if(Math.random()>0.5){
					passwordBuilder.append((char)Math.floor(Math.random()*26+65));
				}else{
					passwordBuilder.append((char)Math.floor(Math.random()*26+97));
				}
			}
			this.password=passwordBuilder.toString();
			(new Signup(this)).execute("http://big-cook.appspot.com/exe/api/Signup", imei, this.password, name);
		}else{
			Network.showNetworkUnavailable(this);
		}
	}
		public void signupCallback(long id){
			this.id=id;
			// Update local player id and password
			SharedPreferences config=this.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor=config.edit();
			editor.putLong("id", id);
			editor.putString("password", this.password);
			editor.commit();
			// Sign in
			this.signin();
		}
	// Signin player with id/password
	private void signin(){
		if(Network.isOnline(this)){
			(new Signin(this)).execute("http://big-cook.appspot.com/exe/api/Signin", this.id+"", this.password);
		}else{
			Network.showNetworkUnavailable(this);
		}
	}
		public void signinCallback(Player player){
			// ShareUI.alert(this, player.id+","+player.name+","+player.token);
			// Check server resources version
			this.checkResourcesVersion();
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