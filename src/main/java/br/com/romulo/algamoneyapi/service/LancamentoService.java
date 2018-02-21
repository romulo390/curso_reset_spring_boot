package br.com.romulo.algamoneyapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.model.Pessoa;
import br.com.romulo.algamoneyapi.repository.LancamentoRepository;
import br.com.romulo.algamoneyapi.repository.PessoaRepository;
import br.com.romulo.algamoneyapi.service.exception.PessoaInexistenteOuInativaException;


@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvar(Lancamento lancamento) {
		validarPessoa(lancamento);

		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long cod, Lancamento lancamento){
	//	Pessoa pessoaSalva = null;
		Lancamento buscaLancamentoExistente = buscaLancamentoExistente(cod);
		
		if(!lancamento.getPessoa().getNome().equals(buscaLancamentoExistente.getPessoa().getNome()) 
			|| lancamento.getPessoa().getEndereco().getBairro().equals(buscaLancamentoExistente.getPessoa().getEndereco().getBairro())
			|| lancamento.getPessoa().getEndereco().getCep().equals(buscaLancamentoExistente.getPessoa().getEndereco().getCep())
			|| lancamento.getPessoa().getEndereco().getCidade().equals(buscaLancamentoExistente.getPessoa().getEndereco().getCidade())
			|| lancamento.getPessoa().getEndereco().getEstado().equals(buscaLancamentoExistente.getPessoa().getEndereco().getEstado())
			|| lancamento.getPessoa().getEndereco().getLogradouro().equals(buscaLancamentoExistente.getPessoa().getEndereco().getLogradouro())
			|| lancamento.getPessoa().getEndereco().getNumero().equals(buscaLancamentoExistente.getPessoa().getEndereco().getNumero()))
		{
			validarESalvarPessoa(lancamento);
		}
		if(!lancamento.getPessoa().equals(buscaLancamentoExistente.getPessoa())){ 
			validarPessoa(lancamento);
		}

		BeanUtils.copyProperties(lancamento, buscaLancamentoExistente, "cod");
		
		return lancamentoRepository.save(buscaLancamentoExistente);
	
			
	}

	private void validarESalvarPessoa(Lancamento lancamento) {
		
		Pessoa pessoaSalva = lancamento.getPessoa(); 
		
		if(pessoaSalva != null){
			pessoaService.atualizaPessoa(pessoaSalva.getCod(), pessoaSalva);
		}
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoaValida = null;
		
		if(lancamento.getPessoa().getCod() != null){
			pessoaValida = pessoaRepository.findOne(lancamento.getPessoa().getCod());
		}
					
		if (pessoaValida == null || pessoaValida.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
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
