package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // @Value 어노테이션 사용시 Open Projections(전체를 가져와서 편집). 미사용시 select 날리는 것 자체를 최적화한 close Projections.
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
