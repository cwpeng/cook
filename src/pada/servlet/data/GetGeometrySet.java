package pada.servlet.data;
import pada.data.GeometrySet;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class GetGeometrySet extends HttpServlet{ // This servlet is for latlng data download
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("id")==null){
			return;
		}else{
			GeometrySet set=GeometrySet.getGeometrySet(Long.parseLong(request.getParameter("id")));
			if(set==null){
				return;
			}
			StringBuilder result=new StringBuilder();
			for(int i=0;i<set.data.length;i++){
				if(i>0){
					result.append("\r\n");
				}
				result.append(set.data[i][0]+","+set.data[i][1]);
			}
			// Make Output
			if(request.getParameter("download")!=null){
				response.setHeader("Content-disposition", "attachment; filename="+set.name);
			}
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain");
			PrintWriter out=response.getWriter();
			out.print(result.toString());
			out.flush(); out.close();
		}
	}
}