package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
public class DeleteImage extends HttpServlet{
	private BlobstoreService blobstoreService=BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imagesService=ImagesServiceFactory.getImagesService();
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// 取得並驗證使用者輸入資訊
		if(request.getParameter("name")==null){
			return;
		}else{
			BlobKey blobKey;
			if((blobKey=pada.data.Image.deleteImage(request.getParameter("name")))==null){
				response.setHeader("result", "false");
			}else{
				blobstoreService.delete(blobKey);
				imagesService.deleteServingUrl(blobKey);
				response.setHeader("result", "true");
			}
		}
	}
}