package models.activity;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class ActivityPeriod extends Model {
	 @Required
	 public String period;
	 public short days;
	 public short sequence;
    
}
