package br.com.romulo.algamoneyapi.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import br.com.romulo.algamoneyapi.model.Categoria_;
import br.com.romulo.algamoneyapi.model.Lancamento;
import br.com.romulo.algamoneyapi.model.Lancamento_;
import br.com.romulo.algamoneyapi.model.Pessoa_;
import br.com.romulo.algamoneyapi.repository.filter.LancamentoFilter;
import br.com.romulo.algamoneyapi.repository.projecao.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery  {

	@PersistenceContext
	private EntityManager entityManager;
	
	
	/**
	 * Método que filtra os lançamento por restriçoes, por quantidade e tamanho da pagina;
	 * Ex: http://localhost:8080/lancamento?size=2&page=3
	 *   
	 */
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
			
			Root<Lancamento> root = criteria.from(Lancamento.class);
			
			
			Predicate[] predicate = criarRestricao(lancamentoFilter, builder, root);
			criteria.where(predicate);
			
			TypedQuery<Lancamento> query = entityManager.createQuery(criteria);
			
			adicionarRestricoeDePaginas(query, pageable);
			
		return new PageImpl<>( query.getResultList(), pageable, total(lancamentoFilter));
	}
	/*
	 * Método que retorna um Lancamento resumido, ou seja retornara informações desejada para
	 * ser apresentada na view  
	 */
	@Override
	public Page<ResumoLancamento> resumo(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteriaQuery = builder.createQuery(ResumoLancamento.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		/*Posso usar assim, caso eu não queira usar Lancamento_
		 * criteria.select(builder.construct(ResumoLancamento.class
		 * root.get("id"), root.get("descricao"), root.get("dataVencimento")
		 * 
		 */
				 
		criteriaQuery.select(builder.construct(ResumoLancamento.class
				, root.get(Lancamento_.cod), root.get(Lancamento_.descricao)
				, root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento)
				, root.get(Lancamento_.valor), root.get(Lancamento_.tipo)
				, root.get(Lancamento_.categoria).get(Categoria_.nome)
				, root.get(Lancamento_.pessoa).get(Pessoa_.nome)));;


		Predicate[] predicates = criarRestricao(lancamentoFilter, builder, root);
		criteriaQuery.where(predicates);
				
		TypedQuery<ResumoLancamento> query = entityManager.createQuery(criteriaQuery);
		adicionarRestricoeDePaginas(query, pageable);
				
	return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}
	
	private Predicate[] criarRestricao(LancamentoFilter lancamentoFilter, CriteriaBuilder build,
			Root<Lancamento> root) {
		
		List<Predicate> predicate = new ArrayList<>();
		
		if(!StringUtils.isEmpty(lancamentoFilter.getDescricao())){
			
			predicate.add(build.like(
					build.lower(root.get(Lancamento_.descricao)),"%"+ lancamentoFilter.getDescricao().toLowerCase()+"%" ));
		} 
		
		if(lancamentoFilter.getDataVencimentoDe()!=null){
			predicate.add(
					build.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
		}
		if(lancamentoFilter.getDataVencimentoAte()!=null){
			predicate.add(
					build.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		}
		
		return predicate.toArray(new Predicate[predicate.size()]);
	}
	
	/**
	 * Médoto que adiciona na filtragem uma restrição, pegando a pagina atual(page=3), total de "lancamento" a ser
	 * 	exibida por pagina(size=2), e aparti do primeiro registo da pagina(paginaAtual*totalDePagina) 
	 * é mostrado  que O primeiro resistro da pagina "+ 3 +" eh na possicao "+ 6, devido ao mutiplicação(2*3)
	 * @param query
	 * @param pageable
	 */
	private void adicionarRestricoeDePaginas(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		System.out.println("Estou na pagina: "+paginaAtual);
		int totalDePaginas = pageable.getPageSize();
		System.out.println("Mostradar "+totalDePaginas+ " conteudo por paginas");
		
		//int primeiroRegistroDaPagina = pageable.getOffset();
		int primeiroRegistroDaPagina =paginaAtual*totalDePaginas;
		System.out.println("O primeiro resistro da pagina "+ paginaAtual +" eh na possicao "+ primeiroRegistroDaPagina);
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalDePaginas);
	}

	/**
	 * Método que 
	 * @param lancamentoFilter
	 * @return
	 */
	private Long total(LancamentoFilter lancamentoFilter) {
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		//é o mesmo que "form Lancamento"
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		//o mesmo que "where"
		Predicate[] predicate = criarRestricao(lancamentoFilter, builder, root);
		criteria.where(predicate);
		
		// é o mesmo que "Select * em lancamento"
		criteria.select(builder.count(root));
		
		return entityManager.createQuery(criteria).getSingleResult();
	}

	
	

}
