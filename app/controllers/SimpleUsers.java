package controllers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import jj.play.ns.nl.captcha.util.ImageUtil;

import com.mysql.jdbc.log.Log;
import com.sun.mail.handlers.image_jpeg;
import com.sun.media.sound.Toolkit;

import notifiers.Notifier;
import play.Logger;
import play.Play;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Crypto;
import play.libs.Files;
import play.libs.Images;
import play.libs.Images.Captcha;
import play.mvc.*;
import models.activity.Activity;
import models.qa.Comments;
import models.qa.FocusQues;
import models.qa.Ques;
import models.users.CSSA;
import models.users.SimpleUser;

public class SimpleUsers extends Application {

	@Before(unless = { "login", "signup", "register", "confirmRegistration",
			"authenticate", "resendConfirmation", "forgetpassword",
			"doforgetpassword", "resetPasswordConfirmation", "resetPassword" })
	public static void isLogged() {
		if (session.get("logged") == null) {
			login();
		}
	}

	public static void index() {
		render();
	}

	public static void signup() {
		render();
	}

	public static void login() {

		render();
	}

	public static void register(@Required @Email String email,
			@Required @MinSize(7) @MaxSize(20) String password,
			@Equals("password") String password2,
			@Required @MinSize(2) @MaxSize(20) String name) {

		if ((!SimpleUser.isEmailAvailable(email))
				|| (!CSSA.isEmailAvailable(email))) {
			validation.keep();
			params.flash();
			flash.error("邮箱已存在。");
			signup();
		} else if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			signup();
		}
		SimpleUser user = new SimpleUser(email, password, name);
		try {
			if (Notifier.welcomeSimpleUser(user)) {
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
		SimpleUser user = SimpleUser.findByRegistrationUUID(uuid);
		notFoundIfNull(user);
		user.needConfirmation = null;
		user.save();
		connectSimple(user);
		flash.success("Welcome %s !", user.name);
		infoCenter(user.id);
	}

	public static void authenticate(String email, String password) {
		SimpleUser user = (SimpleUser) SimpleUser.findByEmail(email);
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
		connectSimple(user);
		flash.success("欢迎回来， %s !", user.name);
		infoCenter(user.id);
	}

	public static void resendConfirmation(String uuid) {
		SimpleUser user = SimpleUser.findByRegistrationUUID(uuid);
		notFoundIfNull(user);
		try {
			if (Notifier.welcomeSimpleUser(user)) {
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
		SimpleUser user = SimpleUser.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	static void connectSimple(SimpleUser user) {
		session.put("logged", user.id);
		session.put("usertype", "simple");
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
		} else if (!((SimpleUser) SimpleUser.findById(id))
				.checkPassword(currentPassword)) {
			validation.keep();
			params.flash();
			flash.error("原密码不正确！");
			changePassword(id);
		} else {
			((SimpleUser) SimpleUser.findById(id)).changePassword(password);
			flash.success("密码更改成功。");
			infoCenter(id);
		}
	}

	public static void updateProfile(Long id) {
		SimpleUser user = SimpleUser.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	public static void doUpdateProfile(SimpleUser user) {
		user.save();
		flash.success("资料更新成功");
		infoCenter(user.id);
	}

	public static void changeEmail(Long id) {
		render(id);
	}

	public static void dochangeEmail(@Required @Email String email, Long id) {
		if (!SimpleUser.isEmailAvailable(email)) {
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
		((SimpleUser) SimpleUser.findById(id)).changeEmail(email);
		flash.success("邮箱更改成功，请重新登陆。");
		session.clear();
		login();
	}

	public static void forgetpassword() {
		render();
	}

	public static void doforgetpassword(@Required @Email String email) {
		System.out.println("here");
		if (validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			forgetpassword();
		} else if (SimpleUser.isEmailAvailable(email)) {
			validation.keep();
			params.flash();
			flash.error("邮箱不存在。");
			forgetpassword();
		}

		SimpleUser user = SimpleUser.findByEmail(email);
		user.passwordConfirmation = Codec.UUID();
		user.save();
		try {
			if (Notifier.resetPasswordSimpleUser(user)) {
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
		SimpleUser user = SimpleUser.findByResetPasswordUUID(uuid);
		notFoundIfNull(user);
		user.passwordConfirmation = null;
		user.save();
		connectSimple(user);
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
			((SimpleUser) SimpleUser.findById(id)).changePassword(password);
			flash.success("密码更改成功。");
			infoCenter(id);
		}
	}

	public static void changeProfile(Long id) {
		SimpleUser user = SimpleUser.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	public static void doChangeProfile(Long id, File f, int left, int top,
			int height, int width) {

		String path1 = "public/images/profile/" + Codec.UUID() + ".jpg";
		Images.crop(f, f, left, top, height, width);
		Files.copy(f, Play.getFile(path1));

		((SimpleUser) SimpleUser.findById(id)).changeProfile(path1);
		flash.success("头像更改成功");
		infoCenter(id);
	}

	public static void infoCenter(long id) {
		long uid = Long.parseLong(session.get("logged"));
		if (id != uid) {
			id = uid;
		}
		SimpleUser user = SimpleUser.findById(id);
		notFoundIfNull(user);
		render(user);
	}

	public static void myActivity() {
		long userId = Long.parseLong(session.get("logged"));
		SimpleUser user = SimpleUser.findById(userId);
		List<Activity> postedActivity = Activity.find(
				"publisher_id=? and publisher_type=? order by id desc", userId,
				"simple").fetch();
		List<Activity> JoinedActivity = Activity
				.find("select a from  ActivityJoiner aj,Activity a where  aj.jid= ? and aj.aid = a.id order by a.id desc ",
						userId).fetch();
		List<Activity> LikedActivity = Activity
				.find("select a from  ActivityLiker al,Activity a where  al.lid= ? and ltype=? and al.aid = a.id order by a.id desc ",
						userId, "simple").fetch();
		
		//User Ques
		
		String fquserType = session.get("usertype");
		
		System.out.println(fquserType);
		
		List<Ques> UQues = Ques.find(
				"userid = ?  and usertype = ? order by id desc", userId,
				"simple").fetch();


		List<Comments> UComment = Comments.find(
				"userid = ? and usertype =? order by id desc", userId,
				"simple").fetch();

		List<FocusQues> FQues = FocusQues.find(
				"userid = ? and userType = ? order by id desc", userId,
				"simple").fetch();
		
		notFoundIfNull(user);
		render(user, postedActivity,JoinedActivity, LikedActivity,UQues,UComment,FQues);
	}

}
