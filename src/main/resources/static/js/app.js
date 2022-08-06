import axios from "axios";

function checkData() {
    axios({
        method: "get",
        url: "/axio",
    })
        .then(function (res) {
            console.log(res.data)
            console.log(res.status)
        });
}
