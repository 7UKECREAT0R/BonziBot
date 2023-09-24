package com.lukecreator.BonziBot.Wrappers;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.InternalLogger;

/**
 * Wrapper for interacting with the reddit API.
 */
public class RedditClient {
	
	public RestClient client = new HttpRestClient();
	private SecureRandom random = new SecureRandom();
	public String getSubredditUrl(String subreddit) {
		return "https://www.reddit.com/r/" + subreddit + "/";
	}
	public String getSubredditAboutUrl(String subreddit) {
		return "https://www.reddit.com/r/" + subreddit + "/about.json";
	}
	public String getSubmissionUrl(Submission sub) {
		String perma = sub.getPermalink();
		if(perma.endsWith("_/"))
			perma = perma.substring(0, perma.length() - 3);
		return "https://www.reddit.com" + perma;
	}
	public String getSubmissionAboutUrl(Submission sub) {
		String perma = sub.getPermalink();
		if(perma.endsWith("_/"))
			perma = perma.substring(0, perma.length() - 2);
		return "https://www.reddit.com" + perma + "about.json";
	}
	
	public Submission[] getSubmissions(String subreddit) {
		return this.getSubmissions(subreddit, 100);
	}
	public Submission[] getSubmissions(String subreddit, int count) {
		Submissions subs = new Submissions(this.client);
		List<Submission> list = subs.ofSubreddit
			(subreddit, SubmissionSort.NEW, -1, count, null, null, true);
		Submission[] array = (Submission[]) list.toArray(new Submission[list.size()]);
		return array;
	}
	public Submission getRandomSubmission(String subreddit) {
		return this.getRandomSubmission(subreddit, 100);
	}
	public Submission getRandomSubmission(String subreddit, int sampleSize) {
		Submission[] subms = this.getSubmissions(subreddit, sampleSize);
		if(subms.length < 1) return null;
		int rng = this.random.nextInt(subms.length);
		Submission pick = subms[rng];
		return pick;
	}
	public Submission getRandomSubmission(String[] subreddits) {
		String pick = subreddits[this.random.nextInt(subreddits.length)];
		return this.getRandomSubmission(pick, 100);
	}
	public Submission getRandomSubmission(String[] subreddits, int sampleSize) {
		String pick = subreddits[this.random.nextInt(subreddits.length)];
		return this.getRandomSubmission(pick, sampleSize);
	}
	
	public SubredditInfo getSubredditInfo(String subreddit) {
		JSONParser parsing = new JSONParser();
		try {
			String url = this.getSubredditAboutUrl(subreddit);
			String content = BonziUtils.getStringFrom(url);
			JSONObject obj = (JSONObject)parsing.parse(content);
			return new SubredditInfo(obj);
		} catch (ParseException e) {
			InternalLogger.printError(e);
		} catch(FileNotFoundException e) {
			return null;
		}
		return null;
	}
	public String getSubredditInfoString(String subreddit) {
		String content = "";
		try {
			String url = this.getSubredditAboutUrl(subreddit);
			content = BonziUtils.getStringFrom(url);
		} catch (FileNotFoundException e) {
			InternalLogger.printError(e);
		}
		return content;
	}
	public SubredditPostVideoData getSubredditPostInfo(String url) {
		if(!url.endsWith("about.json")) {
			if(url.endsWith("/"))
				url += "about.json";
			else
				url += "/about.json";
		}
		
		JSONParser parsing = new JSONParser();
		try {
			String content = BonziUtils.getStringFrom(url);
			JSONArray obj = (JSONArray)parsing.parse(content);
			return new SubredditPostVideoData((JSONObject)obj.get(0));
		} catch (ParseException e) {
			InternalLogger.printError(e);
		} catch (FileNotFoundException e) {
			InternalLogger.printError(e);
		}
		return null;
	}
}