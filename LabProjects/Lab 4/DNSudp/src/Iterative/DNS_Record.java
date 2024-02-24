package Iterative;

public class DNS_Record {
	public String name, value, type;
	public int ttl;
	
	
	public DNS_Record( String name, String value, String type, int ttl ) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.ttl = ttl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue( String value ) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType( String type ) {
		this.type = type;
	}
	
	public int getTtl() {
		return ttl;
	}
	
	public void setTtl( int ttl ) {
		this.ttl = ttl;
	}
	
	@Override
	public String toString() {
		return "Iterative.DNS_Record{" +
				"name='" + name + '\'' +
				", value='" + value + '\'' +
				", type='" + type + '\'' +
				", ttl=" + ttl +
				'}';
	}
}
