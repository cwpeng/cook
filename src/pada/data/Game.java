package pada.data;
import pada.data.*;
import pada.data.action.*;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class Game{
	// Static Method
	/*
		Conditions of collect material:
			1. In the distance.
			2. Not in cooldown.
	*/
	public static boolean collectMaterial(Player player, long baseId, long materialId){
		MaterialBase base=MaterialBase.get(baseId);
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				CollectMaterial collect=CollectMaterial.get(datastore, player.id, baseId);
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Material.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Material.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
}