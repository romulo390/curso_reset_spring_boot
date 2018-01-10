package br.com.romulo.algamoneyapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.romulo.algamoneyapi.event.RecursoCriandoEvento;
import br.com.romulo.algamoneyapi.model.Pessoa;
import br.com.romulo.algamoneyapi.repository.PessoaRepository;
import br.com.romulo.algamoneyapi.service.PessoaService;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private ApplicationEventPublisher publicador;
	
	@Autowired
	private PessoaService pessoaService;

	/*@GetMapping
	@PreAuthorize("hasRole ('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public List<Pessoa> listaPessoas() {
		return pessoaRepository.findAll();

	}*/

	@PostMapping
	@PreAuthorize("hasRole ('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> criarPessoa(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {

		Pessoa pessoaSalva = pessoaRepository.save(pessoa);
		publicador.publishEvent(new RecursoCriandoEvento(this, response, pessoaSalva.getCod()));

		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);

	}

	@GetMapping("/{cod}")
	@PreAuthorize("hasRole ('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public ResponseEntity<Pessoa> buscaPessoaPorCodido(@PathVariable Long cod) {

		Pessoa pessoa = pessoaRepository.findOne(cod);

		return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build();
	}

	/*@PatchMapping("/{nome}")
	public ResponseEntity<Pessoa> buscaPessoaPorNome(@PathVariable String nome) {

		Pessoa pessoa = pessoaRepository.findByNomeStartingWith(nome);

		if (pessoa != null)
			return ResponseEntity.ok(pessoa);

		return ResponseEntity.notFound().build();

	}*/

	@DeleteMapping("/{cod}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole ('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('delete')")
	public void deletarPessoa(@PathVariable Long cod) {

		pessoaRepository.delete(cod);
	}

		
	@PutMapping("/{cod}")
	@PreAuthorize("hasRole('ROLE_ALTERAR_PESSOA') and #oauth2.hasScope('alterar') ")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long cod, @Valid @RequestBody Pessoa pessoa ){
		
		Pessoa pessoaSalva = pessoaService.atualizaPessoa(cod, pessoa);
		
		return ResponseEntity.ok(pessoaSalva);
		
	}
	
	@PutMapping("/{cod}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ROLE_ALTERAR_PESSOA') and #oauth2.hasScope('alterar') ")
	public void atualizarPropriedadeAtivo(@PathVariable Long cod, @RequestBody Boolean ativo ){
		
		pessoaService.atualizaPropriedadePessoaAtiva(cod, ativo);
				
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public Page<Pessoa> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, Pageable pageable) {
		return pessoaRepository.findByNomeContaining(nome, pageable);

	}
}
