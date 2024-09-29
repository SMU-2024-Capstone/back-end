package capstone.courseweb.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> { // ID 타입을 Integer로 수정
    Optional<Place> findById(Integer id);
    Optional<Place> findByName(String name);
    List<Place> findByCategory(@Param("category") String category);

}
