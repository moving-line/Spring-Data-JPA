package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
    }

    // 이 클래스는 보통 queryDSL이나 jdbcTemplate를 사용하려는 케이스에 커스텀하여 사용하는 것이나, 결국에는 MemberRepository 에 포함되는 것이나 마찬가지이다.
    // 핵심 비즈니스 쿼리와 단순 화면을 위한 쿼리는 분리하는게 좋은데, 그 경우에는 이런 방식이 아니라 아예 다른 Repository 를 만드는 것이 좋다.
}
