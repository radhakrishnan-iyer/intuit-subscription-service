package com.intuit.processor;

import com.intuit.common.model.mq.Event;

public interface IRequestProcessor {
    boolean process(Event event);
}
