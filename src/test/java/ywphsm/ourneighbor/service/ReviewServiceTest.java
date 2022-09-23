package ywphsm.ourneighbor.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.*;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static ywphsm.ourneighbor.domain.QReview.*;
import static ywphsm.ourneighbor.domain.member.QMember.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

}