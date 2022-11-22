import validation from "./validation.js";
import mask from "./mask.js";

var main = {
    init: async function () {
        let _this = this;

        const requestAddBtn = document.getElementById("request-add-store");
        const requestDeleteBtnList = document.querySelectorAll(".request-delete");

        if (requestAddBtn !== null) {
            requestAddBtn.addEventListener("click", () => {
                _this.save()
            });
        }

        if (requestDeleteBtnList !== null) {
            requestDeleteBtnList.forEach((btn) => {
                btn.addEventListener("click", () => {
                    _this.delete(btn.id);
                })
            })
        }
    },

    save: function () {
        mask.loadingWithMask();

        // input 태그
        const els = {
            name: document.getElementById("name"),
            zipcode: document.getElementById("zipcode"),
            roadAddr: document.getElementById("roadAddr"),
            numberAddr: document.getElementById("numberAddr")
        }
        let memberId = document.getElementById("memberId").value;

        // input 아래의 validation을 담을 div 태그
        const valids = {
            nameValid: document.getElementById("store-name-valid"),
            zipcodeValid: document.getElementById("store-zipcode-valid"),
            roadAddrValid: document.getElementById("store-roadAddr-valid"),
            numberAddrValid: document.getElementById("store-numberAddr-valid")
        }

        const requestAddStoreForm = document.getElementById("request-add-store-form");

        const formData = new FormData(requestAddStoreForm);

        for (const el in els) {
            els[el].classList.remove("valid-custom");
        }

        for (const v in valids) {
            validation.removeValidation(valids[v]);
        }

        if (els["name"].value !== "" && els["zipcode"].value !== ""
            && els["roadAddr"].value !== "" && els["numberAddr"].value !== "")
        {

            axios({
                method: "post",
                url: "/user/request-add-store",
                data: formData,
                params: {
                    memberId: memberId
                }
            }).then((resp) => {
                alert("가게 추가 요청이 완료됐습니다.");
                window.location.href = "/"
            }).catch((error) => {
                alert("가게 추가 요청이 실패했습니다.");
                console.error(error);
            });
        }

        for (const el in els) {
            if (els[el].value === "") {
                els[el].classList.add("valid-custom");
                validation.addValidation(valids[el + "Valid"], "위의 값들은 필수입니다.");
            }
        }

    },

    delete: function (btnId) {
        const requestId = btnId.substring(18);

        axios({
            method: "delete",
            url: "/admin/request-add-store/delete",
            params: {
                requestAddStoreId: requestId
            }
        }).then((resp) => {
            alert("가게추가 요청 삭제가 완료됐습니다.");
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },

};

main.init();