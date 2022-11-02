package ywphsm.ourneighbor.repository.review;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;
import ywphsm.ourneighbor.domain.QReview;
import ywphsm.ourneighbor.domain.dto.QReviewMemberDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.file.QUploadFile;
import ywphsm.ourneighbor.domain.member.QMember;
import ywphsm.ourneighbor.domain.store.QStore;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewMemberDTO> reviewPage(Pageable pageable, Long storeId) {
        List<ReviewMemberDTO> content = queryFactory
                .select(new QReviewMemberDTO(
                        QReview.review.id.as("reviewId"),
                        QReview.review.content,
                        QReview.review.rating,
                        QReview.review.createdDate,
                        QMember.member.id.as("memberId"),
                        QMember.member.username,
                        QUploadFile.uploadFile.uploadImageUrl))
                .from(QReview.review)
                .where(storeIdEq(storeId))
                .leftJoin(QReview.review.member, QMember.member)
                .leftJoin(QReview.review.file, QUploadFile.uploadFile)
                .orderBy(QReview.review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<ReviewMemberDTO> myReview(Long memberId) {
        return queryFactory
                .select(new QReviewMemberDTO(
                        QReview.review.id.as("reviewId"),
                        QReview.review.content,
                        QReview.review.rating,
                        QReview.review.createdDate,
                        QUploadFile.uploadFile.uploadImageUrl,
                        QStore.store.name.as("storeName"),
                        QStore.store.id.as("storeId")))
                .from(QReview.review)
                .where(memberIdEq(memberId))
                .leftJoin(QReview.review.member, QMember.member)
                .leftJoin(QReview.review.file, QUploadFile.uploadFile)
                .leftJoin(QReview.review.store, QStore.store)
                .orderBy(QReview.review.createdDate.desc())
                .fetch();

    }

    private BooleanExpression storeIdEq(Long storeId) {
        return storeId != null ? QReview.review.store.id.eq(storeId) : null;
    }
    private BooleanExpression memberIdEq(Long memberId) {
        return memberId != null ? QMember.member.id.eq(memberId) : null;
    }
}
