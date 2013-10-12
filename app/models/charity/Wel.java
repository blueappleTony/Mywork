package models.charity;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;
@Entity
public class Wel extends Model {
	public String title;
	public String content;
	public String time;
	public String f;
	public String generalize;
    public int likerCount;
	 
	public Wel(String title,String content,String time,String f,String generalize, int likerCount) {
		
		this.title = title;
		this.content =content;
		this.time = time;
		this.f=f;
		this.generalize = generalize;
		this.likerCount = likerCount;
		 
	 	create();
	}
  
	  
   
	
	

 
}
