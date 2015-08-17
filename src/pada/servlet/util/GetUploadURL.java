package pada.servlet.util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
public class GetUploadURL extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("url")==null){
			return;
		}
		String url=BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(request.getParameter("url"));
		// Make Output
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.print("{\"url\":\""+url+"\"}");
		out.flush(); out.close();
	}
}