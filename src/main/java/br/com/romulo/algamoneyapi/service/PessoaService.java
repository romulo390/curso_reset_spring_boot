package br.com.romulo.algamoneyapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.romulo.algamoneyapi.model.Pessoa;
import br.com.romulo.algamoneyapi.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;


	public Pessoa atualizaPessoa(Long cod, Pessoa pessoa) {

		Pessoa encontrarPessoaEhAtualizar = buscarPessoaPeloCodigo(cod);
		//Classe responsável em atualizar os dados de (pessoa) e copiar para encontrarPessoaEhAtualizar  
		//inguinorando cod. Após feita a copia, retornara o objeto(encontrarPessoaEhAtualizar) salvo.
		BeanUtils.copyProperties(pessoa, encontrarPessoaEhAtualizar, "cod");
		return pessoaRepository.save(encontrarPessoaEhAtualizar);
	}



	public void atualizaPropriedadePessoaAtiva(Long cod, Boolean ativo) {

		Pessoa pessoaSalva = buscarPessoaPeloCodigo(cod);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}

	private Pessoa buscarPessoaPeloCodigo(Long cod) {

		Pessoa encontrarPessoaEhAtualizar = pessoaRepository.findOne(cod);
		if (encontrarPessoaEhAtualizar == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return encontrarPessoaEhAtualizar;
	}


}
