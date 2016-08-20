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

public class VideoItem {
	private String title;
	private String description;
	private String thumbnailURL;
	private String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	} 
	
	public String getThumbnailURL() {
		return thumbnailURL;
	}
	
	public void setThumbnailURL(String thumbnail) {
		this.thumbnailURL = thumbnail;		
	}
		
}
