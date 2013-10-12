package controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.activity.Activity;
import models.activity.ActivityJoiner;
import models.activity.ActivityLiker;
import models.activity.ActivityPeriod;
import models.activity.ActivityScope;
import models.activity.ActivityType;
import models.users.CSSA;
import models.users.SimpleUser;
import play.Play;
import play.data.validation.Validation;
import play.db.jpa.JPABase;
import play.libs.Codec;
import play.libs.Files;
import play.libs.Images;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.*;
import play.mvc.results.Result;

public class Activities extends Application {

	@Before(unless = { "index", "detail", "filterType", "filterPeriod",
			"filterPeriodWeekend", "filterScope", "filterLocation", "getMore" })
	public static void isLogged() {
		if (session.get("usertype") == null) {
			Application.index();
		}
	}

	public static void index() {
		String type = session.get("type") == null ? "" : session.get("type");
		String scope = session.get("scope") == null ? "" : session.get("scope");
		String location = session.get("location") == null ? "" : session
				.get("location");
		int page = 1;
		int pageSize = 3;
		int start = (page - 1) * pageSize;
		System.out.print(start);
		int days = session.get("days") == null ? -1 : Integer.parseInt(session
				.get("days"));
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date;
		List<Activity> a;

		if (days == -1) {
			date = f.format(cal.getTime());
			a = Activity
					.find("type like ? and scope like ? and (timeFrom >= ? or timeTo >= ?) order by id desc",
							"%" + type + "%", scope + "%", date, date)
					.from(start).fetch(pageSize);
		} else if (days == -2) {
			a = Activity
					.find("type like ? and scope like ? and isWeekend=? order by id desc",
							"%" + type + "%", scope + "%", true).from(start)
					.fetch(pageSize);
		} else {
			cal.add(Calendar.DAY_OF_MONTH, +days);
			date = f.format(cal.getTime());
			String nowDate = f.format(new Date());
			a = Activity
					.find("type like ? and scope like ? and  timeTo>=?  and  timeFrom<=? order by id desc",
							"%" + type + "%", scope + "%", nowDate, date)
					.from(start).fetch(pageSize);
		}
		if (a != null) {

			Iterator iter = a.iterator();
			while (iter.hasNext()) {
				Activity aa = (Activity) iter.next();
				if (location.equals("")) {
					aa.distance = -1;
					aa.distances = "";
					aa.duration = "";
				} else {
					JsonArray myArry1 = WS
							.url("http://maps.googleapis.com/maps/api/distancematrix/json?origins="
									+ location
									+ "&destinations="
									+ aa.location
									+ "&sensor=false").get().getJson()
							.getAsJsonObject().getAsJsonArray("rows");
					if (myArry1.size() == 0) {
						flash.error("地点不准确。");
						aa.distance = -1;
						aa.distances = "";
						aa.duration = "";
					} else {
						JsonObject myText2 = myArry1.get(0).getAsJsonObject()
								.get("elements").getAsJsonArray().get(0)
								.getAsJsonObject();
						if (myText2.get("distance") == null) {
							flash.error("地点不准确。");
							aa.distance = -1;
							aa.distances = "";
							aa.duration = "";
						} else {
							String distances = myText2.get("distance")
									.getAsJsonObject().get("text").toString();
							long distance = Long.parseLong(myText2
									.get("distance").getAsJsonObject()
									.get("value").toString());
							String duration = myText2.get("duration")
									.getAsJsonObject().get("text").toString();
							if (distance <= 10000) {
								aa.distance = -distance;
								aa.distances = distances;
								aa.duration = duration;
							} else {
								iter.remove();
							}
						}
					}
				}
			}
		}

		List<ActivityType> t = ActivityType.find("order by sequence asc")
				.fetch();
		List<ActivityScope> s = ActivityScope.find("order by sequence asc")
				.fetch();
		List<ActivityPeriod> p = ActivityPeriod.find("order by sequence asc")
				.fetch();
		render(a, t, s, p, type, scope, days, location);
	}

	public static void create() {
		List<ActivityType> at = ActivityType.findAll();
		List<ActivityScope> s = ActivityScope.findAll();
		render(at, s);
	}

	public static void next(Activity a) {
		final Validation.ValidationResult validationResult = validation
				.valid(a);
		if (!validationResult.ok) {
			
			params.flash();
			validation.keep();
			flash.error("请更正错误。");
			create();
		}
		Calendar date_from = Calendar.getInstance();
		Calendar date_to = Calendar.getInstance();
		date_from.set(Integer.parseInt(a.timeFrom.substring(0, 3)), Integer.parseInt(a.timeFrom.substring(5,6 )),Integer.parseInt(a.timeFrom.substring(8, 9)));
		date_to.set(Integer.parseInt(a.timeTo.substring(0, 3)), Integer.parseInt(a.timeTo.substring(5,6 )),Integer.parseInt(a.timeTo.substring(8, 9)));
		if (date_from.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| date_from.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				|| date_to.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| date_to.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			a.isWeekend = true;
		}
		long publisher_id = Long.parseLong(session.get("logged"));
		String publisher_type = session.get("usertype");
		a.publisher_id = publisher_id;
		a.publisher_type = publisher_type;
		
		System.out.println("user type:"+publisher_type);
		
		
		
		if (publisher_type.equals("simple")) {
			SimpleUser user = SimpleUser.findById(publisher_id);
			a.publisher_name = user.name;
			a.publisher_profile = user.profile;

		} 
		else {
			CSSA user = CSSA.findById(publisher_id);
			a.publisher_name = user.name;
			a.publisher_profile = user.profile;
		}
		a.save();
		Long id = a.id;
		render(id);
	}

	public static void post(Long id, File poster,int top,int left,int height, int width) {
		String path = "public/images/poster/" + Codec.UUID() + ".jpg";
		Images.crop(poster, poster, left, top, height, width);
	 
		Files.copy(poster, Play.getFile(path));
		((Activity) Activity.findById(id)).savePoster(path);
		flash.success("活动发布成功，正在进行审查。");
		index();
	}

	public static void detail(Long id) {
		Activity a = Activity.findById(id);
		a.views = a.views + 1;
		a.save();
		String publisher_name;
		String publisher_profile;
		if (a.publisher_type.equals("simple")) {
			SimpleUser u = SimpleUser.findById(a.publisher_id);
			publisher_name = u.name;
			publisher_profile = u.profile;
		} else {
			CSSA c = CSSA.findById(a.publisher_id);
			publisher_name = c.name;
			publisher_profile = c.profile;
		}
		if (a.publisher_type.equals("simple")) {
			renderArgs.put("publisher", SimpleUser.findById(a.publisher_id));
		} else if (a.publisher_type.equals("cssa")) {
			renderArgs.put("publisher", CSSA.findById(a.publisher_id));
		}
		List<SimpleUser> activityJoiner = SimpleUser
				.find("select user from SimpleUser user,ActivityJoiner aj where aj.jid = user.id and aid=?",
						id).fetch();
		render(a, publisher_name, publisher_profile, activityJoiner);
	}

	public static void filterType(String type) {
		session.put("type", type);
		index();
	}

	public static void filterPeriod(short days) {
		session.put("days", days);

		index();
	}

	public static void filterPeriodWeekend() {
		session.put("days", -2);
		index();
	}

	public static void filterScope(String scope) {
		session.put("scope", scope);
		index();
	}

	public static void filterLocation(String location) {
		session.put("location", location);
		index();
	}

	public static void getMore(int page) {

		String type = session.get("type") == null ? "" : session.get("type");
		String scope = session.get("scope") == null ? "" : session.get("scope");
		String location = session.get("location") == null ? "" : session
				.get("location");

		int pageSize = 3;
		int start = (page - 1) * pageSize;
		int days = session.get("days") == null ? -1 : Integer.parseInt(session
				.get("days"));
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date;
		List<Activity> a;

		if (days == -1) {
			date = f.format(cal.getTime());
			a = Activity
					.find("type like ? and scope like ? and (timeFrom >= ? or timeTo >= ?) order by id desc",
							"%" + type + "%", scope + "%", date, date)
					.from(start).fetch(pageSize);
		} else if (days == -2) {
			a = Activity
					.find("type like ? and scope like ? and isWeekend=? order by id desc",
							"%" + type + "%", scope + "%", true).from(start)
					.fetch(pageSize);
		} else {
			cal.add(Calendar.DAY_OF_MONTH, +days);
			date = f.format(cal.getTime());
			String nowDate = f.format(new Date());
			a = Activity
					.find("type like ? and scope like ? and  timeTo>=?  and  timeFrom<=? order by id desc",
							"%" + type + "%", scope + "%", nowDate, date)
					.from(start).fetch(pageSize);
		}
		if (a != null) {

			Iterator iter = a.iterator();
			while (iter.hasNext()) {
				Activity aa = (Activity) iter.next();
				if (location.equals("")) {
					aa.distance = -1;
					aa.distances = "";
					aa.duration = "";
				} else {
					JsonArray myArry1 = WS
							.url("http://maps.googleapis.com/maps/api/distancematrix/json?origins="
									+ location
									+ "&destinations="
									+ aa.location
									+ "&sensor=false").get().getJson()
							.getAsJsonObject().getAsJsonArray("rows");
					if (myArry1.size() == 0) {
						flash.error("地点不准确。");
						aa.distance = -1;
						aa.distances = "";
						aa.duration = "";
					} else {
						JsonObject myText2 = myArry1.get(0).getAsJsonObject()
								.get("elements").getAsJsonArray().get(0)
								.getAsJsonObject();
						if (myText2.get("distance") == null) {
							flash.error("地点不准确。");
							aa.distance = -1;
							aa.distances = "";
							aa.duration = "";
						} else {
							String distances = myText2.get("distance")
									.getAsJsonObject().get("text").toString();
							long distance = Long.parseLong(myText2
									.get("distance").getAsJsonObject()
									.get("value").toString());
							String duration = myText2.get("duration")
									.getAsJsonObject().get("text").toString();
							if (distance <= 10000) {
								aa.distance = -distance;
								aa.distances = distances;
								aa.duration = duration;
							} else {
								iter.remove();
							}
						}
					}
				}
			}
		}
		String html = "";
		Iterator iter = a.iterator();
		while (iter.hasNext()) {
			Activity aa = (Activity) iter.next();
			html = html
					+ "<div class=\"second1\">"
					+ "<div class=\"position-pic1\"><img src=\"/public/img/active_detailed_tag1.png\"></div>"
					+ "<div class=\"third\">"
					+ "<div class=\"post\">"
					+ "<a href=\"/activity/detail/"
					+ aa.id
					+ "\"><img src=\""
					+ aa.poster
					+ "\"></a>"
					+ "</div>"
					+ "<div><a class=\"activity-title2\">"
					+ aa.name
					+ "</a></div><div>"
					+ "<span></span> <span></span>"
					+ "<span  style=\"font-size:12px;\">"
					+ aa.timeFrom +" 至    "+aa.timeTo
					+ "</span> <span class=\"canjiaNO\"> </span>"
					+ "</div>"
					+ "<a class=\"detailed\">"
					+ aa.intro
					+ "</a>"
					 

					 + "</div></div></div>";
		}

		renderHtml(html);
	}

	public static void join(long aid) {
		long userId = Long.parseLong(session.get("logged"));
		List aj_exist = ActivityJoiner.find("aid = ? and jid = ?", aid, userId)
				.fetch();
		if (session.get("usertype").equals("cssa")) {
			flash.error("抱歉，CSSA用户不可参加。");
			detail(aid);
		}
		if (!aj_exist.isEmpty()) {
			flash.error("您已参加");
			detail(aid);
		}
		ActivityJoiner aj = new ActivityJoiner();
		aj.aid = aid;
		aj.jid = userId;
		aj.save();
		Activity a = Activity.findById(aid);
		a.joinerCount = a.joinerCount + 1;
		a.save();
		flash.success("参加成功");
		detail(aid);
	}

	public static void like(long aid) {
		long userId = Long.parseLong(session.get("logged"));
		String usertype = session.get("usertype");
		List al_exist = ActivityLiker.find(
				"aid = ? and lid = ? and ltype = ? ", aid, userId, usertype)
				.fetch();

		if (!al_exist.isEmpty()) {
			flash.error("您已关注");
			detail(aid);
		}
		ActivityLiker al = new ActivityLiker();
		al.aid = aid;
		al.lid = userId;
		al.ltype = usertype;
		al.save();
		Activity a = Activity.findById(aid);
		a.likerCount = a.likerCount + 1;
		a.save();
		flash.success("关注成功");
		detail(aid);
	}

	public static void test() {
		render();
	}

	public static void userThumb(long id) {
		String user = "Jessie";
		renderHtml(user);
	}

}
