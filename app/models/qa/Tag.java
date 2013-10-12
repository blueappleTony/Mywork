package models.qa;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Tag extends Model {
	public String tagName;
	public Long themeid;
	public Tag(String tagName,Long themeid){
		this.tagName =tagName;
		this.themeid = themeid;
		create();
	}
}
