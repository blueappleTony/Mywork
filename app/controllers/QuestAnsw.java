package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import play.data.validation.Validation;
import models.qa.Attention;
import models.qa.Comments;
import models.qa.FocusQues;
import models.qa.Ques;
import models.qa.QuestionArticle;
import models.qa.Tag;
import models.users.CSSA;
import models.users.SimpleUser;

public class QuestAnsw extends Application {
	private static String label;

	public static void index() {
		render();
	}

	public static void QuesIndex() {
		List<Tag> t = Tag.findAll();
		render(t);
	}

	public static void dispAddQues(String title, String Tag, String content,
			String school, String date, Long id, long answerNum, long focusNum,
			String selfIntro) {
		String usertype = session.get("usertype");
		long userid = Long.parseLong(session.get("logged"));
		String username;
		String userprofile;
		String userselfIntro;
		if (usertype.equals("simple")) {
			SimpleUser simp = SimpleUser.findById(userid);
			username = simp.name;
			userprofile = simp.profile;
			userselfIntro = simp.selfIntro;
		} else {
			CSSA cssa = CSSA.findById(userid);
			username = cssa.name;
			userprofile = cssa.profile;
			userselfIntro = cssa.selfIntro;
		}
		if (!Ques.isTitle(title)) {
			validation.keep();
			params.flash();
			flash.error("错误:请输入标题名");
			QuesIndex();
		} else if (!Ques.isContent(content)) {
			validation.keep();
			params.flash();
			flash.error("错误:请输入内容");
			QuesIndex();
		} else if (!Ques.islabel(label)) {
			validation.keep();
			params.flash();
			flash.error("错误:至少选择一个标签");
			QuesIndex();
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
		String d = (df.format(Calendar.getInstance().getTime()));
		new Ques(title, Tag, school, content, d, userid, usertype, username,
				userprofile, answerNum, focusNum, userselfIntro);
		render();
	}

	// public static void dispAddQues(Ques q) { final
	// Validation.ValidationResult validationResult = validation .valid(q);
	// if(!validationResult.ok) {
	// validation.keep(); params.flash();
	// flash.error("请更正错误。");
	// QuesIndex();
	// }
	// q.save();
	// render();
	// }

	public static void comments(String qid, String comment) {
		long quesid = Long.parseLong(qid);
		long comentUserid = Long.parseLong(session.get("logged"));
		String comentUsertype = session.get("usertype");
		String comentUsername;
		String comentUserprofile;
		String comentUserSelfIntro;
		if (comentUsertype.equals("simple")) {
			SimpleUser simp = SimpleUser.findById(comentUserid);
			comentUsername = simp.name;
			comentUserprofile = simp.profile;
			comentUserSelfIntro = simp.selfIntro;
		} else {
			CSSA cssa = CSSA.findById(comentUserid);
			comentUsername = cssa.name;
			comentUserprofile = cssa.profile;
			comentUserSelfIntro = cssa.selfIntro;
		}
		String QuesTitle;
		Ques ques = Ques.findById(quesid);
		QuesTitle = ques.title;
		ques.answerNum = ques.answerNum + 1;
		ques.save();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
		String d = (df.format(Calendar.getInstance().getTime()));
		new Comments(quesid, comment, 0, comentUserid, comentUsertype,
				comentUsername, comentUserprofile, comentUserSelfIntro, d,QuesTitle);
		renderHtml(comment);
	}

	public static void addComent(long quesid, String comment, long praiseNum,
			long userid, String usertype, String username, String userprofile,
			String userSelfIntro, String date) {

		long comentUserid = Long.parseLong(session.get("logged"));
		String comentUsertype = session.get("usertype");
		String comentUsername;
		String comentUserprofile;
		String comentUserSelfIntro;
		if (comentUsertype.equals("simple")) {
			SimpleUser simp = SimpleUser.findById(comentUserid);
			comentUsername = simp.name;
			comentUserprofile = simp.profile;
			comentUserSelfIntro = simp.selfIntro;
		} else {
			CSSA cssa = CSSA.findById(comentUserid);
			comentUsername = cssa.name;
			comentUserprofile = cssa.profile;
			comentUserSelfIntro = cssa.selfIntro;
		}
		String QuesTitle;
		Ques ques = Ques.findById(quesid);
		QuesTitle = ques.title;
		ques.answerNum = ques.answerNum + 1;
		ques.save();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
		String d = (df.format(Calendar.getInstance().getTime()));
		new Comments(quesid, comment, 0, comentUserid, comentUsertype,
				comentUsername, comentUserprofile, comentUserSelfIntro, d,QuesTitle);
		Ques fQ = Ques.findById(quesid);

		List<FocusQues> FQ = FocusQues.find("quesId = ?", quesid).fetch(5);
		List<Comments> listCom = Comments.find("quesid = ?", quesid).fetch();
		renderTemplate("QuestAnsw/showQuesInfo.html", fQ, listCom,
				comentUsername, FQ);
	}

	public static void searchPage(long id, long question_id) {
		List<Tag> t = Tag.findAll();
		List<Ques> aQues = Ques.find("order by date desc").fetch(5);
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		Iterator iterator = aQues.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques ques = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", ques.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(ques, comment);
			qArticles.add(qa);
		}
		render(t, qArticles, pageCount);
	}

	public static void searchQues(String ques) {
		List<Tag> t = Tag.findAll();
		List<Ques> anq = Ques.find("SELECT a FROM Ques a WHERE title LIKE ?",
				"%" + ques + "%").fetch(5);

		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		Iterator iterator = anq.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques qu = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", qu.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(qu, comment);
			qArticles.add(qa);
		}
		renderTemplate("QuestAnsw/searchPage.html", qArticles, t, pageCount);
	}

	public static void searchTag(String tag) {

		System.out.println(tag);

		List<Ques> anq = Ques.find("SELECT a FROM Ques a WHERE label LIKE ?",
				"%" + tag + "%").fetch(5);
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		Iterator iterator = anq.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques qu = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", qu.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(qu, comment);
			qArticles.add(qa);
		}
		List<Tag> t = Tag.findAll();
		renderTemplate("QuestAnsw/searchPage.html", qArticles, t, pageCount);
	}

	public static void showQuesInfo(long id) {
		String userType = session.get("usertype");
		long userId = Long.parseLong(session.get("logged"));
		String comentUsername;
		String userprofile;
		String userSelfIntro;
		if (userType.equals("simple")) {
			SimpleUser su = SimpleUser.findById(userId);
			comentUsername = su.name;
			userprofile = su.profile;
			userSelfIntro = su.selfIntro;
		} else {
			CSSA cssa = CSSA.findById(userId);
			comentUsername = cssa.name;
			userprofile = cssa.profile;
			userSelfIntro = cssa.selfIntro;
		}
		Ques fQ = Ques.findById(id);

		List<FocusQues> FQ = FocusQues.find("quesId = ?", id).fetch(5);

		List<Comments> listCom = Comments.find("quesid = ?", id).fetch();
		render(fQ, listCom, comentUsername, FQ);
	}

	public static void AttentionQues(long userId, long quesId) {
		Attention att = new Attention(userId, quesId);
		render(att);
	}

	public static void popupUserInfo() {
		render();
	}

	public static void userInfoIndex() {
		render();
	}

	public static void Quespaging(int pageNum, String data) {
		List<Tag> t = Tag.findAll();
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		if (pageNum < 1) {
			pageNum = 1;
		} else if (pageNum >= pageCount) {
			pageNum = (int) pageCount;
		}
		List<Ques> aQues = Ques.find("order by date desc")
				.from((pageNum - 1) * 5).fetch(5);

		// if(){
		// List<Ques> aQues = Ques.find("order by answerNum desc")
		// .from((pageNum - 1) * 5).fetch(5);
		// }else if(){
		// List<Ques> aQues = Ques.find("order by focusNum desc")
		// .from((pageNum - 1) * 5).fetch(5);
		// }else{
		// List<Ques> aQues = Ques.find("order by date desc")
		// .from((pageNum - 1) * 5).fetch(5);
		// }

		Iterator iterator = aQues.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques ques = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", ques.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(ques, comment);
			qArticles.add(qa);
		}
		renderTemplate("QuestAnsw/searchPage.html", qArticles, t, pageCount,
				pageNum);
	}

	public static void editQues() {
		render();
	}

	public static void deleteQues(long id, int pageNum) {
		Ques dques = Ques.findById(id);
		dques.delete();
		List<Tag> t = Tag.findAll();
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		if (pageNum < 1) {
			pageNum = 1;
		} else if (pageNum >= pageCount) {
			pageNum = (int) pageCount;
		}
		List<Ques> aQues = Ques.find("order by date desc")
				.from((pageNum - 1) * 5).fetch(5);
		Iterator iterator = aQues.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques ques = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", ques.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(ques, comment);
			qArticles.add(qa);
		}
		renderTemplate("QuestAnsw/searchPage.html", qArticles, t, pageCount,
				pageNum);
	}

	public static void editComent() {
		render();
	}

	public static void deleteComent() {
		render();
	}

	public static void fcousOnQuestion(long id) {
		String fquserType = session.get("usertype");
		long userId = Long.parseLong(session.get("logged"));

		List<FocusQues> foc = FocusQues.find(
				"userid = ? and quesId = ? and userType = ? ", userId, id,
				fquserType).fetch();
		if (!foc.isEmpty()) {
			flash.error("您已经关注");
			showQuesInfo(id);
		}
		long fquserid;
		String fquserprofile;
		if (fquserType.equals("simple")) {
			SimpleUser simpleUser = SimpleUser.findById(userId);
			fquserid = simpleUser.id;
			fquserprofile = simpleUser.profile;
		} else {
			CSSA cssa = CSSA.findById(id);
			fquserid = cssa.id;
			fquserprofile = cssa.profile;
		}
		String QuesTitle;
		Ques ques = Ques.findById(id);
		QuesTitle = ques.title;
		ques.focusNum = ques.focusNum + 1;
		ques.save();
		flash.success("关注成功");

		new FocusQues(fquserType, fquserid, fquserprofile, id, QuesTitle);

		showQuesInfo(id);
	}

	public static void SearchAnswNum() {
		List<Tag> t = Tag.findAll();
		List<Ques> aQues = Ques.find("order by answerNum DESC").fetch(5);
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		Iterator iterator = aQues.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques ques = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", ques.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(ques, comment);
			qArticles.add(qa);
		}
		render(t, qArticles, pageCount);
	}

	public static void searchSchool(String school) {
		System.out.println("查找 学校:" + school);
		List<Tag> t = Tag.findAll();
		List<Ques> anq = Ques.find("SELECT a FROM Ques a WHERE school LIKE ?",
				"%" + school + "%").fetch(5);
		long pageCount = Ques.count() % 5 == 0 ? Ques.count() / 5 : (Ques
				.count() / 5 + 1);
		Iterator iterator = anq.iterator();
		List<QuestionArticle> qArticles = new ArrayList<QuestionArticle>();
		while (iterator.hasNext()) {
			Ques qu = (Ques) iterator.next();
			List comments = Comments.find("quesid = ?", qu.id).fetch();
			Comments comment = comments.isEmpty() ? null : (Comments) comments
					.get(0);
			QuestionArticle qa = new QuestionArticle(qu, comment);
			qArticles.add(qa);
		}
		renderTemplate("QuestAnsw/searchPage.html", qArticles, t, pageCount);
	}

	// public static void userQues() {
	// String fquserType = session.get("usertype");
	// long userId = Long.parseLong(session.get("logged"));
	// long userid;
	// if (fquserType.equals("simple")) {
	// SimpleUser simpleUser = SimpleUser.findById(userId);
	// userid = simpleUser.id;
	// } else {
	// CSSA cssa = CSSA.findById(userId);
	// userid = cssa.id;
	// }
	//
	// List<Ques> userQues = Ques.find(
	// "SELECT a FROM Ques a WHERE userid LIKE ?", "%" + userid + "%")
	// .fetch();
	//
	// renderTemplate("simple/infocenter.html", userQues);
	// }
	//
	// public static void userQuesComment() {
	// String fquserType = session.get("usertype");
	// long userId = Long.parseLong(session.get("logged"));
	// long userid;
	// if (fquserType.equals("simple")) {
	// SimpleUser simpleUser = SimpleUser.findById(userId);
	// userid = simpleUser.id;
	// } else {
	// CSSA cssa = CSSA.findById(userId);
	// userid = cssa.id;
	// }
	//
	// List<Comments> userComment = Comments.find(
	// "SELECT a FROM Ques a WHERE userid LIKE ?", "%" + userid + "%")
	// .fetch();
	// renderTemplate("simple/infocenter.html", userComment);
	// }

	public static void userQues() {
		long userId = Long.parseLong(session.get("logged"));
		SimpleUser user = SimpleUser.findById(userId);

		List<Ques> UQues = Ques.find(
				"userid = ?  and usertype = ? order by id desc", userId,
				"simpleuser").fetch();

		List<Comments> UComment = Comments.find(
				"userid = ? and usertype =? order by id desc", userId,
				"simpleuser").fetch();

		List<FocusQues> FQues = FocusQues.find(
				"userid = ? and userType = ? order by id desc", userId,
				"simpleuser").fetch();
		notFoundIfNull(user);
		renderTemplate("SimpleUsers/infoCenter.html", user, UQues, UComment,
				FQues);
	}

	public static void cssaQues() {
		long userId = Long.parseLong(session.get("logged"));
		CSSA user = CSSA.findById(userId);

		List<Ques> CQues = Ques
				.find("userid = ?  and usertype = ? order by id desc", userId,
						"cssa").fetch();

		List<Comments> CComment = Comments.find(
				"userid = ? and usertype =? order by id desc", userId, "cssa")
				.fetch();

		List<FocusQues> CFQues = FocusQues.find(
				"userid = ? and userType = ? order by id desc", userId, "cssa")
				.fetch();
		notFoundIfNull(user);
		renderTemplate("CSSAs/infoCenter.html", user, CQues, CComment,
				CFQues);
	}
}
