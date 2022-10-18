// https://inpa.tistory.com/m/entry/Tagify-%F0%9F%93%9A-%ED%95%B4%EC%8B%9C-%ED%83%9C%EA%B7%B8tag-%EC%9E%85%EB%A0%A5%EC%9D%84-%EC%9D%B4%EC%81%98%EA%B2%8C-%EA%B0%84%ED%8E%B8%ED%9E%88-%EA%B5%AC%ED%98%84-%EC%82%AC%EC%9A%A9%EB%B2%95

var main = {
    hashtags: [],

    init: function () {
        this.getHashtags();

        var inputElm = document.querySelector('input[name=hashtag]')
        var whitelist = this.hashtags;

        // initialize Tagify on the above input node reference
        var tagify = new Tagify(inputElm, {

            // make an array from the initial input value
            whitelist: inputElm.value.trim().split(/\s*,\s*/)
        })

        // Chainable event listeners
        tagify.on('input', onInput);

        var mockAjax = (function mockAjax(){
            var timeout;
            return function(duration){
                clearTimeout(timeout);
                return new Promise(function(resolve, reject){
                    timeout = setTimeout(resolve, duration || 700, whitelist)
                })
            }
        })();

        // on character(s) added/removed (user is typing/deleting)
        function onInput(e){
            tagify.settings.whitelist.length = 0;                   // reset current whitelist
            tagify.loading(true).dropdown.hide.call(tagify); // show the loader animation

            // get new whitelist from a delayed mocked request (Promise)
            mockAjax()
                .then(function(result){
                    // replace tagify "whitelist" array values with new values
                    // and add back the ones already choses as Tags
                    tagify.settings.whitelist.push(...result, ...tagify.value)

                    // render the suggestions dropdown.
                    tagify.loading(false).dropdown.show.call(tagify, e.detail.value);
                })
        }
    },

    getHashtags: function () {
        this.hashtags = [];

        axios({
            method: "get",
            url: "/hashtags"
        }).then((resp) => {
            for (const el of resp.data.data) {
                this.hashtags.push(el.name);
            }
        }).catch((error) => {
            console.error(error);
        })
    },
}

main.init();