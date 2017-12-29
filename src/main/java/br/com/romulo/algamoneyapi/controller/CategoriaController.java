package br.com.romulo.algamoneyapi.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.romulo.algamoneyapi.model.Categoria;
import br.com.romulo.algamoneyapi.repository.CategoriaRepository;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
	
	@Autowired
	private CategoriaRepository categoriaRepository; 

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public List<Categoria> listarCategorias(){
		return categoriaRepository.findAll();
	}
	
//	  @PostMapping
//	 public List<Categoria> criarLista(@RequestBody List<Categoria> listaCategoria) {
//
//			List<Categoria> categoriaSalvaLista = categoriaRepository.save(listaCategoria);
//						
////			for(Categoria cat: categoriaSalvaLista){
////				
////				URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{cod}")
////					.buildAndExpand(cat.getCod()).toUri();
////				response.setHeader("Locataion:", uri.toASCIIString());
////				responseEntities.add(ResponseEntity.created(uri).body(categoriaSalvaLista));
////				
////			}
//			
//			return  categoriaSalvaLista;
//			
//	}
//	
/**
  * Método utilizada para inserir uma categoria no DB.
  * @param @Valid Uma anotação que o Spring Data JPA usa para validar os Benas. 
  * @param  @RequestBody, Spring tentará converter o conteúdo do corpo de solicitação recebida para o seu objeto de parâmetro.
  * Depois que uma classe Java é compilada, os comentários não vão
  * para os códigos compilados .class, dessa forma os comentários
  * não interferem no tamanho final do projeto.
*/	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> criarCategoria(@Valid @RequestBody Categoria categoria, HttpServletResponse response ){
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
			
	 		URI uri =ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{cod}")
	 				.buildAndExpand(categoriaSalva.getCod()).toUri();
	 		response.setHeader("Locataion:", uri.toASCIIString());

	 return ResponseEntity.created(uri).body(categoriaSalva);
				
	}
	
	/**
	 * Método responsável por lista uma categoria pelo seu atributo codido.
	 * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL,
	 * @param cod - Variável é passada na URL 
	 * @return - Uma categoria cadastada na DB.
	 */
	@GetMapping("/{cod}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public ResponseEntity<Categoria> buscarCategoria(@PathVariable Long cod){
		
		   Categoria categoria =  categoriaRepository.findOne(cod);
		   
		   if(categoria != null){
			   return ResponseEntity.ok(categoria);
		   }
	//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(categoria);
	return ResponseEntity.notFound().build();
	//ou poderia fazer assim, com pouco codigo
	//return categoria != null ? ReponseEntity.ok(categoria) : ResponseEntity.notFound().build();
	}
	
//	@GetMapping("/{cod}")
//	public  Categoria buscaCategoriaPorCod(@PathVariable Long cod){
//		
//		return categoriaRepository.findOne(cod);
//	}

//	@GetMapping("/{nome}")
//	public Categoria findCategoriaByNome(@PathVariable String nome){
//		
//		return categoriaRepository.findByNome(nome);
//		
//	}
	
	

}
