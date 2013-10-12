package models.activity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class ActivityJoiner extends Model {
	public long aid;
	public long jid;
	   
}
