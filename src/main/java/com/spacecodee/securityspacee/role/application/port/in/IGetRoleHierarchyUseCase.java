package com.spacecodee.securityspacee.role.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.application.query.GetRoleHierarchyQuery;
import com.spacecodee.securityspacee.role.application.response.RoleHierarchyResponse;

public interface IGetRoleHierarchyUseCase {

    @NonNull
    RoleHierarchyResponse execute(@NonNull GetRoleHierarchyQuery query);
}
