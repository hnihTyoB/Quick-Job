package vn.thinher.quickjob.domain.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.thinher.quickjob.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
}
