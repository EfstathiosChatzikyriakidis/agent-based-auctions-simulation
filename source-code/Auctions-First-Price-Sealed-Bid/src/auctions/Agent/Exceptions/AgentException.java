package auctions.Agent.Exceptions;

public class AgentException extends RuntimeException {
    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}