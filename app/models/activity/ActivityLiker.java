package models.activity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class ActivityLiker extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long aid;
	public long lid;
	public String ltype;
}
