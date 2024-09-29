package capstone.courseweb.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {
    private String email;
    private String Id;
    private String name; //카카오에서 받아오는 닉네임
    private String nickname;  //이길로에서 정하는 닉네임
    private String refresh_token;
}
