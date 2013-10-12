package models.activity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class ActivityComment extends Model {
    public long pid; //poster id
    public String content;
}
