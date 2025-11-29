// package apap.ti._5.vehicle_rental_2306203236_be.security.service;

// import java.util.HashSet;
// import java.util.Set;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import apap.ti._5.tour_package_2306219575_be.model.EndUser;
// import apap.ti._5.tour_package_2306219575_be.repository.UserDb;

// @Service
// public class UserDetailsServiceImpl implements UserDetailsService {

//     @Autowired
//     private UserDb userProfileRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         EndUser user = userProfileRepository.findByUsername(username);

//         Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
//         grantedAuthoritySet.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));

//         return new User(user.getUsername(), user.getPassword(), grantedAuthoritySet);
//     }
// }