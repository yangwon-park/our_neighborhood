var main = {
    init: function () {
         let _this = this;

        _this.loadingWithMask();
    },

    loadingWithMask: function () {
        const path = "/images/main/loading.gif";

        let mainBody = document.getElementById("main-body");
        let mask = document.createElement("div");
        mask.id = "mask";

        let loadingImg = document.createElement("img");
        loadingImg.id = "spinner";
        loadingImg.src = path;

        mask.append(loadingImg);
        mainBody.append(mask);
    },

    // index에서 날씨 정보를 다 받아온 후에 호출됨
    closeMask: function () {
        let mask = document.getElementById("mask");
        mask.style.display = "none";
        mask.remove();
    },
}

main.init();

export default main;