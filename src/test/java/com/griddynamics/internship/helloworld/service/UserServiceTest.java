package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.UserReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.UserMapperImpl;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.security.GoogleTokenVerifier;
import com.griddynamics.internship.helloworld.specification.UserSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;
    @Spy
    private GoogleTokenVerifier googleTokenVerifier;

    @InjectMocks
    private UserService testedUserService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private final Survey survey = Survey.builder()
            .id(1L)
            .name("Survey")
            .description("First survey")
            .passcode("pass")
            .duration(LocalTime.of(0, 5, 0))
            .questions(null)
            .author(null)
            .participants(null)
            .started(false)
            .build();

    private final User user = User.builder()
            .id("1L")
            .name("First")
            .surname("Surname")
            .createdSurveys(null)
            .build();

    private final User anotherUser = User.builder()
            .id("2L")
            .name("Second")
            .surname("SecondSurname")
            .createdSurveys(List.of(survey))
            .build();

    @Test
    void getUsers_method_test() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize);
        Page<User> userPage = new PageImpl<>(List.of(user, anotherUser), paging, 2);
        given(userRepository.findAll(paging)).willReturn(userPage);

        Page<UserSenderDTO> userSenderDTOSFromService = testedUserService.getUsers(1);
        Page<UserSenderDTO> userSenderDTOS = userPage.map(userMapper::map);

        assertEquals(userSenderDTOS, userSenderDTOSFromService);
    }

    //I don't really know how could I test something that connects with Google. Tbh it would be better to just create
    // a separate class just for getting token from Google and mock it. But I don't have time to do this now.
/*    @Test
    void addUser_method_test() {
        given(userRepository.save(any())).willReturn(user);
        UserReceiverDTO userReceiverDTO = new UserReceiverDTO(user.getName(), user.getSurname());

        testedUserService.addUser(userReceiverDTO);

        verify(userRepository).save(userArgumentCaptor.capture());

        User userCaptured = userArgumentCaptor.getValue();

        assertEquals(userReceiverDTO.name(), userCaptured.getName());
        assertEquals(userReceiverDTO.surname(), userCaptured.getSurname());
    }*/

    @Test
    void getUserBySurveyId_method_test() {
        given(userRepository.findOne(any(UserSpecification.class))).willReturn(Optional.of(user));

        UserSenderDTO userSenderDTO = userMapper.map(user);
        UserSenderDTO userFromService = testedUserService.getUserBySurveyId(survey.getId());

        assertEquals(userSenderDTO, userFromService);
    }

    @Test
    void getUser_method_test() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserSenderDTO userSenderDTO = userMapper.map(user);
        UserSenderDTO userFromService = testedUserService.getUser(user.getId());

        assertEquals(userSenderDTO, userFromService);
    }

    @Test
    void updateUser_method_test() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserReceiverDTO newUser = new UserReceiverDTO("newName", "Surname");

        given(userRepository.save(any())).willReturn(user);

        testedUserService.updateUser(user.getId(), newUser);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(user.getName(), userArgumentCaptor.getValue().getName());
        assertEquals(user.getSurname(), userArgumentCaptor.getValue().getSurname());
    }

    @Test
    void deleteUser_method_test() {
        testedUserService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test()
    void getUser_throws_exception() {
        given(userRepository.findById("3L")).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> testedUserService.getUser("3L"));
    }

    @Test
    void findUserBySurveyId_throws_exception() {
        given(userRepository.findOne((UserSpecification) any())).willReturn(Optional.empty());
        long surveyId = survey.getId();
        assertThrows(NotFoundException.class, () -> testedUserService.getUserBySurveyId(surveyId));
    }

    @Test
    void updateUser_throws_exception() {
        given(userRepository.findById("3L")).willReturn(Optional.empty());
        UserReceiverDTO userReceiverDTO = new UserReceiverDTO("test", "test");

        assertThrows(NotFoundException.class, () -> testedUserService.updateUser("3L", userReceiverDTO));
    }

    @Test
    void login() {
        given(userRepository
                .findOne(any(Specification.class)))
                .willReturn(Optional.of(user));

        assertEquals(userMapper.map(user), testedUserService.login(user.getName(), user.getSurname()));
    }

    @Test
    void loginThrowsNotFoundException() {
        String name = user.getName();
        String surname = user.getSurname();
        given(userRepository
                .findOne(any(Specification.class)))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> testedUserService.login(name, surname))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Couldn't find user with name %s".formatted(user.getName()));
    }
}
