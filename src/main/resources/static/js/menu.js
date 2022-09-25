var main = {
    init: async function () {
        var _this = this;

        const menuSaveBtn = document.getElementById('menu-save');
        const menuUpdateBtnList = document.querySelectorAll('.menu-edit');
        const menuDeleteBtnList = document.querySelectorAll('.menu-delete');

        if (menuSaveBtn !== null) {
            menuSaveBtn.addEventListener('click', () => {
                _this.save()
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

    save: function () {
        const menuForm = document.getElementById('menuForm')

        axios({
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "post",
            url: "/menu",
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
        console.log("===menuUpdate===");
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
            url: "/menu/" + storeId,
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
            url: "/menu/" + storeId,
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