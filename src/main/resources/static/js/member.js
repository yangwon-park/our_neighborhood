import validation from "./validation.js";

var main = {
    init: async function () {
        var _this = this;

        const signUpSaveBtn = document.getElementById("sign-up-save");
        const sendSMSBtn = document.getElementById("send-SMS");
        const phoneNumberEditBtn = document.getElementById("phoneNumber-edit");
        const memberEditBtn = document.getElementById("member-edit");
        const passwordEditBtn = document.getElementById("password-edit");
        const memberDeleteBtn = document.getElementById("member-delete");
        const findUserIdBtn = document.getElementById("find-userId");
        const findPasswordBtn = document.getElementById("find-password");

        if (signUpSaveBtn !== null) {
            signUpSaveBtn.addEventListener("click", () => {
                _this.check();
            });
        }

        if (sendSMSBtn !== null) {
            sendSMSBtn.addEventListener("click", () => {
                _this.sendSMS();
            });
        }

        if (phoneNumberEditBtn !== null) {
            phoneNumberEditBtn.addEventListener("click", () => {
                _this.editPhoneNumber();
            });
        }

        if (memberEditBtn !== null) {
            memberEditBtn.addEventListener("click", () => {
                _this.update();
            });
        }

        if (passwordEditBtn !== null) {
            passwordEditBtn.addEventListener("click", () => {
                _this.editPassword();
            });
        }

        if (memberDeleteBtn !== null) {
            memberDeleteBtn.addEventListener("click", () => {
                _this.delete();
            });
        }

        if (findUserIdBtn !== null) {
            findUserIdBtn.addEventListener("click", () => {
                _this.findUserId();
            });
        }

        if (findPasswordBtn !== null) {
            findPasswordBtn.addEventListener("click", () => {
                _this.findPassword();
            });
        }
    },

    check: function () {
        const userId = document.getElementById("userId");
        const password = document.getElementById("password");
        const passwordCheck = document.getElementById("passwordCheck");
        const email = document.getElementById("email");
        const username = document.getElementById("username");
        const nickname = document.getElementById("nickname");
        const birthDate = document.getElementById("birthDate");
        const phoneNumber = document.getElementById("phoneNumber");
        const certifiedNumber = document.getElementById("certifiedNumber");

        const userIdValid = document.getElementById('sign-up-userId-valid');
        const passwordValid = document.getElementById('sign-up-password-valid');
        const passwordCheckValid = document.getElementById('sign-up-passwordCheck-valid');
        const emailValid = document.getElementById('sign-up-email-valid');
        const usernameValid = document.getElementById('sign-up-username-valid');
        const nicknameValid = document.getElementById('sign-up-nickname-valid');
        const birthDateValid = document.getElementById('sign-up-birthDate-valid');
        const phoneNumberValid = document.getElementById('sign-up-phoneNumber-valid');
        const certifiedNumberValid = document.getElementById('sign-up-certifiedNumber-valid');

        userId.classList.remove("valid-custom");
        password.classList.remove("valid-custom");
        passwordCheck.classList.remove("valid-custom");
        email.classList.remove("valid-custom");
        username.classList.remove("valid-custom");
        nickname.classList.remove("valid-custom");
        birthDate.classList.remove("valid-custom");
        phoneNumber.classList.remove("valid-custom");
        certifiedNumber.classList.remove("valid-custom");

        validation.removeValidation(userIdValid);
        validation.removeValidation(passwordValid);
        validation.removeValidation(passwordCheckValid);
        validation.removeValidation(emailValid);
        validation.removeValidation(usernameValid);
        validation.removeValidation(nicknameValid);
        validation.removeValidation(birthDateValid);
        validation.removeValidation(phoneNumberValid);
        validation.removeValidation(certifiedNumberValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);
        let birthDateRegExp = /[0-9]{7}$/;
        let birthDateValidation = birthDateRegExp.test(birthDate.value);
        let passwordRegExp = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/;
        let passwordValidation = passwordRegExp.test(password.value);
        let phoneNumberRegExp = /^01(0|1|[6-9]?)([0-9]{3,4})([0-9]{4})$/;
        let phoneNumberValidation = phoneNumberRegExp.test(phoneNumber.value);
        let certifiedNumberRegExp = /[0-9]{5}$/;
        let certifiedNumberValidation = certifiedNumberRegExp.test(certifiedNumber.value);
        let passwordCheckValidation = true;

        if (password.value !== passwordCheck.value) {
            passwordCheckValidation = false;
        }

        const signUpForm = document.getElementById("sign-up-add-form");
        let formData = new FormData(signUpForm);

        if (userId.value !== "" && username.value !== ""
            && nickname.value !== "" && passwordCheckValidation === true
        && emailValidation && birthDateValidation && passwordCheckValidation
        && passwordValidation && phoneNumberValidation && certifiedNumberValidation) {

            axios({
                method: "get",
                url: "/member/check",
                params: {
                    nickname: nickname.value,
                    email: email.value,
                    phoneNumber: phoneNumber.value,
                    certifiedNumber: certifiedNumber.value,
                    userId: userId.value
                }
            }).then((resp) => {
                let check = resp.data;

                if (check === "성공") {
                    this.save();
                } else {
                    alert(check);
                    axios({
                        method: "get",
                        url: "/sign-up",
                        data: formData
                    }).catch((e) => {
                        console.error(e);
                    });
                }
            }).catch((e) => {
                console.error(e);
            });
        }

        if (!emailValidation) {
            email.classList.add("valid-custom");
            validation.addValidation(emailValid, "올바른 이메일 형식이 아닙니다.");
        }

        if (!birthDateValidation) {
            birthDate.classList.add("valid-custom");
            validation.addValidation(birthDateValid, "생년월일 8자리의 숫자여야 합니다.");
        }

        if (!passwordValidation) {
            password.classList.add("valid-custom");
            validation.addValidation(passwordValid, "특수문자,문자,숫자 포함 형태의 8~15자리 이내의 암호여야 합니다.");
        }

        if (!phoneNumberValidation) {
            phoneNumber.classList.add("valid-custom");
            validation.addValidation(phoneNumberValid, "올바른 전화번호 형식이 아닙니다.");
        }

        if (!certifiedNumberValidation) {
            certifiedNumber.classList.add("valid-custom");
            validation.addValidation(certifiedNumberValid, "6자리의 숫자여야 합니다.");
        }

        if (!passwordCheckValidation) {
            passwordCheck.classList.add("valid-custom");
            validation.addValidation(passwordCheckValid, "비밀번호가 일치하지 않습니다.");
        }

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "아이디를 입력해주세요.");
        }

        if (username.value === "") {
            username.classList.add("valid-custom");
            validation.addValidation(usernameValid, "이름을 입력해주세요.");
        }

        if (nickname.value === "") {
            nickname.classList.add("valid-custom");
            validation.addValidation(nicknameValid, "닉네임을 입력해주세요.");
        }

    },

    sendSMS: function () {
        const phoneNumber = document.getElementById("phoneNumber");

        if (phoneNumber.value === "") {
            alert("전화번호를 입력해주세요.")
            window.location.reload()
        }else {
            axios({
                method: "get",
                url: "/member/send-sms",
                params: {
                    phoneNumber: phoneNumber.value
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === false) {
                    alert("이미 있는 번호입니다.");
                } else {
                    alert("인증번호가 발송됐습니다.");
                }
            }).catch((e) => {
                console.error(e);
            });
        }
    },

    save: function () {
        const signUpForm = document.getElementById("sign-up-add-form");

        let formData = new FormData(signUpForm);

        axios({
            method: "post",
            url: "/member/add",
            data: formData
        }).then((resp) => {
            alert("회원가입이 완료됐습니다.")
            window.location.href = "/login";
            console.log(resp)
        }).catch((error) => {
            console.log(error)
        });
    },

    editPhoneNumber: function () {
        const phoneNumber = document.getElementById("phoneNumber");
        const certifiedNumber = document.getElementById("certifiedNumber");

        if (phoneNumber.value === "") {
            alert("전화번호를 입력해주세요.")
            window.location.reload()
        }else {
            axios({
                method: "put",
                url: "/member/edit/phone-number",
                params: {
                    phoneNumber: phoneNumber.value,
                    certifiedNumber: certifiedNumber.value
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("전화번호가 수정됐습니다.");
                    window.location.href = "/logout";
                } else {
                    alert(check);
                }
            }).catch((e) => {
                console.error(e);
            });
        }
    },

    editPassword: function () {
        const beforePassword = document.getElementById("beforePassword");
        const afterPassword = document.getElementById("afterPassword");
        const passwordCheck = document.getElementById("passwordCheck");

        let passwordRegExp = /^.*(?=^.{8,15}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/;
        let afterPasswordValidation = passwordRegExp.test(afterPassword.value);

        if (!afterPasswordValidation) {
            alert('특수문자,문자,숫자 포함 형태의 8~15자리 이내의 암호여야 합니다.')
        }else {
            axios({
                method: "put",
                url: "/member/edit/password",
                params: {
                    beforePassword: beforePassword.value,
                    afterPassword: afterPassword.value,
                    passwordCheck: passwordCheck.value
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("비밀번호가 수정됐습니다.");
                    window.location.href = "/logout";
                } else {
                    alert(check);
                }
            }).catch((e) => {
                console.error(e);
            });
        }
    },

    update: function () {

        const email = document.getElementById("email");
        const nickname = document.getElementById("nickname");

        const emailValid = document.getElementById('sign-up-email-valid');
        const nicknameValid = document.getElementById('sign-up-nickname-valid');

        email.classList.remove("valid-custom");
        nickname.classList.remove("valid-custom");

        validation.removeValidation(emailValid);
        validation.removeValidation(nicknameValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        if (nickname.value !== "" && emailValidation ) {
            axios({
                method: "put",
                url: "/member/edit",
                params: {
                    nickname: nickname.value,
                    email: email.value
                }
            }).then((resp) => {
                let check = resp.data;

                if (check === "성공") {
                    alert("회원정보가 수정됐습니다.");
                    window.location.href = "/logout";
                } else {
                    alert(check);
                }
            }).catch((error) => {
                console.log(error);
            })
        }

        if (!emailValidation) {
            email.classList.add("valid-custom");
            validation.addValidation(emailValid, "올바른 이메일 형식이 아닙니다.");
        }
        if (nickname.value === '') {
            nickname.classList.add("valid-custom");
            validation.addValidation(nicknameValid, "닉네임을 입력해주세요.");
        }

    },

    delete: function () {
        const memberId = document.getElementById('memberId')

        console.log(memberId);

        axios({
            method: "delete",
            url: "/member/withdrawal",
            params: {
                memberId: memberId.value
            }
        }).then((resp) => {
            alert("회원 탈퇴가 완료됐습니다.");
            window.location.href = "/logout";
        }).catch((error) => {
            console.log(error);
        })
    },

    findUserId: function () {

        const email = document.getElementById("email");

        const emailValid = document.getElementById('find-userId-email-valid');

        email.classList.remove("valid-custom");

        validation.removeValidation(emailValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        if (emailValidation) {
            axios({
                method: "post",
                url: "/find-userid",
                params: {
                    email: email.value
                }
            }).then((resp) => {
                let check = resp.data;

                if (check === "성공") {
                    alert("이메일로 아이디가 발송됐습니다.");
                    window.location.href = "/login";
                } else {
                    alert(check);
                }
            }).catch((error) => {
                console.log(error);
            })
        }

        if (!emailValidation) {
            email.classList.add("valid-custom");
            validation.addValidation(emailValid, "올바른 이메일 형식이 아닙니다.");
        }

    },

    findPassword: function () {

        const email = document.getElementById("email");
        const userId = document.getElementById("userId");

        const emailValid = document.getElementById('find-password-email-valid');
        const userIdValid = document.getElementById('find-password-userId-valid');

        email.classList.remove("valid-custom");
        userId.classList.remove("valid-custom");

        validation.removeValidation(emailValid);
        validation.removeValidation(userIdValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        if (emailValidation && userId.value !== '') {
            axios({
                method: "post",
                url: "/find-password",
                params: {
                    email: email.value,
                    userId: userId.value
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("이메일로 비밀번호가 발송됐습니다.");
                    window.location.href = "/login";
                } else {
                    alert(check);
                }
            }).catch((error) => {
                console.log(error);
            })
        }

        if (!emailValidation) {
            email.classList.add("valid-custom");
            validation.addValidation(emailValid, "올바른 이메일 형식이 아닙니다.");
        }

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "닉네임을 입력해주세요.");
        }

    },

};

main.init();