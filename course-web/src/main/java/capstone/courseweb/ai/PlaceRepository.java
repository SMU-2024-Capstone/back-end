package capstone.courseweb.ai;

import capstone.courseweb.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> { // ID 타입을 Integer로 수정
    Optional<Place> findById(Integer id);
    Optional<Place> findByName(String name);
}
