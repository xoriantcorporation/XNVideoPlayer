/*#
	        # Copyright 2016 Xoriant Corporation.
	        #
	        # Licensed under the Apache License, Version 2.0 (the "License");
	        # you may not use this file except in compliance with the License.
	        # You may obtain a copy of the License at
	        #
	        #     http://www.apache.org/licenses/LICENSE-2.0

	        #
	        # Unless required by applicable law or agreed to in writing, software
	        # distributed under the License is distributed on an "AS IS" BASIS,
	        # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	        # See the License for the specific language governing permissions and
	        # limitations under the License.
	        #
*/

package com.xor.xnvideoplayer;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeConnector {
	private YouTube youtube; 
	private YouTube.Search.List query;
	
	// Your developer key goes here
	public static final String KEY = "AIzaSyBpSGU0w7j9NxNtbItv0Cg4pbGnCs66LH8";
	public YoutubeConnector(Context context) { 
		youtube = new YouTube.Builder(new NetHttpTransport(), 
				new JacksonFactory(), new HttpRequestInitializer() {			
			@Override
			public void initialize(HttpRequest hr) throws IOException {}
		}).setApplicationName(/*getString(R.string.app_name)*/"").build();

		try{
			query = youtube.search().list("id,snippet");
			query.setKey(KEY);			
			query.setType("video");
			query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");								
		}catch(IOException e){
			Log.d("YC", "Could not initialize: "+e);
		}
	}
	
	public List<VideoItem> search(String keywords){
		query.setQ(keywords);		
		try{
			SearchListResponse response = query.execute();
			List<SearchResult> results = response.getItems();
			
			List<VideoItem> items = new ArrayList<VideoItem>();
			for(SearchResult result:results){
				VideoItem item = new VideoItem();
				item.setTitle(result.getSnippet().getTitle());
				item.setDescription(result.getSnippet().getDescription());
				item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
				item.setId(result.getId().getVideoId());

				items.add(item);			
			}
			return items;
		}catch(IOException e){
			Log.d("YC", "Could not search: "+e);
			return null;
		}		
	}
}
