package com.doul.dealz.services;

import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.mapper.UserMapper;
import com.doul.dealz.model.dto.request.UserRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDTO createUser (UserRequestDTO userRequestDTO) {
        User user = userMapper.toUserEntity(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));

        if (userRepository.findByEmail(userRequestDTO.email()).isPresent()){
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        if(userRepository.findByTelephone(userRequestDTO.telephone()).isPresent()){
            throw new IllegalArgumentException("Cet numero est déjà utilisé.");
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponseDTO> getAllUsers () {
        List<User> user = userRepository.findAll();
        return user.stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponseDTO getAnUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Utilisateur introuvable")
        );
        return userMapper.toUserResponse(user);
    }

    public UserResponseDTO updateUser(String id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Utilisateur introuvable")
        );

        user.setNom(userRequestDTO.nom());
        user.setPrenom(userRequestDTO.prenom());
        user.setTelephone(userRequestDTO.telephone());
        user.setEmail(userRequestDTO.email());
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));

        User updatedUser = userRepository.save(user);

        return userMapper.toUserResponse(updatedUser);
    }

    public boolean supprimeUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Utilisateur introuvable")
        );

        userRepository.delete(user);
        return true;
    }
}
