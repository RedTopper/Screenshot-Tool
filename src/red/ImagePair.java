package red; 

public class ImagePair {
	String image1;
	String image2;
	int number;
	boolean left = true;
	boolean isNTR = false;
	boolean init = false;
	public ImagePair(String image1, String image2) throws UnmatchedException, NumberFormatException{
		if(image1 == null || image2 == null || image1.equals("null") || image2.equals("null")) {
			System.out.println("Trying to trick me with null images?!");
			throw new UnmatchedException();
		}
		if(image1.substring(0,4).equals("scr_") && image2.substring(0,4).equals("scr_")) {
			if (image1.substring(0,image1.indexOf("_", 4))
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
				if(image1.contains("RIGHT") || image2.contains("RIGHT")) {
					left = false;
				}
				init = true; //nice.
			} 
		} else if((image1.substring(0,3).equals("top") || image1.substring(0,3).equals("bot")) && 
		   (image2.substring(0,3).equals("top") || image2.substring(0,3).equals("bot"))) { //extra check for ntr since I don't want it matching ninjahax ones.
			if (image1.substring(4,8)
					.equals(image2.substring(4,8)) &&
			   !image1.substring(0,image1.indexOf("_"))
					.equals(image2.substring(0,image2.indexOf("_")))) {
				this.isNTR = true;
				this.image1 = image1;
				this.image2 = image2;
				try{
					number = Integer.parseInt(image1.substring(4,8));
				} catch (NumberFormatException e) {
					throw e;
				}
				init = true; //nice.
			}
		} else {
			throw new UnmatchedException();
		} 
	}
	
	public String toString() {
		return (isNTR ? "NTR image," : "Ninjahax image,") + " First: " + image1 + ", Second: " + image2 + ", Number: " + number;
	}
	
	public boolean equals(ImagePair pair2) {
		return this.number == pair2.getNumber() && this.left == pair2.isLeft() && this.isNTR == pair2.isNTR();
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
	
	public boolean isNTR() {
		return isNTR;
	}
	
	public boolean isReal() {
		return init; //not quite sure how one can get through without being real but...
	}
}
