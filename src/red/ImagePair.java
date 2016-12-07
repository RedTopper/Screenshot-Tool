package red; 

public class ImagePair {
	String image1;
	String image2;
	String number;
	Type type = Type.UNKNOWN;
	boolean left = true;
	
	enum Type {
		UNKNOWN,
		NINJAHAX,
		NTR, 
		TESTMENU
	}
	
	public ImagePair(String image1, String image2) throws UnmatchedException, IndexOutOfBoundsException{
		if(image1 == null || image2 == null || image1.equals("null") || image2.equals("null")) {
			System.out.println("Trying to trick me with null images?!");
			throw new UnmatchedException();
		}
		
		//Check if NINJAHAX
		String img1pre, img2pre;
		img1pre = image1.substring(0,4);
		img2pre = image2.substring(0,4);
		if(img1pre.equals("scr_") && img2pre.equals("scr_")) { //NINJAHAX universal prefix
			if (image1.substring(0,image1.indexOf("_", 4)).equals(image2.substring(0,image2.indexOf("_", 4))) && //Check if number matches
			   !image1.substring(image1.indexOf("_",4)).equals(image2.substring(image2.indexOf("_",4)))){ //And that types are different
				this.image1 = image1;
				this.image2 = image2;
				this.number = image1.substring(image1.indexOf("_") + 1,image1.indexOf("_", 4));
				this.left = image1.contains("LEFT") || image2.contains("LEFT");
				if(this.left && image1.contains("RIGHT") || image2.contains("RIGHT")) throw new UnmatchedException(); //Other image cannot be right if one is left!
				this.type = Type.NINJAHAX;
				return;
			} 
		} 
		
		//Check if NTR
		img1pre = image1.substring(0,3);
		img2pre = image2.substring(0,3);
		if (((img1pre.equals("top") || img1pre.equals("bot")) && (img2pre.equals("top") || img2pre.equals("bot"))) || //NTR English prefix
			((img1pre.equals("sup") || img1pre.equals("inf")) && (img2pre.equals("sup") || img2pre.equals("inf"))) ) { //NTR Spanish prefix
			if (image1.substring(4,8).equals(image2.substring(4,8)) && //Check if number matches
			   !image1.substring(0,image1.indexOf("_")).equals(image2.substring(0,image2.indexOf("_")))) {
				this.image1 = image1;
				this.image2 = image2;
				this.number = image1.substring(4,8);
				this.type = Type.NTR;
				return;
			}
		} 
		
		//check if TESTMENU
		String date1 = image1.substring(0, 8); //The date part (first 8 letters)
		String date2 = image2.substring(0, 8);
		String time1 = image1.substring(9, 13); //The time part (After first _, 4 letters)
		String time2 = image2.substring(9, 13);
		String number1 = image1.substring(17, 22); //The number part (After _UL_, _UR_, or _LO_, 5 letters)
		String number2 = image2.substring(17, 22);
		
		//If any of this fails, an exception will be thrown.
		Integer.parseInt(date1);
		Integer.parseInt(date2);
		Integer.parseInt(time1);
		Integer.parseInt(time2);
		Integer.parseInt(number1);
		Integer.parseInt(number2);
		if(date1.equals(date2) && time1.equals(time2) && number1.equals(number2)) {
			if(!image1.substring(13, 17).equals(image2.substring(13, 17))) {
				this.image1 = image1;
				this.image2 = image2;
				this.number = date1 + "_" + time1 + "_" + number1;
				this.left = image1.contains("UL") || image2.contains("UL");
				if(this.left && image1.contains("UR") || image2.contains("UR")) throw new UnmatchedException();
				this.type = Type.TESTMENU;
				return;
			}
		}
		
		throw new UnmatchedException();
	}
	
	public String toString() {
		return type.toString() + " image, First: " + image1 + ", Second: " + image2 + ", Number: " + number;
	}
	
	public String getName() {
		return type.toString().toLowerCase() + "_" + number + (left ? "" : "_RIGHT") + ".png";
	}
	
	public boolean equals(ImagePair pair2) {
		return this.number.equals(pair2.getNumber()) && this.left == pair2.isLeft() && this.type.equals(pair2.getType());
	}
	
	public String getNumber() {
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
	
	public Type getType() {
		return type;
	}
}
