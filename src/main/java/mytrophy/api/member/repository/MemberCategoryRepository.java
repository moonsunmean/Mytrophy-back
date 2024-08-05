package mytrophy.api.member.repository;

import mytrophy.api.member.entity.Member;
import mytrophy.api.member.entity.MemberCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {

    void deleteByMember(Member member);

    List<MemberCategory> findByMemberId(Long memberId);
}
