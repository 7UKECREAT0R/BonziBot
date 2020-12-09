package com.lukecreator.BonziBot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class CustomCommandManager implements Serializable {

	private static final long serialVersionUID = 7382771579672589838L;
	public enum CCM_ReturnCode {
		Success,
		Exists,
		Limited,
		DoesntExist,
		Unowned
	}
	
	public List<CustomCommand> global;
	public HashMap<Long, List<CustomCommand>> specific = new HashMap
				  <Long, List<CustomCommand>>();
	
	public CustomCommandManager() {
		global = new ArrayList<CustomCommand>();
		specific = new HashMap<Long, List<CustomCommand>>();
	}


	public CCM_ReturnCode AddCustomCommand(CustomCommand cc, User u) {
		if(public_Exists(cc)) {
			return CCM_ReturnCode.Exists;
		}
		global.add(cc);
		return CCM_ReturnCode.Success;
	}
	public CCM_ReturnCode RemoveCustomCommand(String name) {
		for(CustomCommand cc : global) {
			if(cc.command.equalsIgnoreCase(name)) {
				global.remove(cc);
				return CCM_ReturnCode.Success;
			}
		}
		return CCM_ReturnCode.DoesntExist;
	}
	
	private void Verify(Guild g) {
		Long id = g.getIdLong();
		if(!specific.containsKey(id)) {
			specific.put(id, new ArrayList
					<CustomCommand>());
		} return;
	}
	private List<CustomCommand> GetPrivate(Guild g) {
		Verify(g);
		return specific.get(g.getIdLong());
	}
	public CCM_ReturnCode AddPrivateCommand(CustomCommand cc, User u, Guild g) {
		Long id = g.getIdLong();
		List<CustomCommand> cmds;
		cmds = GetPrivate(g);
		for(CustomCommand cmd : cmds) {
			if(cmd.command.equalsIgnoreCase(cc.command)) {
				return CCM_ReturnCode.Exists;
			}
		}
		cmds.add(cc);
		specific.put(id,cmds);
		return CCM_ReturnCode.Success;
	}
	public CCM_ReturnCode RemovePrivateCommand(String name, User u, Guild g) {
		List<CustomCommand> cmds;
		cmds = GetPrivate(g);
		for(CustomCommand cmd : cmds) {
			if(cmd.command.equalsIgnoreCase(name)) {
				Long id = cmd.creatorID;
				if(!id.equals(u.getIdLong())) {
					return CCM_ReturnCode.Unowned;
				}
				cmds.remove(cmd);
				specific.put(g.getIdLong(), cmds);
				return CCM_ReturnCode.Success;
			}
		}
		return CCM_ReturnCode.DoesntExist;
	}
	
	private boolean public_Exists(CustomCommand orig) {
		String cmd = orig.command;
		for(CustomCommand cc : global) {
			if(cc.command.equalsIgnoreCase(cmd)) {
				return true;
			}
		} return false;
	}
	public boolean public_Exists(String name) {
		for(CustomCommand cc : global) {
			if(cc.command.equalsIgnoreCase(name)) {
				return true;
			}
		} return false;
	}
	
	public CustomCommand public_GetByName(String name) {
		for(CustomCommand cc : global) {
			if(cc.command.equalsIgnoreCase(name)) {
				return cc;
			}
		}
		return CustomCommand.NOT_FOUND;
	}
	public CustomCommand private_GetByName(String name, Guild g) {
		List<CustomCommand> cmds;
		cmds = GetPrivate(g);
		for(CustomCommand cc : cmds) {
			if(cc.command.equalsIgnoreCase(name)) {
				return cc;
			}
		}
		return CustomCommand.NOT_FOUND;
	}
}
