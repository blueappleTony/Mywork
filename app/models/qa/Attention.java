package models.qa;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Attention extends Model {
	public Long userId;
	public Long quesId;

	public Attention(Long userId, Long quesId) {
		this.quesId = quesId;
		this.userId = userId;
		create();
	}
}
