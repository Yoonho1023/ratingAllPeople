package com.rap.app.profile.interfaces.rest.transform;

import com.rap.app.user.application.UserService;
import com.rap.app.user.domain.model.User;
import com.rap.app.user.rest.dto.SignInRequest;
import com.rap.config.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = UserService.class)
public interface SignInRequestMapper extends GenericMapper<SignInRequest, User> {

}
