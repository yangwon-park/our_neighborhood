import mask from "./mask.js";
import cookie from "./cookies.js";

var main = {
    init: async function () {
        sessionStorage.clear();
        mask.loadingWithMask();

        this.initSlick();

        let customCheckCookie = cookie.getCookie("customCheck");
        let customLocationCookie = cookie.getCookie("customLocation");
        let customLocationInput = document.getElementById("custom-location-input");

        /*
            위치 설정을 직접한 값이 있는 경우에만 input tag에 값을 넣어줌
         */
        if (customLocationCookie !== null && customLocationInput !== null) {
            customLocationInput.value = decodeURIComponent(customLocationCookie);
        }

        if (customCheckCookie === "true") {
            await this.changeCustomPosition();
        } else {
            await this.findCoords();
        }

        this.setCategoryIdInSessionStorage();

        const setCustomLocationModalBtn = document.getElementById("set-custom-location-modal-save");
        const setCurrentLocationBtn = document.getElementById("set-current-location");

        setCustomLocationModalBtn.addEventListener("click", async () => {
            mask.loadingWithMask();
            await this.changeCustomPosition();
        });

        setCurrentLocationBtn.addEventListener("click", async () => {
            mask.loadingWithMask();
            await this.findCoords();
        });
    },

    getStoreBasedOnWeather: function () {
        const recommendPostHeader = document.getElementById("recommend-post-header");
        const recommendPostContent = document.getElementById("recommend-post-content");
        const weatherBtn = document.getElementById("weather-btn");

        axios({
            method: "get",
            url: "/get-recommend-post"
        }).then((resp) => {
            recommendPostHeader.innerText = resp.data.header;
            recommendPostContent.firstElementChild.innerText = resp.data.content;

            weatherBtn.addEventListener("click", () => {
                sessionStorage.setItem("hashtagIdList", resp.data.hashtagIdList);
                window.location = "/recommend/store/list";
            });
        }).catch((error) => {
            console.error(error);
        });
    },

    getStoresRandomly: function () {
        axios({
            method: "get",
            url: "/search-top7-random"
        }).then((resp) => {

            /*
                index, removeBefore, removeAll (true)
             */
            let slide = $("#slick-slide");

            slide.slick("slickRemove", null, null, true);

            for (const store of resp.data.data) {
                let cardWrap = document.createElement("div");
                cardWrap.classList.add("random-col", "mx-2", "my-3");

                let card = document.createElement("div");
                card.classList.add("card");
                card.classList.add("main-border")

                let aTag = document.createElement("a");
                aTag.href = "/store/" + store.storeId;
                aTag.tabIndex = 0;

                let img = document.createElement("img");
                img.classList.add("card-img-top");
                img.src = store.uploadImgUrl;
                img.alt = "대표이미지";

                let cardBody = document.createElement("div");
                cardBody.classList.add("card-body");

                let title = document.createElement("h4");
                title.classList.add("card-title", "fw-bold");
                title.innerText = store.name;

                let cardText = document.createElement("p");
                cardText.classList.add("card-text", "fw-light", "mb-4");

                aTag.appendChild(img);

                cardBody.appendChild(title);
                cardBody.appendChild(cardText);

                card.appendChild(aTag);
                card.appendChild(cardBody);

                cardWrap.appendChild(card);

                slide.slick("slickAdd", cardWrap);
            }
        })
    },

    getCateImages: function () {
        axios({
            method: "get",
            url: "/get-cate-images"
        }).then((resp) => {
            const restaurantImages = resp.data[0];
            const cafeImages = resp.data[1];
            const barImages = resp.data[2];
            const leisureImages = resp.data[3];

            const restDiv = document.getElementById("cate-img1");
            const cafeDiv = document.getElementById("cate-img2");
            const barDiv = document.getElementById("cate-img3");
            const leisureDiv = document.getElementById("cate-img4");

            restDiv.style.backgroundImage = "";
            cafeDiv.style.backgroundImage = "";
            barDiv.style.backgroundImage = "";
            leisureDiv.style.backgroundImage = "";

            if (restaurantImages.length > 0) {
                let restNum = this.randomInt(0, restaurantImages.length);
                restDiv.style.backgroundImage = `url(${restaurantImages[restNum]})`;
            }

            if (cafeImages.length > 0) {
                let cafeNum = this.randomInt(0, cafeImages.length);
                cafeDiv.style.backgroundImage = `url(${cafeImages[cafeNum]})`;
            }

            if (barImages.length > 0) {
                let barNum = this.randomInt(0, barImages.length);
                barDiv.style.backgroundImage = `url(${barImages[barNum]})`;
            }

            if (leisureImages.length > 0) {
                let leisureNum = this.randomInt(0, leisureImages.length);
                leisureDiv.style.backgroundImage = `url(${leisureImages[leisureNum]})`;
            }
        }).catch((error) => {
            console.error(error);
        })
    },

    getWeatherInfoWithAPI: function () {
        axios({
            method: "get",
            url: "/weather",
        }).then((resp) => {
            let skyStatus = resp.data.status;
            let currentTmp = resp.data.tmp;
            let currentPop = resp.data.pop;
            let currentPcp = resp.data.pcp;
            let pm10Value;

            if (resp.data.pm10Value === "-") {
                pm10Value = 50;
            } else {
                pm10Value = resp.data.pm10Value;
            }

            cookie.setCookie("skyStatus", skyStatus, 1);
            cookie.setCookie("tmp", currentTmp, 1);
            cookie.setCookie("pop", currentPop, 1);
            cookie.setCookie("pcp", currentPcp, 1);
            cookie.setCookie("pm10Value", pm10Value, 1);

            this.setWeatherInfoInEl(skyStatus, currentTmp, currentPop, currentPcp, pm10Value);

            this.getStoreBasedOnWeather();

            mask.closeMask();
        }).catch((error) => {
            alert("현재 날씨 정보를 불러올 수 없습니다.");
            mask.closeMask();
            console.error(error);
        });
    },

    getWeatherInfoWithCookies: function () {
        let skyStatus = cookie.getCookie("skyStatus");
        let currentTmp = cookie.getCookie("tmp");
        let currentPop = cookie.getCookie("pop");
        let currentPcp = cookie.getCookie("pcp");
        let pm10Value = cookie.getCookie("pm10Value");

        this.setWeatherInfoInEl(skyStatus, currentTmp, currentPop, currentPcp, pm10Value);

        this.getStoreBasedOnWeather();

        mask.closeMask();
    },

    setWeatherInfoInEl: function (skyStatus, currentTmp, currentPop, currentPcp, pm10Value) {
        const _skyStatus = document.getElementById("sky-status");
        const _tmp = document.getElementById("tmp");
        const _pop = document.getElementById("pop");
        const _pm10Value = document.getElementById("pm-10-value");

        if (skyStatus === "SUNNY") {
            _skyStatus.style.backgroundImage = "url(../images/icon/weather/sunny.png)"
        } else if (skyStatus === "CLOUDY") {
            _skyStatus.style.backgroundImage = "url(../images/icon/weather/cloudy.png)"
        } else if (skyStatus === "VERYCLOUDY") {
            _skyStatus.style.backgroundImage = "url(../images/icon/weather/very_cloudy.png)"
        } else if (skyStatus === "RAINY") {
            _skyStatus.style.backgroundImage = "url(../images/icon/weather/rainy.png)"
        } else if (skyStatus === "SNOWY") {
            _skyStatus.style.backgroundImage = "url(../images/icon/weather/snowy.png)"
        }

        _skyStatus.style.backgroundSize = "contain";
        _skyStatus.style.backgroundRepeat = "no-repeat";
        _skyStatus.style.backgroundPosition = "center";

        if (pm10Value <= 30) {
            _pm10Value.firstElementChild.innerText = "미세먼지 수준";
            _pm10Value.lastElementChild.innerText = "좋음 (농도 : " + pm10Value + ")";
            _pm10Value.lastElementChild.style.color = "green";
        } else if (pm10Value <= 80) {
            _pm10Value.firstElementChild.innerText = "미세먼지 수준";
            _pm10Value.lastElementChild.innerText = "보통 (농도 : " + pm10Value + ")";
        } else if (pm10Value <= 150) {
            _pm10Value.firstElementChild.innerText = "미세먼지 수준";
            _pm10Value.lastElementChild.innerText = "나쁨 (농도 : " + pm10Value + ")";
            _pm10Value.lastElementChild.style.color = "#F01D04";
        } else {
            _pm10Value.firstElementChild.innerText = "미세먼지 수준";
            _pm10Value.lastElementChild.innerText = "매우 나쁨 (농도 : " + pm10Value + ")";
            _pm10Value.lastElementChild.style.color = "#F01D04";
        }

        _tmp.firstElementChild.innerText = "현재 기온";
        _tmp.lastElementChild.innerText = currentTmp + "°";

        _pop.firstElementChild.innerText = "시간당 강수량";
        _pop.lastElementChild.innerText = currentPcp + " (강수 확률 : " + currentPop + "%)";
    },

    initSlick: function () {
        const slickSlide = $("#slick-slide");

        if(slickSlide) {
            slickSlide.slick({
                dots: true,
                arrows: false,
                slidesToShow: 3,
                slideToScroll: 1,
                autoplay: true,
                autoplaySpeed: 3000,
                responsive: [
                    {
                        breakpoint: 768,
                        settings: {
                            slidesToShow: 2
                        }
                    },
                    {
                        breakpoint: 576,
                        settings: {
                            slidesToShow: 1
                        }
                    }
                ]
            });
        }
    },

    changeCustomPosition: async function () {
        const customLocation = document.getElementById("custom-location-input");
        let geocoder = new kakao.maps.services.Geocoder();      // 카카오맵 geocoder 라이브러리 객체 생성

        /*
            직접 설정한 위치인지 체크해주는 쿠키
         */
        cookie.setCookie("customCheck", true, 1);

        /*
            직접 설정한 주소값을 쿠키에 저장하는 조건
                1. 기존의 쿠키값이 없는 경우 (공백, null)
                2. 새로 입력한 주소값과 기존의 주소값이 다른 경우
         */
        let prevCustomLocationCookie = cookie.getCookie("customLocation");

        if (prevCustomLocationCookie === "" || prevCustomLocationCookie === null
            || customLocation.value !== decodeURIComponent(prevCustomLocationCookie)) {
            cookie.setCookie("customLocation", customLocation.value, 1);
        }

        geocoder.addressSearch(decodeURIComponent(cookie.getCookie("customLocation")), async (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                let lat = result[0].y
                let lon = result[0].x

                await this.setMainData(lat, lon);
            }

            if (status === kakao.maps.services.Status.ZERO_RESULT ||
                    status === kakao.maps.services.Status.ERROR) {
                alert("올바른 주소를 입력해주세요.");
                await this.findCoords();
                window.location.reload();
            }
        });
    },

    setMainData: async function (lat, lon) {
        let prevNx = cookie.getCookie("nx");
        let prevNy = cookie.getCookie("ny");

        await this.setCurrentPositionData(lat, lon);

        let currentNx = cookie.getCookie("nx");
        let currentNy = cookie.getCookie("ny");
        let skyStatus = cookie.getCookie("skyStatus");

        /*
            이전에 쿠키값과 현재 쿠키값이 다른 경우 API 호출
            즉, 현재 위치 정보가 변경됐거나 쿠키가 만료됐을 경우 API 재호출
                => 메인 홈페이지 재방문시 로딩 속도 향상
         */
        if (prevNx !== currentNx && prevNy !== currentNy) {
            this.getWeatherInfoWithAPI();

            /*
                어쩌다 날씨 정보가 불러와지지 않았으면 API로 호출
             */
        } else if (skyStatus === null) {
            this.getWeatherInfoWithAPI();
        } else {
            this.getWeatherInfoWithCookies();
        }

        this.getCateImages();
        this.getStoresRandomly()
    },

    setCategoryIdInSessionStorage: function () {
        const categoryDivList = document.querySelectorAll(".category-div");

        if (categoryDivList !== null) {
            categoryDivList.forEach((el) => {
                let categoryId = el.children.namedItem("categoryId").value;

                el.addEventListener("click", () => {
                    sessionStorage.setItem("cateFromHome", categoryId);
                    window.location.href = "map";
                });
            });
        }
    },

    findCoords: async function () {
        let position = await this.getCoords();
        cookie.setCookie("customCheck", false, 1);
        await this.setMainData(position.coords.latitude, position.coords.longitude);
    },

    getCoords: function (options) {
        return new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(resolve, reject, options);
        })
    },

    getSidoName: async function (lat, lon) {
        let data = await this.getRegionCode(lat, lon);

        let sidoName = data.result[0].region_1depth_name;

        cookie.setCookie("sidoName", sidoName, 1);
    },

    getRegionCode: function (lat, lon) {
        // 위경도를 이용하여 행정구역정보 얻기
        var geocoder = new kakao.maps.services.Geocoder();

        var loc = new kakao.maps.LatLng(lat, lon);
        return new Promise((resolve, reject) => {
            geocoder.coord2RegionCode(loc.getLng(), loc.getLat(), function (result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    resolve({result});
                } else {
                    reject(status);
                }
            });
        })
    },

    setCurrentPositionData: async function (lat, lon) {
        let coords = this.dfsXyConv("toXY", lat, lon);
        let nx = coords["nx"];
        let ny = coords["ny"];

        cookie.setCookie("nx", nx, 1);
        cookie.setCookie("ny", ny, 1);
        cookie.setCookie("lat", lat, 1);
        cookie.setCookie("lon", lon, 1);

        await this.getSidoName(lat, lon);
    },

    error: function () {
        alert("현재 위치 정보를 가져올 수 없습니다.");
    },

    params: {
        RE: 6371.00877,     // 지구 반경(km)
        GRID: 5.0,          // 격자 간격(km)
        SLAT1: 30.0,         // 투영 위도1(degree)
        SLAT2: 60.0,        // 투영 위도2(degree)
        OLON: 126.0,        // 기준점 경도(degree)
        OLAT: 38.0,         // 기준점 위도(degree)
        XO: 43,             // 기준점 X좌표(GRID)
        YO: 136             // 기1준점 Y좌표(GRID)
    },

    /*
        LCC DFS 좌표변환 함수
            code : "toXY"(위경도->좌표, v1:위도, v2:경도), "toLL"(좌표->위경도,v1:x, v2:y)

            ** 참고 : https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16
     */
    dfsXyConv: function (code, v1, v2) {
        var DEGRAD = Math.PI / 180.0;
        var RADDEG = 180.0 / Math.PI;

        var re = this.params.RE / this.params.GRID;
        var slat1 = this.params.SLAT1 * DEGRAD;
        var slat2 = this.params.SLAT2 * DEGRAD;
        var olat = this.params.OLAT * DEGRAD;
        var olon = this.params.OLON * DEGRAD;

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        var rs = {};

        if (code === "toXY") {
            rs["lat"] = v1;
            rs["lon"] = v2;

            var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);

            var theta = v2 * DEGRAD - olon;

            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;

            theta *= sn;

            rs["nx"] = Math.floor(ra * Math.sin(theta) + this.params.XO + 0.5);
            rs["ny"] = Math.floor(ro - ra * Math.cos(theta) + this.params.YO + 0.5);
        } else {
            rs["nx"] = v1;
            rs["ny"] = v2;

            var xn = v1 - this.params.XO;
            var yn = ro - v2 + this.params.YO;

            ra = Math.sqrt(xn * xn + yn * yn);

            if (sn < 0.0) -ra;

            var alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) -theta;
                } else theta = Math.atan2(xn, yn);
            }

            var alon = theta / sn + olon;

            rs["lat"] = alat * RADDEG;
            rs["lon"] = alon * RADDEG;
        }

        return rs;
    },

    randomInt: function (min, max) {
        return Math.floor(Math.random() * (max)) + min;
    }
};

main.init();