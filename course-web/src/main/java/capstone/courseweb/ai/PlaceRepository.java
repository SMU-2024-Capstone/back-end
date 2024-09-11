package capstone.courseweb.ai;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Integer> { // ID 타입을 Integer로 수정
}
