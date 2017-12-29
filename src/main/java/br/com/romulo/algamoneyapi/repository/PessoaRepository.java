package br.com.romulo.algamoneyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.romulo.algamoneyapi.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	Pessoa findByNomeStartingWith(String nome);
	Pessoa findByNome(String nome);
}
