var main = {
    init: function () {
        var _this = this;
        var menuSaveBtn = document.getElementById('menu-save')
        menuSaveBtn.addEventListener('click', () => {
            _this.save()
        });
    },

    save: function () {
        var data = {
            name: document.getElementById('name').value,
            price: document.getElementById('price').value,
            storeId: document.getElementById('storeId').value
        };

        console.log(data)

        axios( {
            headers: {
                "Content-Type": "multipart/form-data",
                "Access-Control-Allow_Origin": "*"
            },
            method: "post",
            url: "/menu/add2",
            data: data
        }).then((resp) => {
            alert('메뉴가 등록됐습니다.')
            window.location.reload()
            console.log(resp)
        }).catch((error) => {
            console.log(error)
        })
    },

};

main.init();