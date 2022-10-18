package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @PutMapping("/admin/update_role/{memberId}")
    public Long updateRole(@PathVariable Long memberId, @RequestBody String role) {

        return memberService.updateRole(memberId, role);
    }
}
