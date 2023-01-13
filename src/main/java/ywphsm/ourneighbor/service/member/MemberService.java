package ywphsm.ourneighbor.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.controller.form.EditForm;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.member.email.EmailService;
import ywphsm.ourneighbor.service.validation.ValidationConst;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AwsS3FileStore awsS3FileStore;

    // 회원 가입
    @Transactional
    public Long save(MemberDTO.Add dto) throws IOException {

        int age = ChangeBirthdateToAge(dto.getBirthDate());
        String encodedPassword = encodedPassword(dto.getPassword());
        Member member = dto.toEntity(age, encodedPassword);
        UploadFile uploadFile = awsS3FileStore.storeFile(dto.getFile());
        uploadFile.addMember(member);
        member.setFile(uploadFile);

        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public void apiSave(MemberDTO.ApiAdd dto, UploadFile file) {

        int age = ChangeBirthdateToAge(dto.getBirthDate());
        dto.setFile(file);
        Member member = dto.toEntity(age);
        dto.getFile().addMember(member);

        memberRepository.save(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + memberId));
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findByNickName(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

    //생년월일 나이 계산
    public int ChangeBirthdateToAge(String birthDate) {

        int nowYear = LocalDateTime.now().getYear();
        String birthYear = birthDate.substring(0, 4);

        return nowYear - Integer.parseInt(birthYear) + 1;
    }

    public String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public void updateMember(Long id, EditForm editForm) throws IOException {
        Member member = findById(id);

        UploadFile uploadFile = awsS3FileStore.storeFile(editForm.getFile());
        if (uploadFile != null) {
            awsS3FileStore.deleteFile(member.getFile().getStoredFileName());
            member.getFile().updateUploadedFileName(
                    uploadFile.getStoredFileName(), uploadFile.getUploadedFileName(),
                    uploadFile.getUploadImageUrl());
        }

        member.updateMember(editForm.getNickname(), editForm.getEmail());
    }

    @Transactional
    public void updatePhoneNumber(Long id, String phoneNumber) {
        Member member = findById(id);
        member.updatePhoneNumber(phoneNumber);

    }

    public boolean passwordCheck(String password, String beforePassword) {
        return passwordEncoder.matches(beforePassword, password);
    }

    @Transactional
    public void updatePassword(Long id, String encodedPassword) {
        Member member = findById(id);
        member.updatePassword(encodedPassword);
    }

    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    //아이디 찾기
    public void sendEmailByUserId(String email) {

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. email = " + email));

        emailService.findUserIdSendEmail(email, member.getUserId());
    }

    //비밀번호 찾기
    @Transactional
    public void sendEmailByPassword(String email) {

        Random rand = new Random();
        String temporaryPassword = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            temporaryPassword += ran;
        }

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. email = " + email));
        member.updatePassword(encodedPassword(temporaryPassword)); //임시 비밀번호로 변경
        emailService.findPasswordSendEmail(email, temporaryPassword);
    }

    @Transactional
    public String updateRole(String userId, Role role) {

        Optional<Member> findMember = findByUserId(userId);
        if (findMember.isPresent()) {
            findMember.get().updateRole(role);
            return ValidationConst.SUCCES;
        } else {
            return ValidationConst.USERID_NULL;
        }
    }

    //찜 상태 확인 - detail에 뿌리기 위함
    public boolean likeStatus(Long memberId, Long storeId) {
        Member member = findById(memberId);

        List<MemberOfStore> memberOfStoreList = member.getMemberOfStoreList();
        if (!memberOfStoreList.isEmpty()) {
            for (MemberOfStore memberOfStore : memberOfStoreList) {
                if (memberOfStore.getStore().getId().equals(storeId) && memberOfStore.isStoreLike()) {
                    return true;
                }
            }
        }
        return false;
    }

}
