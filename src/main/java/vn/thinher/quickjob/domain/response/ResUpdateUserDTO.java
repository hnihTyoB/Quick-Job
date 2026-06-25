package vn.thinher.quickjob.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.thinher.quickjob.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updatedAt;
}
