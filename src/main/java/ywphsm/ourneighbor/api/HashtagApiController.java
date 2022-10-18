package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.HashtagDTO;
import ywphsm.ourneighbor.service.HashtagService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HashtagApiController {

    private final HashtagService hashtagService;

    @GetMapping("/hashtags")
    public ResultClass<?> findAllHashtags() {
        List<HashtagDTO> hashtags = hashtagService.findAll();
        return new ResultClass<>(hashtags);
    }
}
