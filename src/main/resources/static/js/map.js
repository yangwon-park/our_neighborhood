// 마커를 담을 배열
// 새로운 검색이 발생하면 초기화 시켜버림
var markers = [];

// 인포윈도우를 담을 배열
// 새로운 검색이 발생하면 초기화 시켜버림
var infowindows = [];

// 검색 결과를 담을 배열
var searchResult = [];

// 엔터키 입력하면 findStores 동작
document.getElementById('keyword').addEventListener('keydown', (e) => {
    if (e.isComposing === false && e.code == 'Enter') { // 한글 입력 시 이벤트 두번 발생 방지
        findStores()
    }
})

// 클릭 이벤트 시 findStores 동작
document.getElementById('searchBtn').addEventListener('click', findStores);

// 메뉴를 찾는 함수
function findStores() {
    const keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {
        alert('키워드를 입력해주세요!');
        return false;
    }

    axios({
        method: "get",
        url: "/searchStores",
        params: {
            keyword: keyword
        }
    })
        .then((res) => {
            if (res.data.count < 1) {
                alert('검색 결과가 없어요!!');
                window.location.href = 'http://localhost:8080/map';
            }

            for (let i = 0; i < res.data.data.length; i++) {
                searchResult.push(res.data.data[i])
            }

            displayMarker(searchResult);

            // input 태그 값 지움
            const input = document.getElementById('keyword');
            input.value = null;
        })
        .catch((error) => {
            alert('검색된 장소가 없어요!!!');
        });
}

function displayMarker(result) {

    removeMarker();

    var bounds = new kakao.maps.LatLngBounds();

    for (var i = 0; i < result.length; i++) {
        var marker = addMarker(result[i]);
        var position = new kakao.maps.LatLng(result[i].lat, result[i].lon);

        bounds.extend(position);
    }

    // 부드러운 이동
    map.panTo(bounds);
}

function addMarker(data) {
    var position = new kakao.maps.LatLng(data.lat, data.lon);

    // 마커를 생성하는 시점에 인포윈도우를 등록하지 않으면
    // 마커 하나에 인포 윈도우 position이 다 고정됨
    var marker = new kakao.maps.Marker({
        position: position,
        image: markerImage
    });

    var infowindow = new kakao.maps.InfoWindow();

    kakao.maps.event.addListener(marker, 'click', addInfoWindow(marker, data, infowindow));

    marker.setMap(map);
    markers.push(marker);
    infowindows.push(infowindow);

    return marker;
}

function addInfoWindow(marker, data, infowindow) {
    return () => {
        closeInfowindow();
        infowindow.setContent(
            '<div class="container">' +
            '    <div class="row mt-2">' +
            '        <div class="col m-auto">' +
            '           <a href="/store/' + data.storeId + '" class="fs-5 me-2">' + data.name + '</a>' +
            '           <span class="badge mb-2 ' + data.status + '">' + data.status + '</span>' +
            '        </div>' +
            '    </div>' +
            '    <div class="row d-flex">' +
            '        <div class="col m-0">' +
            '            <p class="card-text">리뷰 점수 리뷰 별(건수)</p>' +
            '        </div>' +
            '    </div>' +
            '    <div class="row d-flex">' +
            '        <div class="col text-nowrap">' +
            '            <p class="card-text" style="font-size: 0.85rem">' + data.address.roadAddr + ' ' + data.address.detail + '</p>' +
            '        </div>' +
            '    </div>' +
            '    <div class="row d-flex mb-2" style="font-size: 0.78rem; color: #6c757d">' +
            '        <div class="col">\n' +
            '            <p class="card-text">' + data.address.zipcode + '</p>' +
            '        </div>' +
            '    </div>' +
            '    <div class="row d-flex mb-2">' +
            '        <div class="col">' +
            '           <a style="color: #146c43; font-size: 0.85rem" href="tel:' + data.phoneNumber + '">' + data.phoneNumber + '</a>' +
            '        </div>' +
            '    </div>' +
            '</div>')
        // infowindow.setPosition(marker.getPosition());

        // 마커를 주면, 마커에 인포윈도우가 열림
        infowindow.open(map, marker);
    }
}

// 인포윈도우 닫는 함수
// 호출 시점 - 다른 마커 클릭, 새로운 장소 검색
function closeInfowindow() {
    for (var i = 0; i < infowindows.length; i++) {
        infowindows[i].close();
    }
}

// 마커, 인포윈도우 초기화
function removeMarker() {
    closeInfowindow()
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }

    // 전역 변수로 선언한 아래의 배열들 초기화
    // 해줘야 매번 검색 시, 새로운 조건으로 검색 가능
    markers = [];
    infowindows = [];
    searchResult = [];
}
