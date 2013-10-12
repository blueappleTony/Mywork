package models.airport;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;
@Entity
public class School extends Model{
	@Required
	public String name;
	public String synopsis;
}
