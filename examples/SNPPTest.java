import jsnpp.Message;
import java.io.IOException;
import java.net.UnknownHostException;

public class SNPPTest {

	public static void main(String[] args) {
		Message m = new Message("despise.nbill.cellcom.com", 444, "don", "don", "testing");
		try {
			m.send();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}
}
