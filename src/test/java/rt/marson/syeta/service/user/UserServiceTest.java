package rt.marson.syeta.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.user.UserDto;
import rt.marson.syeta.mapper.UserMapper;
import rt.marson.syeta.repository.UserRepo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getActiveUserById_whenUserExists_shouldReturnUserDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(true);

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userRepo.findByIdAndIsActiveTrue(userId)).thenReturn(java.util.Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getActiveUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepo, times(1)).findByIdAndIsActiveTrue(userId);
        verify(userMapper, times(1)).toDto(user);
    }
}