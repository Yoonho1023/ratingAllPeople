package com.rap.app.user.application;

import com.rap.app.user.domain.model.User;
import com.rap.app.user.domain.repository.UserRepository;
import com.rap.app.user.rest.dto.SignInRequest;
import com.rap.config.exception.ValidIdException;
import com.rap.support.messages.PreparedMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signIn(SignInRequest request) {

    }

    public void idDuplicate(String id) throws ValidIdException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidIdException(PreparedMessages.OK_TO_USE_THIS_ID));
    }

}
