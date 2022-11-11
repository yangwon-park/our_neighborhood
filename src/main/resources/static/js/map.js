var main = {
    init: async function () {
        let _this = this;

        let map = _this.getMap();

        _this.setSearchKeywordEvent(map);
        await _this.getCategories();

        let findCate = document.querySelectorAll("a.search-main-cate");
        _this.setSearchByCategoriesEvent(findCate, map);

        _this.setPreviousData(map);

        kakao.maps.event.addListener(map, "click", () => {
            _this.closeInfoWindow();
        });
    },

    getMap: function () {
        // 지도를 담을 div를 찾음
        var mapContainer = document.getElementById('map');

        // 지도에 관한 옵션 부여
        var mapOptions = {
            center: new kakao.maps.LatLng(33.450701, 126.570667),  // 중심 좌표
            level: 1		// 확대 레벨
        };

        // 지도 생성
        var map = new kakao.maps.Map(mapContainer, mapOptions);

        // var zoomControl = new kakao.maps.ZoomControl();
        // map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

        var currentPosition;
        var pointerSrc = "../images/main/blink_pointer.gif",
            pointerSize = new kakao.maps.Size(25, 25);

        var pointerImage = new kakao.maps.MarkerImage(pointerSrc, pointerSize);

        // 현재 위치로 맵 중심 정렬 - geolocation (배포시, https 필수)
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition((position) => {
                var lat = position.coords.latitude,
                    lon = position.coords.longitude;

                currentPosition = new kakao.maps.LatLng(lat, lon);

                var marker = new kakao.maps.Marker({
                    position: currentPosition,
                    image: pointerImage
                });

                marker.setMap(map);

                map.setCenter(currentPosition);

                let moveToCurrentLocationBtn = document.getElementById("moveToCurrentLocationBtn");

                moveToCurrentLocationBtn.addEventListener("click", () => {
                    this.moveToCurrentLocation(map, currentPosition);
                });
            })
        } else {
            var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),
                message = "geolocation을 사용할 수 없음"

            map.setCenter(locPosition, message)
        }

        return map;
    },

    setSearchKeywordEvent: function (map) {
        let prevKeyword = sessionStorage.getItem("keyword");

        // 엔터키 입력하면 searchByKeyword 동작
        document.getElementById("keyword").addEventListener("keydown", (e) => {
            if (e.isComposing === false && e.code === "Enter") { // 한글 입력 시 이벤트 두번 발생 방지
                this.searchByKeyword(prevKeyword, map);
            }
        });

        // 클릭 이벤트 시 searchByKeyword 동작
        document.getElementById("searchBtn").addEventListener("click", () => {
            this.searchByKeyword(prevKeyword, map);
        });
    },

    searchByKeyword: function (prevKeyword, map) {
        let keyword = document.getElementById("keyword").value;

        if (!keyword.replace(/^\s+|\s+$/g, "")) {
            if (prevKeyword === null) {
                alert("키워드를 입력해주세요!");
                return false;
            } else {
                keyword = prevKeyword;
            }
        }

        axios({
            method: "get",
            url: "/search-by-keyword",
            params: {
                keyword: keyword
            }
        })
            .then((resp) => {
                if (resp.data.count < 1) {
                    alert("검색 결과가 없습니다. 다른 조건으로 검색해보시겠어요?");
                    return;
                }

                for (let i = 0; i < resp.data.data.length; i++) {
                    this.searchResult.push(resp.data.data[i])
                }

                this.displayMarker(this.searchResult, map);

                sessionStorage.clear();
                sessionStorage.setItem("keyword", keyword);

                // input 태그 값 지움
                const input = document.getElementById('keyword');
                input.value = null;
            })
            .catch((e) => {
                console.error(e);
                alert("왜 에러가 뜰까요??");
            });
    },

    setSearchByCategoriesEvent: function (findCate, map) {
        for (const el of findCate) {
            el.addEventListener("click", () => {

                // input radio에서 selected된 값 가져옴
                let radio = document.querySelector('input[name="dist"]:checked');

                if (radio === null) {
                    alert("원하는 거리를 설정해주세요!!!");
                }

                let categoryId = el.getAttribute("data-value");
                let dist = radio.value;

                this.searchByCategories(categoryId, dist, map);
            });
        }
    },

    searchByCategories: function (categoryId, dist, map) {
        axios({
            method: "get",
            url: "/search-by-category",
            params: {
                categoryId: categoryId,
                dist: dist
            }
        })
            .then((res) => {
                if (res.data.count < 1) {
                    alert("검색 결과가 없습니다. 다른 조건으로 검색해보시겠어요?");
                    return;
                }

                for (let i = 0; i < res.data.data.length; i++) {
                    this.searchResult.push(res.data.data[i])
                }

                this.displayMarker(this.searchResult, map);

                sessionStorage.clear();
                sessionStorage.setItem("dist", dist);
                sessionStorage.setItem("categoryId", categoryId);
            })
            .catch((e) => {
                console.error(e);
                alert("왜 에러가 뜰까요??");
            });
    },

    getTopNCategories: function (categoryId, map) {
        axios({
            method: "get",
            url: "/get-topN-categories",
            params: {
                categoryId: categoryId
            }
        })
            .then((res) => {
                if (res.data.count < 1) {
                    sessionStorage.clear();
                    alert("검색 결과가 없습니다. 다른 조건으로 검색해보시겠어요?");
                    return;
                }

                for (let i = 0; i < res.data.data.length; i++) {
                    this.searchResult.push(res.data.data[i])
                }

                this.displayMarker(this.searchResult, map);

                sessionStorage.clear();
                sessionStorage.setItem("cateFromHome", categoryId);
            })
            .catch((e) => {
                console.error(e);
                alert("왜 에러가 뜰까요??");
            });
    },

    getCategories: async function () {
        await axios({
            method: "get",
            url: "/categories-hier",
        }).then((resp) => {
            let rootChildren = resp.data.children;
            this.getMainCategories(rootChildren);
        }).catch((e) => {
            console.error(e);
        })
    },

    getMainCategories: function (children) {
        const mapCategory = document.getElementById("map-category");

        let mainChildren = [];

        for (const rc of children) {
            let mainLi = document.createElement("li");
            let liA = document.createElement("a");

            liA.setAttribute("class", "search-main-cate d-inline-flex text-decoration-none rounded");
            liA.setAttribute("href", "#");
            liA.setAttribute("data-value", rc.categoryId);
            liA.textContent = rc.name;

            mainLi.appendChild(liA);
            mapCategory.appendChild(mainLi);

            mainChildren.push(rc.children)
        }

        return mainChildren;
    },


    /*
        === 아래부터 지도 표현 로직 ===
     */
    markers: [],

    infoWindows: [],

    searchResult: [],

    displayMarker: function (result, map) {
        this.removeMarker();

        var bounds = new kakao.maps.LatLngBounds();

        for (let i = 0; i < result.length; i++) {

            this.addMarker(result[i], map);
            var position = new kakao.maps.LatLng(result[i].lat, result[i].lon);

            bounds.extend(position);
        }

        // 부드러운 이동
        map.panTo(bounds);
    },

    closeInfoWindow: function () {
        for (let i = 0; i < this.infoWindows.length; i++) {
            this.infoWindows[i].close();
        }
    },

    addMarker: function (data, map) {
        var imageSrc = '../images/main/map_marker.png',
            imageSize = new kakao.maps.Size(55, 60),
            imageOption = {offset: new kakao.maps.Point(27, 69)};

        var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

        var position = new kakao.maps.LatLng(data.lat, data.lon);

        // 마커를 생성하는 시점에 인포윈도우를 등록하지 않으면
        // 마커 하나에 인포 윈도우 position이 다 고정됨
        var marker = new kakao.maps.Marker({
            position: position,
            image: markerImage,
        });

        var infoWindow = new kakao.maps.InfoWindow({
            removable: true
        });

        kakao.maps.event.addListener(marker, 'click', this.addInfoWindow(marker, data, infoWindow, map));

        marker.setMap(map);
        this.markers.push(marker);
        this.infoWindows.push(infoWindow);

        return marker;
    },

    addInfoWindow: function (marker, data, infoWindow, map) {
        return () => {
            this.closeInfoWindow();
            infoWindow.setContent(
                '<div class="container text-nowrap">' +
                '    <div class="row mt-2">' +
                '        <div class="col m-auto d-inline">' +
                '           <a style="text-decoration: none; color: #0b1526" href="/store/' + data.storeId + '" id="info-title" class="fs-5 me-2">' + data.name + '</a>' +
                '           <span class="badge mb-2 ' + data.status + '">' + data.status + '</span>' +
                // '           <span class="float-end mt-1">' +
                // '               <button type="button" class="btn-close" aria-label="Close"></button>' +
                // '           </span>' +
                '        </div>' +
                '    </div>' +
                '    <div class="row d-flex">' +
                '        <div class="col m-0">' +
                '            <p class="card-text">리뷰 점수 리뷰 별(건수)</p>' +
                '        </div>' +
                '    </div>' +
                '    <div class="row d-flex">' +
                '        <div class="col">' +
                '            <p class="card-text" style="font-size: 0.85rem">' + data.address.roadAddr + '<br>' + data.address.detail + '</p>' +
                '        </div>' +
                '    </div>' +
                '    <div class="row d-flex mb-2" style="font-size: 0.78rem; color: #6c757d">' +
                '        <div class="col">\n' +
                '            <p class="card-text">' + data.address.zipcode + '</p>' +
                '        </div>' +
                '    </div>' +
                '    <div class="row d-flex mb-2">' +
                '        <div class="col-6">' +
                '           <a style="color: #146c43; font-size: 0.85rem; text-decoration: none" href="tel:' + data.phoneNumber + '">' + data.phoneNumber + '</a>' +
                '        </div>' +
                '        <div class="col-6">' +
                '            <p class="card-text text-end main-color fw-bold">' + data.distance + 'km</p>' +
                '        </div>' +
                '    </div>' +
                '</div>')
            // infoWindow.setPosition(marker.getPosition());

            // 마커를 주면, 마커에 인포윈도우가 열림
            infoWindow.open(map, marker);
        }
    },

    removeMarker: function () {
        this.closeInfoWindow();

        for (let i = 0; i < this.markers.length; i++) {
            this.markers[i].setMap(null);
        }

        // 전역 변수로 선언한 아래의 배열들 초기화
        // 해줘야 매번 검색 시, 새로운 조건으로 검색 가능
        this.markers = [];
        this.infoWindows = [];
        this.searchResult = [];
    },

    moveToCurrentLocation: function (map, currentPosition) {
        map.setLevel(1);
        map.panTo(currentPosition);
    },

    setPreviousData: function (map) {
        window.onpageshow = (e) => {
            if (e.persisted || window.performance &&
                (performance.getEntriesByType("navigation")[0].type === "navigate")) {

                if (("sessionStorage" in window) && window["sessionStorage"] !== null) {
                    if (sessionStorage.getItem("cateFromHome") !== null) {
                        let cateFromHome = sessionStorage.getItem("cateFromHome");

                        this.getTopNCategories(cateFromHome, map);
                    }
                }
            }

            if (e.persisted || (window.performance &&
                (performance.getEntriesByType("navigation")[0].type === "reload" ||
                    performance.getEntriesByType("navigation")[0].type === "back_forward"))) {

                // sessionStorage 지원 여부 확인
                if (("sessionStorage" in window) && window["sessionStorage"] !== null) {
                    if (sessionStorage.getItem("keyword") !== null) {
                        let prevKeyword = sessionStorage.getItem("keyword");

                        this.searchByKeyword(prevKeyword, map);
                    }

                    if (sessionStorage.getItem("dist") !== null &&
                        sessionStorage.getItem("categoryId") !== null) {
                        let dist = sessionStorage.getItem("dist");
                        let categoryId = sessionStorage.getItem("categoryId");

                        this.searchByCategories(categoryId, dist, map);
                    }

                    if (sessionStorage.getItem("cateFromHome") != null) {
                        let cateFromHome = sessionStorage.getItem("cateFromHome");

                        this.getTopNCategories(cateFromHome, map);
                    }
                }

                sessionStorage.clear();
            }
        }
    },
};

main.init();

export default main;