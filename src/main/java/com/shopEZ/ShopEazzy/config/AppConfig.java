package com.shopEZ.ShopEazzy.config;

import com.shopEZ.ShopEazzy.model.AppRole;
import com.shopEZ.ShopEazzy.model.Role;
import com.shopEZ.ShopEazzy.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner roleInitializer(RoleRepository roleRepository){
        return args -> {
            for(AppRole role: AppRole.values()){
                if((roleRepository.findByRoleName(role)).isEmpty()){
                    Role newRole = new Role();
                    newRole.setRoleName(role);
                    roleRepository.save(newRole);
                }
            }
        };
    }
}
