package biz.pada.cook.ui;
import biz.pada.cook.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
public class MenuFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.menu_fragment, container, false);
    }
}