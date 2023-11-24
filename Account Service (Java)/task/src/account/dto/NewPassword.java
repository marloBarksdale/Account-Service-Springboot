package account.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


public class NewPassword {

    @NotBlank(message = "Password cannot be empty!")
    @NotEmpty(message = "Password cannot be empty!")
    @Length(min = 12, message = "Password length must be 12 chars minimum!")
    private String new_password;

    public NewPassword(String new_password) {
        this.new_password = new_password;
    }

    public String new_password() {
        return new_password;
    }

    public NewPassword() {

    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    @Override
    public String toString() {
        return "NewPassword[" + "new_password=" + new_password + ']';
    }


}
