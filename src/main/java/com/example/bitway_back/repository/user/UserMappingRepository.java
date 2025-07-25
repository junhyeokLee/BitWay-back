package com.example.bitway_back.repository.user;

import com.example.bitway_back.domain.user.UserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserMappingRepository extends JpaRepository<UserMapping, String> {
    Optional<UserMapping> findByUuid(String uuid);

    @Query(value = " SELECT " +
                   "    um.uuid " +
                   " FROM UserMapping um " +
                   " WHERE um.userId = :userId ", nativeQuery = true)
    String findUuidByUserId(String userId);
}
