package rt.marson.syeta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rt.marson.syeta.mapper.UserMapper;
import rt.marson.syeta.repository.UserRepo;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @Spy
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getActiveUserById() {
    }

    @Test
    void findUserById() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void getUserByEmailForLogin() {
    }

    @Test
    void userDetailsService() {
    }

    @Test
    void getCurrentUser() {
    }

    @Test
    void isEmailAlreadyExist() {
    }

    @Test
    void getEmailExistDto() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void changeCountry() {
    }

    @Test
    void compareUsersIds() {
    }

    @Test
    void changePassword() {
    }
}