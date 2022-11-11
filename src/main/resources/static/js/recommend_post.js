let main = {
    init: function () {

        let recommendSaveBtn = document.getElementById("recommend-post-save");

        recommendSaveBtn.addEventListener("click", () => {
            this.saveRecommendPost();
        });
    },

    saveRecommendPost: function () {
        const form = document.getElementById("recommend-post-add-form");

        let formData = new FormData(form);

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
    }
}

main.init();