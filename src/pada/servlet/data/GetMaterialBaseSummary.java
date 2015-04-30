package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.appengine.api.datastore.Entity;
public class GetMaterialBaseSummary extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		Entity summary=pada.data.MaterialBase.getMaterialBaseSummary();
		long count=((Long)summary.getProperty("count")).longValue();
		long updateTime=((java.util.Date)summary.getProperty("timestamp")).getTime();
		// Make Output
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.print("{\"generated\":"+((Boolean)summary.getProperty("generated")).booleanValue()+",\"count\":"+count+",\"updateTime\":"+updateTime+"}");
		out.flush(); out.close();
	}
}