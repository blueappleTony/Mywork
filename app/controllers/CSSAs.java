package controllers;

import java.io.File;
import java.util.List;

import notifiers.Notifier;
import play.Logger;
import play.Play;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.libs.Codec;
import play.libs.Crypto;
import play.libs.Files;
import play.libs.Images;
import play.mvc.*;
import models.activity.Activity;
import models.qa.Comments;
import models.qa.FocusQues;
import models.qa.Ques;
import models.users.CSSA;
import models.users.SimpleUser;
import models.airport.School;

public class CSSAs extends Application {

	@Before(unless = { "login", "signup", "register", "confirmRegistration",
			"authenticate", "resendConfirmation", "forgetpassword",
			"doforgetpassword", "resetPasswordConfirmation", "resetPassword" })
	public static void isLogged() {
		if (session.get("logged") == null) {
			CSSAs.login();
		}

	}

	public static void index() {
		render();
	}

	public static void signup() {
		List<School> sch = School.findAll();
		render(sch);
	}

	public static void login() {
		render();
	}

	public static void register(@Email @Required String email,
			@Required @MinSize(7) @MaxSize(20) String password,
			@Required @MinSize(1) @MaxSize(40) String name,
			@Required String contract,
			@Required @MaxSize(200) String selfIntro,
			@Required @URL String homepage,
			@Required @MinSize(7) @MaxSize(40) String applicant,
			@Required @MinSize(7) @MaxSize(100) String applicantTitle,
			@Required String peopleNumber, @Equals("password") String password2) {
		if ((!SimpleUser.isEmailAvailable(email))
				|| (!CSSA.isEmailAvailable(email))) {
			validation.keep();
			params.flash();
			flash.error("邮箱已使用。");
			signup();
		} else if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			signup();
		}
		CSSA user = new CSSA(email, password, name, contract, selfIntro,
				homepage, applicant, applicantTitle, peopleNumber);
		try {
			if (Notifier.welcomeCSSA(user)) {
				flash.success("请登录注册邮箱激活帐号。");
				login();
			}
		} catch (Exception e) {
			Logger.error(e, "Mail error");
		}
		flash.error("呃。。邮件发送失败，请重试。");
		login();
	}

	public static void logout() {
		flash.success("注销成功。欢迎再次登录。");
		session.clear();
		Application.index();
	}

	public static void confirmRegistration(String uuid) {
		CSSA user = CSSA.findByRegistrationUUID(uuid);
		notFoundIfNull(user);
		user.needConfirmation = null;
		user.save();
		connectCSSA(user);
		flash.success("Welcome %s !", user.name);
		infoCenter(user.id);
	}

	public static void authenticate(String email, String password) {
		CSSA user = (CSSA) CSSA.findByEmail(email);
		if (user == null || !user.checkPassword(password)) {
			flash.error("用户名或密码错误！");
			flash.put("email", email);
			login();
		} else if (user.needConfirmation != null) {
			flash.error("账户未激活");
			flash.put("notconfirmed", user.needConfirmation);
			flash.put("email", email);
			login();
		}
		connectCSSA(user);
		flash.success("欢迎回来， %s !", user.name);
		infoCenter(user.id);
	}

	public static void resendConfirmation(String uuid) {
		CSSA user = CSSA.findByRegistrationUUID(uuid);
		notFoundIfNull(user);
		try {
			if (Notifier.welcomeCSSA(user)) {
				flash.success("请登陆邮箱激活帐号。");
				flash.put("email", user.email);
				login();
			}
		} catch (Exception e) {
			Logger.error(e, "Mail error");
		}
		flash.error("邮件未能发送。");
		flash.put("email", user.email);
		login();
	}

	public static void show(Long id) {
		long uid = Long.parseLong(session.get("logged"));
		if (id != uid) {
			id = uid;
		}
		CSSA user = CSSA.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	static void connectCSSA(CSSA user) {
		session.put("logged", user.id);
		session.put("usertype", "cssa");
	}

	public static void changePassword(Long id) {
		render(id);
	}

	public static void doChangePassword(@Required String currentPassword,
			@Required @MinSize(7) @MaxSize(20) String password,
			@Required @Equals("password") String password2, Long id) {
		if (id != Long.parseLong(session.get("logged"))) {
			flash.error("帐户有误，请重新登陆");
			session.clear();
			login();
		} else if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			changePassword(id);
		} else if (!((CSSA) CSSA.findById(id)).checkPassword(currentPassword)) {
			validation.keep();
			params.flash();
			flash.error("原密码不正确！");
			changePassword(id);
		} else {
			((CSSA) CSSA.findById(id)).changePassword(password);
			flash.success("密码更改成功。");
			infoCenter(id);
		}
	}

	public static void updateProfile(Long id) {
		CSSA user = CSSA.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	public static void doUpdateProfile(CSSA user) {
		user.save();
		flash.success("资料更新成功");
		infoCenter(user.id);
	}

	public static void changeEmail(Long id) {
		render(id);
	}

	public static void dochangeEmail(@Required @Email String email, Long id) {
		if (!CSSA.isEmailAvailable(email)) {
			validation.keep();
			params.flash();
			flash.error("邮箱已存在。");
			changeEmail(id);
		} else if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			changeEmail(id);
		}
		((CSSA) CSSA.findById(id)).changeEmail(email);
		flash.success("邮箱更改成功，请重新登陆。");
		session.clear();
		login();
	}

	public static void forgetpassword() {
		render();
	}

	public static void doforgetpassword(@Required @Email String email) {
		if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			forgetpassword();
		} else if (CSSA.isEmailAvailable(email)) {
			validation.keep();
			params.flash();
			flash.error("邮箱不存在。");
			forgetpassword();
		}

		CSSA user = CSSA.findByEmail(email);
		user.passwordConfirmation = Codec.UUID();
		user.save();
		try {
			if (Notifier.resetPasswordCSSA(user)) {
				flash.success("请登录注册邮箱重置密码。");
				login();
			}
		} catch (Exception e) {
			Logger.error(e, "Mail error");
		}
		flash.error("呃。。邮件发送失败，请重试。");
		login();
	}

	public static void resetPasswordConfirmation(String uuid) {
		CSSA user = CSSA.findByResetPasswordUUID(uuid);
		notFoundIfNull(user);
		user.passwordConfirmation = null;
		user.save();
		connectCSSA(user);
		flash.success("Welcome %s !", user.name);
		resetPassword(user.id);
	}

	public static void resetPassword(Long id) {
		render(id);
	}

	public static void doResetPassword(
			@Required @MinSize(7) @MaxSize(20) String password,
			@Required @Equals("password") String password2, Long id) {
		if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			resetPassword(id);
		} else {
			((CSSA) CSSA.findById(id)).changePassword(password);
			flash.success("密码更改成功。");
			infoCenter(id);
		}
	}

	public static void changeProfile(Long id) {
		CSSA user = CSSA.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	public static void doChangeProfile(Long id, File f, int left, int top,
			int height, int width) {
		
		
		String path1 = "public/images/profile/" + Codec.UUID() + ".jpg";
		Images.crop(f, f, left, top, height, width);
		Files.copy(f, Play.getFile(path1));
		
		
		
		
		((CSSA) CSSA.findById(id)).changeProfile(path1);
		flash.success("头像更改成功");
		infoCenter(id);
	}

	public static void infoCenter(long id) {
		String usertype=session.get("usertype");
		long userId = Long.parseLong(session.get("logged"));
		if(!usertype.equals("cssa")){
			SimpleUsers.infoCenter(id);
		}else if(userId!=id){
			id = userId;
		}
		CSSA user = CSSA.findById(id);
		notFoundIfNull(user);
		render(user);
	}
	public static void myActivity() {
		long userId = Long.parseLong(session.get("logged"));
		CSSA user = CSSA.findById(userId);
		List<Activity> postedActivity = Activity.find(
				"publisher_id=? and publisher_type=? order by id desc", userId,
				"cssa").fetch();
		
//		List<Activity> JoinedActivity = Activity
//				.find("select a from  ActivityJoiner aj,Activity a where  aj.jid= ? and aj.aid = a.id order by a.id desc ",
//						userId).fetch();
		
		List<Activity> LikedActivity = Activity
				.find("select a from  ActivityLiker al,Activity a where  al.lid= ? and ltype=? and al.aid = a.id order by a.id desc ",
						userId,"cssa").fetch();
		//CSSA Ques
		
		
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
		render(user, postedActivity,LikedActivity,CQues,CComment,CFQues);
	}
}
