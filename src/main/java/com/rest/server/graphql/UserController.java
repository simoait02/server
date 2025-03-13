package com.rest.server.graphql;


import com.rest.server.models.User;
import com.rest.server.models.UserDto;
import com.rest.server.services.UserService;
import lombok.Getter;
import lombok.Setter;
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
                        page != null ? page - 1 : 0,
                        limit != null ? limit : 10
                )
        );

        return new com.rest.server.graphql.UserController.PaginatedUsers(
                usersPage.getContent().stream()
                        .map(this::convertToDto)
                        .toList(),
                (int) usersPage.getTotalElements(),
                usersPage.getNumber() + 1,
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

        if (input.getDateOfBirth() != null) {
            try {
                user.setUserDateOfBirth(String.valueOf(LocalDateTime.parse(input.getDateOfBirth())));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use ISO-8601 format");
            }
        }

        user.setUserPhone(input.getPhone());
        user.setUserPicture(input.getPicture());

        user.setUserRegisterDate(
                OffsetDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );

        return user;
    }


    private User convertUpdateInputToUser(com.rest.server.graphql.UserController.UserUpdateInput input) {
        User user = new User();
        user.setUserTitle(input.getTitle());
        user.setUserFirstName(input.getFirstName());
        user.setUserLastName(input.getLastName());
        user.setUserGender(input.getGender());

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

    @Getter
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

    }

    @Setter
    @Getter
    public static class UserCreateInput {
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String gender;
        private String dateOfBirth;
        private String phone;
        private String picture;

    }

    @Getter
    @Setter
    public static class UserUpdateInput {
        private String title;
        private String firstName;
        private String lastName;
        private String gender;
        private String dateOfBirth;
        private String phone;
        private String picture;

    }
}
