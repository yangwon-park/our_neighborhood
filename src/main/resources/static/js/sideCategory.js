var main = {
    init: async function () {
        var _this = this;

        await _this.getCategories();

        let findCate = document.querySelectorAll("a.search-main-cate")
        _this.searchByCategories(findCate);
    },

    markers2: [],

    infowindows2: [],

    searchResult2: [],

    displayMarker2: function (result) {
        this.removeMarker2();

        var bounds = new kakao.maps.LatLngBounds();


        for (var i = 0; i < result.length; i++) {
            var marker = this.addMarker2(result[i]);
            var position = new kakao.maps.LatLng(result[i].lat, result[i].lon);

            bounds.extend(position);
        }

        // 부드러운 이동
        map.panTo(bounds);
    },

    closeInfowindow2: function() {
        for (var i = 0; i < this.infowindows2.length; i++) {
            this.infowindows2[i].close();
        }
    },

    addMarker2: function(data) {
        var position = new kakao.maps.LatLng(data.lat, data.lon);

        // 마커를 생성하는 시점에 인포윈도우를 등록하지 않으면
        // 마커 하나에 인포 윈도우 position이 다 고정됨
        var marker = new kakao.maps.Marker({
            position: position,
            image: markerImage
        });

        var infowindow = new kakao.maps.InfoWindow();

        kakao.maps.event.addListener(marker, 'click', this.addInfoWindow2(marker, data, infowindow));

        marker.setMap(map);
        this.markers2.push(marker);
        this.infowindows2.push(infowindow);

        return marker;
    },

    addInfoWindow2: function(marker, data, infowindow) {
        return () => {
            this.closeInfowindow2();
            infowindow.setContent(
                '<div class="container">' +
                '    <div class="row mt-2">' +
                '        <div class="col m-auto">' +
                '           <a style="text-decoration: none; color: #0b1526" href="/store/' + data.storeId + '" class="fs-5 me-2">' + data.name + '</a>' +
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
                '           <a style="color: #146c43; font-size: 0.85rem; text-decoration: none" href="tel:' + data.phoneNumber + '">' + data.phoneNumber + '</a>' +
                '        </div>' +
                '        <div class="col">' +
                '            <p class="card-text text-end">' + data.distance + 'km</p>' +
                '        </div>' +
                '    </div>' +
                '</div>')
            // infowindow.setPosition(marker.getPosition());

            // 마커를 주면, 마커에 인포윈도우가 열림
            infowindow.open(map, marker);
        }
    },

    removeMarker2: function () {
        this.closeInfowindow2();
        for (var i = 0; i < this.markers2.length; i++) {
            this.markers2[i].setMap(null);
        }

        // 전역 변수로 선언한 아래의 배열들 초기화
        // 해줘야 매번 검색 시, 새로운 조건으로 검색 가능
        this.markers2 = [];
        this.infowindows2 = [];
        this.searchResult2 = [];
    },

    searchByCategories: function (findCate) {
        for (const el of findCate) {
            el.addEventListener('click', () => {
                let categoryId = el.getAttribute('data-value');

                axios({
                    method: "get",
                    url: "/searchByCategory",
                    params: {
                        categoryId: categoryId
                    }
                })
                    .then((res) => {
                        if (res.data.count < 1) {
                            alert('검색 결과가 없습니다.');
                            window.location.href = 'http://localhost:8080/map';
                        }

                        for (let i = 0; i < res.data.data.length; i++) {
                            this.searchResult2.push(res.data.data[i])
                        }

                        this.displayMarker2(this.searchResult2);
                    })
                    .catch((error) => {
                        alert('왜 에러가 뜰까요??');
                    });
            });
        }

    },

    getCategories: async function () {
        await axios({
            method: "get",
            url: "/categoriesHier",
        }).then((resp) => {
            let rootChildren = resp.data.children;
            let mainChildren = this.getMainCategories(rootChildren);
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


};

main.init();