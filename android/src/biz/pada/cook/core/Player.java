package biz.pada.cook.core;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Circle;
public class Player implements java.io.Serializable{
	private static final long serialVersionUID=0L;
	// instance members
	public long id;
	public String token;
	public String name;
	public Marker marker;
	public Circle range;
	public Player(long id, String token, String name){
		this.id=id;
		this.token=token;
		this.name=name;
	}
}