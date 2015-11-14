package red; 

@SuppressWarnings("serial")
public class UnmatchedException extends Exception {
	public UnmatchedException() {
		super("Images are not compatable");
	}
}