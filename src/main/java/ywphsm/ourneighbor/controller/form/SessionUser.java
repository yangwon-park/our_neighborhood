package ywphsm.ourneighbor.controller.form;

import lombok.Data;
import ywphsm.ourneighbor.domain.member.Member;

@Data
public class SessionUser {
    private String name;
    private String email;
    private String picture;
    private int gender;

    public SessionUser(Member member) {
        this.name = member.getUsername();
        this.email = member.getEmail();
        this.gender = member.getGender();
//        this.picture = member.getPicture();
    }
}
