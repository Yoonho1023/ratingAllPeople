package com.rap.app.user.application;

import com.rap.app.profile.interfaces.rest.transform.SignInRequestMapper;
import com.rap.app.user.domain.model.User;
import com.rap.app.user.domain.repository.UserRepository;
import com.rap.app.user.rest.dto.IdDuplicateResponse;
import com.rap.app.user.rest.dto.SignInRequest;
import com.rap.config.exception.DuplicateIdException;
import com.rap.support.messages.PreparedMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SignInRequestMapper signInRequestMapper;

    public String signIn(SignInRequest request) {

        this.isDuplicate(request.getUserId());

        // password 암호화
        // https를 사용한다는 전제 하에 프론트에서 평문으로 보내고 암호화는 백엔드에서
        User user = signInRequestMapper.toEntity(request);

        userRepository.save(user);

        return "success";
    }

    public IdDuplicateResponse idDuplicate(String id) throws DuplicateIdException {
        this.isDuplicate(id);
        return IdDuplicateResponse.builder().message(PreparedMessages.ALLOW_TO_USE_THIS_ID.getMessage()).build();
    }

    private void isDuplicate(String id) {
        userRepository.findById(id).ifPresent(m -> { throw new DuplicateIdException(PreparedMessages.DUPLICATE_ID);});
    }



}
