package com.shopEZ.ShopEazzy.repository;

import com.shopEZ.ShopEazzy.model.AppRole;
import com.shopEZ.ShopEazzy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole roleName);
}
