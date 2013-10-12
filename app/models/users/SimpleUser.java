package models.users;

import play.*;
import play.db.jpa.*;
import play.libs.*;
import play.data.validation.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class SimpleUser extends Model {
	// Register Info
	public String email;
	public String passwordHash;
	public String needConfirmation;
	public String passwordConfirmation;
	public String profile;
	
	// User Detail

	public String name;
	public String contract;
	public String selfIntro;
	
	// User Detail

	public String school;
	public String hometown;
	public String gender;
	public int age;
	public String ralationship;
	public boolean isAdmin;


	public SimpleUser(String email, String password, String name) {
		this.email = email;
		this.name = name;
		this.passwordHash = Codec.hexMD5(password);
		this.needConfirmation = Codec.UUID();
		this.profile = "/public/images/user_default.jpg";
		create();
	}
	
	public boolean isAdmin() {
        return  email.equals(Play.configuration.getProperty("forum.adminEmail", ""));
    }
	public static SimpleUser findByRegistrationUUID(String uuid) {
		return find("needConfirmation", uuid).first();
	}
	public static SimpleUser findByResetPasswordUUID(String uuid) {
		return find("passwordConfirmation", uuid).first();
	}

	public boolean checkPassword(String password) {
		return passwordHash.equals(Codec.hexMD5(password));
	}
	public void  changePassword(String password){
		this.passwordHash = Codec.hexMD5(password);
		save();
	}
	public void changeProfile(String path){
		this.profile = path;
		save();
	}
	public static SimpleUser findByEmail(String email) {
		return find("email", email).first();
	}

	public static boolean isEmailAvailable(String email) {
		return findByEmail(email) == null;
	}
	public  void changeEmail(String email){
		this.email = email;
		save();
	}


}
