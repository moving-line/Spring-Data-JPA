package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { // 생성자의 파라미터 이름을 가지고 판단
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
