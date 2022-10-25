import mapJs from "./map.js"

var app = {
    init: function () {

        let _this = this;

        // 해당 카테고리 기준 무조건 Top5가 나오게 search되는 SQL 작성 (5개 미만이면 알아서 처리)

        const categoryDivList = document.querySelectorAll(".category-div");

        if (categoryDivList !== null) {
            categoryDivList.forEach((el) => {

                let categoryId = el.children.namedItem("categoryId").value;

                el.addEventListener("click", () => {
                    window.location.href = "map";
                    mapJs.getTop5Categories(categoryId);
                });
            });
        }
    },

}

app.init();