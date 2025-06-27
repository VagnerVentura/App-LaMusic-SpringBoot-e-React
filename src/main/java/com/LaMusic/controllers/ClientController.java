package com.LaMusic.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.UpdateUserRequest;
import com.LaMusic.dto.UserDTO;
import com.LaMusic.entity.User;
import com.LaMusic.repositories.UserRepository;
import com.LaMusic.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public UserDTO getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return userService.findUserDTOById(user.getId());
    }
//    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/me")
    public UserDTO updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestBody UpdateUserRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return userService.update(user.getId(), request);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/me")
    public void deleteMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        userService.softDelete(user.getId());
    }
}
