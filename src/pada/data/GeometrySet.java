package pada.data;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class GeometrySet implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	public static boolean createGeometrySet(String name, String description, double[][] data){ // create new set by raw data
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity set=new Entity("GeometrySet");
				set.setProperty("name", name);
				set.setProperty("description", description);
				set.setProperty("number", data.length); // Total geometry latlng number in this set
				set.setUnindexedProperty("data", new Blob(Data.toBytes(data)));
				datastore.put(set);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("GeometrySets");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	public static boolean createGeometrySet(String name, String description, long[] srcIds){ // create new set by existed sets
		ArrayList<double[]> data=new ArrayList<double[]>();
		GeometrySet set;
		for(int i=0;i<srcIds.length;i++){
			if((set=GeometrySet.getGeometrySet(srcIds[i]))==null){
				return false;
			}
			for(int j=0;j<set.data.length;j++){
				data.add(set.data[j]);
			}
		}
		return GeometrySet.createGeometrySet(name, description, data.toArray(new double[0][0]));
	}
	public static GeometrySet[] getGeometrySets(){
		GeometrySet[] sets;
		MemcacheService cache=MemcacheServiceFactory.getMemcacheService();
		if((sets=(GeometrySet[])cache.get("GeometrySets"))==null){
			Query query=new Query("GeometrySet");
			query.addProjection(new PropertyProjection("name", String.class));
			query.addProjection(new PropertyProjection("description", String.class));
			query.addProjection(new PropertyProjection("number", Integer.class));
			query.addSort("name", Query.SortDirection.ASCENDING);
			Iterable<Entity> entities= DatastoreServiceFactory.getDatastoreService().prepare(query).asIterable();
			List<GeometrySet> list=new ArrayList<GeometrySet>();
			for(Entity entity: entities){
				list.add(new GeometrySet(entity.getKey().getId(),
					(String)entity.getProperty("name"),
					(String)entity.getProperty("description"),
					((Number)entity.getProperty("number")).intValue()));
			}
			sets=list.toArray(new GeometrySet[0]);
			cache.put("GeometrySets", sets);
		}
		return sets;
	}
	public static GeometrySet getGeometrySet(long id){
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity=datastore.get(KeyFactory.createKey("GeometrySet", id));
			return new GeometrySet(entity.getKey().getId(),
				(String)entity.getProperty("name"),
				(String)entity.getProperty("description"),
				((Number)entity.getProperty("number")).intValue(),
				(double[][])Data.fromBytes(((Blob)entity.getProperty("data")).getBytes()));
		}catch(Exception e){
			Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
			return null;
		}
	}
	public static boolean modifyGeometrySet(long id, String name, String description){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity set=datastore.get(KeyFactory.createKey("GeometrySet", id));
				set.setProperty("name", name);
				set.setProperty("description", description);
				datastore.put(set);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("GeometrySets");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	public static boolean deleteGeometrySet(long id){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				datastore.delete(KeyFactory.createKey("GeometrySet", id));
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("GeometrySets");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(GeometrySet.class.getName()).warning(e.toString());
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
	public int number;
	public double[][] data;
	public GeometrySet(long id, String name, String description, int number){
		this.id=id;
		this.name=name;
		this.description=description;
		this.number=number;
	}
	public GeometrySet(long id, String name, String description, int number, double[][] data){
		this.id=id;
		this.name=name;
		this.description=description;
		this.number=number;
		this.data=data;
	}
}