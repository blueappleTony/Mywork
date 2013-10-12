package models.airport;

import java.util.*;

import play.mvc.*;
import play.*;
import models.*;

import javax.persistence.*;

import play.db.jpa.Model;
import play.Logger;
import play.Play;
import play.data.validation.*;
import play.libs.Codec;
import play.libs.Crypto;


@Entity
public class VolInfo extends Model {
	public String name;
	public String gender;
	public String email;
	public long phone;
	public String car;
	public String date;
	public String remarks;
	public String school;
	public String needConfirmation;
	
}
