var main = {
    init: function () {
        var _this = this;

        _this.getCategories();

        const storeUpdateBtn = document.getElementById('store-update');
        const storeDeleteBtn = document.getElementById('store-delete');

        storeUpdateBtn.addEventListener('click', () => {
            _this.updateStore();
        })

        storeDeleteBtn.addEventListener('click', () => {
            _this.deleteStore();
        })
    },

    updateStore: function () {
        const storeForm = document.getElementById('store-edit-form');
        const storeId = document.getElementById('storeId').value;

        const formData = new FormData(storeForm);

        axios({
            method: "put",
            url: "/store/edit/" + storeId,
            data: formData
        }).then((resp) => {
            alert('매장 정보 수정이 완료됐습니다.');
            window.location.href = "/store/" + storeId;
        }).catch((error) => {
            console.log(error);
        })
    },

    deleteStore: function () {
        const storeId = document.getElementById('storeId').value;

        axios({
            method: "delete",
            url: "/store/delete/" + storeId,
        }).then((resp) => {
            alert('매장 삭제가 완료됐습니다.');
            window.location.href = "/";
        }).catch((error) => {
            console.log(error);
        })
    },

    mainChildren: [],

    midChildren: [],

    getCategories: function () {
        axios({
            method: "get",
            url: "/categoriesHier",
        }).then((resp) => {
            let rootChildren = resp.data.children;
            this.getMainCategories(rootChildren);

            this.changeMainCategories(this.mainChildren);
            this.changeMidCategories(this.midChildren);
        }).catch((e) => {
            console.error(e);
        })
    },

    getMainCategories: function (rootChildren) {

        // 대분류는 미리 저장함
        for (const rc of rootChildren) {
            let mainOption = document.createElement("option");
            mainOption.text = rc.name;
            mainOption.value = rc.categoryId;
            this.categoryLayerEl.main.appendChild(mainOption);

            this.mainChildren.push(rc.children)
        }

    },

    changeMainCategories: function (mainChildrenParam) {
        this.categoryLayerEl.main.addEventListener("change", () => {
            this.mainChildren = [];

            this.resetCategories(this.categoryLayerEl.mid, "중분류 선택");
            this.resetCategories(this.categoryLayerEl.sub, "소분류 선택");

            let mainVal = this.categoryLayerEl.main.options
                [this.categoryLayerEl.main.selectedIndex].value;

            for (const mid of mainChildrenParam) {
                for (let i = 0; i < mid.length; i++) {
                    if (mainVal === String(mid[i].parentId)) {
                        let option = document.createElement("option");
                        option.text = mid[i].name;
                        option.value = mid[i].categoryId;
                        main.categoryLayerEl.mid.appendChild(option)

                        this.midChildren.push(mid[i].children);
                    }
                }
            }
        });
    },

    changeMidCategories: function (midChildrenParam) {
        this.categoryLayerEl.mid.addEventListener("change", () => {
            this.midChildren = [];

            this.resetCategories(this.categoryLayerEl.sub, "소분류 선택");

            let midVal = this.categoryLayerEl.mid.options
                [this.categoryLayerEl.mid.selectedIndex].value;

            for (const sub of midChildrenParam) {
                for (let i = 0; i < sub.length; i++) {
                    if (midVal === String(sub[i].parentId)) {
                        let option = document.createElement("option");
                        option.text = sub[i].name;
                        option.value = sub[i].categoryId;
                        main.categoryLayerEl.sub.appendChild(option)
                    }
                }
            }
        });
    },

    resetCategories: function (categoryEl, text) {
        while (categoryEl.hasChildNodes()) {
            categoryEl.removeChild(categoryEl.firstChild);
        }

        let option = document.createElement("option");

        option.text = text;
        option.value = "";
        categoryEl.appendChild(option);
    },

    categoryLayerEl: {
        main: document.getElementById("main-cate"),
        mid: document.getElementById("mid-cate"),
        sub: document.getElementById("sub-cate")
    },
};

main.init();