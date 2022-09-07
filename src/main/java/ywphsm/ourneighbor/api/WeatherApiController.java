package ywphsm.ourneighbor.api;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.api.dto.WeatherDTO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

@Slf4j
@RestController
public class WeatherApiController {

    @GetMapping("/weather")
    public WeatherDTO getWeather() throws Exception{

        WeatherDTO dto = new WeatherDTO();

        String serviceKey = "LBYxEXESOBgpdqLOhf1sRtyCQdCdagCnzNxOIFp45%2FwsK%2BjAY6%2FkK9YbmQLnGr7cbD%2FPEihLyT0u1txhmFyEmg%3D%3D";
        String returnType = "JSON";
        String numOfRows = "290";     // 오늘 하루 시간대별 모든 날씨 예보의 row 수
        String pageNo = "1";

        WeatherDTO foreCast = getForeCast(serviceKey, returnType, numOfRows, pageNo, dto);

        return getAirPollution(serviceKey, returnType, numOfRows, pageNo, foreCast);
    }


    // 단기 예보 조회
    private WeatherDTO getForeCast(String serviceKey, String returnType, String numOfRows, String pageNo, WeatherDTO dto) throws Exception {

        log.info("=== getForeCast Start ===");

        LocalDateTime targetDay = LocalDateTime.now().plusDays(-1);
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String base_date = targetDay.format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREAN));
        String base_time = "2300";

        String nx = "35";
        String ny = "129";

        // URL
        String result = url + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + serviceKey +
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(returnType, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(numOfRows, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(pageNo, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_date, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_time, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(nx, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(ny, StandardCharsets.UTF_8);

        HashMap<String, Object> foreCast = getData(url, result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("foreCast", foreCast);

        JSONArray foreJson = jsonObject.getJSONObject("foreCast").getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

        System.out.println("foreJson = " + foreJson);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00", Locale.KOREAN));

        for (int i = 0; i < foreJson.length(); i++) {
            String fcstTime = foreJson.getJSONObject(i).getString("fcstTime");
            String category = foreJson.getJSONObject(i).getString("category");
            String fcstValue = foreJson.getJSONObject(i).getString("fcstValue");

            // 예보를 내린 시간이 현재의 시간과 같은 경우
            // 즉, 현재 시간의 기상 예보를 받아옴
            if (fcstTime.equals(now)) {
                switch (category) {
                    case "POP":
                        dto.setPOP(fcstValue);
                        break;
                    case "PTY":
                        dto.setPTY(fcstValue);
                        break;
                    case "SKY":
                        dto.setSKY(fcstValue);
                        break;
                    case "TMP":
                        dto.setTMP(fcstValue);
                        break;
                }
            }
        }

        return dto;
    }


    // 시도별 실시간 측정 정보 조회
    private WeatherDTO getAirPollution(String serviceKey, String returnType, String numOfRows, String pageNo, WeatherDTO dto) throws Exception {

        log.info("=== getAirPolution Start ===");

        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
        String sidoName = "서울";

        // URL
        String result = url + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + serviceKey +
                "&" + URLEncoder.encode("returnType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(returnType, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(numOfRows, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(pageNo, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("sidoName", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(sidoName, StandardCharsets.UTF_8);

        HashMap<String, Object> airPollution = getData(url, result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("airPollution", airPollution);
        JSONArray airJson = jsonObject.getJSONObject("airPollution").getJSONObject("response").getJSONObject("body").getJSONArray("items");

        for (int i = 0; i < airJson.length(); i++) {

            String sido = airJson.getJSONObject(i).getString("sidoName");
            String station = airJson.getJSONObject(i).getString("stationName");
            String pm10Value = airJson.getJSONObject(i).getString("pm10Value");

            if (sido.equals("서울") && station.equals("한강대로")) {
                dto.setPm10Value(pm10Value);
                break;
            }
        }

        return dto;
    }

    // 최종 URL로 OPEN API에 통신 후
    // 결과를 HashMap으로 반환해주는 메소드
    private HashMap<String, Object> getData(String url, String result) throws Exception {

        URL apiUrl = new URL(result);

        HttpURLConnection conn = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.setDoOutput(true);     // 서버에 데이터를 보낼 수 있는지에 대한 여부를 설정 (default: false)

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            String line = null;
            StringBuilder sbf = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sbf.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();

            resultMap = mapper.readValue(sbf.toString(), HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(url + " interface failed" + e);
        } finally {
            if (conn != null) conn.disconnect();
            if (br != null) br.close();
            if (bw != null) bw.close();
        }

        return resultMap;
    }
}
