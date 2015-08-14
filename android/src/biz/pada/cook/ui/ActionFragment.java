package biz.pada.cook.ui;
import biz.pada.cook.R;
import biz.pada.cook.core.Base;
import biz.pada.cook.core.Material;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.LinearLayout;
public class ActionFragment extends Fragment{
	public Base base;
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

		}else{ // Show
			if(this.base!=null){
				LinearLayout content=(LinearLayout)this.getActivity().findViewById(R.id.action_fragment_content);
				LinearLayout row;
				content.removeAllViews();
				Material material;
				TextView text;
				row=new LinearLayout(this.getActivity());
				row.setOrientation(LinearLayout.HORIZONTAL);
				for(int i=0;i<this.base.materialIds.length;i++){
					material=Material.getMaterial(this.getActivity(), this.base.materialIds[i]);
					text=new TextView(this.getActivity());
					text.setText(material.name);
					row.addView(text);
				}
				content.addView(row);
			}
		}
	}
}