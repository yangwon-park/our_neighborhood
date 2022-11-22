package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.PageMakeDTO;
import ywphsm.ourneighbor.domain.dto.RequestAddStoreDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.RequestAddStore;
import ywphsm.ourneighbor.repository.requestAddStore.RequestAddStoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestAddStoreService {

    private final RequestAddStoreRepository requestAddStoreRepository;
    private final MemberService memberService;

    @Transactional
    public Long save(RequestAddStoreDTO.Add requestAddStoreDTO, Long memberId) {
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

    public Page<RequestAddStoreDTO.Detail> pagingRequestAddStore(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<RequestAddStoreDTO.Detail> results = requestAddStoreRepository.requestAddStorePage(pageRequest);
        log.info("reviewMemberDTO={}", results);
        return results;

    }
}
