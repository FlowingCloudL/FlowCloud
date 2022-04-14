package com.fcl.flowRPC.example.api;

import java.util.List;

public interface UserService {

    UserEntity queryById(int id);
    List<UserEntity> queryList();
    void add(UserEntity user);
    void delete(int id);
}
