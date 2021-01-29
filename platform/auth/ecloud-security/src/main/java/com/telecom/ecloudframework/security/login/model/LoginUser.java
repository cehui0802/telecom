package com.telecom.ecloudframework.security.login.model;

import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import com.telecom.ecloudframework.org.api.model.dto.UserDTO;
import com.telecom.ecloudframework.org.api.model.dto.UserRoleDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * @author who
 */
public class LoginUser extends UserDTO implements UserDetails {


    private Collection<? extends GrantedAuthority> roles = new ArrayList<>();
    /**
     * 单体角色列表
     */
    private List<IUserRole> singleRoles;

    /**
     *
     */
    private static final long serialVersionUID = 4962121675615445357L;

    public LoginUser() {
        super();
    }

    public LoginUser(IUser user) {
        this.account = user.getAccount();
        this.email = user.getEmail();
        this.fullname = user.getFullname();
        this.id = user.getUserId();
        this.mobile = user.getMobile();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.weixin = user.getWeixin();
        this.telephone = user.getTelephone();
        this.type = user.getType();
        this.managerGroupIdList = user.getManagerGroupIdList();
        this.orgId = user.getOrgId();
        this.orgName = user.getOrgName();
        this.orgCode = user.getOrgCode();
        this.postId = user.getPostId();
        this.postName = user.getPostName();
        this.postCode = user.getPostCode();
        Set<GrantedAuthority> setGrantedAuthority = new HashSet<>();
        for (IUserRole userRole : user.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.getAlias());
            setGrantedAuthority.add(grantedAuthority);
        }
        setAuthorities(setGrantedAuthority);
    }

    /**
     * 设置角色。
     *
     * @param roles 角色集合
     */
    public void setAuthorities(Collection<? extends GrantedAuthority> roles) {
        List<IUserRole> lstUserRoleDTO = new ArrayList<>();
        roles.forEach(role -> lstUserRoleDTO.add(new UserRoleDTO("", "", role.getAuthority())));
        setRoles(lstUserRoleDTO);
        this.roles = roles;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles;
    }


    @Override
    public String getUsername() {
        return this.getAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        int status = this.getStatus();
        return status == 1;
    }

    @Override
    public List<IUserRole> getRoles() {
        return singleRoles;
    }

    @Override
    public void setRoles(List<IUserRole> roles) {
        this.singleRoles = roles;
    }
}
