package com.cmc.suppin.member.controller;

import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.response.ResponseCode;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import com.cmc.suppin.member.controller.dto.MemberRequestDTO;
import com.cmc.suppin.member.controller.dto.MemberResponseDTO;
import com.cmc.suppin.member.converter.MemberConverter;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "Member 관련 API")
@RequestMapping("/api/v1/members")
public class MemberApi {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/join")
    @Operation(summary = "회원가입 API", description = "아이디 중복검사 및 이메일 인증 후, 회원으로 등록합니다. 자세한 요청 및 응답에 대한 설명은 노션 API 문서를 참고하시면 됩니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.JoinResultDTO>> join(@RequestBody @Valid MemberRequestDTO.JoinDTO request) {
        Member member = memberService.join(request);

        return ResponseEntity.ok(ApiResponse.of(MemberConverter.toJoinResultDTO(member)));
    }

    // 이메일 인증번호 요청(회원가입 시)
    @PostMapping("/join/email/auth")
    @Operation(summary = "이메일 인증번호 요청(회원가입 시) API", description = "이메일 인증번호 요청 API입니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.")
    public ResponseEntity<ApiResponse<Void>> requestEmailAuth(@RequestBody @Valid MemberRequestDTO.EmailRequestDTO request) {
        memberService.requestEmailVerification(request.getEmail());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    // 이메일 인증번호 확인(회원가입 시)
    @PostMapping("/join/email/verification")
    @Operation(summary = "이메일 인증번호 확인 API", description = "올바른 이메일 인증번호인지 검증합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.<br><br>" +
            "request : email, verificationCode(인증번호 유효기간은 5분입니다), response: 인증번호 일치 시 true, 불일치 시 false")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(@RequestBody @Valid MemberRequestDTO.EmailVerificationDTO request) {
        memberService.verifyEmailCode(request.getEmail(), request.getVerificationCode());
        return ResponseEntity.ok(ApiResponse.confirm(ResponseCode.CONFIRM));
    }

    // 아이디 중복 체크
    @GetMapping("/checkUserId")
    @Operation(summary = "아이디 중복 체크 API", description = "입력한 아이디가 중복된 id인지 검증합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "중복이면 false, 중복 아니면 true")
    public ResponseEntity<ApiResponse<MemberResponseDTO.IdConfirmResultDTO>> checkUserId(@RequestParam String userId) {
        boolean checkUserId = memberService.confirmUserId(userId);

        return ResponseEntity.ok(ApiResponse.confirm(MemberConverter.toIdConfirmResultDTO(checkUserId)));
    }

    // 이메일 중복 체크
    @GetMapping("/checkEmail")
    @Operation(summary = "이메일 중복 체크 API", description = "입력한 이메일이 중복된 이메일인지 검증합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.EmailConfirmResultDTO>> checkEmail(@RequestParam String email) {
        boolean checkEmail = memberService.confirmEmail(email);

        return ResponseEntity.ok(ApiResponse.confirm(MemberConverter.toEmailConfirmResultDTO(checkEmail)));
    }


    // 회원탈퇴
    @DeleteMapping("/delete")
    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴 시 API입니다. MemberStatus가 \"DELETE\"로 변경됩니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다. <br><br>" +
            "로그인 시 발급받은 토큰으로 인가 필요, Authentication 헤더에 토큰을 넣어서 요청")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@CurrentAccount Account account) {
        memberService.deleteMember(account.id());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 성공 시 JWT 토큰(유효기간 7일)을 발급받습니다. request : userId, password")
    public ResponseEntity<ApiResponse<MemberResponseDTO.LoginResponseDTO>> login(@RequestBody @Valid MemberRequestDTO.LoginRequestDTO request) {
        MemberResponseDTO.LoginResponseDTO response = memberService.login(request);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "사용중인 토큰을 블랙리스트에 등록합니다(토큰 무효화). 로그인 시 발급받은 JWT 토큰으로 인가 필요, Authentication 헤더에 토큰을 넣어서 요청")
    public ResponseEntity<ApiResponse<Void>> logout(@CurrentAccount Account account) {
        memberService.logout(account.id());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    // 비밀번호 변경
    @PostMapping("/password/update")
    @Operation(summary = "비밀번호 변경 API", description = "request : password, newPassword")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid MemberRequestDTO.PasswordUpdateDTO request, @CurrentAccount Account account) {
        memberService.updatePassword(request, account.id());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    // 현재 비밀번호 확인
    @GetMapping("/password/check")
    @Operation(summary = "현재 비밀번호 확인 API", description = "request : password")
    public ResponseEntity<ApiResponse<MemberResponseDTO.CheckPasswordDTO>> checkPassword(@RequestParam String password, @CurrentAccount Account account) {
        memberService.checkPassword(password, account.id());
        return ResponseEntity.ok(ApiResponse.confirm(ResponseCode.CONFIRM));
    }

    // 회원정보 상세 조회
    @GetMapping("/me")
    @Operation(summary = "회원정보 상세 조회 API", description = "회원 상세 정보를 조회합니다. 로그인 시 발급받은 토큰으로 인가 필요, Authentication 헤더에 토큰을 넣어서 요청")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberDetailsDTO>> getUserDetail(@CurrentAccount Account account) {
        MemberResponseDTO.MemberDetailsDTO memberDetails = memberService.getMemberDetails(account.id());
        return ResponseEntity.ok(ApiResponse.of(memberDetails));
    }


    // TODO: 아이디 찾기, 비밀번호 찾기 API 구현 필요

}
