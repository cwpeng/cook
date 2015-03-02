package pada.data;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
public class Data{
	public static byte[] toBytes(Object object){
		ObjectOutputStream out = null;
		try{
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bytes);
			out.writeObject(object);
			return bytes.toByteArray();
		}catch(IOException e){
			Logger.getLogger(Data.class.getName()).warning(e.toString());
			return null;
		}finally{
			try{
				if(out != null){
					out.close();
				}
			}catch(IOException e){
				Logger.getLogger(Data.class.getName()).warning(e.toString());
				return null;
			}
		}
	}
	public static Object fromBytes(byte[] bytes){
		ObjectInputStream in = null;
		try{
			in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return in.readObject();
		}catch(ClassNotFoundException e){
			Logger.getLogger(Data.class.getName()).warning(e.toString());
			return null;
		}catch(IOException e){
			Logger.getLogger(Data.class.getName()).warning(e.toString());
			return null;
		}finally{
			try{
				if(in != null){
					in.close();
				}
			}catch(IOException e){
				Logger.getLogger(Data.class.getName()).warning(e.toString());
				return null;
			}
		}
	}
}