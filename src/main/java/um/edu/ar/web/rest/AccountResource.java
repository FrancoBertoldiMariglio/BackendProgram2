package um.edu.ar.web.rest;

import jakarta.validation.Valid;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import um.edu.ar.domain.User;
import um.edu.ar.repository.UserRepository;
import um.edu.ar.security.SecurityUtils;
import um.edu.ar.service.MailService;
import um.edu.ar.service.UserService;
import um.edu.ar.service.dto.AdminUserDTO;
import um.edu.ar.service.dto.PasswordChangeDTO;
import um.edu.ar.web.rest.errors.*;
import um.edu.ar.web.rest.vm.KeyAndPasswordVM;
import um.edu.ar.web.rest.vm.ManagedUserVM;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        LOG.debug("REST request to register new user account: {}", managedUserVM.getLogin());
        LOG.debug("Validating password requirements");
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            LOG.warn("Invalid password length for user: {}", managedUserVM.getLogin());
            throw new InvalidPasswordException();
        }

        LOG.debug("Registering new user");
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        LOG.info("User registered successfully: {}", user.getLogin());

        LOG.debug("Sending activation email");
        mailService.sendActivationEmail(user);
        LOG.info("Activation email sent to user: {}", user.getLogin());
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        LOG.debug("REST request to activate account");
        LOG.debug("Attempting to activate account with provided key");
        Optional<User> user = userService.activateRegistration(key);

        if (!user.isPresent()) {
            LOG.warn("Failed activation attempt - key not found");
            throw new AccountResourceException("No user was found for this activation key");
        }

        LOG.info("User account activated successfully: {}", user.get().getLogin());
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        LOG.debug("REST request to get current user account");

        return userService
            .getUserWithAuthorities()
            .map(user -> {
                LOG.info("Retrieved account details for user: {}", user.getLogin());
                return new AdminUserDTO(user);
            })
            .orElseThrow(() -> {
                LOG.error("Failed to retrieve current user account");
                return new AccountResourceException("User could not be found");
            });
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        LOG.debug("REST request to update user account");

        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> {
                LOG.error("Failed to retrieve current user login");
                return new AccountResourceException("Current user login not found");
            });

        LOG.debug("Checking email uniqueness for user: {}", userLogin);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            LOG.warn("Email already in use: {}", userDTO.getEmail());
            throw new EmailAlreadyUsedException();
        }

        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            LOG.error("User not found: {}", userLogin);
            throw new AccountResourceException("User could not be found");
        }

        LOG.debug("Updating user account information");
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
        LOG.info("User account updated successfully: {}", userLogin);
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        LOG.debug("REST request to change password for current user");
        LOG.debug("Validating new password requirements");

        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            LOG.warn("Invalid password length in change password request");
            throw new InvalidPasswordException();
        }

        LOG.debug("Processing password change");
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        LOG.info("Password changed successfully for user");
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        LOG.debug("REST request to initialize password reset");
        Optional<User> user = userService.requestPasswordReset(mail);

        if (user.isPresent()) {
            LOG.debug("Sending password reset email");
            mailService.sendPasswordResetMail(user.orElseThrow());
            LOG.info("Password reset email sent successfully");
        } else {
            // Security measure: Don't reveal if email exists
            LOG.warn("Password reset requested for non-existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        LOG.debug("REST request to finish password reset");
        LOG.debug("Validating new password requirements");

        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            LOG.warn("Invalid password length in reset password request");
            throw new InvalidPasswordException();
        }

        LOG.debug("Processing password reset completion");
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            LOG.warn("Failed password reset attempt - invalid reset key");
            throw new AccountResourceException("No user was found for this reset key");
        }

        LOG.info("Password reset completed successfully for user: {}", user.get().getLogin());
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
