package com.pesto.authmanager.service.impl;

import com.pesto.ecomm.common.lib.entity.User;
import com.pesto.ecomm.common.lib.enums.Role;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
@Qualifier("userDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("Username "+ username+ "doesn't exist");
        }
        if(user.getRole() == Role.SELLER) {
            SimpleGrantedAuthority sellerAuthority = new SimpleGrantedAuthority(Role.SELLER.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(sellerAuthority);
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
        } else if(user.getRole() == Role.BUYER) {
            SimpleGrantedAuthority buyerAuthority = new SimpleGrantedAuthority(Role.BUYER.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(buyerAuthority);
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
        }
        return null;
    }
}
