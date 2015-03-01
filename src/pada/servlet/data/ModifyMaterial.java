package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class ModifyMaterial extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// 取得並驗證使用者輸入資訊
		long id;
		String name, description;
		if(request.getParameter("id")==null||request.getParameter("name")==null||request.getParameter("description")==null){
			return;
		}else{
			response.setHeader("result", pada.data.Material.modifyMaterial(Long.parseLong(request.getParameter("id")),
				request.getParameter("name"), request.getParameter("description")) + "");
		}
	}
}