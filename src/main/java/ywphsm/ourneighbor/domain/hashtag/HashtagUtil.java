package ywphsm.ourneighbor.domain.hashtag;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class HashtagUtil {

    public static List<String> getHashtagNameList(String hashtagJson) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(hashtagJson);

        List<String> hashtagNameList = new ArrayList<>();

        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String value = jsonObject.get("value").toString();

            hashtagNameList.add(value);
        }

        return hashtagNameList;
    }
}
