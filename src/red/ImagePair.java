package red; 

public class ImagePair {
	String image1;
	String image2;
	int number;
	boolean left = true;
	public ImagePair(String image1, String image2) throws UnmatchedException, NumberFormatException{
		if(image1.substring(0,image1.indexOf("_", 4))
				.equals(image2.substring(0,image2.indexOf("_", 4))) &&
				!image1.substring(image1.indexOf("_",4))
				.equals(image2.substring(image2.indexOf("_",4)))){
			this.image1 = image1;
			this.image2 = image2;
			try{
				number = Integer.parseInt(image1.substring(image1.indexOf("_") + 1,image1.indexOf("_", 4)));
			} catch (NumberFormatException e) {
				throw e;
			}
		} else {
			throw new UnmatchedException();
		}
		if(image1.contains("RIGHT") || image2.contains("RIGHT")) {
			left = false;
		}
	}
	
	public String toString() {
		return "First: " + image1 + ", Second: " + image2 + ", Number: " + number;
	}
	
	public boolean equals(ImagePair pair2) {
		return this.number == pair2.getNumber() && this.left == pair2.isLeft();
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getOne() {
		return image1;
	}
	
	public String getTwo() {
		return image2;
	}
	
	public boolean isLeft() {
		return left;
	}
}
