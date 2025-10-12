package guru.qa.rococo.ex;

public class PaintingNotFoundException extends RuntimeException {
  public PaintingNotFoundException(String message) {
    super(message);
  }
}
