package security;

import models.entitys.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repos.UserRepo;

@Service
public class MyUserDetails implements UserDetailsService {

  private final UserRepo userRepository;

  public MyUserDetails(UserRepo userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final User user = userRepository.findUserByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("User " + username + " Not found");
    }

    return org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.getPassword())
            .authorities(user.getRoles())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
  }

}
