import validation from "./validation.js";

var main = {
    init: async function () {
        var _this = this;

        const reviewSaveBtn = document.getElementById('review-save');
        const reviewMoreBtn = document.getElementById('review-more');

        if (reviewSaveBtn !== null) {
            reviewSaveBtn.addEventListener('click', () => {
                _this.check()
                // _this.save()
            });
        }

        if (reviewMoreBtn !== null) {
            reviewMoreBtn.addEventListener('click', () => {
                _this.more()
                // _this.save()
            });
        }
    },

    check: function () {
        const rating1 = document.getElementById("rating1");
        const rating2 = document.getElementById("rating2");
        const rating3 = document.getElementById("rating3");
        const rating4 = document.getElementById("rating4");
        const rating5 = document.getElementById("rating5");
        const content = document.getElementById("content");
        const storeId = document.getElementById("storeId").value;
        const memberId = document.getElementById("memberId").value;

        const ratingValid = document.getElementById('review-rating-valid');
        const contentValid = document.getElementById('review-content-valid');

        rating1.classList.remove("valid-custom");
        content.classList.remove("valid-custom");

        validation.removeValidation(ratingValid);
        validation.removeValidation(contentValid);

        let ratingCheck = true;

        if (rating1.value === '' || rating2.value === '' || rating3.value === '' || rating4.value === '' || rating5.value === '') {
            ratingCheck = false;
        }

        if (content.value !== '' && ratingCheck === true
        && memberId !== '' && storeId !== '') {
            this.save();
        }

        if (content.value === '') {
            content.classList.add("valid-custom");
            validation.addValidation(contentValid, "리뷰 내용을 작성해주세요.");
        }

        if (ratingCheck === false) {
            validation.addValidation(ratingValid, "평점을 선택해주세요.");
        }

    },

    createDefaultImg: function (formData) {
        let file = formData.get("file");

        if (file.name === "") {
            formData.delete("file");
        }

        let defaultFile = new File(["foo"], "default.png", {
            type: "image/png"
        })

        formData.append("file", defaultFile);
    },

    save: function () {
        const reviewForm = document.getElementById("review-add-form");

        let formData = new FormData(reviewForm);

        for (let k of formData.keys()) {
            console.log(k);
        }

        for (let v of formData.values()) {
            console.log(v);
        }

        this.createDefaultImg(formData);

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "post",
            url: "/user/review",
            data: formData
        }).then((resp) => {
            alert('리뷰가 등록됐습니다.')
            window.location.reload()
            console.log(resp)
        }).catch((error) => {
            console.log(error)
        });
    },

    more: function () {
        let page = $("#reviewBody tr").length / 5 + 1;  //마지막 리스트 번호를 알아내기 위해서 tr태그의 length를 구함.
        let addListHtml = "";
        console.log("page", page);

        const storeId = document.getElementById("storeId").value;

        axios({
            method: "get",
            url: "/review/more",
            params: {
                page: page,
                storeId: storeId
            }
        }).then((resp) => {
            console.log("result", resp);
            let data = resp.data
            console.log("data", data);

            if (data.last) {
                $("#review-more").remove();
            }
            for (let contentElement of data.content) {
                addListHtml += "<tr>";
                addListHtml += "<td>" + contentElement.reviewId + "</td>";
                switch (contentElement.rating) {
                    case 1:
                        addListHtml += "<td>★☆☆☆☆</td>";
                        break;
                    case 2:
                        addListHtml += "<td>★★☆☆☆</td>";
                        break;
                    case 3:
                        addListHtml += "<td>★★★☆☆</td>";
                        break;
                    case 4:
                        addListHtml += "<td>★★★★☆</td>";
                        break;
                    case 5:
                        addListHtml += "<td>★★★★★</td>";
                        break;
                }
                addListHtml += "<td>" + contentElement.content + "</td>";
                addListHtml += "<td>" + contentElement.username + "</td>";
                addListHtml += "<td>" + contentElement.createDate + "</td>";
                addListHtml += "</tr>";
            }
            $("#reviewBody").append(addListHtml);


        }).catch((e) => {
            console.error(e);
        });
    }
};

main.init();