package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.service.MemberService;
import ywphsm.ourneighbor.service.StoreService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    private final StoreService storeService;

    @PutMapping("/admin/update_role/{memberId}")
    public Long updateRole(@PathVariable Long memberId, @RequestBody String role) {
        return memberService.updateRole(memberId, role);
    }

    @GetMapping("/user/like")
    public void likeAdd(boolean likeStatus, Long memberId, Long storeId) {
        log.info("likeStatus={}", likeStatus);
        storeService.updateLike(likeStatus, memberId, storeId);
    }
}
