import validation from "./validation.js";
import mask from "./mask.js";

var main = {
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
        console.log("동작중?");
        const recommendPostFormEls = {
            header: document.getElementById("header"),
            content: document.getElementById("content"),
            hashtag: document.getElementById("hashtag")
        }

        const kind = document.getElementsByName("recommendKind");

        const recommendPostFormValids = {
            headerValid: document.getElementById("post-header-valid"),
            contentValid: document.getElementById("post-content-valid"),
            hashtagValid: document.getElementById("post-hashtag-valid")
        }
        const kindValid = document.getElementById("post-kind-valid");

        for (const el in recommendPostFormEls) {
            recommendPostFormEls[el].classList.remove("valid-custom");
        }

        for (const el in recommendPostFormValids) {
            validation.removeValidation(recommendPostFormValids[el]);
        }

        let kindCheck = false;

        for (let i = 0; i < kind.length; i++) {
            if (kind[i].checked === true) {
                kindCheck = true;
            }
        }

        console.log(recommendPostFormEls["header"].value);
        console.log(recommendPostFormEls["content"].value);
        console.log(recommendPostFormEls["hashtag"].value);
        console.log(kindCheck);

        if (recommendPostFormEls["header"].value !== ""
            && recommendPostFormEls["content"].value !== ""
            && recommendPostFormEls["hashtag"].value !== ""
            && kindCheck) {

            const form = document.getElementById("recommend-post-add-form");
            const formData = new FormData(form);

            mask.loadingWithMask();
            console.log("동작중?22");

            axios({
                method: "post",
                url: "/admin/recommend-post",
                data: formData
            }).then((resp) => {
                alert("양식이 등록됐습니다.");
                window.location.reload();
                mask.closeMask();
            }).catch((error) => {
                console.log(error)
                mask.closeMask();
            });
        }

        for (const el in recommendPostFormEls) {
            if (recommendPostFormEls[el].value === "") {
                recommendPostFormEls[el].classList.add("valid-custom");
                validation.addValidation(recommendPostFormValids[el + "Valid"], "필수값입니다.");
            }
        }

        validation.addValidation(kindValid, "종류를 선택해주세요.");
    },

    getRecommendPost: function () {
        let hashtagIdList = sessionStorage.getItem("hashtagIdList");

        axios({
            method: "get",
            url: "/recommend-post",
            params: {
                hashtagIdList: hashtagIdList
            }
        }).then((resp) => {
            let i = 1;

            const ul = document.getElementById("store-list");

            for (const content of resp.data.content) {
                let li = document.createElement("li");
                li.classList.add("store-list-item", "list-group-item", "border", "border-0");

                let itemMainWrap = document.createElement("div");
                itemMainWrap.classList.add("item-main-wrap");

                let figure = document.createElement("figure");
                figure.classList.add("store-item", "d-flex", "mb-0");

                let thumbnail = document.createElement("div");
                thumbnail.classList.add("thumbnail", "me-2");

                let img = document.createElement("img");
                img.id = "thumbnail-img";
                img.src = content.uploadImgUrl;

                let figcaption = document.createElement("figcaption");
                figcaption.classList.add("w-100");

                let figWrap = document.createElement("div");
                figWrap.classList.add("w-100", "h-100");

                let storeItemHeader = document.createElement("div");
                storeItemHeader.classList.add("store-item-header");

                let headerWrap = document.createElement("div");
                headerWrap.classList.add("header-wrap");

                let titleImgWrap = document.createElement("div");
                titleImgWrap.classList.add("d-flex", "justify-content-between");

                let storeAddress = document.createElement("p");
                storeAddress.id = "store-address";
                storeAddress.classList.add("text-secondary", "fs-5");
                storeAddress.innerText = content.address.roadAddr;

                let titleWrap = document.createElement("div");
                titleWrap.classList.add("fs-2");

                let storeItemTitle = document.createElement("span");
                storeItemTitle.id = "store-item-title";

                let storeLink = document.createElement("a");
                storeLink.href = "/store/" + content.storeId;
                storeLink.innerText = i + ". " + content.name;

                let storeScore = document.createElement("span");
                storeScore.id = "store-score";
                storeScore.classList.add("main-color", "fw-bolder", "ms-2");
                storeScore.innerText = content.average;

                let rightArea = document.createElement("div");
                rightArea.classList.add("right-area", "my-auto");

                let heartLink = document.createElement("a");
                heartLink.href = "javascript:;";
                heartLink.classList.add("icon", "hear");

                let heartImg = document.createElement("img");
                heartImg.src = "https://cdn-icons-png.flaticon.com/512/803/803087.png";
                heartImg.style.height = "3vh";
                heartImg.style.width = "3vh";
                heartImg.alt = "찜하기";

                let storeWay = document.createElement("div");
                storeWay.classList.add("store-way");

                let wayWrap = document.createElement("div");
                wayWrap.classList.add("d-flex", "float-end", "my-auto", "pb-2");

                let storeDist = document.createElement("div");
                storeDist.id = "store-dist";
                storeDist.classList.add("fs-4", "my-auto", "me-3", "h-100", "fw-bolder");
                storeDist.innerText = content.distance + "km";

                let findWayBtn = document.createElement("button");
                findWayBtn.classList.add("btn", "btn-main-color");
                findWayBtn.id = "find-way-btn";
                findWayBtn.innerText = "길찾기";

                storeItemTitle.appendChild(storeLink);

                titleWrap.appendChild(storeItemTitle);
                titleWrap.appendChild(storeScore);

                heartLink.appendChild(heartImg);
                rightArea.appendChild(heartLink)

                titleImgWrap.appendChild(titleWrap);
                titleImgWrap.appendChild(rightArea);

                headerWrap.appendChild(titleImgWrap);
                headerWrap.appendChild(storeAddress);

                wayWrap.appendChild(storeDist);
                wayWrap.appendChild(findWayBtn);

                storeItemHeader.appendChild(headerWrap);
                storeWay.appendChild(wayWrap);

                figWrap.appendChild(storeItemHeader);
                figWrap.appendChild(storeWay);

                figcaption.appendChild(figWrap);

                thumbnail.appendChild(img);

                figure.appendChild(thumbnail);
                figure.appendChild(figcaption);

                itemMainWrap.appendChild(figure);

                li.appendChild(itemMainWrap);

                ul.appendChild(li)

                i++;
            }
        }).catch((error) => {
            console.log(error);
        });
    },
}

main.init();