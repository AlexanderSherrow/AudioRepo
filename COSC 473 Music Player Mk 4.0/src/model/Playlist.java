package model;
import java.util.ArrayList;

import javafx.scene.media.Media;
public class Playlist
{
	ArrayList<Song> playlist;
	String path;
	String playlistName;
	
	public Playlist(String playlistName, String path, ArrayList<Song> playlist) 
	{
		super();
		this.playlist = playlist;
		this.playlistName = playlistName;
		this.path = path;

	}

	public ArrayList<Song> getPlaylist() 
	{
		return playlist;
	}

	public void setPlaylist(ArrayList<Song> playlist) 
	{
		this.playlist = playlist;
	}

	public String getPath() 
	{
		return path;
	}

	public void setPath(String path) 
	{
		this.path = path;
	}

	public String getPlaylistName() 
	{
		return playlistName;
	}

	public void setPlaylistName(String playlistName) 
	{
		this.playlistName = playlistName;
	}

	

}