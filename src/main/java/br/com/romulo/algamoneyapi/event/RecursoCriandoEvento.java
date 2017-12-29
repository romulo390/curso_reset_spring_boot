package br.com.romulo.algamoneyapi.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

public class RecursoCriandoEvento extends ApplicationEvent{


	private static final long serialVersionUID = 1L;

	HttpServletResponse response;
	Long cod;
	
	public RecursoCriandoEvento(Object source, HttpServletResponse response, Long cod) {
		super(source);
		this.response=response;
		this.cod=cod;
		
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCod() {
		return cod;
	}
	
	
}
