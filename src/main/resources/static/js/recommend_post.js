let main = {
    init: function () {

        let recommendSaveBtn = document.getElementById("recommend-post-save");

        if (recommendSaveBtn != null) {
            recommendSaveBtn.addEventListener("click", () => {
                this.saveRecommendPost();
            });
        }

        this.getRecommendPost();
    },

    saveRecommendPost: function () {
        const form = document.getElementById("recommend-post-add-form");
        const formData = new FormData(form);

        axios({
            method: "post",
            url: "/admin/recommend-post",
            data: formData
        }).then((resp) => {
            alert("양식이 등록됐습니다.");
            window.location.reload();
        }).catch((error) => {
           console.log(error)
        });
    },

    getRecommendPost: function () {
        console.log(sessionStorage.getItem("hashtagIdList"));

        let hashtagIdList = sessionStorage.getItem("hashtagIdList");

        axios({
            method: "get",
            url: "/recommend-post",
            params: {
                hashtagIdList: hashtagIdList
            }
        }).then((resp) => {
        }).catch((error) => {

        });
    },
}

main.init();