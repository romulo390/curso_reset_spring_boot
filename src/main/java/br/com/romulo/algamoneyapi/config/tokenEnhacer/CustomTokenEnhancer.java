package br.com.romulo.algamoneyapi.config.tokenEnhacer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import br.com.romulo.algamoneyapi.security.UsuarioLogado;

/**
 * Classe criada da aula 7.5, para pegar usuario logado.
 */
public class CustomTokenEnhancer  implements TokenEnhancer{
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		UsuarioLogado usuarioLogado = (UsuarioLogado) authentication.getPrincipal();
		
		Map<String, Object> addNomeUsuario = new HashMap<>();
		addNomeUsuario.put("nome", usuarioLogado.getUsuario().getNome());
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addNomeUsuario);
	
	return accessToken;
	}
	
}
