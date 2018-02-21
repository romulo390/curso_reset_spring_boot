package br.com.romulo.algamoneyapi.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.romulo.algamoneyapi.event.RecursoCriandoEvento;
import br.com.romulo.algamoneyapi.exceptiohandler.AlgamoneyExceptionHandler.Erro;
import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.repository.LancamentoRepository;
import br.com.romulo.algamoneyapi.repository.filter.LancamentoFilter;
import br.com.romulo.algamoneyapi.repository.projecao.ResumoLancamento;
import br.com.romulo.algamoneyapi.service.LancamentoService;
import br.com.romulo.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;


@RestController
@RequestMapping("/lancamento")
public class LancamentoController {

	@Autowired
	private LancamentoRepository  lancamentoRepository;

	@Autowired
	private ApplicationEventPublisher publicador;

	@Autowired 
	private LancamentoService lancamentoService;

	@Autowired
	private MessageSource messageSource;


	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento>criarLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response ){

		Lancamento salvaLancamento  = lancamentoService.salvar(lancamento);				
		publicador.publishEvent(new RecursoCriandoEvento(this, response, salvaLancamento.getCod()));
	/**	
		try {
			
			salvaLancamento  = lancamentoService.salva(lancamento);				
			publicador.publishEvent(new RecursoCriandoEvento(this, response, salvaLancamento.getCod()));
			
		} catch (PessoaInexisistenteOuInativaException e) {
			
			String messagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa",null, LocaleContextHolder.getLocale());
			String messagemDesenvolvedor = e.toString();
			List<Erro> erros = Arrays.asList(new Erro(messagemUsuario, messagemDesenvolvedor));
			
			return ResponseEntity.badRequest().body(erros);
		}

*/
		return ResponseEntity.status(HttpStatus.CREATED).body(salvaLancamento); 
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<Lancamento> pesquisar(LancamentoFilter lacamentoFilter, Pageable pageable ){
		return lancamentoRepository.filtrar(lacamentoFilter,pageable);
	}
	
	@PutMapping("/{cod}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	public ResponseEntity<Lancamento> atualizar(@PathVariable Long cod, @Valid @RequestBody Lancamento lancamento){
		
		try {
				Lancamento lancSalvo = lancamentoService.atualizar(cod, lancamento);
				return ResponseEntity.ok(lancSalvo);
		
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@GetMapping(params="resumo")
	public Page<ResumoLancamento> resumir(LancamentoFilter lacamentoFilter, Pageable pageable ){
		return lancamentoRepository.resumo(lacamentoFilter,pageable);
	}
	

	@GetMapping("/{cod}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Lancamento> buscarLancamentoPeloCodigo(@PathVariable Long cod){

		Lancamento  lancamento = lancamentoRepository.findOne(cod);

		return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build() ;

	}
	
	@DeleteMapping("/{cod}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority ('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('delete')")
	public void deletarLancamento(@Valid @PathVariable Long cod){
		
		lancamentoService.deleteLacamento(cod);		
		
		
	//	return lancamnetoRemovido != null ? ResponseEntity.ok(lancamnetoRemovido) :ResponseEntity.notFound().build();
	}
	/*
	 * Método responsável em lançar uma exceção, caso a pessoa (/cod) passada não foi encontrada ou
	 * esteja como o tipo=false ao inserir um lancamento;
	 */
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	private ResponseEntity<Object> handlerPessoaInexisistenteOuInativaException(PessoaInexistenteOuInativaException pes){
		
		String messagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa",null, LocaleContextHolder.getLocale());
		String messagemDesenvolvedor = pes.toString();
		List<Erro> erros = Arrays.asList(new Erro(messagemUsuario, messagemDesenvolvedor));
		
		return ResponseEntity.badRequest().body(erros);
		
	}
	

}
