package com.sc.messaging.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Record<S, U> {

    private S key;
    private U value;

}
