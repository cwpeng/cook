package biz.pada.cook.core;
import com.google.android.gms.maps.model.Marker;
public class Base{
	// instance members
	public long id;
	public Marker marker;
	public Base(long id, Marker marker){
		this.id=id;
		this.marker=marker;
	}
}