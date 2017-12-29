package br.com.romulo.algamoneyapi.exceptiohandler;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	public MessageSource messageSource;

	/**
	 *Método que captura messagens inválida, passado no atributo.
	 *Ex: nome = dd; atributo diferente do desejado; 
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String menssagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String menssagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		
		List<Erro> erros =  Arrays.asList(new Erro(menssagemUsuario, menssagemDesenvolvedor));

		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);


	}
	
		
	/**
	 *Método responsavel em captura os argumentos obrigatórios ao inserir dados no DB. 
	 *Ex: nome = null;
	 *Antes de tudo, terar que usar o Validetion do Spring Data JPA nos Beans @NotNull 
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> erros = listaDeErros(ex.getBindingResult());


		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);

	}
	/**
	 * Método lança uma execeção(EmptyResultDataAccessException) caso, não encontre conteúdo na base de dados. 
	 * @param ex Responsável em lançar a execação. Aula 4.1
	 * 
	 */
	@ExceptionHandler({EmptyResultDataAccessException.class})
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request){

		String menssagemUsuario = messageSource.getMessage("conteudo.nao-encontra", null, LocaleContextHolder.getLocale());
		String menssagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(menssagemUsuario, menssagemDesenvolvedor));

		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	/**
	 * 
	 * Método lança uma execeção(DataIntegrityViolationException) caso um Lancamento seja salvo e não passar um 
	 * codigo valido, tanto em pessoa quando em categoria;
	 * @return
	 */
	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<Object> handlerDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){

			String menssagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
			//ExceptionUtils.getRootCauseMessage add uma dependencia no pom.xml(org.apache.commons) para deixar mais explicito
			//a causa da messagem, caso venha ocorrer um erro.
			String menssagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
			List<Erro> erros = Arrays.asList(new Erro(menssagemUsuario, menssagemDesenvolvedor));

		
		return handleExceptionInternal(ex,erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
		
	}

	/**
	 * Método responsavél em Listar varios erros que venha dos campos preenchidos!! 
	 * @param bindingResult - Pega a lista de todos os erros..
	 * @return
	 */
	public  List<Erro> listaDeErros(BindingResult bindingResult){

		List<Erro> erros = new ArrayList<>();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {

			String mesangemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mesagenDesenvolvedor = fieldError.toString();

			erros.add(new Erro(mesangemUsuario, mesagenDesenvolvedor));
		}


		return erros;
	}

	public static class Erro{

		private String mesagemUsuario;
		private String mesagemDesenvolvedor;



		public Erro(String mesagemUsuario, String mesagemDesenvolvedor) {

			this.mesagemUsuario = mesagemUsuario;
			this.mesagemDesenvolvedor = mesagemDesenvolvedor;
		}

		public Erro(String mesagemUsuario) {

			this.mesagemUsuario = mesagemUsuario;

		}

		public String getMesagemUsuario() {
			return mesagemUsuario;
		}
		public void setMesagemUsuario(String mesagemUsuario) {
			this.mesagemUsuario = mesagemUsuario;
		}
		public String getMesagemDesenvolvedor() {
			return mesagemDesenvolvedor;
		}
		public void setMesagemDesenvolvedor(String mesagemDesenvolvedor) {
			this.mesagemDesenvolvedor = mesagemDesenvolvedor;
		}




	}
}
