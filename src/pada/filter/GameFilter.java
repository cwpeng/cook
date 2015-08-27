package pada.filter;
import pada.data.Player;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class GameFilter implements Filter{
	public void init(FilterConfig filterConfig){}
	public void destroy(){}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		HttpServletRequest httpReq=(HttpServletRequest)request;
		HttpServletResponse httpRes=(HttpServletResponse)response;
		if(httpReq.getParameter("id")==null||httpReq.getParameter("token")==null){
			httpRes.setHeader("Game-Access-Denied", "true");
			return;
		}else{
			Player player;
			if((player=pada.data.Player.check(Long.parseLong(httpReq.getParameter("id")), httpReq.getParameter("token")))==null){
				httpRes.setHeader("Game-Access-Denied", "true");
				return;
			}else{
				request.setAttribute("player", player);
				chain.doFilter(request, response);
			}
		}
	}
}