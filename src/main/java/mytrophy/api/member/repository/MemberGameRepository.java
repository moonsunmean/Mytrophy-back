package mytrophy.api.member.repository;

import mytrophy.api.member.entity.MemberGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberGameRepository extends JpaRepository<MemberGame, Long> {
}
