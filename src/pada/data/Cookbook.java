package pada.data;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
// Cookbook data is read-only in Game. Only update offline (under maintenance).
public class Cookbook implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	// Static Method
	public static boolean createCookbook(String name, String description, long[][] materials){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
			try{
				Entity cookbook=new Entity("Cookbook");
				cookbook.setProperty("name", name);
				cookbook.setUnindexedProperty("description", description);
				cookbook.setProperty("materials", new ShortBlob(Data.toBytes(materials)));
				cookbook.setProperty("create-time", new Date());
				datastore.put(cookbook);
				Entity material;
				for(int i=0;i<materials.length;i++){
					material=datastore.get(KeyFactory.createKey("Material", materials[i][0]));
					material.setProperty("cookbook", ((Number)material.getProperty("cookbook")).intValue()+1);
					datastore.put(material);
				}
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Materials");
				MemcacheServiceFactory.getMemcacheService().delete("Cookbooks");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Cookbook.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Cookbook.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	public static Cookbook getCookbook(long id){
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity=datastore.get(KeyFactory.createKey("Cookbook", id));
			return new Cookbook(entity.getKey().getId(),
				(String)entity.getProperty("name"),
				(String)entity.getProperty("description"),
				(long[][])Data.fromBytes(((ShortBlob)entity.getProperty("materials")).getBytes()));
		}catch(Exception e){
			Logger.getLogger(Cookbook.class.getName()).warning(e.toString());
			return null;
		}
	}
	public static Cookbook[] getCookbooks(){
		Cookbook[] cookbooks;
		MemcacheService cache=MemcacheServiceFactory.getMemcacheService();
		if((cookbooks=(Cookbook[])cache.get("Cookbooks"))==null){
			Query query=new Query("Cookbook");
			Iterable<Entity> entities= DatastoreServiceFactory.getDatastoreService().prepare(query).asIterable();
			List<Cookbook> list=new ArrayList<Cookbook>();
			for(Entity entity: entities){
				list.add(new Cookbook(entity.getKey().getId(),
					(String)entity.getProperty("name"),
					(String)entity.getProperty("description"),
					(long[][])Data.fromBytes(((ShortBlob)entity.getProperty("materials")).getBytes())));
			}
			cookbooks=list.toArray(new Cookbook[0]);
			cache.put("Cookbooks", cookbooks);
		}
		return cookbooks;
	}
	public static boolean deleteCookbook(long id){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
			try{
				Entity cookbook=datastore.get(KeyFactory.createKey("Cookbook", id));
				long[][] materials=(long[][])Data.fromBytes(((ShortBlob)cookbook.getProperty("materials")).getBytes());
				Entity material;
				for(int i=0;i<materials.length;i++){
					material=datastore.get(KeyFactory.createKey("Material", materials[i][0]));
					material.setProperty("cookbook", ((Number)material.getProperty("cookbook")).intValue()-1);
					datastore.put(material);
				}
				datastore.delete(cookbook.getKey());
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Materials");
				MemcacheServiceFactory.getMemcacheService().delete("Cookbooks");
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
	public static boolean modifyCookbook(long id, String name, String description, long[][] materials){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity cookbook=datastore.get(KeyFactory.createKey("Cookbook", id));
				cookbook.setProperty("name", name);
				cookbook.setUnindexedProperty("description", description);
				cookbook.setProperty("materials", new ShortBlob(Data.toBytes(materials)));
				datastore.put(cookbook);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Cookbooks");
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
	// Instance Definition
	public long id;
	public String name;
	public String description;
	public long[][] materials;
	public Cookbook(long id, String name, String description, long[][] materials){
		this.id=id;
		this.name=name;
		this.description=description;
		this.materials=materials;
	}
}