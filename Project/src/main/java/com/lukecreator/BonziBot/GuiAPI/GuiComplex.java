package com.lukecreator.BonziBot.GuiAPI;

import java.util.function.Function;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

/**
 * Represents a child element inside a GUI. Implementation is GUI-specific.
 * As of 12/12/20, GuiComplex is only used in GuiGuildSettings.
 * @author Lukec
 * @param T The type of data to store.
 */
public class GuiComplex<T> {
	
	public class ComplexButtonClickData {
		public final String actionId;
		public final long executorId;
		public final JDA jda;
		public final Gui sender;
		
		private ComplexButtonClickData(Gui sender, String actionId, long executorId, JDA jda) {
			this.actionId = actionId;
			this.executorId = executorId;
			this.jda = jda;
			this.sender = sender;
		}
	}
	
	T data;
	
	public GuiComplex(T data, GuiElement element) {
		this.data = data;
		this.element = element;
		this.actionId = element.id;
	}
	
	public final String actionId;
	public final GuiElement element;
	
	Function<EmbedBuilder, EmbedBuilder> onDraw = null;
	
	public GuiComplex<T> whenDrawn(Function<EmbedBuilder, EmbedBuilder> function) {
		this.onDraw = function;
		return this;
	}
	public EmbedBuilder draw(EmbedBuilder base) {
		if(this.onDraw != null)
			return this.onDraw.apply(base);
		return base;
	}
}
