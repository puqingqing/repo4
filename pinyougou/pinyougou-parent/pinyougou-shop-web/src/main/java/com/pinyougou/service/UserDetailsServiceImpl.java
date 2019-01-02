package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	  private SellerService sellerService;
	  
	
	public void setSellerService(SellerService sellerService) {
		
		this.sellerService = sellerService;
	}

		@Override
		public UserDetails loadUserByUsername(String username) 
				throws UsernameNotFoundException {
			
			System.out.println("经过了 UserDetailsServiceImpl");
			// TODO Auto-generated method stub
			List<GrantedAuthority> grant=new ArrayList<>();
			 grant.add(new SimpleGrantedAuthority("ROLE_SELLER"));
			TbSeller seller = sellerService.findOne(username);
		
			if(seller!=null) {
			   if(seller.getStatus().equals("1")) {
				return new User(username, seller.getPassword(), grant);
			}
					
		}
			
		     return null;	
	

	}
}
