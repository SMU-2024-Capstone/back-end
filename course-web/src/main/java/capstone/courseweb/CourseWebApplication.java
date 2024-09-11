package capstone.courseweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableBatchProcessing // 배치 사용을 위한 선언
@SpringBootApplication
public class CourseWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseWebApplication.class, args);
	}

}
