var main = {
    init: function () {
        let _this = this;
        let categorySaveBtn = document.getElementById('category-add')
        let categoryDeleteBtn = document.getElementById('category-delete');

        categorySaveBtn.addEventListener('click', () => {
            _this.checkDuplicate();
            // _this.save();
        });

        categoryDeleteBtn.addEventListener('click', () => {
            _this.deleteCategory();
        })
    },

    checkDuplicate: function () {
        let parentId = $('#category-select option:selected').val();
        let name = document.getElementById('name').value;
        let selectValid = document.getElementById('category-select-valid');
        let nameValid = document.getElementById('category-name-valid');

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
                    alert('중복된 카테고리입니다.');
                }
            }).catch((e) => {
                console.error(e);
            });
        }

        if (parentId === '') {
            document.getElementById('category-select').classList.add('input-error-border');
            selectValid.innerText = '상위 카테고리를 선택해주세요.'
            selectValid.classList.add('input-error');
            selectValid.style.display = 'block';
        }

        if (name === '') {
            document.getElementById('name').classList.add('input-error');
            nameValid.innerText = '카테고리명을 입력해주세요.'
            nameValid.classList.add('input-error');
            nameValid.style.display = 'block';
        }

    },

    save: function () {
        const categoryForm = document.getElementById('category-form');
        const formData = new FormData(categoryForm);

        let parentId = $('#category-select option:selected').val();

        formData.append('parentId', parentId);

        axios({
            method: "post",
            url: "/category/add",
            data: formData
        }).then((resp) => {
            alert('카테고리가 등록됐습니다.');
            window.location.reload();
        }).catch((error) => {
            console.log(error)
        })
    },

    deleteCategory: function () {
        let categoryId = $('#category-select option:selected').val();

        console.log(categoryId);

        axios({
            method: "delete",
            url: "/category/" + categoryId,
        }).then((resp) => {
            alert('카테고리 삭제가 완료됐습니다.');
            window.location.reload();
        }).catch((error) => {
            console.log(error);
        })
    },
}

main.init();