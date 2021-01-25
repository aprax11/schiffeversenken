package network;

public interface TCPStreamCreatedListener {
    /**
     * creates a TCP stream as listener
     * @param channel listener object
     */
    void streamCreated(TCPStream channel);
}
