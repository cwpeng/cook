package biz.pada.cook;
import java.util.HashMap;
import biz.pada.cook.ui.ShareUI;
import biz.pada.cook.ui.ActionFragment;
import biz.pada.cook.util.SphericalUtil;
import biz.pada.cook.db.CookDBHelper;
import biz.pada.cook.core.Player;
import biz.pada.cook.core.Base;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.View;
import android.view.animation.*;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.location.Location;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.database.Cursor;
import android.database.sqlite.*;
public class Main extends Activity implements
	OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
	GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener,
	LocationListener{
	// Static final constants
	private static final int REQUEST_RESOLVE_ERROR=1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR="dialog_error"; // Unique tag for the error dialog fragment
	// Instance variables
	private GoogleApiClient google;
	private LocationRequest location;
	// Map object
	private GoogleMap map;
	// Game objects
	private Player player;
	private HashMap<Long, Base> bases;
	// Track whether this activity is already resolving an error about coonecting to Google Play Service
	private boolean triedResolvingError;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		// Get player from Start activity
		this.player=(Player)this.getIntent().getSerializableExtra("player");
		// Google API client
		this.google=new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();
		// Location service
		this.location=new LocationRequest();
		this.location.setInterval(10000);
		this.location.setFastestInterval(5000);
		this.location.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Flags
		this.triedResolvingError=false;
		// Restore activity state
		this.updateValuesFromBundle(savedInstanceState);
		// Map service
		FragmentManager fragmentManager=this.getFragmentManager();
		MapFragment mapFragment=(MapFragment)fragmentManager.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		this.hideSystemUI(this.findViewById(R.id.main));
		// Menu Initialize
		this.findViewById(R.id.menu_trigger).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Main.this.slideinMenu();
			}
		});
		this.findViewById(R.id.invisible_focusable_btn).setOnFocusChangeListener(new View.OnFocusChangeListener(){
			public void onFocusChange(View v, boolean hasFocus){
				if(!hasFocus){
					Main.this.slideoutMenu();
				}
			}
		});
		// Init fragments other than map
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.menu_fragment));
		fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.action_fragment));
		fragmentTransaction.commit();
	}
	// Menu in the bottom
	private void slideinMenu(){
		// Hide menu trigger
		this.findViewById(R.id.menu_trigger).setVisibility(View.GONE);
		// Slide in menu
		View menu=(View)this.findViewById(R.id.menu);
		menu.setVisibility(View.VISIBLE);
		menu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slidein_menu));
		// Request focus, keep menu open.
		// Menu will hide automatically once this button lose focus.
		View btn=this.findViewById(R.id.invisible_focusable_btn);
		if(!btn.requestFocus()){
			btn.requestFocusFromTouch();
		}
	}
	private void slideoutMenu(){
		// Slide out menu
		Animation slideOut=AnimationUtils.loadAnimation(this, R.anim.slideout_menu);
		slideOut.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation anim){}           
			@Override
			public void onAnimationRepeat(Animation anim){}           
			@Override
			public void onAnimationEnd(Animation anim){
				Main.this.hideMenu();
			}
		});
		this.findViewById(R.id.menu).startAnimation(slideOut);
	}
		private void hideMenu(){
			// Hide menu
			this.findViewById(R.id.menu).setVisibility(View.GONE);
			// Show menu trigger
			this.findViewById(R.id.menu_trigger).setVisibility(View.VISIBLE);
		}
	// Click market in menu
	public void clickMarket(View view){
		this.findViewById(R.id.fragment_mask).setVisibility(View.VISIBLE);
		FragmentManager fragmentManager=this.getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
		fragmentTransaction.show(fragmentManager.findFragmentById(R.id.menu_fragment));
		fragmentTransaction.commit();
	}
	// Click close in menu fragment
	public void clickCloseMenuFragment(View view){
		this.findViewById(R.id.fragment_mask).setVisibility(View.GONE);
		FragmentManager fragmentManager=this.getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
		fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.menu_fragment));
		fragmentTransaction.commit();
	}
	// Click close in action fragment
	public void clickCloseActionFragment(View view){
		this.findViewById(R.id.fragment_mask).setVisibility(View.GONE);
		FragmentManager fragmentManager=this.getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
		fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.action_fragment));
		fragmentTransaction.commit();
	}
	// Restore activity states
	private void updateValuesFromBundle(Bundle savedInstanceState){
		if(savedInstanceState!=null){
			// Restore values
		}
	}
	// Location
	private void startLocationUpdates(){
		LocationServices.FusedLocationApi.requestLocationUpdates(
			this.google, this.location, this);
	}
	private void stopLocationUpdates(){
		LocationServices.FusedLocationApi.removeLocationUpdates(
			this.google, this);
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
	// Activity overrides
	@Override
	protected void onStart(){
		super.onStart();
		this.google.connect();
	}
	@Override
	protected void onPause(){
		super.onPause();
		this.stopLocationUpdates();
	}
	@Override
	public void onResume() {
		super.onResume();
		if(this.google.isConnected()){
			startLocationUpdates();
		}
	}
	@Override
	protected void onStop(){
		this.google.disconnect();
		super.onStop();
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){ // Save activity states
		/*
		savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
			mRequestingLocationUpdates);
		savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
		savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
		*/
		super.onSaveInstanceState(savedInstanceState);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus){ // Refocus will call out system ui, rehide
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			this.hideSystemUI(this.findViewById(R.id.main));
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==Main.REQUEST_RESOLVE_ERROR){
			this.triedResolvingError=false;
			if(resultCode==this.RESULT_OK){
				// Make sure the app is not already connected or attempting to connect
				if(!this.google.isConnecting() && !this.google.isConnected()){
					this.google.connect();
				}
			}
		}
	}
	// OnMapReadyCallback implements
	@Override
	public void onMapReady(GoogleMap map){ // First get GoogleMap object on ready callback
		this.map=map;
		this.map.getUiSettings().setMyLocationButtonEnabled(false);
		this.map.setOnCameraChangeListener(this);
		this.map.setOnMarkerClickListener(this);
	}
	// GoogleMap.OnMarkerClickListener implements
	public boolean onMarkerClick(Marker marker){
		Base base=this.bases.get(Long.parseLong(marker.getSnippet()));
		this.findViewById(R.id.fragment_mask).setVisibility(View.VISIBLE);
		FragmentManager fragmentManager=this.getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
		fragmentTransaction.show(fragmentManager.findFragmentById(R.id.action_fragment));
		fragmentTransaction.commit();
		return true;
	}
	// GoogleMap.OnCameraChangeListener implements
	@Override
	public void onCameraChange(CameraPosition position){
		if(this.player.marker==null){
			return;
		}
		// Update resource bases markers
		// Only show bases in 10 kilometers respect to player's position
		LatLngBounds bounds=this.map.getProjection().getVisibleRegion().latLngBounds;
		LatLng playerPosition=this.player.marker.getPosition();
		LatLng north=SphericalUtil.computeOffset(playerPosition, 10000, 0);
		LatLng east=SphericalUtil.computeOffset(playerPosition, 10000, 90);
		LatLng south=SphericalUtil.computeOffset(playerPosition, 10000, 180);
		LatLng west=SphericalUtil.computeOffset(playerPosition, 10000, 270);
		SQLiteDatabase db=(new CookDBHelper(this)).getReadableDatabase();
		Cursor cursor=db.query("material_base", new String[]{"id", "lat", "lng"},
			"(lat BETWEEN "+Math.max(bounds.southwest.latitude, south.latitude)+" and "+Math.min(bounds.northeast.latitude, north.latitude)+") and (lng BETWEEN "+Math.max(bounds.southwest.longitude, west.longitude)+" and "+Math.min(bounds.northeast.longitude, east.longitude)+")",
			null, null, null, null);
		if(cursor==null){
			return;
		}
		if(this.bases==null){
			this.bases=new HashMap<Long, Base>();
		}
		if(cursor.moveToFirst()){
			int idIndex=cursor.getColumnIndex("id");
			int latIndex=cursor.getColumnIndex("lat");
			int lngIndex=cursor.getColumnIndex("lng");
			long id;
			do{
				id=cursor.getLong(idIndex);
				if(!this.bases.containsKey(id)){
					this.bases.put(id, new Base(id, this.map.addMarker(new MarkerOptions().snippet(Long.toString(id))
						.position(new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lngIndex))))));
				}
			}while(cursor.moveToNext());
			cursor.close();
		}
	}
	// LocationListener implements
	@Override
	public void onLocationChanged(Location location){
		if(this.map==null){
			return;
		}
		LatLng position=new LatLng(location.getLatitude(), location.getLongitude());
		// Ignore subtle(less than 3 meters) location change
		if(this.player.marker!=null&&SphericalUtil.computeDistanceBetween(position, this.player.marker.getPosition())<3){
			return;
		}
		// Update player marker and collection circle
		if(this.player.marker==null){
			this.player.range=this.map.addCircle(new CircleOptions()
				.center(position)
				.radius(500)
				.fillColor(this.getResources().getColor(R.color.green_circle_fill))
				.strokeWidth(0));
			this.player.marker=this.map.addMarker(new MarkerOptions()
				.position(position)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.cook_0)));
		}else{
			this.player.range.setCenter(position);
			this.player.marker.setPosition(position);
		}
		// Update map camera: before get visible region bounds
		this.map.moveCamera(CameraUpdateFactory.newLatLng(position));
	}
	// ConnectionCallbacks, OnConnectionFailedListener implements
	@Override
	public void onConnected(Bundle connectionHint){
		// Connected to Google Play services!
		// The good stuff goes here.
		// Start tracking user location
		this.startLocationUpdates();
	}
	@Override
	public void onConnectionSuspended(int cause){
		// The connection has been interrupted.
		// Disable any UI components that depend on Google APIs
		// until onConnected() is called.
	}
	@Override
	public void onConnectionFailed(ConnectionResult result){
		// This callback is important for handling errors that
		// may occur while attempting to connect with Google.
		if(this.triedResolvingError){
			return; // Already attempting to resolve an error.
		}else if(result.hasResolution()){
			try{
				this.triedResolvingError=true;
				result.startResolutionForResult(this, Main.REQUEST_RESOLVE_ERROR);
			}catch(SendIntentException e){
				// There was an error with the resolution intent. Try again.
				this.google.connect();
			}
		}else{
			// Show dialog using GooglePlayServicesUtil.getErrorDialog()
			this.showErrorDialog(result.getErrorCode());
			this.triedResolvingError=true;
		}
	}
	// The rest of this code is all about building the error dialog
	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode){
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment=new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args=new Bundle();
		args.putInt(Main.DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(this.getFragmentManager(), "errordialog");
	}
	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed(){
		this.triedResolvingError=false;
	}
    /* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment{
		public ErrorDialogFragment(){}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			// Get the error code and retrieve the appropriate dialog
			int errorCode=this.getArguments().getInt(Main.DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
				this.getActivity(), Main.REQUEST_RESOLVE_ERROR);
		}
		@Override
		public void onDismiss(DialogInterface dialog){
			((Main)this.getActivity()).onDialogDismissed();
		}
	}
}