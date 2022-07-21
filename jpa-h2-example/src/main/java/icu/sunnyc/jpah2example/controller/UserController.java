package icu.sunnyc.jpah2example.controller;

import icu.sunnyc.jpah2example.entity.UserEntity;
import icu.sunnyc.jpah2example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author houcheng
 * @version V1.0
 * @date 2022/7/20 15:35:57
 */
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String listUser() {
        List<UserEntity> users = userRepository.findAll();
        return users.toString();
    }

    @PostMapping("/save")
    public String saveUser(@RequestBody UserEntity userEntity) {
        return userRepository.save(userEntity).toString();
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody UserEntity userEntity) {
        return userRepository.save(userEntity).toString();
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userRepository.deleteById(id);
        return "success!";
    }
}
