import validation from "./validation.js";
import mask from "./mask.js";
import menu from "./menu.js";

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
        const memberRoleEditBtn = document.getElementById("member-role-edit");
        const adminMemberDeleteBtn = document.getElementById("admin-member-delete");
        const apiSignUpSaveBtn = document.getElementById("api-sign-up-save");

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

        if (memberRoleEditBtn !== null) {
            memberRoleEditBtn.addEventListener("click", () => {
                _this.memberRoleEdit();
            });
        }

        if (adminMemberDeleteBtn !== null) {
            adminMemberDeleteBtn.addEventListener("click", () => {
                _this.adminDelete();
            });
        }

        if (apiSignUpSaveBtn !== null) {
            apiSignUpSaveBtn.addEventListener("click", () => {
                _this.apiSave();
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
        const file = document.getElementById("file").files;

        const userIdValid = document.getElementById("sign-up-userId-valid");
        const passwordValid = document.getElementById("sign-up-password-valid");
        const passwordCheckValid = document.getElementById("sign-up-passwordCheck-valid");
        const emailValid = document.getElementById("sign-up-email-valid");
        const usernameValid = document.getElementById("sign-up-username-valid");
        const nicknameValid = document.getElementById("sign-up-nickname-valid");
        const birthDateValid = document.getElementById("sign-up-birthDate-valid");
        const phoneNumberValid = document.getElementById("sign-up-phoneNumber-valid");
        const certifiedNumberValid = document.getElementById("sign-up-certifiedNumber-valid");
        const fileValid = document.getElementById("sign-up-file-valid");

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
        validation.removeValidation(fileValid);

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
        let passwordCheckValidation = password.value === passwordCheck.value;

        let fileSizeCheck;
        let targetSize = 1024 * 1024 * 2;

        if (file.length === 0) {
            fileSizeCheck = true;
        } else {
            fileSizeCheck = file[0].size <= targetSize;
        }

        const signUpForm = document.getElementById("sign-up-add-form");
        let formData = new FormData(signUpForm);

        if (userId.value !== "" && username.value !== ""
            && nickname.value !== "" && passwordCheckValidation === true
        && emailValidation && birthDateValidation && passwordCheckValidation
        && passwordValidation && phoneNumberValidation
            && certifiedNumberValidation && fileSizeCheck) {

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

        if (file.length !== 0 && file[0].size > targetSize) {
            validation.addValidation(fileValid, "이미지의 크기는 2MB를 넘을 수 없습니다.");
        }

    },

    sendSMS: function () {
        const phoneNumber = document.getElementById("phoneNumber");
        let phoneNumberRegExp = /^01(0|1|[6-9]?)([0-9]{3,4})([0-9]{4})$/;
        let phoneNumberValidation = phoneNumberRegExp.test(phoneNumber.value);

        if (phoneNumber.value === "") {
            alert("전화번호를 입력해주세요.")
        }
        else if (!phoneNumberValidation) {
            alert("전화번호를 올바르게 입력해주세요.")
        } else {
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
                    let display = document.getElementById("send-SMS-time");
                    let leftSec = 120;

                    if (this.isRunning) {
                        clearInterval(this.timer)
                        display.innerText = "";
                        this.startTimer(leftSec, display);
                    } else {
                        this.startTimer(leftSec, display);
                    }
                }
            }).catch((e) => {
                console.error(e);
            });
        }
    },

    timer : null,
    isRunning : false,
    isPaused : false,

    startTimer: function (count, display) {
        let minutes, seconds;
        this.isPaused = false;

        this.timer = setInterval(function () {
            if (!this.isPaused) {
                count--;
                minutes = parseInt(count / 60, 10);
                seconds = parseInt(count % 60, 10);

                minutes = minutes < 10 ? "0" + minutes : minutes;
                seconds = seconds < 10 ? "0" + seconds : seconds;

                display.innerText = minutes + ":" + seconds;

                if (count === 0) {
                    clearInterval(this.timer);
                    alert("인증번호 시간 초과");
                    this.isPaused = true;
                    this.isRunning = false;
                    display.innerText = "시간초과";
                }
            }

        }, 1000);

        this.isRunning = true;
    },

    save: function () {
        const signUpForm = document.getElementById("sign-up-add-form");

        let formData = new FormData(signUpForm);
        menu.createDefaultImg(formData);

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
                    alert("전화번호가 수정됐습니다, 다시 로그인해주세요.");
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
            alert("특수문자,문자,숫자 포함 형태의 8~15자리 이내의 암호여야 합니다.")
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
                    alert("비밀번호가 수정됐습니다, 다시 로그인해주세요.");
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
        const file = document.getElementById("file").files;

        const emailValid = document.getElementById("sign-up-email-valid");
        const nicknameValid = document.getElementById("sign-up-nickname-valid");
        const fileValid = document.getElementById("member-edit-file-valid");

        email.classList.remove("valid-custom");
        nickname.classList.remove("valid-custom");

        validation.removeValidation(emailValid);
        validation.removeValidation(nicknameValid);
        validation.removeValidation(fileValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        let fileSizeCheck;
        let targetSize = 1024 * 1024 * 2;

        if (file.length === 0) {
            fileSizeCheck = true;
        } else {
            fileSizeCheck = file[0].size <= targetSize;
        }

        const memberEditForm = document.getElementById("member-edit-form");
        let formData = new FormData(memberEditForm);

        if (nickname.value !== "" && emailValidation && fileSizeCheck) {
            axios({
                method: "put",
                url: "/member/edit",
                data: formData
            }).then((resp) => {
                let check = resp.data;

                if (check === "성공") {
                    alert("회원정보가 수정됐습니다, 다시 로그인해주세요.");
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

        if (nickname.value === "") {
            nickname.classList.add("valid-custom");
            validation.addValidation(nicknameValid, "닉네임을 입력해주세요.");
        }

        if (file.length !== 0 && file[0].size > targetSize) {
            validation.addValidation(fileValid, "이미지의 크기는 2MB를 넘을 수 없습니다.");
        }

    },

    delete: function () {
        const memberId = document.getElementById("memberId")

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

    adminDelete: function () {
        const userId = document.getElementById("userId");

        const userIdValid = document.getElementById("member-role-edit-userId-valid");

        validation.removeValidation(userIdValid);


        axios({
            method: "delete",
            url: "/admin/withdrawal",
            params: {
                userId: userId.value
            }
        }).then((resp) => {
            let check = resp.data;
            if (check) {
                alert("회원 탈퇴가 완료됐습니다.");
            } else {
                alert("존재하지 않는 아이디 입니다.");
            }
        }).catch((error) => {
            console.log(error);
        })

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "아이디를 입력해주세요.");
        }
    },

    findUserId: function () {
        const email = document.getElementById("email");
        const emailValid = document.getElementById("find-userId-email-valid");

        email.classList.remove("valid-custom");

        validation.removeValidation(emailValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        if (emailValidation) {
            mask.loadingWithMask();

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
                    mask.closeMask();
                } else {
                    alert(check);
                    mask.closeMask();
                }
            }).catch((error) => {
                console.log(error);
                mask.closeMask();
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

        const emailValid = document.getElementById("find-password-email-valid");
        const userIdValid = document.getElementById("find-password-userId-valid");

        email.classList.remove("valid-custom");
        userId.classList.remove("valid-custom");

        validation.removeValidation(emailValid);
        validation.removeValidation(userIdValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);

        if (emailValidation && userId.value !== "") {
            mask.loadingWithMask();
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
                    mask.closeMask();
                } else {
                    alert(check);
                    mask.closeMask();
                }
            }).catch((error) => {
                console.log(error);
                mask.closeMask();
            })
        }

        if (!emailValidation) {
            email.classList.add("valid-custom");
            validation.addValidation(emailValid, "올바른 이메일 형식이 아닙니다.");
        }

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "아이디를 입력해주세요.");
        }
    },

    memberRoleEdit: function () {

        const userId = document.getElementById("userId");
        const role = document.getElementById("role").options
            [document.getElementById("role").selectedIndex].value;

        const userIdValid = document.getElementById("member-role-edit-userId-valid");
        const roleValid = document.getElementById("member-role-edit-role-valid");

        userId.classList.remove("valid-custom");
        // role.classList.remove("valid-custom");

        validation.removeValidation(userIdValid);
        validation.removeValidation(roleValid);

        if (userId.value !== "" && role !== "") {
            axios({
                method: "put",
                url: "/admin/member-role/edit",
                params: {
                    userId: userId.value,
                    role: role
                }
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("권한이 성공적으로 변경되었습니다");
                    window.location.reload();
                } else {
                    alert(check);
                }
            }).catch((error) => {
                console.log(error);
            })
        }

        if (role.value === "") {
            role.classList.add("valid-custom");
            validation.addValidation(roleValid, "권한을 정해주세요.");
        }

        if (userId.value === "") {
            userId.classList.add("valid-custom");
            validation.addValidation(userIdValid, "아이디를 입력해주세요.");
        }

    },

    apiSave: function () {
        const email = document.getElementById("email");
        const username = document.getElementById("username");
        const nickname = document.getElementById("nickname");
        const birthDate = document.getElementById("birthDate");

        const emailValid = document.getElementById("sign-up-email-valid");
        const usernameValid = document.getElementById("sign-up-username-valid");
        const nicknameValid = document.getElementById("sign-up-nickname-valid");
        const birthDateValid = document.getElementById("sign-up-birthDate-valid");

        email.classList.remove("valid-custom");
        username.classList.remove("valid-custom");
        nickname.classList.remove("valid-custom");
        birthDate.classList.remove("valid-custom");

        validation.removeValidation(emailValid);
        validation.removeValidation(usernameValid);
        validation.removeValidation(nicknameValid);
        validation.removeValidation(birthDateValid);

        let emailRegExp = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        let emailValidation = emailRegExp.test(email.value);
        let birthDateRegExp = /[0-9]{7}$/;
        let birthDateValidation = birthDateRegExp.test(birthDate.value);

        const signUpForm = document.getElementById("api-sign-up-add-form");
        let formData = new FormData(signUpForm);

        if (username.value !== "" && nickname.value !== ""
            && emailValidation && birthDateValidation) {

            axios({
                method: "post",
                url: "/member/api-add",
                data: formData
            }).then((resp) => {
                let check = resp.data;
                if (check === "성공") {
                    alert("회원가입이 완료됐습니다.");
                    window.location.href = "/login";
                } else {
                    alert(check)
                }
            }).catch((error) => {
                console.log(error)
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

        if (username.value === "") {
            username.classList.add("valid-custom");
            validation.addValidation(usernameValid, "이름을 입력해주세요.");
        }

        if (nickname.value === "") {
            nickname.classList.add("valid-custom");
            validation.addValidation(nicknameValid, "닉네임을 입력해주세요.");
        }

    },


};

main.init();