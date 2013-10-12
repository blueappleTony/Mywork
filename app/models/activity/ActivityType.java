package models.activity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class ActivityType extends Model {
	public String name;
    public short sequence;
}
