package com.studytracker.identity.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    LocalDate dob;
    String email;
    boolean emailVerified;
    boolean active;
    String name;
    String gender;
    Integer age;
    String occupation;

    Set<RoleResponse> roles;
}
