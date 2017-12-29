package br.com.romulo.algamoneyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{

	
}
