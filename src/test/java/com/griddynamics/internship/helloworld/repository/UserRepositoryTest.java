package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.mother.UserMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractContainerBaseTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void givenUser_whenSave_thenReturnSavedUser() {
        User user = UserMother.basic()
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(savedUser.getName(), user.getName());
        Assertions.assertEquals(savedUser.getSurname(), user.getSurname());
    }
}
