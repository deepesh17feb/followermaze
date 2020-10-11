package com.sc.clients.manager;

import com.sc.clients.constants.AppConstants;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceManager {

    private static Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    static {
        instanceMap.put(AppConstants.CLIENT_FOLLOWER_MANAGER, new ClientFollowerManager());
    }

    public static Object getInstanceUsingName(String objName) {
        return Optional.ofNullable(instanceMap.get(objName)).orElseGet(null);
    }
}
