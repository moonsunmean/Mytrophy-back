package mytrophy.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.member.dto.CategoryUpdateDto;
import mytrophy.global.jwt.CustomSuccessHandler;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.dto.MemberResponseDto;
import mytrophy.api.member.dto.SteamOpenidLoginDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.security.SteamAutenticationToken;
import mytrophy.api.member.security.SteamUserPrincipal;
import mytrophy.api.member.service.SteamService;
import mytrophy.global.jwt.CustomUserDetails;
import mytrophy.global.jwt.JWTUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import mytrophy.api.member.service.MemberService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Map;

import mytrophy.api.image.service.ImageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final AuthenticationManager authenticationManager;
    private final SteamService steamService;
    private final MemberService memberService;
    private final CustomSuccessHandler successHandler;
    private final JWTUtil jwtUtil;
    private final ImageService imageService;



    // 중복 회원 검증
    @Operation(summary = "중복회원검증",description = "회원가입시 중복회원이있는지 검증합니다.")
    @GetMapping("/checkUsername")
    public boolean isUsernameExists(@RequestParam String username) {
        return memberService.isUsernameExists(username);
    }

    // 회원 가입
    @Operation(summary = "회원가입",description = "회원 정보를 받아 회원가입을 진행해 서버에 정보를 저장합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signupMember(@RequestBody MemberDto memberDto) {
        memberService.signupMember(memberDto);
        return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
    }

    // 카테고리 업데이트
    @Operation(summary = "카테고리 업데이트",description = "회원이 선택 카테고리정보를 받아 갱신합니다.")
    @PatchMapping("/{id}/categories")
    public ResponseEntity<String> updateMemberCategories(@PathVariable("id") Long id,
                                                         @RequestBody CategoryUpdateDto categoryUpdateDto) {
        memberService.updateMemberCategories(id, categoryUpdateDto);
        return new ResponseEntity<>("카테고리 업데이트 성공", HttpStatus.OK);
    }

    // 회원 조회 (토큰)
    @Operation(summary = "회원정보조회(토큰)",description = "헤더에서 토큰을 통해 회원정보를 반환합니다.")
    @GetMapping("/get-userinfo")
    public ResponseEntity<MemberResponseDto> getMemberById(@AuthenticationPrincipal CustomUserDetails userInfo) {
        String username = userInfo.getUsername();
        MemberResponseDto member = memberService.findMemberDtoByUsername(username);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(member);
    }

    // 회원 조회 (id)
    @Operation(summary = "회원정보조회",description = "회원정보를 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable("id") Long id) {
        MemberResponseDto member = memberService.getMemberDtoById(id);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(member);
    }

    // 회원 수 조회
    @Operation(summary = "회원 수 조회",description = "전체 회원 수를 반환합니다.")
    @GetMapping("/count")
    public ResponseEntity<Long> getMemberCount() {
        long count = memberService.getMemberCount();
        return ResponseEntity.ok(count);
    }

    // 회원 리스트 조회
    @Operation(summary = "전체 회원 리스트 조회",description = "전체 회원을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<MemberResponseDto>> getAllMembers(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemberResponseDto> members = memberService.findAll(pageable);
        return ResponseEntity.ok(members);
    }

    // 회원 수정 (토큰)
    @Operation(summary = "회원 정보 수정 (토큰)",description = "회원정보를 수정합니다.")
    @PatchMapping("/modify-userinfo")
    public ResponseEntity<String> updateMember(@AuthenticationPrincipal CustomUserDetails userInfo,
                                               @RequestBody MemberDto memberDto) {
        String username = userInfo.getUsername();
        log.info("Username:{}",username);
        boolean isUpdated = memberService.updateMemberByUsername(username, memberDto);
        if (isUpdated) {
            return new ResponseEntity<>("회원 수정 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("회원 수정 실패", HttpStatus.NOT_FOUND);
        }
    }

    // 회원 수정 (id)
    @Operation(summary = "회원 정보 수정 (id)",description = "회원정보를 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateMemberById(@PathVariable("id") Long id, @RequestBody MemberDto memberDto) {
        boolean isUpdated = memberService.updateMemberById(id, memberDto);
        if (isUpdated) {
            return new ResponseEntity<>("회원 수정 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("회원 수정 실패", HttpStatus.NOT_FOUND);
        }
    }

    // 회원 삭제 (토큰)
    @Operation(summary = "회원 정보 삭제 (토큰)",description = "회원정보를 삭제합니다.")
    @DeleteMapping("/delete-userinfo")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal CustomUserDetails userInfo) {
        String username = userInfo.getUsername();
        boolean isDeleted = memberService.deleteMemberByUsername(username);
        if (isDeleted) {
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("회원 삭제 실패", HttpStatus.NOT_FOUND);
        }
    }

    // 회원 삭제 (id)
    @Operation(summary = "회원 정보 삭제 (id)",description = "회원정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMemberById(@PathVariable("id") Long id) {
        boolean isDeleted = memberService.deleteMemberById(id);
        if (isDeleted) {
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("회원 삭제 실패", HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "스팀 로그인창에서 리다이렉션 URL",description = "스팀로그인 정보를 가지고 로그인 및 연동을 진행합니다.")
    @GetMapping("/steam/login/redirect")
    public ResponseEntity<?> loginRedirect(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> allRequestParams) throws IOException {
        SteamOpenidLoginDto dto = new SteamOpenidLoginDto(
                allRequestParams.get("openid.ns"),
                allRequestParams.get("openid.op_endpoint"),
                allRequestParams.get("openid.claimed_id"),
                allRequestParams.get("openid.identity"),
                allRequestParams.get("openid.return_to"),
                allRequestParams.get("openid.response_nonce"),
                allRequestParams.get("openid.assoc_handle"),
                allRequestParams.get("openid.signed"),
                allRequestParams.get("openid.sig")
        );

        String token =  allRequestParams.get("access");

        String currentUsername = "anonymousUser";
        if(StringUtils.hasText(token) && token !=null &&token.length()>5) {
            currentUsername = jwtUtil.getUsername(token);
        }
        System.out.println("currentUsername : "+currentUsername);
        if(currentUsername !="anonymousUser"){
            try {
                String steamUserId = steamService.validateLoginParameters(dto);
                memberService.linkSteamAccount(currentUsername, steamUserId);
                response.sendRedirect("http://localhost:3000");
                return new ResponseEntity<>("스팀 연동 성공", HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    response.sendRedirect("http://localhost:3000?error=steam-link-failed");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return new ResponseEntity<>("스팀 연동 실패", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            try {
                String                  steamUserId = steamService.validateLoginParameters(dto);
                SteamAutenticationToken authReq     = new SteamAutenticationToken(steamUserId);
                Authentication auth        = authenticationManager.authenticate(authReq);
                SecurityContext sc          = SecurityContextHolder.getContext();
                sc.setAuthentication(auth);
                successHandler.onAuthenticationSuccess(request,response, auth);
                return new ResponseEntity<>("로그인 성공", HttpStatus.OK);


            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("http://localhost:3000?error=steam-link-failed");
                return new ResponseEntity<>("스팀 로그인 실패", HttpStatus.BAD_REQUEST);
            }
        }

    }


    // 회원 사진 추가
    @Operation(summary = "회원 사진 추가",description = "사진을 업로드하면 이미지 URL 을 반환합니다")
    @PostMapping("/files")
    public ResponseEntity uploadOnlyFiles(@RequestPart ("file") List<MultipartFile> files) throws IOException {
        List<String> uploadFiles = imageService.uploadFiles(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFiles);
    }

    // 회원 사진 삭제
    @Operation(summary = "회원 사진 삭제",description = "사진을 삭제합니다")
    @DeleteMapping("/files")
    public ResponseEntity removeOnlyFiles(List<String> files) {
        imageService.removeFile(files);
        return ResponseEntity.ok().build();
    }
}
