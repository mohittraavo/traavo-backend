package com.travo.tcms.platform.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.service.UserService;

@RestController
@RequestMapping("/admin")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  public List<UserRecord> listUsers(@RequestParam(required = false) Long tenantId) {
    return userService.listUsers(tenantId);
  }

  @PostMapping("/users")
  public UserRecord createUser(@Valid @RequestBody UserCreateRequest request) {
    return userService.createUser(request);
  }

  @GetMapping("/internal-users")
  public List<InternalUserRecord> listInternalUsers() {
    return userService.listInternalUsers();
  }

  @PostMapping("/internal-users")
  public InternalUserRecord createInternalUser(@Valid @RequestBody InternalUserRequest request) {
    return userService.createInternalUser(request);
  }

  @PutMapping("/internal-users/{externalId}")
  public InternalUserRecord updateInternalUser(@PathVariable String externalId,
                                               @Valid @RequestBody InternalUserRequest request) {
    return userService.updateInternalUser(externalId, request);
  }

  @DeleteMapping("/internal-users/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInternalUser(@PathVariable String externalId) {
    userService.deleteInternalUser(externalId);
  }
}
