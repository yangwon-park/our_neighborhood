package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Transactional
    public Hashtag save(HashtagDTO dto) {
        return hashtagRepository.save(dto.toEntity());
    }

    @Transactional
    public Long delete(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId).orElseThrow(
                () -> new IllegalArgumentException("해당 해쉬태그가 없습니다. hashtagId = " + hashtagId));

        hashtagRepository.delete(hashtag);

        return hashtagId;
    }

    public HashtagDTO findById(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId).orElseThrow(
                () -> new IllegalArgumentException("해당 해쉬태그가 없습니다. hashtagId = " + hashtagId));

        return HashtagDTO.of(hashtag);
    }

    public boolean checkHashtagDuplicate(String name) {
        return hashtagRepository.existsByName(name);
    }

    public List<HashtagDTO> findAll() {
        return hashtagRepository.findAll().stream()
                .map(HashtagDTO::new).collect(Collectors.toList());
    }

    public Hashtag findByName(String name) {
        return hashtagRepository.findByName(name);
    }
}