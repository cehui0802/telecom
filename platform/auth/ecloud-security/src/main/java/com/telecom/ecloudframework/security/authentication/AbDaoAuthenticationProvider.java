package com.telecom.ecloudframework.security.authentication;

import com.telecom.ecloudframework.org.api.model.dto.UserDTO;
import com.telecom.ecloudframework.security.authentication.api.MultipleFromAuthentication;
import com.telecom.ecloudframework.security.login.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 重写根据系统来源登录
 *
 * @author wacxhs
 */
public class AbDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(AbDaoAuthenticationProvider.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, MultipleFromAuthentication> multipleFromAuthenticationBeanMap;

    /**
     * 将来源转换为小写，在匹配时不区分大小写
     *
     * @param from 账户来源
     * @return 小写字符来源
     */
    private String fromToLowerCase(String from) {
        return StringUtils.lowerCase(from);
    }

    private MultipleFromAuthentication getMultipleFromAuthenticationImplement(String from) {
        if (multipleFromAuthenticationBeanMap == null) {
            synchronized (this) {
                if (multipleFromAuthenticationBeanMap == null) {
                    Collection<MultipleFromAuthentication> multipleFromAuthenticationBeans = applicationContext.getBeansOfType(MultipleFromAuthentication.class).values();
                    multipleFromAuthenticationBeanMap = new HashMap<>(multipleFromAuthenticationBeans.size());
                    for (MultipleFromAuthentication multipleFromAuthenticationBean : multipleFromAuthenticationBeans) {
                        multipleFromAuthenticationBeanMap.put(fromToLowerCase(multipleFromAuthenticationBean.getFrom()), multipleFromAuthenticationBean);
                    }
                }
            }
        }
        return multipleFromAuthenticationBeanMap.get(fromToLowerCase(from));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken
            authentication) throws AuthenticationException {
        if (!(userDetails instanceof LoginUser)) {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } else {
            LoginUser loginUser = (LoginUser) userDetails;
            if (StringUtils.isEmpty(loginUser.getFrom()) || UserDTO.FROM_SYSTEM.equalsIgnoreCase(loginUser.getFrom())) {
                super.additionalAuthenticationChecks(userDetails, authentication);
            } else {
                multipleFromAuthentication(loginUser, authentication);
            }
        }
    }

    private void multipleFromAuthentication(LoginUser userDetails, UsernamePasswordAuthenticationToken
            authentication) throws AuthenticationException {
        MultipleFromAuthentication multipleFromAuthentication = getMultipleFromAuthenticationImplement(userDetails.getFrom());
        if (multipleFromAuthentication == null) {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } else {
            multipleFromAuthentication.authentication(userDetails, authentication);
        }
    }
}
