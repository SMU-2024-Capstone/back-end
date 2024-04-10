package capstone.courseweb.user.controller;

import capstone.courseweb.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/callback/kakao")
    public void kakaoLogin(@RequestParam("code") String code) {
        String kakaoUserInfo = userService.getUserInfo(code);
        log.info(kakaoUserInfo);
    }

}
