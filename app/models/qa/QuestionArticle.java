package models.qa;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;


public class QuestionArticle extends Model {
	Ques ques;
	Comments comments;
	
    public QuestionArticle(Ques ques,Comments comments){
    	this.ques = ques;
    	this.comments = comments;
    }
}
