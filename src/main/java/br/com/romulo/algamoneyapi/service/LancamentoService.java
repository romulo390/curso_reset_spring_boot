package br.com.romulo.algamoneyapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.model.Pessoa;
import br.com.romulo.algamoneyapi.repository.LancamentoRepository;
import br.com.romulo.algamoneyapi.repository.PessoaRepository;
import br.com.romulo.algamoneyapi.service.exception.PessoaInexisistenteOuInativaException;

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

	public void deleteLacamento(Long cod) {
		 lancamentoRepository.delete(cod);
		
	}
	
	
}
