package capstone.courseweb;

import capstone.courseweb.user.domain.User;
import capstone.courseweb.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseWebApplicationTests {
	@Autowired
	private UserRepository userRepository;



	@Test
	public void saveUserTest() {
		User user = User.builder()
				.email("vvvv@naver.com")
				.nickname("aa")
				.build();

		userRepository.save(user);
	}

}
