var main = {
    init: function () {
        var _this = this;

        _this.loadCoords();
    },

    setCookie: function (key, value, exp) {
        let date = new Date();
        date.setDate(date.getDate() + (exp * 1000 * 60 * 60)); // 1000 * 60 * 60 = 1시간
        document.cookie = key + "=" + value + "; path=/; expires=" + date.toUTCString() + ";";
    },

    findCoords: function () {
        navigator.geolocation.getCurrentPosition(
            this.success, this.error
        );
    },

    loadCoords: function () {
        // const coords = sessionStorage.getItem("coords");

        // if (coords == null) {
        //     this.findCoords();
        // }

        this.findCoords();
    },

    success: function (position) {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        const coords = {
            lat,
            lon
        };

        console.log(coords);

        main.setCookie("lat", lat, 6);
        main.setCookie("lon", lon, 6);
        main.setCookie("coords", JSON.stringify(coords), 6);
    },

    error: function () {
        alert('현재 위치 정보를 가져올 수 없습니다.');
    }

};

main.init();