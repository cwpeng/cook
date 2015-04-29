package pada.servlet.data;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
public class DeleteMaterialBases extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		int result;
		while(true){
			result=pada.data.MaterialBase.delete();
			if(result<=0){ // Completed
				break;
			}
		}
		response.setIntHeader("result", result); // 0: completed, -1:error happened
	}
}