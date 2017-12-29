package br.com.romulo.algamoneyapi.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/*
 * Classe que filtra o refresh_token da aplicação  
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //Ordem de prioridade alta
public class RefreshTokenCookiePreProcessoFilter implements Filter {

	/*
	 * Metodo responsavel em filtra {refreshtoken} do cookei e adicionar na aplicação   
	 * 
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	
		HttpServletRequest req = (HttpServletRequest)request;
		
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI()) 
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null){
			for(Cookie cookei: req.getCookies()){
				if(cookei.getName().equals("refreshToken")){
					String refreshToken = cookei.getValue();
					req = new MyRequestWrapper(req,refreshToken);
				}
			}
		}
		
		chain.doFilter(req, response);
	}
	
	@Override
	public void destroy() {}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	
	/*
	 * 
	 */
	static class MyRequestWrapper extends HttpServletRequestWrapper{
		
		private String refreshToken;
		
		public MyRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken=refreshToken;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			map.put("refresh_token", new String[]{refreshToken});
			map.setLocked(true);
			return map;
		}
		
	}
}
