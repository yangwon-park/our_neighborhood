import validation from "./validation.js";

var main = {
    init: async function () {
        var _this = this;

        const menuSaveBtn = document.getElementById("menu-save");
        const menuUpdateBtnList = document.querySelectorAll(".menu-edit");
        const menuDeleteBtnList = document.querySelectorAll(".menu-delete");
        const menuEditImageCheckList = document.querySelectorAll(".menu-edit-image");

        if (menuSaveBtn !== null) {
            menuSaveBtn.addEventListener("click", () => {
                _this.check();
            });
        }

        if (menuUpdateBtnList !== null) {
            menuUpdateBtnList.forEach((btn) => {
                btn.addEventListener("click", () => {
                    _this.update(btn.id);
                });
            });
        }

        if (menuDeleteBtnList !== null) {
            menuDeleteBtnList.forEach((btn) => {
                btn.addEventListener("click", () => {
                    _this.delete(btn.id);
                });
            });
        }

        if (menuEditImageCheckList !== null) {
            menuEditImageCheckList.forEach((el) => {
                el.addEventListener("change", function () {
                    _this.imgActive(el.id);
                });
            });
        }
    },

    // 이미지 업로드 여부 설정 함수
    imgActive: function (elId) {
        const id = elId.substring(13);

        const activeCheckBox = document.getElementById("active-upload" + id);
        const fileInput = document.getElementById("menu-edit-image" + id);

        fileInput.disabled = !activeCheckBox.checked;
    },

    check: function () {
        const name = document.getElementById("name");
        const price = document.getElementById("price");
        const type = document.getElementsByName("type");
        const feature = document.getElementsByName("feature")

        const nameValid = document.getElementById("menu-name-valid");
        const priceValid = document.getElementById("menu-price-valid");
        const typeValid = document.getElementById("menu-type-valid");
        const featureValid = document.getElementById("menu-feature-valid");

        const storeId = document.getElementById("storeId").value;

        name.classList.remove("valid-custom");
        price.classList.remove("valid-custom");

        validation.removeValidation(nameValid);
        validation.removeValidation(priceValid);
        validation.removeValidation(typeValid);
        validation.removeValidation(featureValid);

        let typeCheck = false;
        let featureCheck = false;

        let numReg = /^[0-9]+$/;
        let numRegCheck = numReg.test(price.value);

        for (let i = 0; i < type.length; i++) {
            if (type[i].checked === true) {
                typeCheck = true;
            }
        }

        for (let i = 0; i < feature.length; i++) {
            if (feature[i].checked === true) {
                featureCheck = true;
            }
        }

        if (name.value !== "" && storeId !== "" && price.value !== ""
            && typeCheck === true && featureCheck === true && numRegCheck) {
            axios({
                method: "get",
                url: "/seller/menu/check",
                params: {
                    name: name.value,
                    storeId: storeId
                }
            }).then((resp) => {
                let check = resp.data;

                if (check === false) {
                    this.save();
                } else {
                    alert("이미 등록된 메뉴입니다.");
                    window.location.reload();
                }
            }).catch((e) => {
                console.error(e);
            });
        }

        if (name.value === "") {
            name.classList.add("valid-custom");
            validation.addValidation(nameValid, "메뉴 이름을 등록해주세요.");
        }

        if (price.value === "") {
            price.classList.add("valid-custom");
            validation.addValidation(priceValid, "가격을 등록해주세요.");
        } else {
            if (!numRegCheck) {
                price.classList.add("valid-custom");
                validation.addValidation(priceValid, "숫자만 입력해주세요");
            }
        }

        if (typeCheck === false) {
            validation.addValidation(typeValid, "메뉴의 종류를 선택해주세요.");
        }

        if (featureCheck === false) {
            validation.addValidation(featureValid, "메뉴의 특징을 선택해주세요.");
        }
    },

    createDefaultImg: function (formData) {
        let file = formData.get("file");

        if (file === null) {
            return;
        }

        if (file.name === "") {
            formData.delete("file");
        }

        let defaultFile = new File(["foo"], "default.png", {
            type: "image/png"
        })

        formData.append("file", defaultFile);
    },

    save: function () {
        const menuForm = document.getElementById("menu-add-form");

        const formData = new FormData(menuForm);

        this.createDefaultImg(formData);

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "post",
            url: "/seller/menu",
            data: formData
        }).then((resp) => {
            alert("메뉴가 등록됐습니다.")
            window.location.reload()
            console.log(resp)
        }).catch((error) => {
            console.log(error)
        });
    },

    update: function (btnId) {
        const id = btnId.substring(13);

        const menuForm = document.getElementById("menu-edit-form" + id);
        const storeIdVal = document.getElementById("storeId").value;

        let formData = new FormData(menuForm);
        this.createDefaultImg(formData);

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "put",
            url: "/seller/menu/" + storeIdVal,
            data: formData
        }).then((resp) => {
            alert('메뉴 정보 수정이 완료됐습니다.');
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },

    delete: function (btnId) {
        const id = btnId.substring(15);
        const storeId = document.getElementById('storeId').value;
        const menuId = document.getElementById('menuId' + id).value;

        console.log(menuId);

        axios({
            method: "delete",
            url: "/seller/menu/" + storeId,
            params: {
                menuId: menuId
            }
        }).then((resp) => {
            alert("메뉴 삭제가 완료됐습니다.");
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },

};

main.init();