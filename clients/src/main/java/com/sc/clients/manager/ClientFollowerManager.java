package com.sc.clients.manager;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton Class - Multiple threads will access this class
 */
@Slf4j
public class ClientFollowerManager implements IClientOperations {

    private volatile Map<Integer, LinkedHashSet<Integer>> clientFollowersMap = new ConcurrentHashMap<>();

    @Override
    public boolean register(int clientId) {
        synchronized (this) {

            if (clientFollowersMap.containsKey(clientId)) {
                log.info("ClientId [{}] is already registered", clientId);
            } else {
                this.clientFollowersMap.put(clientId, new LinkedHashSet<>());
                log.info("ClientId [{}] ^^^^^ Registered", clientId);
            }

            return true;
        }
    }

    @Override
    public boolean follow(int clientId, int followerId) {
        synchronized (this) {

            if (clientFollowersMap.containsKey(clientId)) {
                LinkedHashSet<Integer> followerSet = clientFollowersMap.get(clientId);
                followerSet.add(followerId);
                this.clientFollowersMap.put(clientId, followerSet);
                log.info("ClientId [{}] ~~~> FOLLOW ~~~> followerId [{}]", clientId, followerId);
                return true;
            } else {
                log.debug("Invalid Followed ClientId [{}] supplied", clientId);
            }

        }
        return false;
    }

    @Override
    public boolean unFollow(int clientId, int followerId) {
        synchronized (this) {

            if (clientFollowersMap.containsKey(clientId)) {
                LinkedHashSet<Integer> followerSet = clientFollowersMap.get(clientId);
                followerSet.remove(followerId);
                this.clientFollowersMap.put(clientId, followerSet);
                log.info("ClientId [{}] ~~~> UN_FOLLOW ~~~> followerId [{}]", clientId, followerId);
                return true;
            } else {
                log.debug("Invalid Followed ClientId [{}] supplied", clientId);
            }

        }
        return false;
    }

    @Override
    public Set<Integer> followers(int clientId) {
        return Optional.ofNullable(clientFollowersMap.get(clientId)).orElseGet(LinkedHashSet::new);
    }
}
