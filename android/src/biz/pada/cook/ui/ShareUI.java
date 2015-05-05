package biz.pada.cook.ui;
import biz.pada.cook.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
public class ShareUI{
	public static void alert(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){}
		});
		builder.create().show();
	}
	public static void alert(Context context, int messageId){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(messageId).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){}
		});
		builder.create().show();
	}
}