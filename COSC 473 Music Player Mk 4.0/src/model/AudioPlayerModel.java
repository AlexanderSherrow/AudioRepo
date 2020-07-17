package model;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AudioPlayerModel extends Application 
{

	static CurrentFolder currentFolder = new CurrentFolder(null);
	Boolean currentSelection = false;
	static MediaPlayer mediaPlayer;
	Scene scene;
	Media media;
	double width;
	double height;
	int labelBarFontSize = 24;

	MediaView mediaView;

	//Strings
	String playListBoxBackgroundColor = ("-fx-background-color:#121212");
	String playListBodyColor = ("-fx-body-color:#121212");
	String bottomBoxBackgroundColor = ("-fx-background-color:#282828");
	String songLibraryBackgroundColor = ("-fx-background-color:#181818");
	String songLibraryBodyColor = ("-fx-background-color:#181818");
	String selectedButtonColorBackground = ("-fx-background-color:#1DB954");
	String selectedButtonColorBody = ("-fx-body-color:#1DB954");
	String labelBarFont = ("Arial");
	String labelBarFontColor = ("#b3b3b3");

	//Images
	Image playButtonImage = new Image("/icons/play.png", 40, 40, false, false);
	Image pauseButtonImage = new Image("/icons/pause.png", 40, 40, false, false);
	Image forwardButtonImage = new Image("/icons/forward.png", 40, 40, false, false);
	Image backwardButtonImage = new Image("/icons/backward.png", 40, 40, false, false);
	Image volumeButtonImage = new Image("/icons/volume.png", 40, 40, false, false);
	Image volumeMuteImage = new Image("/icons/volume_mute.png", 40, 40, false, false);

	//Labels
	Label titleLabel = new Label("Title");
	Label artistLabel = new Label("Artist");
	Label genreLabel = new Label("Genre");
	static Label currentSongLabel = new Label();
	Label yourLibraryLabel = new Label("Your Library");
	Label yourPlayListLabel = new Label("Your Playlists");
	Label currentArtistLabel = new Label("Aritst(Coming soon)");

	//Boxes
	VBox outterTotalBox = new VBox();
	VBox songButtonBox = new VBox();
	VBox playListBox = new VBox();
	VBox playListControlBox = new VBox();
	VBox songLibraryBox = new VBox();
	VBox bmcBox = new VBox(); // bottom middle control box
	VBox songInfoBox = new VBox();
	VBox albumImageBox = new VBox();

	HBox outterTopBox = new HBox();
	HBox outterCenterBox = new HBox();
	HBox outterBottomBox = new HBox();
	HBox labelBox = new HBox();
	HBox timerBox = new HBox();
	HBox controlBox = new HBox();
	HBox blcBox = new HBox(); // bottom left control box
	HBox brcBox = new HBox(); // bottom right control box

	//ScrollPanes
	ScrollPane sp = new ScrollPane();
	ScrollPane playListSrollPane = new ScrollPane();

	//Buttons
	Button forwardButton = new Button();
	Button playButton = new Button();
	Button backwardButton = new Button();
	Button volumeButton = new Button();
	Button addSongButton = new Button("Add Song");
	Button addPlaylistButton = new Button("Add playlist");

	//TextField
	TextField playListNameField = new TextField();

	//Sliders
	Slider volumeSlider = new Slider(0, 100, 50);

	ArrayList<Song> mediaList = new ArrayList<Song>();
	static ArrayList<Playlist> folderList = new ArrayList<Playlist>();
	ArrayList<String> toBeDeleted = new ArrayList<String>();


	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException 
	{
		if (localDirectoryExist() == true) 
		{
			addLocalLibrary();
		}
		else
		{
			setLibraryPath();
			String localLibraryPath = (filePathGetter() + "\\Local Library\\");
			String mainFolderPath = (localLibraryPath + "Main\\");
			createLocalDirectory(localLibraryPath);
			createLocalDirectory(mainFolderPath);
			buildLocalLibrary();
		}
		scene = setScene(this.width, this.height);
		scene.getStylesheets().add("sheet.css");
		primaryStage.setTitle("Audio Player Project");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				System.gc();
				deleteOnClose();
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public String filePathGetter() throws IOException //Takes a path from libraryPath.txt and uses it to find library
	{
		File files = new File("libraryPath.txt");
		if (files.exists()) 
		{
			Scanner scan = new Scanner(files);
			String filePath = scan.nextLine();
			return filePath;    		
		}
		return "nah";
	}

	public void filePathWriter() throws IOException //If the path file currently does not exist, method will write one and store in local program directory
	{
		String filePath = "libraryPath.txt";
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		chooser.showOpenDialog(null);

		File files = chooser.getSelectedFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(files.getAbsolutePath());
		writer.close();
	}

	public void setLibraryPath() throws IOException //creates a file and summons the path writer to write to it
	{
		File file =  new File("libraryPath.txt"); 
		file.createNewFile();
		filePathWriter();
	}

	public Scene setScene(double width, double height) throws IOException 
	{
		this.height = height;
		this.width = width;
		System.out.println(folderList.get(0));
		Playlist mainPlaylist = folderList.get(0);
		ArrayList<Song> mainList = mainPlaylist.getPlaylist();
		Song song =  mainList.get(0);

		media = song.getMedia();
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(false);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(addLibraryDisplay());
		borderPane.setStyle("-fx-background-color: Black");

		scene = new Scene(borderPane, 950, 580);
		scene.setFill(Color.BLACK);
		return scene;
	}

	public void addLocalLibrary() throws IOException //attempts to add local library from files if one exist
	{
		folderList = new ArrayList<Playlist>();
		File[] directories = new File(filePathGetter() + "\\Local Library\\").listFiles(File::isDirectory);
		for (int i = 0; i < directories.length; i++) 
		{
			ArrayList<Song> songList = new ArrayList<Song>();
			File folder = (directories[i]);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) 
			{
				if (file.isFile()) 
				{
					String name = file.getName();
					String path = file.getAbsolutePath();
					path = path.replace("\\", "/");
					media = new Media(new File(path).toURI().toString());
					Song song = new Song(media, name, "artist", "genre", path);
					songList.add(song);
				}
			}
			Playlist playlist = new Playlist(folder.getName(), folder.getAbsolutePath(), songList);
			folderList.add(playlist);
		}
	}

	public void buildLocalLibrary() throws IOException //if a local library is not found, method builds one
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		Component frame = null;
		String destinationFilePath = (filePathGetter() + "\\Local Library\\Main\\");
		chooser.showOpenDialog(frame);
		ArrayList<Song> songList = new ArrayList<Song>();

		File[] files = chooser.getSelectedFiles();

		for (int i = 0; i < files.length; i++) 
		{
			String name = files[i].getName();
			String copiedFilePath = (destinationFilePath + name);
			File localLibraryPath = new File(copiedFilePath);

			String path = files[i].getAbsolutePath();
			File file = new File(path);
			copyFiles(file, localLibraryPath);
			path = path.replace("\\", "/");
			media = new Media(new File(path).toURI().toString());
			Song song = new Song(media, name, "artist", "genre", path);
			songList.add(song);
		}

		Playlist playlist = new Playlist("Main", filePathGetter() + "\\Local Library\\Main\\", songList);
		folderList.add(playlist);
	}

	public void deleteSong(String path) throws IOException
	{
		for(int i = 0; i < folderList.size(); i++)
		{
			if(folderList.get(i).getPath() == currentFolder.getCurrentFolder())
			{
				for(int j = 0; j < folderList.get(i).getPlaylist().size(); j++)
				{
					System.out.println(folderList.get(i).getPlaylist().get(j).getPath());
					if(folderList.get(i).getPlaylist().get(j).getPath().equals(path))
					{
						System.out.println(path + "found");
						toBeDeleted.add(path);
						folderList.get(i).getPlaylist().remove(j);
					}
				}
			}
		}
		createSongButtons();
		createPlaylistButtons();
	}

	public void deleteOnClose()
	{
		for(int i = 0; i < toBeDeleted.size(); i++)
		{
			File file = new File((String) toBeDeleted.get(i));
			if(file.delete())
			{
				System.out.println(toBeDeleted.get(i) + "deleted.");
			}
			else
			{
				System.out.println("Failed to delete" + toBeDeleted.get(i));
			}
		}
	}

	public void createPlaylistButtons() throws IOException //creates the playlist buttons
	{
		sp.setStyle("-fx-background:#181818");//Do not delete
		playListSrollPane.setStyle("-fx-background:#121212");
		VBox playListBox = new VBox();

		for (int i = 0; i < folderList.size(); i++) //Loops to create all the playlist buttons
		{
			Button button = new Button(folderList.get(i).getPlaylistName());
			button.setTextFill(Color.web("#b3b3b3"));
			button.setStyle(playListBodyColor);
			button.setStyle(playListBoxBackgroundColor);			
			String currentPath = folderList.get(i).getPath();


			button.setTextFill(Color.WHITE);
			button.setOnAction((ActionEvent e) -> 
			{
				currentFolder.setCurrentFolder(currentPath);
				createSongButtons();
			}
					);

			button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
			{
				button.setTextFill(Color.WHITE);
				button.setStyle(selectedButtonColorBody);
				button.setStyle(selectedButtonColorBackground);
			}
					);

			button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
			{

				button.setTextFill(Color.WHITE);
				button.setStyle(playListBodyColor);
				button.setStyle(playListBoxBackgroundColor);
			}
					);
			System.out.println(currentPath + " loaded");
			playListBox.getChildren().add(button);
		}

		playListSrollPane.setMinWidth(200);
		playListBox.setStyle(playListBoxBackgroundColor);
		playListControlBox.setStyle(playListBoxBackgroundColor);
		playListSrollPane.setContent(playListBox);
	}

	public void makePlayListButton() throws IOException //creates the button to handle adding more playlist buttons
	{
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String playlistName = scan.nextLine();
		String directoryPath = (filePathGetter() + "\\Local Library\\"+ playlistName);
		Playlist playlist = new Playlist(playlistName, directoryPath, mediaList);
		createLocalDirectory(directoryPath);
		folderList.add(playlist);
		createPlaylistButtons();
	}

	public VBox addLibraryDisplay() throws IOException //creates the display
	{
		isPlaying isPlaying = new isPlaying(false);

		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		playListSrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		playListSrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

		forwardButton.setGraphic(new ImageView(forwardButtonImage));
		playButton.setGraphic(new ImageView(playButtonImage));
		backwardButton.setGraphic(new ImageView(backwardButtonImage));
		volumeButton.setGraphic(new ImageView(volumeButtonImage));

		controlBox.setPadding(new Insets(20));
		controlBox.setAlignment(Pos.BOTTOM_CENTER);
		controlBox.alignmentProperty().isBound();
		controlBox.setSpacing(5);

		brcBox.setPadding(new Insets(20));
		brcBox.setAlignment(Pos.BOTTOM_CENTER);
		brcBox.alignmentProperty().isBound();
		brcBox.setSpacing(5);

		volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
		volumeSlider.setMinWidth(30);
		currentSongLabel.setMinWidth(300);
		titleLabel.setMinWidth(375);
		artistLabel.setMinWidth(175);
		currentArtistLabel.maxWidth(200);
		currentSongLabel.maxWidth(200);

		albumImageBox.setMinWidth(100);

		labelBox.setMinHeight(25);
		labelBox.setMaxHeight(25);

		addSongButton.setMinWidth(375);

		songButtonBox.setMinHeight(425);
		songButtonBox.setMinWidth(900);

		sp.setMaxHeight(400);
		sp.setMinWidth(750);

		songLibraryBox.setMaxHeight(465);
		songInfoBox.setMinWidth(200);
		songInfoBox.setMinHeight(40);

		controlBox.setMinWidth(350);
		controlBox.setMaxHeight(250);

		blcBox.setMaxHeight(250);

		brcBox.setMaxHeight(250);
		brcBox.setMinHeight(40);
		brcBox.setMinWidth(300);

		outterTopBox.setMinHeight(80);

		outterCenterBox.setMaxWidth(950);
		outterCenterBox.setMinHeight(425);
		outterCenterBox.setMaxHeight(425);


		outterBottomBox.setMinWidth(900);
		outterBottomBox.setMinHeight(20);

		outterTotalBox.setPrefWidth(250);
		outterTotalBox.setMinHeight(600);

		currentSongLabel.setTextFill(Color.web(labelBarFontColor));
		currentSongLabel.setFont(new Font("Proxima Nova", 12));
		currentArtistLabel.setTextFill(Color.web(labelBarFontColor));

		yourPlayListLabel.setTextFill(Color.web(labelBarFontColor));

		titleLabel.setFont(new Font(labelBarFont, labelBarFontSize));
		titleLabel.setTextFill(Color.web(labelBarFontColor));
		artistLabel.setFont(new Font(labelBarFont, labelBarFontSize));
		artistLabel.setTextFill(Color.web(labelBarFontColor));
		genreLabel.setFont(new Font(labelBarFont, labelBarFontSize));
		genreLabel.setTextFill(Color.web(labelBarFontColor));

		playListBox.setStyle(playListBoxBackgroundColor);
		outterTopBox.setStyle(playListBoxBackgroundColor);

		songLibraryBox.setStyle(songLibraryBackgroundColor);
		songButtonBox.setStyle(songLibraryBackgroundColor);
		sp.setStyle(songLibraryBackgroundColor);
		addSongButton.setStyle(songLibraryBackgroundColor);

		backwardButton.setStyle(bottomBoxBackgroundColor);
		forwardButton.setStyle(bottomBoxBackgroundColor);
		playButton.setStyle(bottomBoxBackgroundColor);
		volumeButton.setStyle(bottomBoxBackgroundColor);
		outterCenterBox.setStyle(bottomBoxBackgroundColor);
		outterBottomBox.setStyle(bottomBoxBackgroundColor);
		addSongButton.setTextFill(Color.WHITE);
		addSongButton.setAlignment(Pos.CENTER_LEFT);

		addPlaylistButton.setTextFill(Color.WHITE);
		addPlaylistButton.setStyle(playListBodyColor);
		addPlaylistButton.setStyle(playListBoxBackgroundColor);

		songInfoBox.getChildren().addAll(currentSongLabel, currentArtistLabel);
		songButtonBox.getChildren().add(addSongButton);
		outterTopBox.getChildren().addAll(yourLibraryLabel);
		playListControlBox.getChildren().addAll(yourPlayListLabel);
		playListControlBox.getChildren().addAll(addPlaylistButton);
		createPlaylistButtons();
		playListControlBox.getChildren().add(playListSrollPane);
		labelBox.getChildren().addAll(titleLabel, artistLabel, genreLabel);
		songLibraryBox.getChildren().addAll(labelBox, sp);
		controlBox.getChildren().addAll(backwardButton, playButton, forwardButton);
		bmcBox.getChildren().addAll(controlBox, timerBox);
		brcBox.getChildren().addAll(volumeButton, volumeSlider);
		blcBox.getChildren().addAll(albumImageBox, songInfoBox);
		outterBottomBox.getChildren().addAll(blcBox, bmcBox, brcBox);
		outterCenterBox.getChildren().addAll(playListControlBox, songLibraryBox);
		outterTotalBox.getChildren().addAll(outterTopBox, outterCenterBox, outterBottomBox);
		playListNameField.setStyle(playListBoxBackgroundColor);


		playButton.setOnAction((ActionEvent e) -> 
		{
			if (isPlaying.getBool() == false) 
			{
				mediaPlayer.play();
				isPlaying.setBool(true);
				playButton.setGraphic(new ImageView(pauseButtonImage));
			}
			else if (isPlaying.getBool() == true) 
			{
				mediaPlayer.pause();
				isPlaying.setBool(false);
				playButton.setGraphic(new ImageView(playButtonImage));
			}
		}
				);

		playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
		{
			playButton.setStyle("-fx-body-color:#282828");
			playButton.setStyle("-fx-background-color:#282828");
		}
				);

		playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
		{
			playButton.setStyle("-fx-body-color:#282828");
			playButton.setStyle("-fx-background-color:#282828");
		}
				);

		forwardButton.setOnAction((ActionEvent e) -> {
			incrementSongForward();
			isPlaying.setBool(true);
			playButton.setGraphic(new ImageView(pauseButtonImage));
		}
				);

		forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
		{
			forwardButton.setStyle("-fx-body-color:#282828");
			forwardButton.setStyle("-fx-background-color:#282828");
		}
				);

		forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) ->
		{
			forwardButton.setStyle("-fx-body-color:#282828");
			forwardButton.setStyle("-fx-background-color:#282828");
		}
				);

		backwardButton.setOnAction((ActionEvent e) -> 
		{
			incrementSongBackward();
			isPlaying.setBool(true);
			playButton.setGraphic(new ImageView(pauseButtonImage));
		}
				);

		backwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
		{
			backwardButton.setStyle("-fx-body-color:#282828");
			backwardButton.setStyle("-fx-background-color:#282828");
		}
				);

		backwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
		{
			backwardButton.setStyle("-fx-body-color:#282828");
			backwardButton.setStyle("-fx-background-color:#282828");
		}
				);

		volumeButton.setOnAction((ActionEvent e) -> 
		{
			if (mediaPlayer.isMute() == true) 
			{
				volumeButton.setGraphic(new ImageView(volumeButtonImage));
				mediaPlayer.setMute(false);
			}
			else 
			{
				volumeButton.setGraphic(new ImageView(volumeMuteImage));
				mediaPlayer.setMute(true);
			}

		}
				);

		volumeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) ->
		{
			volumeButton.setStyle("-fx-body-color:#282828");
			volumeButton.setStyle("-fx-background-color:#282828");
		}
				);

		volumeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) ->
		{
			volumeButton.setStyle("-fx-body-color:#282828");
			volumeButton.setStyle("-fx-background-color:#282828");
		}
				);

		volumeSlider.valueProperty().addListener(new InvalidationListener() 
		{
			@Override
			public void invalidated(Observable ov) 
			{
				if (volumeSlider.isValueChanging()) 
				{
					mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
					if (volumeSlider.getValue() == 0) 
					{
						volumeButton.setGraphic(new ImageView(volumeMuteImage));
					}
					else 
					{
						volumeButton.setGraphic(new ImageView(volumeButtonImage));
					}
				}
			}
		}
				);

		addSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) ->
		{
			mediaPlayer.stop();
			isPlaying.setBool(false);
			playButton.setGraphic(new ImageView(playButtonImage));
			String path = null;

			try 
			{
				path = (filePathGetter() + "//Local Library//Main//");
			}
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			path = path.replace("\\", "/");
			String destinationFilePath = (path);
			try 
			{
				addSong(destinationFilePath);
			}
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
				);

		addSongButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
		{
			addSongButton.setStyle(selectedButtonColorBody);
			addSongButton.setStyle(selectedButtonColorBackground);
		}
				);

		addSongButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
		{
			addSongButton.setStyle("-fx-body-color:#181818");
			addSongButton.setStyle("-fx-background-color:#181818");
		}
				);

		addPlaylistButton.setOnAction((ActionEvent e) -> {

			try 
			{
				makePlayListButton();
			}
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
				);

		addPlaylistButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
		{
			addPlaylistButton.setStyle(selectedButtonColorBody);
			addPlaylistButton.setStyle(selectedButtonColorBackground);

		}
				);

		addPlaylistButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
		{

			addPlaylistButton.setStyle(playListBodyColor);
			addPlaylistButton.setStyle(playListBoxBackgroundColor);
		}
				);
		return outterTotalBox;
	}

	public void createSongButtons() //creates song buttons
	{
		String playListPath = getMatchingPlaylist(folderList);
		mediaList = new ArrayList<Song>();
		for(int i = 0; i < folderList.size(); i++)
		{
			if(playListPath.equals(folderList.get(i).getPath()))
			{
				mediaList = folderList.get(i).getPlaylist();
			}
		}

		VBox songButtons = new VBox();
		songButtons.setStyle(songLibraryBackgroundColor);
		songButtons.getChildren().add(addSongButton);
		for (int i = 0; i < mediaList.size(); i++) 
		{
			isPlaying isPlaying = new isPlaying(false);
			ContextMenu contextMenu = new ContextMenu();
			MenuItem menuItem1 = new MenuItem("Delete Song");

			HBox buttonBox = new HBox();
			Song song = mediaList.get(i);
			Media newMedia = song.getMedia();
			Button button = new Button(song.getSongName().replace(".mp3", ""));
			button.setMinWidth(375);
			button.setMaxWidth(375);
			button.setTextFill(Color.WHITE);
			button.setAlignment(Pos.CENTER_LEFT);
			button.setStyle("-fx-body-color:#181818");
			button.setStyle("-fx-background-color:#181818");

			Button artistButton = new Button("Artist");
			artistButton.setMinWidth(175);
			artistButton.setMaxWidth(175);
			artistButton.setTextFill(Color.WHITE);
			artistButton.setAlignment(Pos.CENTER_LEFT);
			artistButton.setStyle("-fx-body-color:#181818");
			artistButton.setStyle("-fx-background-color:#181818");

			Button genreButton = new Button("Genre");
			genreButton.setMinWidth(175);
			genreButton.setMaxWidth(175);
			genreButton.setTextFill(Color.WHITE);
			genreButton.setAlignment(Pos.CENTER_LEFT);
			genreButton.setStyle("-fx-body-color:#181818");
			genreButton.setStyle("-fx-background-color:#181818");

			button.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

				@Override
				public void handle(ContextMenuEvent event) {

					contextMenu.show(button, event.getScreenX(), event.getScreenY());
				}
			});

			artistButton.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

				@Override
				public void handle(ContextMenuEvent event) {

					contextMenu.show(artistButton, event.getScreenX(), event.getScreenY());
				}
			});
			genreButton.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

				@Override
				public void handle(ContextMenuEvent event) {

					contextMenu.show(genreButton, event.getScreenX(), event.getScreenY());
				}
			});

			menuItem1.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent e) {
					try {
						deleteSong(song.getPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
					);

			button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> 
			{
				if(e.getButton() == MouseButton.PRIMARY)
				{
					System.out.println("Primary button pressed");
					playButton.setGraphic(new ImageView(pauseButtonImage));
					isPlaying.setBool(true);
					mediaPlayer.stop();
					mediaPlayer = new MediaPlayer(newMedia);
					mediaPlayer.setAutoPlay(true);
					currentSongLabel.setText(song.getSongName().replace(".mp3", ""));
					buttonBox.setStyle(selectedButtonColorBackground);
					button.setStyle(selectedButtonColorBackground);
					artistButton.setStyle(selectedButtonColorBackground);
					genreButton.setStyle(selectedButtonColorBackground);
				}
			}
					);
			button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
			{
				buttonBox.setStyle(selectedButtonColorBackground);
				button.setStyle(selectedButtonColorBackground);
				artistButton.setStyle(selectedButtonColorBackground);
				genreButton.setStyle(selectedButtonColorBackground);
			}
					);

			button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
			{
				if(isPlaying.getBool() == false)
				{
					buttonBox.setStyle(songLibraryBackgroundColor);
					button.setStyle(songLibraryBackgroundColor);
					artistButton.setStyle(songLibraryBackgroundColor);
					genreButton.setStyle(songLibraryBackgroundColor);
				}
			}
					);

			artistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> 
			{
				if(e.getButton() == MouseButton.PRIMARY)
				{
					playButton.setGraphic(new ImageView(pauseButtonImage));
					isPlaying.setBool(true);
					mediaPlayer.stop();
					mediaPlayer = new MediaPlayer(newMedia);
					mediaPlayer.setAutoPlay(true);
					currentSongLabel.setText(song.getSongName().replace(".mp3", ""));
					buttonBox.setStyle(selectedButtonColorBackground);
					button.setStyle(selectedButtonColorBackground);
					artistButton.setStyle(selectedButtonColorBackground);
					genreButton.setStyle(selectedButtonColorBackground);
				}

				if(e.getButton() == MouseButton.SECONDARY) //song options
				{
					System.out.println("Seconday button pressed");
				}
			}
					);

			artistButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
			{
				buttonBox.setStyle(selectedButtonColorBackground);
				button.setStyle(selectedButtonColorBackground);
				artistButton.setStyle(selectedButtonColorBackground);
				genreButton.setStyle(selectedButtonColorBackground);
			}
					);

			artistButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
			{
				if(isPlaying.getBool() == false)
				{
					buttonBox.setStyle(songLibraryBackgroundColor);
					button.setStyle(songLibraryBackgroundColor);
					artistButton.setStyle(songLibraryBackgroundColor);
					genreButton.setStyle(songLibraryBackgroundColor);
				}
			}
					);

			genreButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> 
			{
				if(e.getButton() == MouseButton.PRIMARY)
				{
					playButton.setGraphic(new ImageView(pauseButtonImage));
					isPlaying.setBool(true);
					mediaPlayer.stop();
					mediaPlayer = new MediaPlayer(newMedia);
					mediaPlayer.setAutoPlay(true);
					currentSongLabel.setText(song.getSongName().replace(".mp3", ""));
					buttonBox.setStyle(selectedButtonColorBackground);
					button.setStyle(selectedButtonColorBackground);
					artistButton.setStyle(selectedButtonColorBackground);
					genreButton.setStyle(selectedButtonColorBackground);
				}

				if(e.getButton() == MouseButton.SECONDARY) //song options
				{
					System.out.println("Seconday button pressed");
				}
			}
					);

			genreButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> 
			{
				buttonBox.setStyle(selectedButtonColorBackground);
				button.setStyle(selectedButtonColorBackground);
				artistButton.setStyle(selectedButtonColorBackground);
				genreButton.setStyle(selectedButtonColorBackground);
			}
					);

			genreButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> 
			{
				if(isPlaying.getBool() == false)
				{
					buttonBox.setStyle(songLibraryBackgroundColor);
					button.setStyle(songLibraryBackgroundColor);
					artistButton.setStyle(songLibraryBackgroundColor);
					genreButton.setStyle(songLibraryBackgroundColor);
				}
			}
					);
			contextMenu.getItems().add(menuItem1);
			buttonBox.getChildren().addAll(button, artistButton, genreButton);
			songButtons.getChildren().add(buttonBox);
		}
		sp.setContent(songButtons);
	}

	public static void incrementSongForward() //Increments the current song forward or resets to the start of the list
	{
		String currentPlaylist = currentFolder.getCurrentFolder();
		for(int i = 0; i < folderList.size(); i++)
		{
			String arrayPlaylist = folderList.get(i).getPath();
			System.out.println(arrayPlaylist);
			if(currentPlaylist.equals(arrayPlaylist))
			{
				ArrayList<Song> playlist = folderList.get(i).getPlaylist();

				for(int j = 0; j < playlist.size(); j++)
				{
					if(mediaPlayer.getMedia() == playlist.get(j).getMedia())
					{
						mediaPlayer.stop();
						if(j+1 < playlist.size())
						{
							mediaPlayer = new MediaPlayer(playlist.get(j+1).getMedia());
							currentSongLabel.setText(playlist.get(j+1).getSongName().replace(".mp3", ""));
						}
						else
						{
							mediaPlayer = new MediaPlayer(playlist.get(0).getMedia());
							currentSongLabel.setText(playlist.get(0).getSongName().replace(".mp3", ""));
						}
						mediaPlayer.setAutoPlay(true);
						j = playlist.size();
					}
				}
			}
		}
	}

	public static void incrementSongBackward() //Increments the song backwards to the previous song or to the very end of the list if the start of the list is reached
	{
		String currentPlaylist = currentFolder.getCurrentFolder();
		for(int i = 0; i < folderList.size(); i++)
		{
			String arrayPlaylist = folderList.get(i).getPath();
			System.out.println(arrayPlaylist);
			if(currentPlaylist.equals(arrayPlaylist))
			{
				System.out.println("Matching playlist found");
				ArrayList<Song> playlist = folderList.get(i).getPlaylist();

				for(int j = 0; j < playlist.size(); j++)
				{
					if(mediaPlayer.getMedia() == playlist.get(j).getMedia())
					{
						System.out.println("Matching song found");
						mediaPlayer.stop();
						if(j-1 >= 0)
						{
							mediaPlayer = new MediaPlayer(playlist.get(j-1).getMedia());
							currentSongLabel.setText(playlist.get(j-1).getSongName().replace(".mp3", ""));
						}
						else
						{
							mediaPlayer = new MediaPlayer(playlist.get(playlist.size()-1).getMedia());
							currentSongLabel.setText(playlist.get(playlist.size()-1).getSongName().replace(".mp3", ""));
						}
						mediaPlayer.setAutoPlay(true);
						j = playlist.size();
					}
				}
			}
		}
	}

	public boolean localDirectoryExist() throws IOException //Checks whether the local directory exists for installation purposes
	{
		String path = filePathGetter();
		File file = new File(path + "\\Local Library");
		if (file.exists()) 
		{
			return true;
		}
		return false;
	}

	public void createLocalDirectory(String directoryPath) //Creates the local directory
	{
		File file = new File(directoryPath);
		if (!file.exists()) 
		{
			file.mkdir();
		}
	}

	private static void copyFiles(File source, File dest) //Copy files
	{
		try 
		{
			Files.copy(source.toPath(), dest.toPath());
		}
		catch (IOException e) 
		{
			System.out.println("This song already exist within the playlist");
		}
	}

	public String getMatchingPlaylist(ArrayList<Playlist> folderList)//finds the path for the current playlist
	{
		String currentFolderName = currentFolder.getCurrentFolder();
		System.out.println("This is the path it is searching for: " + currentFolder.getCurrentFolder());
		for(int j = 0; j < folderList.size(); j++)
		{
			if(currentFolderName.equals(folderList.get(j).getPath()))
			{
				System.out.println("The target folder is " + folderList.get(j).getPath());
				return folderList.get(j).getPath();
			}
		}
		return "null";
	}

	public void addSong(String destinationFilePath) throws IOException //adds song to the current folder 
	{
		JFileChooser chooser = new JFileChooser();
		String playListPath = getMatchingPlaylist(folderList);
		chooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MPEG3 songs", "mp3");
		chooser.addChoosableFileFilter(filter);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("mp3", "mp4"));
		chooser.setMultiSelectionEnabled(true);
		Component frame = null;
		chooser.showOpenDialog(frame);
		destinationFilePath = filePathGetter();
		File[] files = chooser.getSelectedFiles();

		for (int i = 0; i < files.length; i++) 
		{
			String name = files[i].getName();
			String copiedFilePath = (playListPath + "\\" + name);
			File localLibraryPath = new File(copiedFilePath);
			String path = files[i].getAbsolutePath();
			File file = new File(path);
			copyFiles(file, localLibraryPath);
		}
		addLocalLibrary(); 
		createPlaylistButtons();
		createSongButtons();
	}

}