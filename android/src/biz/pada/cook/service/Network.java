package biz.pada.cook.service;
import biz.pada.cook.R;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class Network{
	public static boolean isOnline(Context context){
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	public static void showNetworkUnavailable(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.network_unavailable).setCancelable(false).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){}
		});
		builder.create().show();
	}
}