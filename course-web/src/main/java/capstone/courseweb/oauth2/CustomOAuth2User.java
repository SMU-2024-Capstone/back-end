package capstone.courseweb.oauth2;

import capstone.courseweb.user.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    // OAuth2 성공 핸들러에 리다이렉트하는 페이지에 이메일 전송
    // 해당 이메일로 accessToken 발급
    // 이메일을 통해 DB에 저장된 유저를 파악해서 유저의 아이드를 포함한 accessToken
    private String email;
    private Role role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }
}
