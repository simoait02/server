package com.rest.server.models;

import lombok.*;

@Getter
@Setter
public class UserDto {
    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String dateOfBirth;
    private String registerDate;
    private String phone;
    private String picture;
    private String userLocationId;


}