package com.lukecreator.BonziBot.Commands;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;

public class LeetCodeCommand extends Command {

	private final String api = "https://leetcode.com/graphql/";
	private final String randomJson = "{\"query\":\"\\n    query randomQuestion($categorySlug: String, $filters: QuestionListFilterInput) {\\n  randomQuestion(categorySlug: $categorySlug, filters: $filters) {\\n    titleSlug\\n  }\\n}\\n    \",\"variables\":{\"categorySlug\":\"\",\"filters\":{}}}";
	private final HttpClient client;
	
	public LeetCodeCommand() {
		this.client = HttpClient.newHttpClient();
		this.subCategory = 0;
		this.name = "LeetCode";
		this.icon = GenericEmoji.fromEmote(1152285006585155676L, false);
		this.description = "Sends a random leetcode question in chat! Made for collaborative growth.";
		this.args = null;
		this.category = CommandCategory.TECHNICAL;
	}

	private String getRandomQuestion() {
		HttpRequest request = HttpRequest.newBuilder(URI.create(this.api))
				.header("Content-Type", "application/json")
				.header("Cache-Control", "no-cache")
				.POST(BodyPublishers.ofString(this.randomJson))
				.build();
		
		this.client.sendAsync(request, BodyHandlers.ofString()).handle((response, exception) -> {
			return response;
		});
	}
	
	@Override
	public void run(CommandExecutionInfo e) {
		
	}
}