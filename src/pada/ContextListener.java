package pada;
import java.io.File;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
public class ContextListener implements ServletContextListener{
    public void contextInitialized(ServletContextEvent e){
		ServletContext context = e.getServletContext();
		// 找到網站重要的路徑並儲存
		// 以下程式確保在不同環境下的路徑一致性 ( 確保結尾都有加上 File Separator )
		String separatorCharacter = File.separator;
		if(context.getRealPath("/").endsWith(File.separator)){
			separatorCharacter = "";
		}
		System.setProperty("cook.root", context.getRealPath("/") + separatorCharacter);
		System.setProperty("cook.res", context.getRealPath("/") + separatorCharacter + "WEB-INF" + File.separator + "res" + File.separator);
		// 儲存初始化參數
		System.setProperty("cook.resources.version", context.getInitParameter("RESOURCES_VERSION"));
	}
	public void contextDestroyed(ServletContextEvent e){}
}