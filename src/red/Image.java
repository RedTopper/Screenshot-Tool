package red;

import java.io.File;
import java.util.regex.Matcher;

public class Image {
	public static final String DELEM = "-";
	public static final String TYPE = "png";
	
	private Type type = Type.UNKNOWN;
	private File file;
	private String id = "";
	private boolean right = false;
	private boolean bottom = false;
	
	public Image(File file) {
		if (file.isDirectory()) return;
		String name = file.getName();
		
		//Try and match any type
		for (Type type : Type.values()) {
			if (type.IS == null) continue;
			Matcher match = type.IS.matcher(name);
			if (!match.find()) continue;
			
			//We matched a type, now extract the "id"
			String delem = "";
			for (int i : type.GROUP) {
				this.id += delem + match.group(i);
				delem = DELEM;
			}
			
			//Set data
			if (type.RIGHT != null) this.right = type.RIGHT.matcher(name).find();
			this.bottom = type.BOT.matcher(name).find();
			this.type = type;
			this.file = file;
			
			//Exit after match
			break;
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getId() {
		return id;
	}
	
	public String getOutputName() {
		return type.toString().toLowerCase() + DELEM + id + "." + TYPE;
	}
	
	public boolean isRight() {
		return right;
	}
	
	public boolean isBottom() {
		return bottom;
	}
}
