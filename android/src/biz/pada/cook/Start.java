package biz.pada.cook;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.location.Location;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.*;
public class Start extends FragmentActivity
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
	// Static final constants
	private static final int REQUEST_RESOLVE_ERROR=1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR="dialog_error"; // Unique tag for the error dialog fragment
	// Instance variables
	private GoogleApiClient google;
	private LocationRequest location;
	// Track whether this activity is already resolving an error about coonecting to Google Play Service
	private boolean triedResolvingError;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.google=new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();
		this.location=new LocationRequest();
		this.location.setInterval(10000);
		this.location.setFastestInterval(5000);
		this.location.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		this.triedResolvingError=false;
		//String imei=((TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE)).getDeviceId();
		this.updateValuesFromBundle(savedInstanceState);
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
	// Activity override
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==Start.REQUEST_RESOLVE_ERROR){
			this.triedResolvingError=false;
			if(resultCode==this.RESULT_OK){
				// Make sure the app is not already connected or attempting to connect
				if(!this.google.isConnecting() && !this.google.isConnected()){
					this.google.connect();
				}
			}
		}
	}
	// LocationListener implements
	@Override
	public void onLocationChanged(Location location){
		System.out.println(location.getLatitude()+","+location.getLongitude());
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
				result.startResolutionForResult(this, Start.REQUEST_RESOLVE_ERROR);
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
		args.putInt(Start.DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(this.getSupportFragmentManager(), "errordialog");
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
			int errorCode=this.getArguments().getInt(Start.DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
				this.getActivity(), Start.REQUEST_RESOLVE_ERROR);
		}
		@Override
		public void onDismiss(DialogInterface dialog){
			((Start)this.getActivity()).onDialogDismissed();
		}
	}
}