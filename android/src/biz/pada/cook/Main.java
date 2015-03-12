package biz.pada.cook;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.view.View;
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
public class Main extends Activity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	// Static final constants
	private static final int REQUEST_RESOLVE_ERROR=1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR="dialog_error"; // Unique tag for the error dialog fragment
	// Instance variables
	private GoogleApiClient google;
	private LocationRequest location;
	// Map objects
	private GoogleMap map;
	private Marker marker;
	private Circle circle;
	// Track whether this activity is already resolving an error about coonecting to Google Play Service
	private boolean triedResolvingError;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
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
		MapFragment mapFragment=(MapFragment)this.getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		this.hideSystemUI(this.findViewById(R.id.main));
	}
	private void updateValuesFromBundle(Bundle savedInstanceState){ // Restore activity states
		if(savedInstanceState!=null){
			// Restore values
		}
	}
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
	}
	// LocationListener implements
	@Override
	public void onLocationChanged(Location location){
		if(this.map==null){
			return;
		}
		LatLng position=new LatLng(location.getLatitude(), location.getLongitude());
		if(this.marker==null){
			this.circle=this.map.addCircle(new CircleOptions()
				.center(position)
				.radius(500)
				.fillColor(this.getResources().getColor(R.color.green_circle_fill))
				.strokeWidth(0));
			this.marker=this.map.addMarker(new MarkerOptions()
				.position(position)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.cook_0)));
		}else{
			this.circle.setCenter(position);
			this.marker.setPosition(position);
		}
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