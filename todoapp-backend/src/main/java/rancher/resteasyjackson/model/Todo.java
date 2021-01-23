package rancher.resteasyjackson.model;

import java.sql.Timestamp;

import javax.annotation.processing.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Todo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String todoType;
	
	private String todoDetail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTodoType() {
		return todoType;
	}

	public void setTodoType(String todoType) {
		this.todoType = todoType;
	}

	public String getTodoDetail() {
		return todoDetail;
	}

	public void setTodoDetail(String todoDetail) {
		this.todoDetail = todoDetail;
	}

	@Override
	public String toString() {
		return "Todo [id=" + id + ", todoType=" + todoType + ", todoDetail=" + todoDetail + "]";
	}

}
