package com.sc.clients.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppConfig {

    private String host;
    private int port;
    private int threads;
    private String mode;
}
