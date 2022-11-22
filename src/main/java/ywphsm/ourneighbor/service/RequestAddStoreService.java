package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.dto.RequestAddStoreDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.RequestAddStore;
import ywphsm.ourneighbor.repository.requestAddStore.RequestAddStoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestAddStoreService {

    private final RequestAddStoreRepository requestAddStoreRepository;
    private final MemberService memberService;

    @Transactional
    public Long save(RequestAddStoreDTO requestAddStoreDTO, Long memberId) {
        Member member = memberService.findById(memberId);
        requestAddStoreDTO.setMember(member);
        RequestAddStore requestAddStore = requestAddStoreDTO.toEntity();
        requestAddStoreRepository.save(requestAddStore);
        return requestAddStore.getId();
    }

    // 회원 한명 조회
    public RequestAddStore findById(Long requestAddStoreId) {
        return requestAddStoreRepository.findById(requestAddStoreId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 추가요청입니다. id = " + requestAddStoreId));
    }

    // 회원 전체 조회
    public List<RequestAddStore> findAll() {
        return requestAddStoreRepository.findAll();
    }

    @Transactional
    public Long delete(Long requestAddStoreId) {
        RequestAddStore requestAddStore = requestAddStoreRepository.findById(requestAddStoreId).orElseThrow(
                () -> new IllegalArgumentException("해당 추가요청이 없습니다. Id = " + requestAddStoreId));

        requestAddStoreRepository.delete(requestAddStore);

        return requestAddStoreId;
    }
}
