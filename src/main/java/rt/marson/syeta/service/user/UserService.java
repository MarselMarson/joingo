package rt.marson.syeta.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.Country;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.CountryDto;
import rt.marson.syeta.dto.event.EventDto;
import rt.marson.syeta.dto.user.ChangePasswordDto;
import rt.marson.syeta.dto.user.EmailExistDto;
import rt.marson.syeta.dto.user.UserDto;
import rt.marson.syeta.dto.user.UserPatchDto;
import rt.marson.syeta.exception.DataValidationException;
import rt.marson.syeta.filter.EventFilter;
import rt.marson.syeta.mapper.UserMapper;
import rt.marson.syeta.repository.UserRepo;
import rt.marson.syeta.security.AuthenticationService;
import rt.marson.syeta.service.FileService;
import rt.marson.syeta.service.event.EventService;
import rt.marson.syeta.util.DateUtil;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final CountryService countryService;
    @Lazy
    private final AuthenticationService authenticationService;
    @Lazy
    private final FileService fileService;
    @Lazy
    private final EventService eventService;

    public void create(User user) {
        userRepo.save(user);
    }

    public UserDto getActiveUserById(Long id) {
        User user = findActiveUserById(id);
        return userMapper.toDto(user);
    }

    private User findActiveUserById(Long id) {
        return userRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User id: " + id + " not found"));
    }

    public User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id: " + id + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email: " + email + " not found"));
    }

    public User getUserByEmailForLogin(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
    }

    public User getUserByEmailOrNullIfNotExists(String email) {
        return userRepo.findByEmail(email)
                .orElse(null);
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getUserByEmail;
    }

    public User getCurrentUserOrNullIfAnonymous() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return getUserByEmail(auth.getName());
    }

    public boolean isEmailAlreadyExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public EmailExistDto getEmailExistDto(String email) {
        return new EmailExistDto(userRepo.existsByEmail(email));
    }

    public List<UserDto> getUsers() {
        return userMapper.toUserDots(getActiveUsers());
    }

    private List<User> getActiveUsers() {
        return userRepo.findByIsActiveTrue();
    }

    @Transactional
    public UserDto updateUser(UserPatchDto dto, Long userId) {
        User user = findUserById(userId);

        userMapper.updateEntity(dto, user);

        if (dto.getBirthDate() == null || dto.getBirthDate().isBlank() || dto.getBirthDate().trim().equals("null")) {
            user.setBirthDate(null);
        } else {
            user.setBirthDate(DateUtil.stringToLocal(dto.getBirthDate()));
        }

        if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().isBlank()) {
            String fileName = fileService.getNameFromUrl(dto.getPhotoUrl());
            user.setPhoto(fileService.getByFileName(fileName));
        } else {
            user.setPhoto(null);
        }

        User savedUser = userRepo.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        user.setEmail(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setDescription(null);
        user.setCountry(null);
        user.setBirthDate(null);
        user.setActive(false);
        user.setPhoto(null);
        user.setEmailVerified(false);
        userRepo.save(user);
    }

    public void changeCountry(Long userId, CountryDto countryDto) {
        User user = findUserById(userId);
        Country country = countryService.findCountryByName(countryDto.getName());
        user.setCountry(country);
        userRepo.save(user);
    }

    public void compareUsersIds(Long userId, Long authUserId) {
        if (!authUserId.equals(userId)) {
            throw new AccessDeniedException("User id: " + authUserId
                    + " does not have access to change the data of user id: " + userId);
        }
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = findUserById(userId);
        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getNewPassword())) {
            throw new DataValidationException("Passwords must be different");
        }
        if (!authenticationService.isPasswordsEqual(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new DataValidationException("Invalid old password");
        }
        user.setPassword(authenticationService.encodePassword(changePasswordDto.getNewPassword()));

        userRepo.save(user);
    }

    public void changePassword(Long userId, String password) {
        User user = findUserById(userId);

        user.setPassword(authenticationService.encodePassword(password));

        userRepo.save(user);
    }

    @Transactional
    public void addEventToFavourites(Long userId, Long eventId) {
        User user = findUserById(userId);
        Event event = eventService.findActiveEventById(eventId);
        user.addFavouriteEvent(event);
        userRepo.saveAndFlush(user);
    }

    @Transactional
    public void removeEventFromFavourites(Long userId, Long eventId) {
        User user = findUserById(userId);
        user.getFavouriteEvents()
                .remove(eventService.findActiveEventById(eventId));
        userRepo.save(user);
    }

    public Page<EventDto> getUserEvents(Long userId, EventFilter filter, Pageable pageable) {
        return eventService.getAllEventsByOwnerId(userId, filter, pageable);
    }

    public Page<EventDto> getAllFavouriteEvents(Long userId, EventFilter filter, Pageable pageable) {
        return eventService.getAllEventsByFavouriteUserId(userId, filter, pageable);
    }

    public Page<EventDto> getParticipateEvents(Long userId, EventFilter filter, Pageable pageable) {
        return eventService.getAllEventsByParticipantId(userId, filter, pageable);
    }

    public void setEmailVerified(User user) {
        user.setEmailVerified(true);
        userRepo.save(user);
    }
}
