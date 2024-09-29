package capstone.courseweb.user.service;


import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return getMemberById(id);
    }

    public Member signUp(SignUpForm form) { //JwtDto
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("사용중인 이메일입니다.");
        }

        return memberRepository.save(Member.builder()
                .email(form.getEmail())
                .Id(form.getId())
                .name(form.getName())
                .nickname(form.getNickname())
                .refresh_token(form.getRefresh_token())
                .build());
    }


    private Member getMemberById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 정보가 없습니다."));
    }
}
