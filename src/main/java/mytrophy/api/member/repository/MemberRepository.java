package mytrophy.api.member.repository;

import mytrophy.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository  extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Member findByUsername(String username);
    Member deleteByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<Member> findBySteamId(String id);

    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.memberCategories mc LEFT JOIN FETCH mc.category WHERE m.username = :username")
    Optional<Member> findByUsernameWithCategories(@Param("username") String username);
}
