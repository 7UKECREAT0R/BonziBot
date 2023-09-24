package com.lukecreator.BonziBot.Script.Model.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.Data.Achievement;
import com.lukecreator.BonziBot.Data.UserAccount;
import com.lukecreator.BonziBot.Managers.UserAccountManager;
import com.lukecreator.BonziBot.Script.Model.ScriptGetter;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class StatementMemberGetBonziData extends ScriptGetter {
	
	private static final long serialVersionUID = 1L;
	
	public static final Comparator<Role> ROLE_SORT = new Comparator<Role>() {
		@Override
		public int compare(Role o1, Role o2) {
			return o1.getPosition() - o2.getPosition();
		}
	};
	
	public StatementMemberGetBonziData() {
		super();
		this.nameOfType = "Member";
		this.keyword = "m_bonzidata";
		this.requiredType = Member.class;
		
		this.propertyBindings = new ArrayList<Binding>();
		
		this.propertyBindings.add(new Binding("Coins", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.getCoins();
		}));
		this.propertyBindings.add(new Binding("XP", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.getXP();
		}));
		this.propertyBindings.add(new Binding("Level", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.calculateLevel();
		}));
		this.propertyBindings.add(new Binding("Achievements", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			List<Achievement> achievements = account.getAchievements();
			return BonziUtils.stringJoinTransform(", ", a -> {
				return ((Achievement)a).name;
			}, achievements);
		}));
		this.propertyBindings.add(new Binding("Achievement Count", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.getAchievements().size();
		}));
		this.propertyBindings.add(new Binding("Warn Count", member -> {
			long id = ((Member)member).getIdLong();
			long gid = ((Member)member).getGuild().getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.getWarns(gid).length;
		}));
		this.propertyBindings.add(new Binding("Reputation", member -> {
			long id = ((Member)member).getIdLong();
			UserAccountManager uam = this.bonziInstance.accounts;
			UserAccount account = uam.getUserAccount(id);
			return account.getRep();
		}));
	}
	
}
