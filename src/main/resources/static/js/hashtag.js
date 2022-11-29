// https://inpa.tistory.com/m/entry/Tagify-%F0%9F%93%9A-%ED%95%B4%EC%8B%9C-%ED%83%9C%EA%B7%B8tag-%EC%9E%85%EB%A0%A5%EC%9D%84-%EC%9D%B4%EC%81%98%EA%B2%8C-%EA%B0%84%ED%8E%B8%ED%9E%88-%EA%B5%AC%ED%98%84-%EC%82%AC%EC%9A%A9%EB%B2%95
var main = {
    allHashtags: [],

    init: function () {
        this.getAllHashtags();

        const hashtagDeleteBtnList = document.querySelectorAll(".hashtag-delete-btn");

        if (hashtagDeleteBtnList !== null) {
            hashtagDeleteBtnList.forEach((btn) => {
                btn.addEventListener("click", () => {
                    this.delete(btn.id);
                });
            });
        }

        const hashtagInputList = document.querySelectorAll(".tagify--outside");

        if (hashtagInputList !== null) {
            hashtagInputList.forEach((el) => {
                this.createHashtagInput(el.id.substring(7))
            });
        }

        const hashtagBtn = document.getElementById("hashtag-modal-save");

        if (hashtagBtn !== null) {
            hashtagBtn.addEventListener("click", () => {
                this.saveHashtag();
            });
        }

        const hashtagInput = document.getElementById("hashtag");

        // 엔터키 입력하면 saveHashtag() 동작
        hashtagInput.addEventListener("keydown", (e) => {
            if (hashtagInput.value !== "") {
                if (e.isComposing === false && e.code === "Enter") { // 한글 입력 시 이벤트 두번 발생 방지
                    e.preventDefault();
                    this.saveHashtag();
                }
            }
        });

    },

    createHashtagInput: function (menuId) {
        var inputElm = document.getElementById("hashtag" + menuId);

        if (inputElm !== null) {

            this.getHashtags(menuId).then((resp) => {
                inputElm.value = resp;
            })

            var whitelist = this.allHashtags;

            // initialize Tagify on the above input node reference
            var tagify = new Tagify(inputElm, {

                // make an array from the initial input value
                whitelist: inputElm.value.trim().split(/\s*,\s*/)
            })

            // Chainable event listeners
            tagify.on("input", onInput);

            var mockAjax = (function mockAjax() {
                var timeout;
                return function (duration) {
                    clearTimeout(timeout);
                    return new Promise(function (resolve, reject) {
                        timeout = setTimeout(resolve, duration || 700, whitelist)
                    })
                }
            })();

            // on character(s) added/removed (user is typing/deleting)
            function onInput(e) {
                tagify.settings.whitelist.length = 0;                   // reset current whitelist
                tagify.loading(true).dropdown.hide.call(tagify); // show the loader animation

                // get new whitelist from a delayed mocked request (Promise)
                mockAjax()
                    .then(function (result) {
                        // replace tagify "whitelist" array values with new values
                        // and add back the ones already choses as Tags
                        tagify.settings.whitelist.push(...result, ...tagify.value)

                        // render the suggestions dropdown.
                        tagify.loading(false).dropdown.show.call(tagify, e.detail.value);
                    })
            }
        }
    },

    getHashtags: async function (menuId) {
        let defaultValue = "";

        await axios({
            method: "get",
            url: "/hashtags",
            params: {
                menuId: menuId
            }
        }).then((resp) => {
            for (const el of resp.data.data) {
                defaultValue += (String(el.name) + ", ");
            }

        }).catch((error) => {
            console.error(error);
        });

        return defaultValue.trim();
    },

    getAllHashtags: function () {
        // 초기화
        this.allHashtags = [];

        axios({
            method: "get",
            url: "/hashtags/all"
        }).then((resp) => {
            for (const el of resp.data.data) {
                this.allHashtags.push(el.name);
            }
        }).catch((error) => {
            console.error(error);
        })
    },

    saveHashtag: function () {
        const storeId = document.getElementById("storeId").value;
        const hashtagForm = document.getElementById("hashtag-form");
        const formData = new FormData(hashtagForm);

        axios({
            method: "post",
            url: "/seller/hashtag/" + storeId,
            data: formData
        }).then((resp) => {
            if (resp.data === -1) {
                alert("존재하는 카테고리입니다.");
                window.location.reload();
            } else {
                alert("카테고리 등록이 성공했습니다.");
                window.location.reload();
            }
        })
    },

    delete: function (btnId) {
        const hashtagId = btnId.substring(18);
        const storeId = document.getElementById("storeId").value;

        axios({
            method: "delete",
            url: "/seller/hashtag/" + hashtagId,
            params: {
                storeId: storeId
            }
        }).then((resp) => {
            alert("해쉬태그 삭제가 완료됐습니다.");
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },
}

main.init();