import validation from "./validation.js";
import mask from "./mask.js";

var main = {
    init: function () {
        let _this = this;

        // mask.loadingWithMask();
        const categoryMain = document.getElementById("category-main").value;
        const categoryMid = document.getElementById("category-mid").value;
        const categorySub = document.getElementById("category-sub").value;
        let categoryList = [];
        categoryList.push(categoryMain);
        categoryList.push(categoryMid);
        categoryList.push(categorySub);
        console.log("categoryList = ", categoryList)
        _this.getCategories(categoryList);

        const storeSaveBtn = document.getElementById("store-save");
        const storeUpdateBtn = document.getElementById("store-update");
        const storeDeleteBtn = document.getElementById("store-delete");
        const storeOwnerAddBtn = document.getElementById("store-owner-add");
        const storeOwnerDeleteBtn = document.querySelectorAll(".store-owner-delete");

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

        if (storeOwnerAddBtn !== null) {
            storeOwnerAddBtn.addEventListener("click", () => {
                _this.addStoreOwner();
            });
        }

        if (storeOwnerDeleteBtn !== null) {
            storeOwnerDeleteBtn.forEach((btn) => {
                btn.addEventListener("click", () => {
                    _this.deleteStoreOwner(btn.id);
                })
            })
        }

        // mask.closeMask();
    },

    save: function () {
        mask.loadingWithMask();

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
        const storeForm = document.getElementById("store-add-form");

        const formData = new FormData(storeForm);

        for (const el in els) {
            els[el].classList.remove("valid-custom");
        }

        for (const v in valids) {
            validation.removeValidation(valids[v]);
        }

        validation.removeValidation(cateValid);

        this.categoryLayerEl.main.classList.remove("input-error-border");

        if (els["name"].value !== "" && els["zipcode"].value !== ""
            && els["roadAddr"].value !== "" && els["numberAddr"].value !== ""
            && els["openingTime"].value !== "" && els["closingTime"].value !== ""
            && mainCateVal !== "") {

            axios({
                method: "post",
                url: "/seller/store",
                data: formData
            }).then((resp) => {
                alert("매장 등록이 완료됐습니다.");
                window.location.href = "/store/" + resp.data;
                mask.closeMask();
            }).catch((error) => {
                alert("매장 등록에 실패했습니다.");
                console.error(error);
                mask.closeMask();
            });
        }

        for (const el in els) {
            if (els[el].value === "") {
                els[el].classList.add("valid-custom");
                validation.addValidation(valids[el + "Valid"], "위의 값들은 필수입니다.");
            }
        }

        if (mainCateVal === "") {
            this.categoryLayerEl.main.classList.add("input-error-border");
            validation.addValidation(cateValid, "대분류는 필수입니다.");
        }
    },

    update: function () {
        mask.loadingWithMask();

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
        const storeForm = document.getElementById("store-edit-form");

        const formData = new FormData(storeForm);
        const storeIdVal = document.getElementById("storeId").value;

        for (const el in els) {
            els[el].classList.remove("valid-custom");
        }

        for (const v in valids) {
            validation.removeValidation(valids[v]);
        }

        validation.removeValidation(cateValid);

        this.categoryLayerEl.main.classList.remove("input-error-border");

        if (els["name"].value !== "" && els["zipcode"].value !== ""
            && els["roadAddr"].value !== "" && els["numberAddr"].value !== ""
            && els["openingTime"].value !== "" && els["closingTime"].value !== ""
            && mainCateVal !== "") {

            axios({
                method: "put",
                url: "/seller/store/" + storeIdVal,
                data: formData
            }).then((resp) => {
                alert("매장 정보 수정이 완료됐습니다.");
                window.location.href = "/store/" + storeIdVal;
                mask.closeMask();
            }).catch((error) => {
                alert("매장 수정에 실패했습니다.");
                console.error(error);
                mask.closeMask();
            })
        }

        for (const el in els) {
            if (els[el].value === "") {
                els[el].classList.add("valid-custom");
                validation.addValidation(valids[el + "Valid"], "위의 값들은 필수입니다.");
            }
        }

        if (mainCateVal === "") {
            this.categoryLayerEl.main.classList.add("input-error-border");
            validation.addValidation(cateValid, "대분류는 필수입니다.");
        }
    },

    delete: function () {
        mask.loadingWithMask();
        const storeId = document.getElementById("storeId");

        axios({
            method: "delete",
            url: "/admin/store/" + storeId.value
        }).then((resp) => {
            alert("매장 삭제가 완료됐습니다.");
            window.location.href = "/";
            mask.closeMask();
        }).catch((error) => {
            console.error(error);
        })
    },

    mainChildren: [],

    midChildren: [],

    storeEditCheck: false,

    getCategories: function (categoryList) {
        axios({
            method: "get",
            url: "/categories-hier",
        }).then((resp) => {
            let rootChildren = resp.data.children;
            this.getMainCategories(rootChildren, categoryList);
            console.log("mainChildren = ", this.mainChildren)
            if (this.storeEditCheck) {
                if (categoryList[1] !== '') {
                    this.getMidCategories(this.mainChildren, categoryList);
                }
                if (categoryList[2] !== '') {
                    this.getSubCategories(this.midChildren, categoryList);
                }
            }
            this.changeMainCategories(this.mainChildren);
            this.changeMidCategories(this.midChildren);

        }).catch((e) => {
            console.error(e);
        })
    },

    getMainCategories: function (rootChildren, categoryList) {
        // 대분류는 미리 저장함
        for (const rc of rootChildren) {
            let mainOption = document.createElement("option");
            mainOption.text = rc.name;
            mainOption.value = rc.categoryId;
            if (mainOption.value === categoryList[0]) {
                mainOption.selected = true;
                this.storeEditCheck = true;
                console.log("true")
            }
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

                        main.categoryLayerEl.mid.appendChild(option);

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

    getMidCategories: function (mainChildrenParam, categoryList) {
        axios({
            method: "get",
            url: "/categories-hier-edit",
            params: {
                categoryId: categoryList[1]
            }
        }).then((resp) => {
            let midParentId = resp.data

            this.mainChildren = [];

            this.resetCategories(this.categoryLayerEl.mid, "중분류 선택");
            this.resetCategories(this.categoryLayerEl.sub, "소분류 선택");

            for (const mid of mainChildrenParam) {
                for (let i = 0; i < mid.length; i++) {
                    if (midParentId === mid[i].parentId) {
                        let option = document.createElement("option");
                        option.text = mid[i].name;
                        option.value = mid[i].categoryId;
                        if (option.value === categoryList[1]) {
                            option.selected = true;
                            console.log("true2")
                        }

                        main.categoryLayerEl.mid.appendChild(option);
                        this.midChildren.push(mid[i].children);
                    }
                }
            }
        }).catch((e) => {
            console.error(e);
        })
    },

    getSubCategories: function (midChildrenParam, categoryList) {
        axios({
            method: "get",
            url: "/categories-hier-edit",
            params: {
                categoryId: categoryList[2]
            }
        }).then((resp) => {
            let subParentId = resp.data

            this.midChildren = [];

            this.resetCategories(this.categoryLayerEl.sub, "소분류 선택");

            for (const sub of midChildrenParam) {
                for (let i = 0; i < sub.length; i++) {
                    if (subParentId === sub[i].parentId) {
                        let option = document.createElement("option");
                        option.text = sub[i].name;
                        option.value = sub[i].categoryId;
                        if (option.value === categoryList[2]) {
                            option.selected = true;
                            console.log("true3")
                        }

                        main.categoryLayerEl.sub.appendChild(option)
                    }
                }
            }
        }).catch((e) => {
            console.error(e);
        })
    },

    addStoreOwner: function () {

        const userId = document.getElementById("userId");
        const storeId = document.getElementById("storeId");

        console.log("userId", userId.value);

        const userIdValid = document.getElementById("store-owner-add-userId-valid");

        userId.classList.remove("valid-custom");

        validation.removeValidation(userIdValid);

        if (userId.value !== "") {
            axios({
                method: "post",
                url: "/admin/store-owner/add",
                params: {
                    userId: userId.value,
                    storeId: storeId.value
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("가게 관리자가 추가 되었습니다");
                    window.location.reload();
                } else {
                    alert(check);
                }
            }).catch((error) => {
                console.log(error);
            })
        }

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "닉네임을 입력해주세요.");
        }
    },

    deleteStoreOwner: function (btnId) {
        const memberId = btnId.substring(22);
        const storeId = document.getElementById("storeId");

        console.log("storeId = ", memberId)
        console.log("userId = ", storeId.value)

        axios({
            method: "delete",
            url: "/admin/store-owner/delete",
            params: {
                memberId: memberId,
                storeId: storeId.value
            }
        }).then((resp) => {
            let check = resp.data;
            if (check === "성공") {
                alert("가게 관리자 삭제가 완료됐습니다.");
                window.location.reload();
            } else {
                alert(check);
            }
        }).catch((error) => {
            console.log(error);
        })
    },
};

main.init();