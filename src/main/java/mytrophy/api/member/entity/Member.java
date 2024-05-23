package mytrophy.api.member.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String steamId;

    @Column(nullable = true)
    private String loginType;


    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String role;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate createdAt;


}