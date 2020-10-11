package com.sc.clients.manager;

import java.util.Set;

public interface IClientOperations {

    boolean register(int clientId);

    boolean follow(int clientId, int followerId);

    boolean unFollow(int clientId, int followerId);

    Set<Integer> followers(int clientId);
}
