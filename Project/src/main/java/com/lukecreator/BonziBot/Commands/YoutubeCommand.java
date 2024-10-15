package com.lukecreator.BonziBot.Commands;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.lukecreator.BonziBot.BonziUtils;
import com.lukecreator.BonziBot.CommandAPI.Command;
import com.lukecreator.BonziBot.CommandAPI.CommandArgCollection;
import com.lukecreator.BonziBot.CommandAPI.CommandCategory;
import com.lukecreator.BonziBot.CommandAPI.CommandExecutionInfo;
import com.lukecreator.BonziBot.Data.GenericEmoji;
import com.lukecreator.BonziBot.NoUpload.Constants;

import java.io.IOException;
import java.util.List;

public class YoutubeCommand extends Command {

    public YoutubeCommand() {
        this.subCategory = 1;
        this.name = "Youtube";
        this.icon = GenericEmoji.fromEmote(1224517596410286090L, false);
        this.description = "Searches for a YouTube video and sends the best match.";
        this.args = CommandArgCollection.single("query");
        this.category = CommandCategory.UTILITIES;
        this.adminOnly = false;
        this.setCooldown(BonziUtils.getMsForSeconds(5));
    }

    @Override
    public void run(CommandExecutionInfo e) {
        String query = e.args.getString("query").replace("`", "");
        SearchListResponse _results = null;
        
        try {
            YouTube.Search.List request = e.bonzi.youtube.search().list("snippet");
            _results = request
                    .setKey(Constants.YTAPI_KEY)
                    .setMaxResults(1L)
                    .setQ(query)
                    .setOrder("relevance")
                    .setType("video")
                    .execute();
        } catch (IOException ex) {
            e.reply("Everything blew up.\n```" + ex + "```");
            return;
        }
        
        List<SearchResult> results = _results.getItems();
        
        if(results.isEmpty()) {
            e.reply(3, BonziUtils.failureEmbed("No results found.", "For search query:\n`" + query + "`"));
            return;
        }

        SearchResult result = results.get(0);
        String id = result.getId().getVideoId();
        String url = "https://www.youtube.com/watch?v=" + id;
        e.reply("Search result for `" + query + "`:\t\n" + url);
    }
}