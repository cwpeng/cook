package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class ModifyCookbook extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		// 取得並驗證使用者輸入資訊
		if(request.getParameter("id")==null||request.getParameter("name")==null||request.getParameter("description")==null||request.getParameter("materials")==null){
			return;
		}else{
			String[] materialsData=request.getParameter("materials").split(";");
			String[] materialData;
			long[][] materials=new long[materialsData.length][2];
			for(int i=0;i<materialsData.length;i++){
				materialData=materialsData[i].split(":");
				materials[i][0]=Long.parseLong(materialData[0]);
				materials[i][1]=Long.parseLong(materialData[1]);
			}
			response.setHeader("result", pada.data.Cookbook.modifyCookbook(Long.parseLong(request.getParameter("id")), request.getParameter("name"), request.getParameter("description"), materials) + "");
		}
	}
}