package guru.qa.rococo.ex;

public class ArtistNotFoundException extends RuntimeException {
  public ArtistNotFoundException(String message) {
    super(message);
  }
}
