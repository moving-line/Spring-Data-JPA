package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();

        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        // 도메인 클래스 컨버터가 엔티티자체를 반환해주지만... 실무는 거의 쓸수없고 아주 간단한 경우만 사용하자. 트랙잭션도 범위가 아니므로 조회용도로만..
        return member.getUsername();
    }


    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        // 페이지 인덱스를 0이 아닌 1부터 시작하고 싶다면? 방법 2개 존재(사실 그냥 쓰는걸 추천)
        // 1) Pageable 을 인수로 받지말고 내가 PageRequest 만들어서 findAll()의 인자 넘겨주자.
        // PageRequest request = PageRequest.of(1, 2). 이때는 반환인 Page 도 내가 만들어야함
        // 2) application.yml김에 data.web.pageable.one-indexed-parameter:true로! 그러나 Page내 다른 정보들이 현재 page와 다른 문제생김
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
