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
	public Base.State getState(){
		int changeInterval=4*60*60*1000;
		// About 100 meters to shift offset
		int hourOffset=(((int)((this.marker.getPosition().latitude+this.marker.getPosition().longitude)*1000))%24)*60*60*1000;
		long seed=System.currentTimeMillis()+hourOffset;
		int nextCountdown=changeInterval-(int)(seed%changeInterval);
		int currentIndex=(int)((seed/changeInterval)%this.materialIds.length);
		return new Base.State(this.materialIds[currentIndex], nextCountdown);
	}
	// Static class for Base state
	public static class State{
		public long currentMaterialId;
		public int nextCountdown;
		private State(long currentMaterialId, int nextCountdown){
			this.currentMaterialId=currentMaterialId;
			this.nextCountdown=nextCountdown;
		}
	}
}