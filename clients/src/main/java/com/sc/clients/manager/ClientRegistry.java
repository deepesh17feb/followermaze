package com.sc.clients.manager;

import com.sc.clients.io.threads.IOClientThread;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton Store to store threads reference corresponding to a clientId
 * so that it could be used to put new messages in their corresponding exchanges
 */
@Slf4j
public class ClientRegistry {

    private static volatile Map<Integer, List<IOClientThread>> clientThreadMap = new ConcurrentHashMap<>();

    public static void registerClientThread(int clientId, IOClientThread clientThread) {
        synchronized (ClientRegistry.class) {

            if (!clientThreadMap.containsKey(clientId)) {
                List<IOClientThread> clientConnections = new LinkedList<>();
                clientConnections.add(clientThread);

                clientThreadMap.put(clientId, clientConnections);
                log.info("Client Connection Registered :: {}", clientId);
            } else {
                log.debug("ClientId [{}] connection mapping already detected in registry [{}]", clientId,
                        clientThreadMap.get(clientId).stream().map(IOClientThread::getConnectionName).collect(Collectors.toList()));

                List<IOClientThread> clientConnections = clientThreadMap.get(clientId);

                // Filter to remove null thread references
                List<IOClientThread> updatedConnections = clientConnections.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                // Added new connection
                updatedConnections.add(clientThread);
                clientThreadMap.put(clientId, updatedConnections);
            }

            log.info("ClientId [{}] ->> connection mapped ->> [{}]",
                    clientId,
                    clientThreadMap.get(clientId)
                            .stream().map(IOClientThread::getConnectionName).collect(Collectors.toList()));

        }
    }

    public static List<IOClientThread> locateClientThreadUsingId(Integer clientId) {
        return Optional.ofNullable(clientThreadMap.get(clientId)).orElseGet(ArrayList::new);
    }

    public static Map<Integer, List<IOClientThread>> getAllConnectedClient() {
        return Optional.of(clientThreadMap).orElseGet(HashMap::new);
    }

}
