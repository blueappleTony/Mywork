package models.qa;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class FocusQues extends Model {
	public String userType;
	public Long userid;
	public String userprofile;
	public Long quesId;
	public String quesTitle;

	public FocusQues(String userType, Long userid, String userprofile,
			Long quesId,String quesTitle) {
		this.userid = userid;
		this.userprofile = userprofile;
		this.userType = userType;
		this.quesId = quesId;
		this.quesTitle = quesTitle;
		create();
	}
}
