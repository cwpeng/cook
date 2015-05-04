package biz.pada.cook.service;
import biz.pada.cook.R;
import biz.pada.cook.ui.ShareUI;
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
		ShareUI.alert(context, R.string.network_unavailable);
	}
}