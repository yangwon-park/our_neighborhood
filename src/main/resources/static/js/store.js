import validation from "./validation.js";

var main = {
    init: function () {
        var _this = this;

        _this.getCategories();

        const storeSaveBtn = document.getElementById("store-save");
        const storeUpdateBtn = document.getElementById("store-update");
        const storeDeleteBtn = document.getElementById("store-delete");

        if (storeSaveBtn !== null) {
            storeSaveBtn.addEventListener("click", () => {
                _this.save();
            });
        }

        if (storeUpdateBtn !== null) {
            storeUpdateBtn.addEventListener("click", () => {
                _this.update();
            });
        }

        if (storeDeleteBtn !== null) {
            storeDeleteBtn.addEventListener("click", () => {
                _this.delete();
            });
        }
    },

    save: function () {

        // input 태그
        const els = {
            name: document.getElementById("name"),
            zipcode: document.getElementById("zipcode"),
            roadAddr: document.getElementById("roadAddr"),
            numberAddr: document.getElementById("numberAddr"),
            openingTime: document.getElementById("openingTime"),
            closingTime: document.getElementById("closingTime")
        }

        // input 아래의 validation을 담을 div 태그
        const valids = {
            nameValid: document.getElementById("store-name-valid"),
            zipcodeValid: document.getElementById("store-zipcode-valid"),
            roadAddrValid: document.getElementById("store-roadAddr-valid"),
            numberAddrValid: document.getElementById("store-numberAddr-valid"),
            openingTimeValid: document.getElementById("store-openingTime-valid"),
            closingTimeValid: document.getElementById("store-closingTime-valid")
        }

        const cateValid = document.getElementById("store-category-valid");
        const mainCateVal = document.getElementById("main-cate").options
            [document.getElementById("main-cate").selectedIndex].value;
        const storeForm = document.getElementById('store-add-form');

        const formData = new FormData(storeForm);

        for (const el in els) {
            els[el].classList.remove("valid-custom");
        }

        for (const v in valids) {
            validation.removeValidation(valids[v]);
        }

        validation.removeValidation(cateValid);

        this.categoryLayerEl.main.classList.remove("input-error-border");

        if (els["name"].value !== '' && els["zipcode"].value !== ''
            && els["roadAddr"].value !== '' && els["numberAddr"].value !== ''
            && els["openingTime"].value !== '' && els["closingTime"].value !== ''
            && mainCateVal !== '') {

            axios({
                method: "post",
                url: "/seller/store",
                data: formData
            }).then((resp) => {
                alert("매장 등록이 완료됐습니다.");
                window.location.href = "/store/" + resp.data;
            }).catch((error) => {
                console.error(error);
            });
        }

        for (const el in els) {
            if (els[el].value === '') {
                els[el].classList.add("valid-custom");
                validation.addValidation(valids[el + "Valid"], "위의 값들은 필수입니다.");
            }
        }

        if (mainCateVal === '') {
            this.categoryLayerEl.main.classList.add("input-error-border");
            validation.addValidation(cateValid, "대분류는 필수입니다.");
        }
    },

    update: function () {
        const storeForm = document.getElementById("store-edit-form");
        const storeIdVal = document.getElementById("storeId").value;

        const formData = new FormData(storeForm);

        axios({
            method: "put",
            url: "/seller/store/" + storeIdVal,
            data: formData
        }).then((resp) => {
            alert('매장 정보 수정이 완료됐습니다.');
            window.location.href = "/store/" + storeIdVal;
        }).catch((error) => {
            console.error(error);
        })
    },

    delete: function () {
        const storeIdVal = document.getElementById("storeId").value;

        axios({
            method: "delete",
            url: "/admin/store/" + storeIdVal
        }).then((resp) => {
            alert("매장 삭제가 완료됐습니다.");
            window.location.href = "/";
        }).catch((error) => {
            console.error(error);
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