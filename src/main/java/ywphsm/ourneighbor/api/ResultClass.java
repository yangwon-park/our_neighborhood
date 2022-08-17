package ywphsm.ourneighbor.api;

import lombok.AllArgsConstructor;
import lombok.Data;


// JSON 확장성을 위한 껍데기 클래스
@Data
@AllArgsConstructor
public class ResultClass<T> {

    private int count;
    private T data;
}
