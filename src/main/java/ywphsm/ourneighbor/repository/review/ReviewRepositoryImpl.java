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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewMemberDTO> ReviewPage(Pageable pageable, Long storeId) {
        QueryResults<ReviewMemberDTO> response = queryFactory
                .select(new QReviewMemberDTO(
                        QReview.review.id.as("reviewId"),
                        QReview.review.content,
                        QReview.review.rating,
                        QReview.review.createdDate,
                        QMember.member.id.as("memberId"),
                        QMember.member.username,
                        QUploadFile.uploadFile.storedFileName))
                .from(QReview.review)
                .where(storeIdEq(storeId))
                .leftJoin(QReview.review.member, QMember.member)
                .leftJoin(QReview.review.file, QUploadFile.uploadFile)
                .orderBy(QReview.review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetchResults();

        List<ReviewMemberDTO> content = new ArrayList<>();
        for (ReviewMemberDTO eachResponse : response.getResults()) {
            content.add(new ReviewMemberDTO(eachResponse));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression storeIdEq(Long storeId) {
        return storeId != null ? QReview.review.store.id.eq(storeId) : null;
    }
}
