package controllers;

import play.*;
import play.db.jpa.JPABase;
import play.libs.Files;
import play.mvc.*;
import play.mvc.Scope.Session;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
  






import models.*;
import models.activity.Activity;
import models.activity.ActivityLiker;
import models.charity.Wel;
import models.charity.welLiker;
import models.users.SimpleUser;

public class Charities extends Application{
 
	@Before
	public static void isAdmin(){
		if (session.get("logged") != null && session.get("usertype").equals("simple")) {
			SimpleUser su = SimpleUser.findById(Long.parseLong(session.get("logged")));
			if(su.isAdmin == true)
			renderArgs.put("isAdmin",true);			
		}
	}
	
	public static void fabu() {
		render();
	 }	
 
	@SuppressWarnings("unused")
	public static void WelSave(String title,String content,String time,File f,String  generalize,int likerCount) {
		 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
		String d = (df.format(Calendar.getInstance().getTime()));
		
		//上传图片
		 
		if(f!=null){
			String fileName = f.getName();
			String extName = fileName.substring(fileName.lastIndexOf("."));
			UUID uuid = UUID.randomUUID();
			fileName = uuid.toString() + extName;
			
			String path = "/public/images/upload/"+fileName;
			Files.copy(f,Play.getFile(path));
			
			Wel w = new Wel(title,content,d,path, generalize,likerCount);
			wel();
 	}else{
			 
			 validation.keep();
			 params.flash();
			 flash.error("请上传封面图片");
			 
			 fabu();
			
		} 
	}	
	 
 
 	public static void wel() {
		List<Wel> we= Wel.find("order by likerCount desc").fetch(5);
		long pageCount = Wel.count()%5==0? Wel.count()/5:(Wel.count()/5+1);
		int pageNo = (int) (Wel.count()/5);
		render(we,pageCount,pageNo );
	}	
 
	public static void edit(long id,int pageNo){
		 
		Wel w = Wel.findById(id);
		 render(w,pageNo);
 	 }
	
	public static void update(Wel w,File f,int pageNo){
	 
		if(f==null){
			 
			 w.save();
		 
		}else{
			String fileName = f.getName();
			String extName = fileName.substring(fileName.lastIndexOf("."));
			UUID uuid = UUID.randomUUID();
			fileName = uuid.toString() + extName;
			String path = "/public/images/upload/"+fileName;
			Files.copy(f,Play.getFile(path));
			w.f = path;
	 
			w.save();
		}
		pigination(pageNo);
	 }
	
	
	public static void welfare(long id){
	  Wel w = Wel.findById(id);
		render(w);
 	
	}

	
	public static void del(long id,int pageNo){
		 
		Wel w = Wel.findById(id);
		w.delete();
		pigination(pageNo);
	  }




	public static void pigination(int pageNo) {
		long pageCount = Wel.count()%5==0? Wel.count()/5:(Wel.count()/5+1);
		
		
		if(pageNo < 1) {
			pageNo =  1;
		} else if(pageNo >= pageCount) {
			pageNo =  (int) pageCount;
		}
		List<Wel> we= Wel.find("order by likerCount desc").from((pageNo-1)*5).fetch(5);
	 renderTemplate("Charities/wel.html",we,pageCount,pageNo);
	 }
	
	
	public static void like(long aid,int pageNo) {
		long userId = Long.parseLong(session.get("logged"));
		String usertype = session.get("usertype");
		List al_exist = welLiker.find(
				"aid = ? and lid = ? and ltype = ? ", aid, userId, usertype)
				.fetch();

		if (!al_exist.isEmpty()) {
			flash.error("您已关注");
			pigination(pageNo);
		}
		welLiker al = new welLiker();
		al.aid = aid;
		al.lid = userId;
		al.ltype = usertype;
		al.save();
		Wel a = Wel.findById(aid);
	
		a.likerCount = a.likerCount + 1;
		a.save();
		flash.success("关注成功");
		pigination(pageNo);
	}
	 
}