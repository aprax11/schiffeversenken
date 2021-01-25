package network;

/**
 * call back interface
 */
public interface GameSessionEstablishedListener {
    /**
     * is called when oracle was created
     * @param oracle
     * @param partnerName
     */
    void gameSessionEstablished(boolean oracle, String partnerName);
}
