package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;

    @Test
    void memberTest() {

        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCrudTest() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }


    @Test
    void findHelloByTest() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    void findByUsernameAndAgeGreaterThanTest() {

        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void namedQueryTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    void findUserTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    void findUsernameListTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.get(0)).isEqualTo(member1.getUsername());
        assertThat(result.get(1)).isEqualTo(member2.getUsername());
    }

    @Test
    void findMemberDtoTest() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.changeTeam(team);
        memberRepository.save(member1);
        Member member2 = new Member("BBB", 20);
        member2.changeTeam(team);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }
    }

    @Test
    void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 30);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "CCC"));
        assertThat(result.get(0)).isEqualTo(member1);
        assertThat(result.get(1)).isEqualTo(member3);
    }

    @Test
    void variousReturnTypeTest() {
        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);

        List<Member> result1 = memberRepository.findListByUsername("AAA");
        Member result2 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> result3 = memberRepository.findOptionalByUsername("AAA");

        assertThat(result1.get(0)).isEqualTo(member1);
        assertThat(result2).isEqualTo(member1);
        assertThat(result3.get()).isEqualTo(member1);

        List<Member> result4 = memberRepository.findListByUsername("asdasdwqf");
        Member result5 = memberRepository.findMemberByUsername("asdasdwqf");
        Optional<Member> result6 = memberRepository.findOptionalByUsername("asdasdwqf");

        System.out.println(result4);
        System.out.println(result5);
        System.out.println(result6);

        // Spring data JPA 에서는 순수 JPA와 다르게, 없는걸 조회해도 Exception 터지지 않음. 내부적으로 try/catch 하여 빈값 반환. (result 4,5)
        // Java8 이후엔 Optional 으로 대동단결 (result6)
    }

    @Test
    void pagingTest() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Slice<Member> pageSlice = memberRepository.findSliceByAge(age, pageRequest);

        // 절대 엔티티를 반환해선 안되므로, 실제로는 아래처럼 DTO로 변환 필수.
        Page<MemberDto> toDTo = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();

        // for Page
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        // for Slice
        assertThat(pageSlice.getContent().size()).isEqualTo(3);
        assertThat(pageSlice.getNumber()).isEqualTo(0);
        assertThat(pageSlice.isFirst()).isTrue();
        assertThat(pageSlice.hasNext()).isTrue();

        // for DTO
        assertThat(toDTo.getContent().size()).isEqualTo(3);
        assertThat(toDTo.getTotalElements()).isEqualTo(5);
        assertThat(toDTo.getNumber()).isEqualTo(0);
        assertThat(toDTo.getTotalPages()).isEqualTo(2);
        assertThat(toDTo.isFirst()).isTrue();
        assertThat(toDTo.hasNext()).isTrue();
    }

    @Test
    void bulkUpdateTest() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        // 벌크연산은 영속성컨텍스트 무시하고 바로 DB에 때림.
        // 해당 쿼리에 @Modifying(clearAutomatically = true) 옵션을 쓰던가, 벌크연산 끝나면 flush/clear 해주는 식으로 영속성컨텍스트 초기화 필요
        int resultCount = memberRepository.bulkAgePlus(20);

//        em.flush();
//        em.clear();

        assertThat(memberRepository.findByUsername("member5").get(0).getAge()).isEqualTo(41);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) { // 이 경우, LAZY 로딩으로 인해 N+1 문제 발생
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();
        System.out.println("===============================");

        List<Member> membersFetch = memberRepository.findMemberFetchJoin();

        for (Member member : membersFetch) { // fetch join 으로 문제 해결
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();
        System.out.println("===============================");

        List<Member> membersEntityGraph = memberRepository.findMemberEntityGraph();

        for (Member member : membersEntityGraph) { // @EntityGraph 로 문제해결
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember2 = memberRepository.findReadOnlyByUsername("member1");
        findMember2.setUsername("member2"); // 더티체킹을 못해서 값이 안변함
        em.flush();
    }

    @Test
    void lockTest() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }
}