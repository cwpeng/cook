package biz.pada.cook.ui;
import biz.pada.cook.R;
import biz.pada.cook.core.Base;
import biz.pada.cook.core.Material;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.LinearLayout;
public class ActionFragment extends Fragment{
	public Base base;
	public CountDownTimer timer;
	public void setBase(Base base){
		this.base=base;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.action_fragment, container, false);
	}
	@Override
	public void onHiddenChanged(boolean hidden){
		super.onHiddenChanged(hidden);
		if(hidden){ // Hide
			if(this.timer!=null){ // Cancel timer if exists
				this.timer.cancel();
				this.timer=null;
			}
		}else{ // Show
			Activity activity=this.getActivity();
			if(this.base!=null){
				Base.State state=this.base.getState();
				// Add title
				Material material=Material.getMaterial(activity, state.currentMaterialId);
				TextView title=(TextView)activity.findViewById(R.id.action_fragment_title);
				title.setText(material.name);
				// Add content
				LinearLayout content=(LinearLayout)activity.findViewById(R.id.action_fragment_content);
				LinearLayout row;
				content.removeAllViews();
				TextView text;
				row=new LinearLayout(activity);
				row.setOrientation(LinearLayout.HORIZONTAL);
				for(int i=0;i<this.base.materialIds.length;i++){
					material=Material.getMaterial(activity, this.base.materialIds[i]);
					text=new TextView(activity);
					text.setText(material.name);
					row.addView(text);
				}
				content.addView(row);
				// Add countdown text
				TextView countdown=new TextView(activity);
				content.addView(countdown);
				// Set timer
				this.timer=new MaterialChangeTimer(state, title, countdown);
				this.timer.start();
			}
		}
	}
	// Private static class
	private static class MaterialChangeTimer extends CountDownTimer{
		private Base.State state;
		private TextView title;
		private TextView countdown;
		private MaterialChangeTimer(Base.State state, TextView title, TextView countdown){
			super(state.nextCountdown, 1000);
			this.state=state;
			this.title=title;
			this.countdown=countdown;
		}
		@Override
		public void onTick(long millisUntilFinished){
			this.state.nextCountdown=(int)millisUntilFinished;
			int seconds=this.state.nextCountdown/1000;
			int hours=seconds/3600;
			int minutes=(seconds%3600)/60;
			seconds=seconds%60;
			this.countdown.setText((hours<10?"0"+hours:hours)+":"+(minutes<10?"0"+minutes:minutes)+":"+(seconds<10?"0"+seconds:seconds));
		}
		@Override
		public void onFinish(){
			
		}
	}
}