package br.com.romulo.algamoneyapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.model.Pessoa;
import br.com.romulo.algamoneyapi.repository.LancamentoRepository;
import br.com.romulo.algamoneyapi.repository.PessoaRepository;
import br.com.romulo.algamoneyapi.service.exception.PessoaInexisistenteOuInativaException;
import ch.qos.logback.core.joran.util.beans.BeanUtil;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public Lancamento salva(Lancamento lancamento) {
		
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCod());
		if(pessoa == null || pessoa.isInativo()){
			
			throw new PessoaInexisistenteOuInativaException();
		}

		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long cod, Lancamento lancamento){
		
		Lancamento buscaLancamentoExistente = buscaLancamentoExistente(cod);
		
		if(!lancamento.getPessoa().equals(buscaLancamentoExistente.getPessoa())){
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, buscaLancamentoExistente, "cod");
	
		return lancamentoRepository.save(buscaLancamentoExistente);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoaValida = null;
		if(lancamento.getPessoa().getCod() != null){
			pessoaValida = pessoaRepository.findOne(lancamento.getPessoa().getCod());
		}
		if(pessoaValida.isInativo()){
			throw new PessoaInexisistenteOuInativaException();
		}
	}

	private Lancamento buscaLancamentoExistente(Long cod) {
	
		Lancamento  lanamentoSalvo = lancamentoRepository.findOne(cod);
		if(lanamentoSalvo == null){
			throw new IllegalArgumentException();
		}
		
		return lanamentoSalvo;
	}

	public void deleteLacamento(Long cod) {
		 lancamentoRepository.delete(cod);
		
	}
	
	
}
