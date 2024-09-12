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
        log.info("loadUserByUsername id: {}", id);
        System.out.println("lodaUserByUsername");
        return getMemberById(id);
        //return getMemberByEmail(email);
    }

    public Member signUp(SignUpForm form) { //JwtDto
        if (memberRepository.existsByEmail(form.getEmail())) {
            //memberRepository.deleteAll();
            throw new RuntimeException("사용중인 이메일입니다.");
        }
        System.out.println("signup 직전 id: " + form.getId());
        System.out.println("signup 직전 nickname: " + form.getNickname());
        System.out.println("signup 직전 refresh: " + form.getRefresh_token());

        return memberRepository.save(Member.builder()
                .email(form.getEmail())
                .Id(form.getId())//passwordEncoder.encode(form.getId())
                .name(form.getName())
                .nickname(form.getNickname())
                //.provider(Member.MemberProvider.LOCAL)
                .refresh_token(form.getRefresh_token())
                .build());
    }


    private Member getMemberById(String id) {
        log.info("겟멤버바이아이디 id 출력: {]", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 정보가 없습니다."));
    }
    /*private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 정보가 없습니다."));
    }*/


}
