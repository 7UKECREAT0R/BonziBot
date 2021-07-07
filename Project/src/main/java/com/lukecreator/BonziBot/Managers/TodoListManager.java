package com.lukecreator.BonziBot.Managers;

import java.io.EOFException;
import java.util.HashMap;

import com.lukecreator.BonziBot.Data.DataSerializer;
import com.lukecreator.BonziBot.Data.IStorableData;
import com.lukecreator.BonziBot.Data.TodoList;

import net.dv8tion.jda.api.entities.User;

public class TodoListManager implements IStorableData {
	
	HashMap<Long, TodoList> data = new HashMap<Long, TodoList>();
	
	public TodoList getTodoList(User user) {
		return this.getTodoList(user.getIdLong());
	}
	public TodoList getTodoList(long userId) {
		TodoList obj = this.data.get(userId);
		if(obj == null)
			return new TodoList();
		else return obj;
	}
	public void setTodoList(User user, TodoList list) {
		this.setTodoList(user.getIdLong(), list);
	}
	public void setTodoList(long userId, TodoList list) {
		this.data.put(userId, list);
	}

	@Override
	public void saveData() {
		DataSerializer.writeObject(data, "todolists");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void loadData() throws EOFException {
		Object load = DataSerializer.retrieveObject("todolists");
		if(load != null)
			data = (HashMap<Long, TodoList>) load;
	}
}
