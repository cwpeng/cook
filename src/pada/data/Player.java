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
	public static boolean check(long id, String token){
		try{
			Entity player=DatastoreServiceFactory.getDatastoreService().get(KeyFactory.createKey("Player", id));
			return player.getProperty("token").equals(token);
		}catch(Exception e){
			Logger.getLogger(Material.class.getName()).warning(e.toString());
			return false;
		}
	}
	public static long signup(String imei, String password, String name){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Date now=new Date();
				Entity player=new Entity("Player");
				player.setProperty("imei", imei);
				player.setProperty("password", password);
				player.setProperty("token", null);
				player.setProperty("name", name);
				player.setProperty("login-time", now);
				player.setProperty("create-time", now);
				datastore.put(player);
				txn.commit();
				return player.getKey().getId();
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Material.class.getName()).warning(e.toString());
					return -1;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Material.class.getName()).warning(e.toString());
				return -1;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	public static Player signin(long id, String password){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity player=datastore.get(KeyFactory.createKey("Player", id));
				if(!player.getProperty("password").equals(password)){
					return null;
				}
				String token=pada.util.SHA.digestToHex(password+"3i%@dsD45Q"+(Math.random()*100000));
				player.setProperty("token", token);
				player.setProperty("login-time", new Date());
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