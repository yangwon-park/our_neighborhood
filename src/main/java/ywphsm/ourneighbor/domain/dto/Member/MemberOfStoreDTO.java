package ywphsm.ourneighbor.domain.dto.Member;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.member.MemberOfStore;

import javax.validation.constraints.NotNull;

@Data
public class MemberOfStoreDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private Long memberId;

    @Builder
    public MemberOfStoreDTO(Long storeId, Long memberId) {
        this.storeId = storeId;
        this.memberId = memberId;
    }

    public MemberOfStoreDTO(MemberOfStore memberOfStore) {
        this.storeId = memberOfStore.getStore().getId();
        this.memberId = memberOfStore.getMember().getId();
    }

}
