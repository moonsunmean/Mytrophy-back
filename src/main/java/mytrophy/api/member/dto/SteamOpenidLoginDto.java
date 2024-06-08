package mytrophy.api.member.dto;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SteamOpenidLoginDto {
    @URL
    @NotBlank
    private String ns;

    @URL
    @NotBlank
    private String op_endpoint;

    @URL
    @NotBlank
    private String claimed_id;

    @URL
    @NotBlank
    private String identity;

    @URL
    @NotBlank
    private String return_to;

    @NotBlank
    private String response_nonce;

    @NotBlank
    private String assoc_handle;

    @NotBlank
    @Pattern(regexp = "^\\w+(?:,\\w+)*$")
    private String signed;

    @NotBlank
    private String sig;


    public SteamOpenidLoginDto(String ns, String op_endpoint, String claimed_id, String identity, String return_to, String response_nonce, String assoc_handle, String signed, String sig) {
        this.ns = ns;
        this.op_endpoint = op_endpoint;
        this.claimed_id = claimed_id;
        this.identity = identity;
        this.return_to = return_to;
        this.response_nonce = response_nonce;
        this.assoc_handle = assoc_handle;
        this.signed = signed;
        this.sig = sig;

        this.validate();
    }


    private void validate() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator                                     validator  = factory.getValidator();
            Set<ConstraintViolation<SteamOpenidLoginDto>> violations = validator.validate(this);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }
}