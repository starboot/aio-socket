package io.github.mxd888.socket.intf;

/**
 * 抽象处理器，使用责任链模式进行解码
 */
public abstract class AioHandler implements Handler, IProtocol{

    private AioHandler nextHandler;

    public AioHandler Next() {
        return this.nextHandler;
    }

    public void setNext(AioHandler handler) {
        this.nextHandler = handler;
    }
}
