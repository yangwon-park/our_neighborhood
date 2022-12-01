var main = {
    setCookie: function (key, value, exp) {
        let date = new Date();
        date.setTime(date.getTime() + (exp * 1000 * 60 * 30)); // 1000 * 60 * 60 = 1시간, exp 1 => 1ms
        document.cookie = key + "=" + value + "; path=/; expires=" + date.toUTCString() + ";";
    },

    getCookie: function (name) {
        let cookieValue = null;

        if (document.cookie) {
            let array = document.cookie.split((encodeURI(name) + "="));

            if (array.length >= 2) {
                let arraySub = array[1].split(";");
                cookieValue = encodeURI(arraySub[0]);
            }
        }

        return cookieValue;
    },
}

export default main