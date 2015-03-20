package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class DeleteGeometrySet extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// 取得並驗證使用者輸入資訊
		if(request.getParameter("id")==null){
			return;
		}else{
			response.setHeader("result", pada.data.GeometrySet.deleteGeometrySet(Long.parseLong(request.getParameter("id"))) + "");
		}
	}
}