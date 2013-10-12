package models.qa;

import java.awt.Label;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Ques extends Model {
	@Required
	public String title;
	@Required
	public String content;
	@Required
	public String label;
	public String date;
	public String school;
	public long userid;
	public String usertype;
	public String username;
	public String userprofile;
	public long  answerNum;
	public long  focusNum;
	public String selfIntro;

	public Ques(String title, String Theme, String school, String content,
			String date, long userid, String usertype, String username,
			String userprofile,long  answerNum,long  focusNum,String selfIntro) {
		this.title = title;
		this.content = content;
		this.label = Theme;
		this.school = school;
		this.date = date;
		this.userid = userid;
		this.usertype = usertype;
		this.username = username;
		this.userprofile = userprofile;
		this.answerNum = answerNum;
		this.focusNum = focusNum;
		this.selfIntro =selfIntro;
		create();
	}

	public static Ques findByTitle(String title) {
		return find("title", title).first();
	}

	public static boolean isTitle(String title) {
		return findByTitle(title) == null;
	}
	public static Ques findByContent(String content) {
		return find("content", content).first();
	}

	public static boolean isContent(String content) {
		return findByContent(content) == null;
	}

	public static Ques findBySchool(String school) {
		return find("school", school).first();
	}

	public static boolean isSchool(String school) {
		return findBySchool(school) == null;
	}
	
	public static Ques findByLabel(String label) {
		return find("label", label).first();
	}

	public static boolean islabel(String label) {
		return findByLabel(label) == null;
	}
}
