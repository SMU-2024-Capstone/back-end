package capstone.courseweb.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // 기본 생성자 필수
@Entity
@Table(name = "USERS")
public class User {

    @Id
    // TODO: MySQL로 DB 변경하면 generationType 변경하기
    // 현재는 50씩 증가
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long id;

    private String email;   // 이메일

    private String name;

    private String nickname; // 클라이언트에서 설정할 닉네임

    // Enum을 DB에 String으로 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(Long id, String email, String name, String nickname, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
    }

    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
