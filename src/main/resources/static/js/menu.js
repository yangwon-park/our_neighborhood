import validation from "./validation.js";

var main = {
    init: async function () {
        var _this = this;

        const menuSaveBtn = document.getElementById('menu-save');
        const menuUpdateBtnList = document.querySelectorAll('.menu-edit');
        const menuDeleteBtnList = document.querySelectorAll('.menu-delete');

        if (menuSaveBtn !== null) {
            menuSaveBtn.addEventListener('click', () => {
                _this.check()
                // _this.save()
            });
        }

        if (menuUpdateBtnList !== null) {
            menuUpdateBtnList.forEach((btn) => {
                btn.addEventListener('click', () => {
                    _this.update(btn.id);
                })
            });
        }

        if (menuDeleteBtnList !== null) {
            menuDeleteBtnList.forEach((btn) => {
                btn.addEventListener('click', () => {
                    _this.delete(btn.id);
                })
            })
        }
    },

    check: function () {
        const name = document.getElementById("name");
        const price = document.getElementById("price");
        const file = document.getElementById("file");
        const type = document.getElementsByName("type");

        const storeId = document.getElementById("storeId").value;

        const nameValid = document.getElementById('menu-name-valid');
        const priceValid = document.getElementById('menu-price-valid');
        const fileValid = document.getElementById('menu-file-valid');
        const typeValid = document.getElementById('menu-type-valid');

        name.classList.remove("valid-custom");
        price.classList.remove("valid-custom");
        file.classList.remove("valid-custom");

        validation.removeValidation(nameValid);
        validation.removeValidation(priceValid);
        validation.removeValidation(fileValid);
        validation.removeValidation(typeValid);

        let typeCheck = false;

        for (let i = 0; i < type.length; i++) {
            if (type[i].checked === true) {
                typeCheck = true
            }
        }

        if (name.value !== '' && storeId !== ''
                && price.value !== '' && typeCheck === true) {
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

        if (name.value === '') {
            name.classList.add("valid-custom");
            validation.addValidation(nameValid, "메뉴 이름을 등록해주세요.");
        }

        if (price.value === '') {
            price.classList.add("valid-custom");
            validation.addValidation(priceValid, "가격을 등록해주세요.");
        }

        if (file.value === '') {
            file.classList.add("valid-custom");
            validation.addValidation(fileValid, "메뉴 이미지를 등록해주세요.");
        }

        if (typeCheck === false) {
            validation.addValidation(typeValid, "메뉴의 종류를 선택해주세요.");
        }

    },

    save: function () {
        const menuForm = document.getElementById('menu-add-form')

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "post",
            url: "/seller/menu",
            data: new FormData(menuForm)
        }).then((resp) => {
            alert('메뉴가 등록됐습니다.')
            window.location.reload()
            console.log(resp)
        }).catch((error) => {
            console.log(error)
        })
    },

    update: function (btnId) {
        const id = btnId.substring(13);

        const menuForm = document.getElementById('menu-edit-form' + id);
        const storeId = document.getElementById('storeId').value;

        const formData = new FormData(menuForm);

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "put",
            url: "/seller/menu/" + storeId,
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
            alert('메뉴 삭제가 완료됐습니다.');
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },

};

main.init();