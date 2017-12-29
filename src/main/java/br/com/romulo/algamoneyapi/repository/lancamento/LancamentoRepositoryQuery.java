package br.com.romulo.algamoneyapi.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.repository.filter.LancamentoFilter;
import br.com.romulo.algamoneyapi.repository.projecao.ResumoLancamento;

public interface LancamentoRepositoryQuery {

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public Page<ResumoLancamento> resumo(LancamentoFilter lancamentoFilter, Pageable pageable);
}
