package com.rest.server.graphql;


import com.rest.server.models.User;
import com.rest.server.models.UserDto;
import com.rest.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public com.rest.server.graphql.UserController.PaginatedUsers users(
            @Argument Integer page,
            @Argument Integer limit,
            @Argument String sortBy
    ) {
        Page<User> usersPage = userService.allUsers(
                PageRequest.of(
                        page != null ? page - 1 : 0,  // Page numbers start from 1 in client
                        limit != null ? limit : 10
                )
        );

        return new com.rest.server.graphql.UserController.PaginatedUsers(
                usersPage.getContent().stream()
                        .map(this::convertToDto)
                        .toList(),
                (int) usersPage.getTotalElements(),
                usersPage.getNumber() + 1,  // Adjust for 1-based index
                usersPage.getSize()
        );
    }

    @QueryMapping
    public UserDto user(@Argument String id) {
        return userService.singleUser(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @MutationMapping
    public UserDto createUser(@Argument com.rest.server.graphql.UserController.UserCreateInput input) {
        validateInput(input);
        User user = convertInputToUser(input);
        return convertToDto(userService.createUser(user));
    }

    @MutationMapping
    public UserDto updateUser(@Argument String id, @Argument com.rest.server.graphql.UserController.UserUpdateInput input) {
        User user = convertUpdateInputToUser(input);
        return convertToDto(userService.updateUser(id, user));
    }

    @MutationMapping
    public String deleteUser(@Argument String id) {
        userService.deleteUser(id);
        return id;
    }

    // Conversion methods
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setTitle(user.getUserTitle());
        dto.setFirstName(user.getUserFirstName());
        dto.setLastName(user.getUserLastName());
        dto.setGender(user.getUserGender());
        dto.setEmail(user.getUserEmail());
        dto.setDateOfBirth(user.getUserDateOfBirth());
        dto.setRegisterDate(user.getUserRegisterDate());
        dto.setPhone(user.getUserPhone());
        dto.setPicture(user.getUserPicture());
        return dto;
    }

    private User convertInputToUser(com.rest.server.graphql.UserController.UserCreateInput input) {
        User user = new User();
        user.setUserTitle(input.getTitle());
        user.setUserFirstName(input.getFirstName());
        user.setUserLastName(input.getLastName());
        user.setUserEmail(input.getEmail());
        user.setUserGender(input.getGender());
        user.setUserPassword(input.getPassword());

        // Handle date parsing
        if (input.getDateOfBirth() != null) {
            try {
                user.setUserDateOfBirth(String.valueOf(LocalDateTime.parse(input.getDateOfBirth())));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use ISO-8601 format");
            }
        }

        user.setUserPhone(input.getPhone());
        user.setUserPicture(input.getPicture());

        // Set register date to current date-time
        user.setUserRegisterDate(
                OffsetDateTime.now(ZoneOffset.UTC)  // Use UTC or your desired timezone
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)  // RFC3339-compliant
        );

        return user;
    }


    private User convertUpdateInputToUser(com.rest.server.graphql.UserController.UserUpdateInput input) {
        User user = new User();
        user.setUserTitle(input.getTitle());
        user.setUserFirstName(input.getFirstName());
        user.setUserLastName(input.getLastName());
        user.setUserGender(input.getGender());

        // Handle date parsing
        if(input.getDateOfBirth() != null) {
            try {
                user.setUserDateOfBirth(String.valueOf(LocalDateTime.parse(input.getDateOfBirth())));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use ISO-8601 format");
            }
        }

        user.setUserPhone(input.getPhone());
        user.setUserPicture(input.getPicture());
        return user;
    }

    private void validateInput(com.rest.server.graphql.UserController.UserCreateInput input) {
        if(input.getFirstName() == null || input.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if(input.getLastName() == null || input.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if(input.getEmail() == null || input.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if(input.getPassword() == null || input.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    // Pagination wrapper class
    public static class PaginatedUsers {
        private final List<UserDto> data;
        private final int total;
        private final int page;
        private final int limit;

        public PaginatedUsers(List<UserDto> data, int total, int page, int limit) {
            this.data = data;
            this.total = total;
            this.page = page;
            this.limit = limit;
        }

        public List<UserDto> getData() { return data; }
        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getLimit() { return limit; }
    }

    // Input types
    public static class UserCreateInput {
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String password;  // Added password field
        private String gender;
        private String dateOfBirth;
        private String phone;
        private String picture;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }  // Added
        public void setPassword(String password) { this.password = password; }  // Added
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
    }

    public static class UserUpdateInput {
        private String title;
        private String firstName;
        private String lastName;
        private String gender;
        private String dateOfBirth;
        private String phone;
        private String picture;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
    }
}
