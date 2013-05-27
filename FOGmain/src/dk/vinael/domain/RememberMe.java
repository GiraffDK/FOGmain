package dk.vinael.domain;

import java.io.Serializable;

public class RememberMe implements Serializable {
	public String email;
	public String password;
	
	public String toString(){
		return email + "." + password;
	}
}
