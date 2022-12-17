package ywphsm.ourneighbor.repository;

import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

/*
    참고 블로그
    https://jojoldu.tistory.com/477
 */
public class OrderByNull extends OrderSpecifier {

    public static final OrderByNull DEFAULT = new OrderByNull();

    private OrderByNull() {
        super(Order.ASC, NullExpression.DEFAULT, NullHandling.Default);
    }
}
