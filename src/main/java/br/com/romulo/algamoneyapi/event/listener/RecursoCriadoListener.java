package br.com.romulo.algamoneyapi.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.romulo.algamoneyapi.event.RecursoCriandoEvento;

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriandoEvento> {

	@Override
	public void onApplicationEvent(RecursoCriandoEvento event) {
		
		HttpServletResponse response = event.getResponse();
		Long cod = event.getCod();
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{cod}")
				.buildAndExpand(cod).toUri();
		response.setHeader("Location",uri.toASCIIString());
		
	}

}
