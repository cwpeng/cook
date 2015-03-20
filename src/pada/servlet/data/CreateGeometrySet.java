package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class CreateGeometrySet extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// 取得並驗證使用者輸入資訊
		if(request.getParameter("name")==null||request.getParameter("description")==null||request.getParameter("data")==null||request.getParameter("src")==null){
			return;
		}else{
			String[] data=request.getParameter("data").split(";");
			if(request.getParameter("src").equals("input")){
				double[][] latlngs=new double[data.length][2];
				String[] latlng;
				for(int i=0;i<data.length;i++){
					latlng=data[i].split(",");
					latlngs[i][0]=Double.parseDouble(latlng[0]);
					latlngs[i][1]=Double.parseDouble(latlng[1]);
				}
				response.setHeader("result", pada.data.GeometrySet.createGeometrySet(request.getParameter("name"), request.getParameter("description"), latlngs) + "");
			}else if(request.getParameter("src").equals("combine")){
				long[] ids=new long[data.length];
				for(int i=0;i<data.length;i++){
					ids[i]=Long.parseLong(data[i]);
				}
				response.setHeader("result", pada.data.GeometrySet.createGeometrySet(request.getParameter("name"), request.getParameter("description"), ids) + "");
			}else{
				response.setHeader("result", "false");
			}
		}
	}
}