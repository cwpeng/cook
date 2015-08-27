package pada.data.action;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class CollectMaterial implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	// Static Method
	public static boolean save(long playerId, long baseId){		
		return true;
	}
	public static CollectMaterial get(DatastoreService datastore, long playerId, long baseId) throws ConcurrentModificationException, Exception{
		Query query=new Query("CollectMaterial").setFilter(
			CompositeFilterOperator.and(
				new FilterPredicate("player-id", FilterOperator.EQUAL, playerId),
				new FilterPredicate("base-id", FilterOperator.EQUAL, baseId)
			)
		);
		Entity entity=datastore.prepare(datastore.getCurrentTransaction(), query).asSingleEntity();
		if(entity==null){
			return null;
		}else{
			return new CollectMaterial(
				entity.getKey().getId(),
				((Number)entity.getProperty("player-id")).longValue(),
				((Number)entity.getProperty("base-id")).longValue(),
				(Date)entity.getProperty("time")
			);
		}
	}
	// Instance Definition
	public long id;
	public long playerId;
	public long baseId;
	public Date time;
	public CollectMaterial(long id, long playerId, long baseId, Date time){
		this.id=id;
		this.playerId=playerId;
		this.baseId=baseId;
		this.time=time;
	}
}