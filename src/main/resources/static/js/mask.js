var main = {
    init: function () {
         var _this = this;

        _this.loadingWithMask();
    },

    loadingWithMask: function () {
        const path = "/images/main/loading.gif";

        // jquery => vanilla js 로 변경
        // let mask =  "<div id='mask'>";
        //     mask += "    <img src='" + path + "' alt='로딩 이미지'/>";
        //     mask += "</div>";
        //
        // $('body')
        //     .append(mask)

        let mainBody = document.getElementById('main-body');

        let mask = document.createElement("div");
        mask.id = "mask";

        let loadingImg = document.createElement("img");
        loadingImg.src = path;

        mask.append(loadingImg);
        mainBody.append(mask);
    },

    // index에서 날씨 정보를 다 받아온 후에 호출됨
    closeMask: function () {
        let mask = document.getElementById('mask');
        mask.style.display = "none";
        mask.remove();
    },
}

main.init();

export default main;