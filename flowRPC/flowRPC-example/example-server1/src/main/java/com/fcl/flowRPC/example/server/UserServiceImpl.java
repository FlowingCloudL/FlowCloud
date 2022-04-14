package com.fcl.flowRPC.example.server;

import com.fcl.flowRPC.annotation.ServiceConsumer;
import com.fcl.flowRPC.example.api.UserEntity;
import com.fcl.flowRPC.example.api.UserService;

import java.util.ArrayList;
import java.util.List;

@ServiceConsumer
public class UserServiceImpl implements UserService {

    @Override
    public UserEntity queryById(int id) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName("测试对象");
        user.setAge(16);
        return user;
    }

    @Override
    public List<UserEntity> queryList() {
        UserEntity user1 = new UserEntity();
        user1.setId(1);
        user1.setName("测试对象1");
        user1.setAge(16);
        UserEntity user2 = new UserEntity();
        user2.setId(2);
        user2.setName("测试对象2");
        user2.setAge(18);
        UserEntity user3 = new UserEntity();
        user3.setId(3);
        user3.setName("测试对象3");
        user3.setAge(14);
        List<UserEntity> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        return list;
    }

    @Override
    public void add(UserEntity user) {
        System.out.println("增加用户:"+user);
    }

    @Override
    public void delete(int id) {
        System.out.println("删除用户:"+id);
    }
}
