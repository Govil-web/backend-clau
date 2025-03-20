package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.bd.clavecompas.entities.RolEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.UserRolEntity;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.UserRepository;
import com.proyectofinal.clave_compas.bd.clavecompas.repositories.UserRolRepository;
import com.proyectofinal.clave_compas.controller.requests.UserRolRequest;
import com.proyectofinal.clave_compas.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRolService {

    private final UserRolRepository userRolRepository;
    private final UserRepository userRepository;
    private final RolService rolService;

    @Transactional(transactionManager = "txManagerClavecompas", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class})
    public void save(UserRolEntity userRolEntity) {
        userRolRepository.save(userRolEntity);
    }

    @Transactional(transactionManager = "txManagerClavecompas", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class})
    public boolean assignRoles(UserRolRequest userRolRequest) {
        UserEntity user = userRepository.findById(userRolRequest.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con ID " + userRolRequest.getIdUser() + " no existe."));

        Set<Long> newRolsId = userRolRequest.getIdsRol();
        Set<UserRolEntity> existingUserRoles = userRolRepository.findByUserId(user.getId());
        Map<Long, UserRolEntity> existingRolesMap = existingUserRoles.stream()
                .collect(Collectors.toMap(ure -> ure.getRole().getId(), ure -> ure));

        Set<UserRolEntity> userRolsToUpdate = new HashSet<>();

        if (!newRolsId.isEmpty()) {
            newRolsId.forEach(rolId -> {
                UserRolEntity userRol = existingRolesMap.get(rolId);
                if (userRol != null) {
                    if (!userRol.getEnable()) {
                        userRol.setEnable(true);
                        userRolsToUpdate.add(userRol);
                    }
                    existingRolesMap.remove(rolId);
                } else {
                    RolEntity rolexist = rolService.findById(rolId);
                    userRolsToUpdate.add(newUserRol(user, rolexist.getId()));
                }
            });
            existingRolesMap.values().forEach(ure -> {
                ure.setEnable(false);
                userRolsToUpdate.add(ure);
            });
        } else {
            existingUserRoles.forEach(ure -> ure.setEnable(false));
            userRolsToUpdate.addAll(existingUserRoles);
        }

        if (!userRolsToUpdate.isEmpty()) {
            userRolRepository.saveAll(userRolsToUpdate);
        }

        return true;
    }

    private UserRolEntity newUserRol(UserEntity user, Long rolId) {
        return UserRolEntity.builder()
                .role(RolEntity.builder().id(rolId).build())
                .user(user)
                .enable(true)
                .build();
    }

}
