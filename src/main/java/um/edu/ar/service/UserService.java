package um.edu.ar.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;
import um.edu.ar.config.Constants;
import um.edu.ar.domain.Authority;
import um.edu.ar.domain.User;
import um.edu.ar.repository.AuthorityRepository;
import um.edu.ar.repository.UserRepository;
import um.edu.ar.security.AuthoritiesConstants;
import um.edu.ar.security.SecurityUtils;
import um.edu.ar.service.dto.AdminUserDTO;
import um.edu.ar.service.dto.UserDTO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    public Optional<User> activateRegistration(String key) {
        LOG.debug("Request to activate user registration with key: {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                LOG.debug("Found user with activation key, proceeding with activation");
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                LOG.info("Successfully activated user: {}", user.getLogin());
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        LOG.debug("Request to reset password for key: {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> {
                LOG.debug("Validating reset date for user: {}", user.getLogin());
                return user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS));
            })
            .map(user -> {
                LOG.debug("Updating password for user: {}", user.getLogin());
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                LOG.info("Successfully reset password for user: {}", user.getLogin());
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        LOG.debug("Request to initiate password reset for email: {}", mail);
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                LOG.debug("Setting reset key for user: {}", user.getLogin());
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                LOG.info("Password reset initiated for user: {}", user.getLogin());
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        LOG.debug("Request to register new user: {}", userDTO.getLogin());

        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                LOG.debug("Checking existing user by login: {}", existingUser.getLogin());
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    LOG.error("Login name already used: {}", userDTO.getLogin());
                    throw new UsernameAlreadyUsedException();
                }
            });

        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                LOG.debug("Checking existing user by email: {}", existingUser.getEmail());
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    LOG.error("Email already used: {}", userDTO.getEmail());
                    throw new EmailAlreadyUsedException();
                }
            });

        LOG.debug("Creating new user account");
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setActivated(true);
        newUser.setActivationKey(RandomUtil.generateActivationKey());

        LOG.debug("Setting user authorities");
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);

        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        LOG.info("Created new user account: {}", newUser.getLogin());
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        LOG.debug("Attempting to remove non-activated user: {}", existingUser.getLogin());
        if (existingUser.isActivated()) {
            LOG.debug("User is already activated, cannot remove: {}", existingUser.getLogin());
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        LOG.info("Removed non-activated user: {}", existingUser.getLogin());
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        LOG.debug("Request to create new user: {}", userDTO.getLogin());

        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());

        LOG.debug("Setting language for user");
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        } else {
            user.setLangKey(userDTO.getLangKey());
        }

        LOG.debug("Generating encrypted password and reset key");
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        LOG.debug("Setting user authorities");
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }

        userRepository.save(user);
        this.clearUserCaches(user);
        LOG.info("Created new user account: {}", user.getLogin());
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        LOG.debug("Request to update user: {}", userDTO.getLogin());

        return Optional.of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                LOG.debug("Found existing user, clearing caches");
                this.clearUserCaches(user);

                LOG.debug("Updating user details");
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());

                LOG.debug("Updating user authorities");
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);

                userRepository.save(user);
                this.clearUserCaches(user);
                LOG.info("Updated user account: {}", user.getLogin());
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        LOG.debug("Request to delete user: {}", login);
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                LOG.debug("Found user, proceeding with deletion");
                userRepository.delete(user);
                this.clearUserCaches(user);
                LOG.info("Deleted user account: {}", login);
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        LOG.debug("Request to update current user's information");
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                LOG.debug("Updating user details for: {}", user.getLogin());
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                userRepository.save(user);
                this.clearUserCaches(user);
                LOG.info("Updated user information for: {}", user.getLogin());
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        LOG.debug("Request to change password for current user");
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                LOG.debug("Validating current password for user: {}", user.getLogin());
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    LOG.error("Invalid password provided for user: {}", user.getLogin());
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                LOG.info("Changed password for user: {}", user.getLogin());
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        LOG.debug("Request to get all managed users with pageable: {}", pageable);
        Page<AdminUserDTO> page = userRepository.findAll(pageable).map(AdminUserDTO::new);
        LOG.info("Retrieved {} managed users", page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        LOG.debug("Request to get all public users with pageable: {}", pageable);
        Page<UserDTO> page = userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
        LOG.info("Retrieved {} public users", page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        LOG.debug("Request to get user with authorities by login: {}", login);
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        LOG.debug("Request to get current user with authorities");
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        LOG.debug("Starting scheduled removal of non-activated users");
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                LOG.debug("Deleting non-activated user: {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
        LOG.info("Completed scheduled removal of non-activated users");
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        LOG.debug("Request to get all authorities");
        List<String> authorities = authorityRepository.findAll().stream().map(Authority::getName).toList();
        LOG.debug("Retrieved {} authorities", authorities.size());
        return authorities;
    }

    private void clearUserCaches(User user) {
        LOG.debug("Clearing user caches for: {}", user.getLogin());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }
}
