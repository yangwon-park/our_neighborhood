package ywphsm.ourneighbor.api;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.api.dto.SkyStatus;
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
    public WeatherDTO getWeather(@CookieValue(value="nx", required = false, defaultValue = "") String nx,
                                 @CookieValue(value="ny", required = false, defaultValue = "") String ny,
                                 @CookieValue(value="sidoName", required = false, defaultValue = "") String sidoName) throws Exception{

        WeatherDTO dto = new WeatherDTO();

        String serviceKey = "LBYxEXESOBgpdqLOhf1sRtyCQdCdagCnzNxOIFp45%2FwsK%2BjAY6%2FkK9YbmQLnGr7cbD%2FPEihLyT0u1txhmFyEmg%3D%3D";
        String returnType = "JSON";
        String numOfRows = "290";     // 오늘 하루 시간대별 모든 날씨 예보의 row 수
        String pageNo = "1";

        WeatherDTO foreCast = getForeCast(serviceKey, returnType, numOfRows, pageNo, nx, ny, dto);

        return getAirPollution(serviceKey, returnType, numOfRows, pageNo, sidoName, foreCast);
    }


    // 단기 예보 조회
    private WeatherDTO getForeCast(String serviceKey, String returnType, String numOfRows, String pageNo,
                                   String nx, String ny, WeatherDTO dto) throws Exception {

        log.info("=== getForeCast Start ===");


        LocalDateTime targetDay = LocalDateTime.now().plusDays(-1);
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String base_date = targetDay.format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREAN));
        String base_time = "2300";

        // URL
        String result = url + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + serviceKey +
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(returnType, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(numOfRows, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(pageNo, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_date, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_time, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(nx, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(ny, StandardCharsets.UTF_8);

        log.info("result={}", result);

        HashMap<String, Object> foreCast = getData(url, result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("foreCast", foreCast);

        JSONArray foreJson = jsonObject.getJSONObject("foreCast").getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

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

        String pty = dto.getPTY();
        String sky = dto.getSKY();

        if (!pty.equals("0")) {
            if (pty.equals("3")) {
                dto.setStatus(SkyStatus.SNOWY);
            } else {
                dto.setStatus(SkyStatus.RAINY);
            }
        } else {
            if (sky.equals("1")) {
                dto.setStatus(SkyStatus.SUNNY);
            } else if (sky.equals("3")) {
                dto.setStatus(SkyStatus.CLOUDY);
            } else {
                dto.setStatus(SkyStatus.VERYCLOUDY);
            }
        }

        log.info("=== getForeCast End ===");
        return dto;
    }

    // 시도별 실시간 측정 정보 조회
    private WeatherDTO getAirPollution(String serviceKey, String returnType, String numOfRows, String pageNo,
                                       String sidoName, WeatherDTO dto) throws Exception {

        log.info("=== getAirPollution Start ===");

        // API 파라미터 sidoName 조건에 맞는 시도 명으로 변환하는 로직
        // 8도의 이름은 아래와 같이 줄여서 받고, 나머지 지역명은 앞의 2글자로 받음
        // ex) 경상북도 => 경북
        if (sidoName.length() == 4) {
            sidoName = String.valueOf(sidoName.charAt(0)).concat(String.valueOf(sidoName.charAt(2)));
        } else {
            sidoName = sidoName.substring(0, 2);
        }

        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

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

        // 추후에 디테일한 현재 위치에 있는 관측소 기반으로 미세먼지 농도를 체크할 수 있게 변경 예정
        for (int i = 0; i < 1; i++) {

//            String station = airJson.getJSONObject(i).getString("stationName");
            dto.setPm10Value(airJson.getJSONObject(i).getString("pm10Value"));
        }

        log.info("=== getAirPollution End ===");

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
            throw new Exception(url + "은 유효한 URL이 아닙니다." + e);
        } finally {
            if (conn != null) conn.disconnect();
            if (br != null) br.close();
            if (bw != null) bw.close();
        }

        return resultMap;
    }
}