import validation from "./validation.js";

var main = {
    init: function () {
        let _this = this;
        let categorySaveBtn = document.getElementById('category-add')
        let categoryDeleteBtn = document.getElementById('category-delete');

        categorySaveBtn.addEventListener('click', () => {
            _this.check();
            // _this.save();
        });

        categoryDeleteBtn.addEventListener('click', () => {
            _this.deleteCategory();
        })
    },

    check: function () {
        const parentId = document.getElementById("category-select").options
            [document.getElementById("category-select").selectedIndex].value;

        let name = document.getElementById('name').value;
        let selectValid = document.getElementById('category-select-valid');
        let nameValid = document.getElementById('category-name-valid');
        const selectedCategory = document.getElementById('category-select')

        selectedCategory.classList.remove('input-error-border');

        validation.removeValidation(selectValid);
        validation.removeValidation(nameValid);

        if (parentId !== '' && name !== '') {
            axios({
                method: "get",
                url: "/categoryCheck",
                params: {
                    parentId: parentId,
                    name: name
                }
            }).then((resp) => {
                let check = resp.data;

                if (check === false) {
                    this.save();
                } else {
                    alert("이미 등록된 카테고리입니다.");
                }
            }).catch((e) => {
                console.error(e);
            });
        }

        if (parentId === '') {
            selectedCategory.classList.add('input-error-border');

            validation.addValidation(selectValid, "상위 카테고리를 선택해주세요");
        }

        if (name === '') {
            document.getElementById("name").classList.add("valid-custom");

            validation.addValidation(nameValid, "카테고리명을 입력해주세요.");
        }

    },

    save: function () {
        const parentId = document.getElementById("category-select").options
            [document.getElementById("category-select").selectedIndex].value;

        const name = document.getElementById("name").value;

        let data= {
            parentId: parentId,
            name: name
        }

        axios({
            headers: {
                "Content-Type": "application/json"
            },
            method: "post",
            url: "/category",
            data: JSON.stringify(data),
        }).then((resp) => {
            alert('카테고리가 등록됐습니다.');
            window.location.reload();
        }).catch((error) => {
            console.log(error)
        })
    },

    deleteCategory: function () {
        const categoryId = document.getElementById("category-select").options
            [document.getElementById("category-select").selectedIndex].value;

        console.log(categoryId);

        let selectValid = document.getElementById('category-select-valid');

        if (categoryId === '') {
            document.getElementById('category-select').classList.add('input-error-border');
            validation.addValidation(selectValid, "삭제할 카테고리를 선택해주세요.");
        } else {
            validation.removeValidation(selectValid);

            axios({
                method: "delete",
                url: "/category/" + categoryId,
            }).then((resp) => {
                alert('카테고리 삭제가 완료됐습니다.');
                window.location.reload();
            }).catch((error) => {
                console.log(error);
            })
        }
    },
}

main.init();