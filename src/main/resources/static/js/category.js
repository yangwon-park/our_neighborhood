var main = {
    init: function () {
        var _this = this;
        var categorySaveBtn = document.getElementById('category-add')

        categorySaveBtn.addEventListener('click', () => {
            _this.save()
        });
    },

    save: function () {
        const categoryForm = document.getElementById('category-form');
        const form = new FormData(categoryForm);

        var parentId = $('#category-select option:selected').val()

        form.append('parentId', parentId);

        axios( {
            method: "post",
            url: "/category/add",
            data: form
        }).then((resp) => {
            alert('카테고리가 등록됐습니다.')
            window.location.reload()
        }).catch((error) => {
            console.log(error)
        })
    },

};

main.init();