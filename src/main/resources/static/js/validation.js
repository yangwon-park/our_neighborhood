var main = {
    removeValidation: function (el) {
        el.classList.remove("valid-custom");
        el.classList.add("hidden");
    },

    addValidation: function (el, text) {
        el.innerText = text;
        el.classList.add("valid-custom", "fw-bold");
        el.classList.remove("hidden");
    },
}

export default main;