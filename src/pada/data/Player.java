package pada.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class Player implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	// Static Method
	public static Player login(String imei, String password){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Date now=new Date();
				Query query=new Query("Player").setFilter(new FilterPredicate("imei", FilterOperator.EQUAL, imei));
				String token=pada.util.SHA.digestToHex(password+"3i%@dsD45Q"+(Math.random()*100000));
				Entity player=datastore.prepare(query).asSingleEntity();
				if(player==null){
					player=new Entity("Player");
					player.setProperty("imei", imei);
					player.setProperty("password", password);
					player.setProperty("token", token);
					player.setProperty("name", null);
					player.setProperty("login-time", now);
					player.setProperty("create-time", now);
				}else{
					player.setProperty("token", token);
					player.setProperty("login-time", now);
				}
				datastore.put(player);
				txn.commit();
				return new Player(player.getKey().getId(), (String)player.getProperty("token"), (String)player.getProperty("name"));
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Material.class.getName()).warning(e.toString());
					return null;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Material.class.getName()).warning(e.toString());
				return null;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	/*
	public static boolean createMaterial(String name, String description){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity material=new Entity("Material");
				material.setProperty("name", name);
				material.setUnindexedProperty("description", description);
				material.setProperty("cookbook", 0); // Cookbook number this material supported
				material.setProperty("create-time", new Date());
				datastore.put(material);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Materials");
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
	public static Material getMaterial(long id){
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity=datastore.get(KeyFactory.createKey("Material", id));
			return new Material(entity.getKey().getId(),
				(String)entity.getProperty("name"),
				(String)entity.getProperty("description"),
				((Number)entity.getProperty("cookbook")).intValue());
		}catch(Exception e){
			Logger.getLogger(Material.class.getName()).warning(e.toString());
			return null;
		}
	}
	public static Material[] getMaterials(){
		Material[] materials;
		MemcacheService cache=MemcacheServiceFactory.getMemcacheService();
		if((materials=(Material[])cache.get("Materials"))==null){
			Query query=new Query("Material");
			Iterable<Entity> entities= DatastoreServiceFactory.getDatastoreService().prepare(query).asIterable();
			List<Material> list=new ArrayList<Material>();
			for(Entity entity: entities){
				list.add(new Material(entity.getKey().getId(),
					(String)entity.getProperty("name"),
					(String)entity.getProperty("description"),
					((Number)entity.getProperty("cookbook")).intValue()));
			}
			materials=list.toArray(new Material[0]);
			cache.put("Materials", materials);
		}
		return materials;
	}
	public static boolean deleteMaterial(long id){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity material=datastore.get(KeyFactory.createKey("Material", id));
				if(((Number)material.getProperty("cookbook")).intValue()<1){
					datastore.delete(material.getKey());
				}
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Materials");
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
	public static boolean modifyMaterial(long id, String name, String description){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity material=datastore.get(KeyFactory.createKey("Material", id));
				material.setProperty("name", name);
				material.setUnindexedProperty("description", description);
				datastore.put(material);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Materials");
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
	*/
	// Instance Definition
	public long id;
	public String token;
	public String name;
	public Player(long id, String token, String name){
		this.id=id;
		this.token=token;
		this.name=name;
	}
}