package capstone.courseweb.user.repository;

import capstone.courseweb.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //boolean findById(String id);
    Optional<Member> findById(String id);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsById(String Id);
}
