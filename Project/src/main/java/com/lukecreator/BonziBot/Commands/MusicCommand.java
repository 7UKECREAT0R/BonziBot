package com.lukecreator.BonziBot.Commands;

import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Gui.GuiMusic;
import com.lukecreator.BonziBot.Music.MusicQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommand extends Command {

	public MusicCommand() {
		this.subCategory = 0;
		this.name = "Music";
		this.unicodeIcon = "🎵";
		this.description = "I'll join the voice chat and open the music dashboard.";
		this.args = null;
		this.worksInDms = false;
		this.category = CommandCategory.MUSIC;
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		Member member = e.member;
		GuildVoiceState vc = member.getVoiceState();
		Guild guild = e.guild;
		
		if(!vc.inVoiceChannel()) {
			e.reply(BonziUtils.failureEmbed("You need to be in a voice channel."));
			return;
		}
		
		VoiceChannel channel = vc.getChannel();
		
		if(channel == null) {
			e.reply(BonziUtils.failureEmbed("i have no idea what just happened.", "channel == null"));
			return;
		}
		
		AudioManager am = guild.getAudioManager();
		am.openAudioConnection(channel);
		
		AudioPlayerManager playerManager = e.bonzi.audioPlayerManager;
		MusicQueue queue = e.bonzi.music.getQueue(guild, playerManager);
		
		GuiMusic gui = new GuiMusic(queue);
		BonziUtils.sendGui(e, gui);
		return;
	}
}