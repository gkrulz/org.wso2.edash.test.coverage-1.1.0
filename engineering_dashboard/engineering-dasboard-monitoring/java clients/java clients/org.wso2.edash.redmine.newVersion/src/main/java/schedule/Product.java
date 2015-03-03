package schedule;

import java.util.ArrayList;

public class Product {
	private String name;
	private String id;
	private String identifier;
    // make this private
    public ArrayList<Release> release = new ArrayList<Release>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String toString(){
        String out;
        out = "Project Name : " + name + "\nProject ID : " + id+ "\nProject Identifier : " + identifier;
        // Add Issues to this also
        return out;
    }


}
