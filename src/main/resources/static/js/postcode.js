var main = {
    init: function () {
        const postBtn = document.getElementById("post-btn");

        if (postBtn !== null) {
            postBtn.addEventListener("click", async () => {
                await this.openPostcode(this.findLatLon);
            });
        }
    },

    openPostcode: function(findLatLon) {
        let geocoder = new kakao.maps.services.Geocoder(); // 카카오맵 geocoder 라이브러리 객체 생성

        let addr; // 우편 번호 API로 받아온 도로명 주소를 담을 변수
        let lat;  // addr의 위도
        let lon;  // addr의 경도

        new daum.Postcode({
            /*
                팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
             */
            oncomplete: function (data) {

                /*
                    도로명 주소의 노출 규칙에 따라 주소를 표시한다.
                    내려오는 변수가 값이 없는 경우엔 공백("")값을 가지므로, 이를 참고하여 분기 한다.
                 */
                let roadAddr = data.roadAddress; // 도로명 주소 변수

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                document.getElementById("zipcode").value = data.zonecode;
                document.getElementById("roadAddr").value = roadAddr;
                document.getElementById("numberAddr").value = data.jibunAddress;

                addr = roadAddr;

                let guideTextBox = document.getElementById("guide");

                /*
                    사용자가 "선택 안함"을 클릭한 경우, 예상 주소라는 표시를 해준다.
                 */
                if (data.autoRoadAddress) {
                    let expRoadAddr = data.autoRoadAddress + extraRoadAddr;
                    guideTextBox.innerHTML = "(예상 도로명 주소 : " + expRoadAddr + ")";
                    guideTextBox.style.display = "block";

                } else if (data.autoJibunAddress) {
                    let expJibunAddr = data.autoJibunAddress;
                    guideTextBox.innerHTML = "(예상 지번 주소 : " + expJibunAddr + ")";
                    guideTextBox.style.display = "block";
                } else {
                    guideTextBox.innerHTML = "";
                    guideTextBox.style.display = "none";
                }
            },

            /*
                state는 우편번호 찾기 화면이 어떻게 닫혔는지에 대한 상태 변수
             */
            onclose: function (state) {
                /*
                    브라우저 닫기 버튼을 통해 팝업창을 닫았을 경우, 실행될 코드를 작성하는 부분.
                 */
                if (state === "FORCE_CLOSE") {

                /*
                    사용자가 검색결과를 선택하여 팝업창이 닫혔을 경우, 실행될 코드를 작성하는 부분.
                    oncomplete 콜백 함수가 실행 완료된 후에 실행.
                 */
                } else if (state === "COMPLETE_CLOSE") {

                    findLatLon(geocoder, addr, lat, lon);
                }
            }
        }).open();
    },

    findLatLon: function (geocoder, addr, lat, lon) {
        geocoder.addressSearch(addr, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                lat = result[0].y
                lon = result[0].x
            }

            document.getElementById("lat").value = lat;
            document.getElementById("lon").value = lon;
        });
    },
}

main.init();