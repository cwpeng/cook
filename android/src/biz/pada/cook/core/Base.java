package biz.pada.cook.core;
import com.google.android.gms.maps.model.Marker;
public class Base{
	// instance members
	public long id;
	public long[] materialIds;
	public Marker marker;
	public Base(long id, long[] materialIds, Marker marker){
		this.id=id;
		this.materialIds=materialIds;
		this.marker=marker;
	}
	// Get current collectable material id based on material number and time.
	// Change every 4 hours.
	public long getCurrentMaterialId(){
		int changeInterval=4*60*60*1000;
		int currentIndex=(int)((System.currentTimeMillis()/changeInterval)%this.materialIds.length);
		return this.materialIds[currentIndex];
	}
}