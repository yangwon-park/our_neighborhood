package ywphsm.ourneighbor.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;
import ywphsm.ourneighbor.domain.member.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDTO {

    @Data
    @NoArgsConstructor
    public static class Detail {

        private Long memberId;

        private String userId;

        private String username;

        private String nickname;

        private String email;

        private String phoneNumber;

        private int age;

        private String birthDate;

        private int gender;         // 0 : 남자, 1 : 여자

        private List<MemberOfStoreDTO> memberOfStoreList = new ArrayList<>();

        private List<ReviewDTO.Detail> reviewList = new ArrayList<>();


        @Builder
        public Detail(Member member) {
            memberId = member.getId();
            userId = member.getUserId();
            username = member.getUsername();
            nickname = member.getNickname();
            email = member.getEmail();
            phoneNumber = member.getPhoneNumber();
            age = member.getAge();
            birthDate = member.getBirthDate();
            gender = member.getGender();
            reviewList = member.getReviewList().stream()
                    .map(ReviewDTO.Detail::new)
                    .collect(Collectors.toList());
            memberOfStoreList = member.getMemberOfStoreList().stream()
                    .map(MemberOfStoreDTO::new)
                    .collect(Collectors.toList());
        }

    }
}
