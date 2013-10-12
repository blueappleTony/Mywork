package models.qa;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Comments extends Model {

	public long quesid;
	public String comment;
	public long praiseNum;
	public long userid;
	public String usertype;
	public String username;
	public String userprofile;
	public String userSelfIntro;
	public String date;
	public String quesTitle;

	public Comments(long quesid, String comment, long praiseNum, long userid,
			String usertype, String username, String userprofile,
			String userSelfIntro, String date, String quesTitle) {
		this.comment = comment;
		this.praiseNum = praiseNum;
		this.quesid = quesid;
		this.userid = userid;
		this.usertype = usertype;
		this.username = username;
		this.userprofile = userprofile;
		this.userSelfIntro = userSelfIntro;
		this.date = date;
		this.quesTitle = quesTitle;
		create();
	}
}
