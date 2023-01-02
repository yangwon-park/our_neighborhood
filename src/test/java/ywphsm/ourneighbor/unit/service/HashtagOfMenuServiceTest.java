
package ywphsm.ourneighbor.unit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.repository.hashtag.hashtagofmenu.HashtagOfMenuRepository;
import ywphsm.ourneighbor.service.HashtagOfMenuService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class HashtagOfMenuServiceTest {

    @Mock
    HashtagOfMenuRepository hashtagOfMenuRepository;

    @InjectMocks
    HashtagOfMenuService hashtagOfMenuService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("메뉴에 등록된 해쉬태그 조회")
    void should_FindAllHashtags_When_ByMenuId() {
        // given
        HashtagDTO dto1 = HashtagDTO.builder()
                .name("해쉬태그1")
                .build();

        HashtagDTO dto2 = HashtagDTO.builder()
                .name("해쉬태그2")
                .build();

        Menu menu = Menu.builder()
                .name("메뉴")
                .price(1000)
                .type(MenuType.MAIN)
                .hashtagOfMenuList(new ArrayList<>())
                .build();

        Long mockMenuId = 1L;

        setField(menu, "id", mockMenuId);

        Hashtag entity1 = dto1.toEntity();
        Hashtag entity2 = dto2.toEntity();

        linkHashtagAndMenu(entity1, menu);
        linkHashtagAndMenu(entity2, menu);

        given(hashtagOfMenuRepository.findAllHashtagByMenuId(mockMenuId)).willReturn(menu.getHashtagOfMenuList());

        // when
        List<HashtagDTO> result = hashtagOfMenuService.findAllHashtagByMenuId(mockMenuId);

        // then
        then(hashtagOfMenuRepository).should().findAllHashtagByMenuId(mockMenuId);
        assertThat(result).hasSize(2).contains(dto1, dto2);
    }
}
