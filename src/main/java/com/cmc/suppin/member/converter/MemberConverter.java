package com.cmc.suppin.member.converter;

import com.cmc.suppin.member.controller.dto.MemberRequestDTO;
import com.cmc.suppin.member.controller.dto.MemberResponseDTO;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.domain.TermsAgree;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberConverter {

    public Member toMemberEntity(MemberRequestDTO.JoinDTO request, PasswordEncoder encoder) {
        return Member.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .password(encoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .userType(request.getUserType())
                .build();
    }

    public TermsAgree toTermsAgreeEntity(MemberRequestDTO.TermsAgreeDTO termsAgreeDTO, Member member) {
        return TermsAgree.builder()
                .ageOver14Agree(termsAgreeDTO.getAgeOver14Agree())
                .serviceUseAgree(termsAgreeDTO.getServiceUseAgree())
                .personalInfoAgree(termsAgreeDTO.getPersonalInfoAgree())
                .marketingAgree(termsAgreeDTO.getMarketingAgree())
                .member(member)
                .build();
    }

    public static MemberResponseDTO.JoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.JoinResultDTO.builder()
                .memberId(member.getId())
                .userId(member.getUserId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberResponseDTO.IdConfirmResultDTO toIdConfirmResultDTO(boolean checkUserId) {
        return MemberResponseDTO.IdConfirmResultDTO.builder()
                .checkUserId(checkUserId)
                .build();
    }

    public static MemberResponseDTO.EmailConfirmResultDTO toEmailConfirmResultDTO(boolean checkEmail) {
        return MemberResponseDTO.EmailConfirmResultDTO.builder()
                .checkEmail(checkEmail)
                .build();
    }

    public static MemberResponseDTO.LoginResponseDTO toLoginResponseDTO(String token, Member member) {
        return MemberResponseDTO.LoginResponseDTO.builder()
                .token(token)
                .userId(member.getUserId())
                .build();
    }

    public static MemberResponseDTO.MemberDetailsDTO toMemberDetailsDTO(Member member) {
        return MemberResponseDTO.MemberDetailsDTO.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .userType(member.getUserType())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
