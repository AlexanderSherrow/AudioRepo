package model;
import javafx.scene.media.Media;
public class Song
{
	Media media;
	String songName;
	String artistName;
	String genre;
	String path;
	
	public Song(Media media, String songName, String artistName, String genre, String path) 
	{
		super();
		this.media = media;
		this.songName = songName;
		this.artistName = artistName;
		this.genre = genre;
		this.path = path;

	}

	public Media getMedia() 
	{
		return media;
	}

	public void setMedia(Media media)
	{
		this.media = media;
	}

	public String getSongName()
	{
		return songName;
	}

	public void setSongName(String songName) 
	{
		this.songName = songName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	


	
	
}
