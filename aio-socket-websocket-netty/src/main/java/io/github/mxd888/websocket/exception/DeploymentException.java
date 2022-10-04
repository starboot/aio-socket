package io.github.mxd888.websocket.exception;

/**
 * Created by DELL(mxd) on 2022/8/9 15:22
 */
public class DeploymentException extends Exception {

    private static final long serialVersionUID = 1804688192666730149L;

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}