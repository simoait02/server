package com.rest.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @MongoId(FieldType.OBJECT_ID)
    private String id;

    private String userTitle;

    @NotBlank(message = "First name is mandatory")
    @NotNull
    @Size(min = 2, max = 50)
    @Field("userFirstName")
    @JsonProperty("firstName")
    private String userFirstName;

    @NotBlank(message = "Last name is mandatory")
    @NotNull
    @Size(min = 2, max = 50)
    @Field("userLastName")
    @JsonProperty("lastName")
    private String userLastName;

    private String userGender;

    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String userEmail;
    @NotBlank(message = "Password is mandatory")
    private String userPassword;
    private String userDateOfBirth;
    private String userRegisterDate;
    private String userPhone;
    private String userPicture;
    private String userLocationId;
    // ... other fields (password, dob, etc.)

    // Add @SchemaMapping to map getters to GraphQL field names
    @SchemaMapping("firstName")
    public String getUserFirstName() {
        return userFirstName;
    }

    @SchemaMapping("lastName")
    public String getUserLastName() {
        return userLastName;
    }

    @SchemaMapping("email")
    public String getUserEmail() {
        return userEmail;
    }
}