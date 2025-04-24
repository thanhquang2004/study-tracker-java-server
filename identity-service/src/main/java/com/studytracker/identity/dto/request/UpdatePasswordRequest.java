package com.studytracker.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {
    @NotBlank
    @Size(min = 8)
    String currentPassword;
    @NotBlank
    @Size(min = 8)
    String newPassword;
    @NotBlank
    @Size(min = 8)
    String confirmPassword;
}
