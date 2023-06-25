package gwkim.smartorder.member.repository;

import gwkim.smartorder.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
