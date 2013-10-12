package models.charity;

import javax.persistence.Entity;

import play.db.jpa.Model;


@Entity
public class welLiker extends Model {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long aid;
    public long lid;
    public String ltype;
}
