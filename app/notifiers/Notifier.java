package notifiers;

import play.mvc.*;

import javax.mail.internet.*;

import models.users.CSSA;
import models.users.SimpleUser;;

public class Notifier extends Mailer {

    public static boolean welcomeSimpleUser(SimpleUser user) throws Exception {
    	  setSubject("Welcome %s",user.name);
    	      addRecipient(user.email);
    	      setFrom("253337876@qq.com");
        return sendAndWait(user);
    }
    public static boolean welcomeCSSA(CSSA user) throws Exception {
  	  setSubject("Welcome %s",user.name);
  	      addRecipient(user.email);
  	      setFrom("253337876@qq.com");
      return sendAndWait(user);
  }
    public static boolean resetPasswordSimpleUser(SimpleUser user) throws Exception {
  	  setSubject("重置密码");
  	      addRecipient(user.email);
  	      setFrom("253337876@qq.com");
      return sendAndWait(user);
  }
    public static boolean resetPasswordCSSA(CSSA user) throws Exception {
    	  setSubject("重置密码");
    	      addRecipient(user.email);
    	      setFrom("253337876@qq.com");
        return sendAndWait(user);
    }
    
}

