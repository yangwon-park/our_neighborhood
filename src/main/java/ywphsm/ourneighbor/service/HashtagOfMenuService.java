package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.repository.hashtag.hashtagofmenu.HashtagOfMenuRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HashtagOfMenuService {

    private final HashtagOfMenuRepository hashtagOfMenuRepository;

    public List<HashtagDTO> findHashtagsByMenuId(Long menuId) {
        return hashtagOfMenuRepository.findAllHashtagByMenuId(menuId)
                .stream().map(hashtagOfMenu -> HashtagDTO.builder()
                            .hashtagId(hashtagOfMenu.getHashtag().getId())
                            .name(hashtagOfMenu.getHashtag().getName())
                            .build())
                .collect(Collectors.toList());
    }
}
