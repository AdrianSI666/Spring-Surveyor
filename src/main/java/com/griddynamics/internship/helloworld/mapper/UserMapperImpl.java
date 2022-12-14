package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import org.springframework.stereotype.Component;

/**
 * This class is used to map answer entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class UserMapperImpl implements Mapper<User, UserSenderDTO> {
    @Override
    public UserSenderDTO map(User source) {
        String id = source.getId();
        String name = source.getName();
        String surname = source.getSurname();
        return new UserSenderDTO(id, name, surname);
    }
}
