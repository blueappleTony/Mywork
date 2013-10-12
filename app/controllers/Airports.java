package controllers;

import java.util.List;

import models.airport.School;
import models.airport.StuInfo;
import models.airport.VolInfo;
import models.users.CSSA;
import models.users.SimpleUser;
import play.data.validation.Validation;
import play.mvc.*;

public class Airports extends Application {

	@Before
	public static void isCSSA() {
		if (session.get("logged") != null
				&& !session.get("usertype").equals("simple")) {
			CSSA cssa = CSSA.findById(Long.parseLong(session.get("logged")));
			if (cssa.name != null == true)
				renderArgs.put("isCSSA", true);
		}
	}

	@Before
	public static void isSimpleUser() {
		if (session.get("logged") != null
				&& session.get("usertype").equals("simple")) {
			SimpleUser simp = SimpleUser.findById(Long.parseLong(session
					.get("logged")));
			if (simp.name != null == true)
				renderArgs.put("isSimpleUser", true);
		}
	}

	@Before(unless = "index")
	public static void isLogged() {
		if (session.get("usertype") == null) {
			Application.index();
		}
	}

	public static void index() {
		String usertype = session.get("usertype");
		String username = null;
		if (!usertype.equals("simple")) {
			CSSA cssa = CSSA.findById(Long.parseLong(session.get("logged")));
			username = cssa.name;
		}
		List ls = School.findAll();
		render(ls, username);
	}

	public static void addStuInfo(Long id) {

		School sch = School.findById(id);
		String explain = sch.synopsis;
		Long schoolId = sch.id;
		String schoolName = sch.name;
		render(schoolId, explain, schoolName);
	}

	public static void doAddStuInfo(StuInfo s, Long schoolId) {
		Long sid = schoolId;
		final Validation.ValidationResult validationResult = validation
				.valid(s);
		if (!validationResult.ok) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			addStuInfo(sid);
		}
		s.save();
		render();
	}

	public static void addVolInfo(Long id) {
		School sch = School.findById(id);
		String explain = sch.synopsis;
		Long schoolId = sch.id;
		String schoolName = sch.name;
		render(schoolId, explain, schoolName);
	}

	public static void doAddVolInfo(VolInfo v, Long schoolId) {
		Long sid = schoolId;
		final Validation.ValidationResult vr = validation.valid(v);
		if (!vr.ok) {
			validation.keep();
			params.flash();
			flash.error("请更正错误。");
			addVolInfo(sid);
		}
		v.save();
		render();
	}

	public static void stuInfo(String username) {
		List<StuInfo> stu = StuInfo.find(
				"SELECT a FROM StuInfo a WHERE school LIKE ?",
				"%" + username + "%").fetch();
		render(stu);
	}
	public static void volInfoInfo(String username) {
		List<VolInfo> vol = VolInfo.find(
				"SELECT a FROM VolInfo a WHERE school LIKE ?",
				"%" + username + "%").fetch();
		render(vol);
	}
}
