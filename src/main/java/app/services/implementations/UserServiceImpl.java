package app.services.implementations;

import app.ExceptionHandler.exceptions.ResourceNotFoundException;
import app.models.User;
import app.repositories.UserRepository;
import app.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Andrey Nazarov on 7/27/2018.
 */
@Service
@Primary
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findAll()
                .stream().filter(u -> Objects.equals(u.getName(), username)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    public User create(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User edit(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public List<User> findAll(Pageable page) {
        return this.userRepository.findAllUsers(page);
    }
}
