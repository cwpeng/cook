package pada.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class MaterialBase implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	// Static Method
	public static int delete(){
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		Query query=new Query("MaterialBase").setKeysOnly();
		Iterable<Entity> entities=datastore.prepare(query).asIterable();
		List<Key> keys=new ArrayList<Key>();
		for(Entity entity: entities){
			keys.add(entity.getKey());
		}
		datastore.delete(keys);
		return keys.size();
	}
	public static MaterialBase[] generate(){
		Material[] materials=Material.getMaterials();
		List<MaterialBase> baseList=new ArrayList<MaterialBase>(32768);
		GeometrySet geometrySet;
		for(int i=0;i<materials.length;i++){
			if(materials[i].geometrySet==0){
				continue;
			}
			geometrySet=GeometrySet.getGeometrySet(materials[i].geometrySet);
			if(geometrySet==null){
				continue;
			}
			for(int j=0;j<geometrySet.data.length;j++){
				baseList.add(new MaterialBase(0, geometrySet.data[j][0], geometrySet.data[j][1], new long[]{materials[i].id}));
			}
		}
		MaterialBase[] bases=baseList.toArray(new MaterialBase[0]);
		java.util.Arrays.sort(bases, new java.util.Comparator<MaterialBase>(){
			public int compare(MaterialBase b1, MaterialBase b2){
				return b2.lat-b1.lat>0?-1:1;
			}
		});
		// Combined bases where distance between whom is lower than 500 meters, about 0.005 longitude or 0.0045 latitude.
		MaterialBase base;
		for(int i=0;i<bases.length;i++){
			base=bases[i];
			if(base==null){
				continue;
			}
			// Upward check
			for(int j=i+1;j<bases.length;j++){
				if(bases[j]==null){
					continue;
				}
				if(bases[j].lat-base.lat>0.005){
					break;
				}
				if(MaterialBase.distance(bases[j].lat, bases[j].lng, base.lat, base.lng)<500){
					MaterialBase.combine(base, bases[j]);
					bases[j]=null;
				}
			}
			// Downward check
			for(int j=i-1;j>-1;j--){
				if(bases[j]==null){
					continue;
				}
				if(base.lat-bases[j].lat>0.005){
					break;
				}
				if(MaterialBase.distance(bases[j].lat, bases[j].lng, base.lat, base.lng)<500){
					MaterialBase.combine(base, bases[j]);
					bases[j]=null;
				}
			}
		}
		//MaterialBase.save(bases);
		return bases;
	}
		private static void combine(MaterialBase base1, MaterialBase base2){
			// Calculate repeated materials
			int repeatedCount=0;
			for(int i=0;i<base2.materials.length;i++){
				for(int j=0;j<base1.materials.length;j++){
					if(base2.materials[i]==base1.materials[j]){
						base2.materials[i]=0;
						repeatedCount++;
						break;
					}
				}
			}
			// Combine base2 materials to base1, excluding repeated ones.
			long[] materials=base1.materials;
			base1.materials=new long[materials.length+base2.materials.length-repeatedCount];
			System.arraycopy(materials, 0, base1.materials, 0, materials.length);
			int currentIndex=materials.length;
			for(int i=0;i<base2.materials.length;i++){
				if(base2.materials[i]==0){
					continue;
				}
				base1.materials[currentIndex]=base2.materials[i];
				currentIndex++;
			}
		}
		// In Taiwan, latitude is about 23, 1 longitude equals about 100 kilometers, 1 latitude equals about 111 kilometers
		private static double distance(double lat1, double lng1, double lat2, double lng2){
			lat1=lat1*1.11;
			lat2=lat2*1.11;
			return Math.sqrt(Math.pow(lat2-lat1, 2)+Math.pow(lng2-lng1, 2))*100000;
		}
	/* General distance calculator between geometry lat/lng
		private static double distance(double lat1, double lng1, double lat2, double lng2){
			double earthRadius=6371000; // 6371000 meters
			double dLat=Math.toRadians(lat2-lat1);
			double dLng=Math.toRadians(lng2-lng1);
			double sindLat=Math.sin(dLat / 2);
			double sindLng=Math.sin(dLng / 2);
			double a=Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
			double c=2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			double dist=earthRadius * c;
			return dist;
		}
	*/
	/*
		private static boolean save(MaterialBase[] bases){
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
	public double lat, lng;
	public long[] materials; // material ids
	public MaterialBase(long id, double lat, double lng, long[] materials){
		this.id=id;
		this.lat=lat;
		this.lng=lng;
		this.materials=materials;
	}
}