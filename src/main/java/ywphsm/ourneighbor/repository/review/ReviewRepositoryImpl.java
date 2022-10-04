package ywphsm.ourneighbor.repository.review;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ywphsm.ourneighbor.domain.QReview;
import ywphsm.ourneighbor.domain.dto.QReviewMemberDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.member.QMember;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewMemberDTO> ReviewPage(Pageable pageable) {
        QueryResults<ReviewMemberDTO> response = queryFactory
                .select(new QReviewMemberDTO(
                        QReview.review.id.as("reviewId"),
                        QReview.review.content,
                        QReview.review.rating,
                        QReview.review.createdBy,
                        QMember.member.id.as("memberId"),
                        QMember.member.username))
                .from(QReview.review)
                .leftJoin(QReview.review.member, QMember.member)
                .orderBy(QReview.review.createdBy.desc())
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
}
