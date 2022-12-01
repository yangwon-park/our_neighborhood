
var main = {
    init: async function () {
        var _this = this;

        if (window.location.pathname != '/login') {
            _this.deleteCache();
        }

    },

    deleteCache: function () {
        axios({
            method: "get",
            url: "/delete-request-cache",
        }).then((resp) => {
            // alert("캐시 삭제")
            // console.log(resp)
        }).catch((error) => {
            console.log(error)
        });
    },


};

main.init();