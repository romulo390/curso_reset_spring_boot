package br.com.romulo.algamoneyapi.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import br.com.romulo.algamoneyapi.model.Categoria;


public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
	
	Categoria findByNome(String nome);
	
	//List<Categoria> salvarLista(List<Categoria> listaCategoria);
	
}
