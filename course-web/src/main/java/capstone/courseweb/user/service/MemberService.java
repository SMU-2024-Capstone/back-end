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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getMemberByEmail(email);
    }

    public Member signUp(SignUpForm form) { //JwtDto
        if (memberRepository.existsByEmail(form.getEmail())) {
            //memberRepository.deleteAll();
            throw new RuntimeException("사용중인 이메일입니다.");
        }

        return memberRepository.save(Member.builder()
                .email(form.getEmail())
                .Id(form.getId())//passwordEncoder.encode(form.getId())
                .name(form.getName())
                .memberRole(Member.MemberRole.USER)
                .provider(Member.MemberProvider.LOCAL)
                .build());
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 정보가 없습니다."));
    }
}
