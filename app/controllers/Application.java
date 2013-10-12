package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import models.users.CSSA;
import models.users.SimpleUser;

public class Application  extends Controller {

	@Before
	static void globals() {
	
		String userType = session.get("usertype");
		if(userType==null){
			renderArgs.put("connectedSimpleUser",null);
			renderArgs.put("connectedCSSA", null);
		}else if(userType.equals("simple")){		
			renderArgs.put("connectedSimpleUser", connectedSimple());
		}else if(userType.equals("cssa") ){
			renderArgs.put("connectedCSSA", connectedCSSA());
		}
		
	}
    public static void index() {
    	String userId = session.get("logged");
    	if(userId!=null){
    		String userType = session.get("usertype");
    		if(userType.equals("simple")){		
    			SimpleUsers.infoCenter(Long.parseLong(userId));
    		}else if(userType.equals("cssa") ){
    			CSSAs.infoCenter(Long.parseLong(userId));
    		}
    	}
        render();
    }
    static SimpleUser connectedSimple() {
    	String userId = session.get("logged");
		return userId == null ? null : (SimpleUser) SimpleUser.findById(Long
				.parseLong(userId));
	}
    static CSSA connectedCSSA() {
    	String userId = session.get("logged");
		return userId == null ? null : (CSSA) CSSA.findById(Long
				.parseLong(userId));
	}
	@Before
	static void checkSecure() {
		Secure secure = getActionAnnotation(Secure.class);
		if (secure != null) {
			if (connectedSimple() == null
					|| (secure.admin() && !connectedSimple().isAdmin())) {
				forbidden();
			}
		}
	}
    @Before
    static void rememberedUser(){
    	//response.cookies.get("email").value;
    	
    }
}