package pada.data;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.util.ConcurrentModificationException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
public class Image implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	// Static Method
	public static boolean createImage(String name, BlobKey blobKey){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				Entity img=new Entity("Image", name);
				img.setUnindexedProperty("blob-key", blobKey);
				datastore.put(img);
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Images");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Image.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Image.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	public static Image getImage(String name){
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		try{
			Entity entity=datastore.get(KeyFactory.createKey("Image", name));
			return new Image(entity.getKey().getName(), (BlobKey)entity.getProperty("blob-key"));
		}catch(Exception e){
			Logger.getLogger(Image.class.getName()).warning(e.toString());
			return null;
		}
	}
	public static Image[] getImages(){
		Image[] imgs;
		MemcacheService cache=MemcacheServiceFactory.getMemcacheService();
		if((imgs=(Image[])cache.get("Images"))==null){
			Query query=new Query("Image");
			Iterable<Entity> entities= DatastoreServiceFactory.getDatastoreService().prepare(query).asIterable();
			List<Image> list=new ArrayList<Image>();
			for(Entity entity: entities){
				list.add(new Image(entity.getKey().getName(), (BlobKey)entity.getProperty("blob-key")));
			}
			imgs=list.toArray(new Image[0]);
			cache.put("Images", imgs);
		}
		return imgs;
	}
	public static boolean deleteImage(String name){
		int retries=1;
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		while(true){
			Transaction txn=datastore.beginTransaction();
			try{
				datastore.delete(KeyFactory.createKey("Image", name));
				txn.commit();
				// 清空快取
				MemcacheServiceFactory.getMemcacheService().delete("Images");
				return true;
			}catch(ConcurrentModificationException e){
				if(retries==0){
					Logger.getLogger(Image.class.getName()).warning(e.toString());
					return false;
				}
				retries--;
			}catch(Exception e){
				Logger.getLogger(Image.class.getName()).warning(e.toString());
				return false;
			}finally{
				if(txn.isActive()){
					txn.rollback();
				}
			}
		}
	}
	// Instance Definition
	public String name;
	public BlobKey blobKey;
	public Image(String name, BlobKey blobKey){
		this.name=name;
		this.blobKey=blobKey;
	}
}