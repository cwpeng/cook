package pada.servlet.util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
public class UploadMaterialImage extends HttpServlet{
	private BlobstoreService blobstoreService=BlobstoreServiceFactory.getBlobstoreService();
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		java.util.Set<java.util.Map.Entry<String, java.util.List<BlobKey>>> uploads=blobstoreService.getUploads(request).entrySet();
		String name;
		BlobKey blobKey;
		String blobKeyStr="";
		for(java.util.Map.Entry<String, java.util.List<BlobKey>> entry: uploads){
			name=entry.getKey();
			blobKey=entry.getValue().get(0);
			pada.data.Image.createImage(name, blobKey);
			blobKeyStr=blobKey.getKeyString();
		}
		// Make Output
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		out.print("<script>window.parent.gm.img.material.uploaded({\"blobKey\":\""+blobKeyStr+"\"});</script>");
		out.flush(); out.close();
	}
}