package com.spacecodee.securityspacee.role.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.role.adapter.mapper.IRoleRestMapper;
import com.spacecodee.securityspacee.role.adapter.mapper.impl.RoleRestMapperImpl;
import com.spacecodee.securityspacee.role.application.mapper.IRoleResponseMapper;
import com.spacecodee.securityspacee.role.application.mapper.impl.RoleResponseMapperImpl;
import com.spacecodee.securityspacee.role.application.port.in.ICreateRoleUseCase;
import com.spacecodee.securityspacee.role.application.port.in.IGetRoleHierarchyUseCase;
import com.spacecodee.securityspacee.role.application.service.RoleHierarchyService;
import com.spacecodee.securityspacee.role.application.usecase.CreateRoleUseCase;
import com.spacecodee.securityspacee.role.application.usecase.GetRoleHierarchyUseCase;
import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;
import com.spacecodee.securityspacee.role.domain.service.RoleHierarchyValidator;
import com.spacecodee.securityspacee.role.infrastructure.persistence.RolePersistenceAdapter;
import com.spacecodee.securityspacee.role.infrastructure.persistence.jpa.SpringJpaRoleRepository;
import com.spacecodee.securityspacee.role.infrastructure.persistence.mapper.IRolePersistenceMapper;
import com.spacecodee.securityspacee.role.infrastructure.persistence.mapper.impl.RolePersistenceMapperImpl;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RoleConfigurationProperties.class)
public class RoleBeanConfiguration {

    @Bean
    public IRolePersistenceMapper rolePersistenceMapper() {
        return new RolePersistenceMapperImpl();
    }

    @Bean
    public IRoleResponseMapper roleResponseMapper() {
        return new RoleResponseMapperImpl();
    }

    @Bean
    public IRoleRepository roleRepository(
            SpringJpaRoleRepository springJpaRoleRepository,
            IRolePersistenceMapper persistenceMapper) {
        return new RolePersistenceAdapter(springJpaRoleRepository, persistenceMapper);
    }

    @Bean
    public RoleHierarchyValidator roleHierarchyValidator(IRoleRepository roleRepository) {
        return new RoleHierarchyValidator(roleRepository);
    }

    @Bean
    public RoleHierarchyService roleHierarchyService(IRoleRepository roleRepository) {
        return new RoleHierarchyService(roleRepository);
    }

    @Bean
    public ICreateRoleUseCase createRoleUseCase(
            IRoleRepository roleRepository,
            IRoleResponseMapper roleResponseMapper,
            ApplicationEventPublisher eventPublisher,
            RoleConfigurationProperties roleProperties) {
        return new CreateRoleUseCase(roleRepository, roleResponseMapper, eventPublisher, roleProperties);
    }

    @Bean
    public IGetRoleHierarchyUseCase getRoleHierarchyUseCase(IRoleRepository roleRepository) {
        return new GetRoleHierarchyUseCase(roleRepository);
    }

    @Bean
    public IRoleRestMapper roleRestMapper() {
        return new RoleRestMapperImpl();
    }
}
