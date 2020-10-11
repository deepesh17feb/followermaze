package com.sc.clients.strategy;

import com.sc.clients.exceptions.EventProcessingException;

/**
 * Templating methods which will be required to execute a strategy
 *
 * @param <S>
 * @param <T>
 */
public interface IEventStrategy<S, T> {

    T parse(S msg);

    void process(S msg) throws EventProcessingException;

}

