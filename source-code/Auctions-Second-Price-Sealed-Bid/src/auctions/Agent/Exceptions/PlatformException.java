package auctions.Agent.Exceptions;

public class PlatformException extends RuntimeException {
    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable throwable) {
        super(message, throwable);
    }
}