package io.github.mxd888.socket.utils.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class AioCallerRunsPolicy extends CallerRunsPolicy {
    private static final Logger log = LoggerFactory.getLogger(AioCallerRunsPolicy.class);

    public AioCallerRunsPolicy() {
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.error(r.getClass().getSimpleName());
        super.rejectedExecution(r, e);
    }

}
