package cbrowne.Courser.service;

import cbrowne.Courser.models.UserModel;
import cbrowne.Courser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserModelDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserModelDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            var userobj = user.get();
            return User.builder()
                    .username(userobj.getUsername())
                    .password(userobj.getPassword())
                    .roles(getRoles(userobj))
                    .build();
        } else{
            throw new UsernameNotFoundException("Username not found");
        }
    }

    private String[] getRoles(UserModel user){
        if(user.getRole() == null){
            return new String[]{"USER"};
        }
        return user.getRole().split(",");
    }

}
