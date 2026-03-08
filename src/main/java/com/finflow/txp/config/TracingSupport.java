package com.finflow.txp.config;

import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

@Component
public class TracingSupport {

    private final Tracer tracer;

    public TracingSupport(Tracer tracer) {
        this.tracer = tracer;
    }

    public String currentTraceId() {
        return tracer.currentSpan() == null ? null : tracer.currentSpan().context().traceId();
    }
}
