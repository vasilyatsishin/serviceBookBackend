package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.JWTEntity;
import com.example.serviceBookBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JWTRepository extends JpaRepository<JWTEntity, Integer> {

    @Modifying
    JWTEntity deleteByToken(String token);

    void deleteByUserId(UserEntity userId);

    Optional<JWTEntity> findByToken(String token);
}
