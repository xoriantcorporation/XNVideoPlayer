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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnDragListener{

	private EditText searchInput;
	private ListView videosFound;
	
	private Handler handler;
	
	private List<VideoItem> searchResults;
	ArrayAdapter<VideoItem> adapter;

	private static final int RECOVERY_REQUEST = 1;
	private YouTubePlayerView youTubeView;
	private MyPlayerStateChangeListener playerStateChangeListener;
	private MyPlaybackEventListener playbackEventListener;
	private YouTubePlayer player;
	private LinearLayout linearLayout;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		linearLayout = (LinearLayout) findViewById(R.id.rootLinearLayout);
		linearLayout.setOnDragListener(this);
		searchInput = (EditText)findViewById(R.id.search_input);
		videosFound = (ListView)findViewById(R.id.videos_found);
		
		handler = new Handler();
		
		addClickListener();
		
		searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					searchOnYoutube(v.getText().toString());
					return false;
				}
				return true;
			}
		});


		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view1);
		youTubeView.initialize(Config.YOUTUBE_API_KEY, this);


		playerStateChangeListener = new MyPlayerStateChangeListener();
		playbackEventListener = new MyPlaybackEventListener();

		init("Trending Videos");
				
	}
	
	private void addClickListener(){
		videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,
									long id) {

				Config.YOUTUBE_ID=searchResults.get(pos).getId();
				player.loadVideo(Config.YOUTUBE_ID/*"hkKi1upK4z0"*/);
			}

		});
	}

	private void searchOnYoutube(final String keywords){
		new Thread(){
			public void run(){
				YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
				searchResults = yc.search(keywords);
				handler.post(new Runnable(){
					public void run(){
						updateVideosFound(0);
					}
				});
			}
		}.start();

	}


	private void init(final String keywords){
		new Thread(){
			public void run(){
				YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
				Log.d("Search Avtivity ", "Search Avtivity keywords "+keywords);

				searchResults = yc.search(keywords);

				handler.post(new Runnable(){
					public void run(){
						updateVideosFound(0);
					}
				});
			}
		}.start();

	}

	private void initnew(final String keywords){

		new Thread(){
			public void run(){
				YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
				Log.d("Search Avtivity ", "Search Avtivity keywords "+keywords);
				searchResults = yc.search(keywords);
				handler.post(new Runnable(){
					public void run(){
						updateVideosFound(1);
					}
				});
			}
		}.start();

	}
	
	private void updateVideosFound(int id){

		adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
				}

				ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
				TextView title = (TextView)convertView.findViewById(R.id.video_title);
				TextView description = (TextView)convertView.findViewById(R.id.video_description);
				
				VideoItem searchResult = searchResults.get(position);
				
				Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
				title.setText(searchResult.getTitle());
				description.setText(searchResult.getDescription());


				return convertView;
			}

			@Override
			public int getCount() {
				if(searchResults != null)
				return searchResults.size();
				return 0;
			}
		};
		if(adapter!=null /*&& videosFound.getChildCount() > 0*/){
			videosFound.setAdapter(adapter);
			adapter.notifyDataSetChanged();

			if(id==1)
			{
				if(searchResults != null) {
					for (int i = 0; i < searchResults.size(); i++) {
						Log.d("Search Results ", "Search Results " + searchResults.get(i).getTitle() + " " + searchResults.get(i).getId());
					}
					Config.YOUTUBE_ID = searchResults.get(0).getId();
					Log.d("Search Results ", "Search Results youtube id " + Config.YOUTUBE_ID);
					player.loadVideo(Config.YOUTUBE_ID/*"hkKi1upK4z0"*/);
				}
				searchInput.setVisibility(View.GONE);
			}
			else
			{
				if(searchResults != null) {
					Config.YOUTUBE_ID = searchResults.get(0).getId();
					Log.d("Search ", "Search " + Config.YOUTUBE_ID);
				}
				searchInput.setVisibility(View.VISIBLE);
			}
		}

	}


	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		this.player = player;
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.setPlaybackEventListener(playbackEventListener);
		Log.d("initialisation ", "initialisation success called");
		if (!wasRestored)
		{
			player.loadVideo(Config.YOUTUBE_ID);
		}
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
		} else {
			String error = /*String.format(getString(R.string.player_error),*/ errorReason.toString();//);
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECOVERY_REQUEST) {
			// Retry initialization if user performed a recovery action
			getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
		}
	}

	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return youTubeView;
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

		@Override
		public void onPlaying() {
			// Called when playback starts, either due to user action or call to play().
		}

		@Override
		public void onPaused() {
			// Called when playback is paused, either due to user action or call to pause().
		}

		@Override
		public void onStopped() {
			// Called when playback stops for a reason other than being paused.
		}

		@Override
		public void onBuffering(boolean b) {
			// Called when buffering starts or ends.
		}

		@Override
		public void onSeekTo(int i) {
			// Called when a jump in playback position occurs, either
			// due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
		}
	}

	private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

		@Override
		public void onLoading() {
			// Called when the player is loading a video
			// At this point, it's not ready to accept commands affecting playback such as play() or pause()
		}

		@Override
		public void onLoaded(String s) {
			// Called when a video is done loading.
			// Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
		}

		@Override
		public void onAdStarted() {
			// Called when playback of an advertisement starts.
		}

		@Override
		public void onVideoStarted() {
			// Called when playback of the video starts.
		}

		@Override
		public void onVideoEnded() {
			// Called when the video reaches its end.
		}

		@Override
		public void onError(YouTubePlayer.ErrorReason errorReason) {
			// Called when an error occurs.
			showMessage("Error "+errorReason.name());
		}
	}


	@Override
	public boolean onDrag(View view, DragEvent dragEvent) {

		switch (dragEvent.getAction())
		{
			case DragEvent.ACTION_DRAG_ENTERED :

				break;
			case DragEvent.ACTION_DRAG_EXITED :
				break;
			case DragEvent.ACTION_DRAG_ENDED :
				break;
			case DragEvent.ACTION_DROP :
				String str = ""+dragEvent.getClipData().getItemAt(0).getText();
				Toast.makeText(this, "on drop "+str, Toast.LENGTH_SHORT).show();
				initnew(str);

				break;
			case DragEvent.ACTION_DRAG_LOCATION:

				break;
			case DragEvent.ACTION_DRAG_STARTED:

				break;
		}
		return true;
	}

}
