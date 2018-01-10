package br.com.romulo.algamoneyapi.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.romulo.algamoneyapi.model.Permissao;
import br.com.romulo.algamoneyapi.model.Usuario;
import br.com.romulo.algamoneyapi.repository.UsuarioRepository;

@Service
public class AppUserDetailService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			
		 Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
						
		 Usuario usuario = usuarioOptional.orElseThrow(()->new UsernameNotFoundException("Usuario e/ou senha incorretos"));
		
		return new UsuarioLogado(usuario, getPermissoes(usuario)); 
		//return new User(email, usuario.getSenha(), getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		
		Set<SimpleGrantedAuthority> autorisacao = new HashSet<>();
		//Caso não funcionar usar dessa forma
	//	usuario.getPermissoes().forEach(permissa-> 
		//	      autorisacao.add(new SimpleGrantedAuthority(permissa.getDescricao())));
			
		List<Permissao> usuarioPemissao = usuario.getPermissoes();
			
			for (Permissao permissao : usuarioPemissao) {
				
				autorisacao.add(new SimpleGrantedAuthority(permissao.getDescricao().toUpperCase()));
			}
		
		return autorisacao;
	}

}
