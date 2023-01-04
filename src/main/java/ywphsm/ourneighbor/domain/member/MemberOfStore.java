package ywphsm.ourneighbor.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class MemberOfStore {

    @Id @GeneratedValue
    private Long Id;

    private boolean myStore;

    private boolean storeLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    public MemberOfStore(Member member, Store store) {
        this.member = member;
        this.store = store;
    }

    public static MemberOfStore linkMemberOfStore(Member member, Store store) {
        MemberOfStore memberOfStore = new MemberOfStore(member, store);
        member.getMemberOfStoreList( ).add(memberOfStore);
        store.getMemberOfStoreList().add(memberOfStore);

        return memberOfStore;
    }

    public void updateMyStore(boolean myStore) {
        this.myStore = myStore;
    }

    public void updateStoreLike(boolean storeLike) {
        this.storeLike = storeLike;
    }
}
