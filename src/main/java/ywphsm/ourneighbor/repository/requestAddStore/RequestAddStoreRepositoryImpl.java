package ywphsm.ourneighbor.repository.requestaddstore;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ywphsm.ourneighbor.domain.dto.QRequestAddStoreDTO_Detail;
import ywphsm.ourneighbor.domain.dto.RequestAddStoreDTO;
import ywphsm.ourneighbor.domain.member.QMember;
import ywphsm.ourneighbor.domain.store.QRequestAddStore;

import java.util.List;

@RequiredArgsConstructor
public class RequestAddStoreRepositoryImpl implements RequestAddStoreRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RequestAddStoreDTO.Detail> requestAddStorePage(Pageable pageable) {
        List<RequestAddStoreDTO.Detail> results = queryFactory
                .select(new QRequestAddStoreDTO_Detail(
                        QRequestAddStore.requestAddStore.id,
                        QRequestAddStore.requestAddStore.name,
                        QRequestAddStore.requestAddStore.address.zipcode,
                        QRequestAddStore.requestAddStore.address.roadAddr,
                        QRequestAddStore.requestAddStore.address.numberAddr,
                        QMember.member.email,
                        QRequestAddStore.requestAddStore.content
                ))
                .from(QRequestAddStore.requestAddStore)
                .leftJoin(QRequestAddStore.requestAddStore.member, QMember.member)
                .orderBy(QRequestAddStore.requestAddStore.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int total = queryFactory
                .selectFrom(QRequestAddStore.requestAddStore)
                .fetch().size();

        return new PageImpl<>(results, pageable, total);
    }
}
