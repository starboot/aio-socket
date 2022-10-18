package io.github.mxd888.socket.intf;

public abstract class AioHandler implements Handler, IProtocol{

    private AioHandler nextHandler;

    public AioHandler Next() {
        return this.nextHandler;
    }

    public void setNext(AioHandler handler) {
        this.nextHandler = handler;
    }
}
