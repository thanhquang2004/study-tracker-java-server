package com.studytracker.identity.service;

import java.util.HashSet;
import java.util.List;

import com.studytracker.identity.dto.request.UpdatePasswordRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studytracker.event.dto.NotificationEvent;
import com.studytracker.identity.constant.PredefinedRole;
import com.studytracker.identity.dto.request.UserCreationRequest;
import com.studytracker.identity.dto.request.UserUpdateRequest;
import com.studytracker.identity.dto.response.UserResponse;
import com.studytracker.identity.entity.Role;
import com.studytracker.identity.entity.User;
import com.studytracker.identity.exception.AppException;
import com.studytracker.identity.exception.ErrorCode;
import com.studytracker.identity.mapper.ProfileMapper;
import com.studytracker.identity.mapper.UserMapper;
import com.studytracker.identity.repository.RoleRepository;
import com.studytracker.identity.repository.UserRepository;
import com.studytracker.identity.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    ProfileMapper profileMapper;
    PasswordEncoder passwordEncoder;
    ProfileClient profileClient;
    KafkaTemplate<String, Object> kafkaTemplate;

    public UserResponse createUser(UserCreationRequest request) {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
        user.setEmailVerified(false);
        user.setActive(true);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        var profile = profileClient.createProfile(profileRequest);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("welcome :")
                .body("hello: ")
                .build();

        // Public message to kafka
        kafkaTemplate.send("register-successful", notificationEvent);

        var userCreationResponse = userMapper.toUserResponse(user);
        userCreationResponse.setId(profile.getResult().getId());

        return userCreationResponse;
    }

    private User getMe(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public UserResponse getMyInfo() {
        User user = getMe();
        return userMapper.toUserResponse(user);
    }
    public String updateMyPassword(UpdatePasswordRequest request) {
        User user = getMe();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_IS_INCORRECT);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_IS_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }
    public String updateMyInfo(UserUpdateRequest request){
        User user = getMe();
        userMapper.updateUser(user, request);
        return "success";
    }



    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }




    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject("Account Deletion Notification")
                .body("Your account has been deleted by an administrator.")
                .build();

        kafkaTemplate.send("user-deleted", notificationEvent);

        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setActive(!user.isActive());
        userRepository.save(user);

        log.info("User with ID {} has been {}.", userId, user.isActive() ? "activated" : "deactivated");

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject(user.isActive() ? "Account Activated" : "Account Deactivated")
                .body(user.isActive() ? "Your account has been activated by an administrator." : "Your account has been deactivated by an administrator.")
                .build();

        kafkaTemplate.send("user-status-changed", notificationEvent);
    }


}
