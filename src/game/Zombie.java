package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;



import javafx.scene.image.Image;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;

import javafx.geometry.Point3D;

import javafx.scene.transform.Rotate;

/**
 * 
 * @author John Clark
 * @author Christian Seely
 * @author Duong 
 * 
 * This class represents a single zombie inside of the game, 
 * it holds its attributes such as sound, flags for collision detection, 
 * zombie type, its location, if its pathing finding and if it is path 
 * finding its path. 
 *
 */
public class Zombie
{
  private final String PATH = System.getProperty("user.dir") + "/src";
  private double zombieSpeed;
  private static double zombieDecRate; 
  private Point currentLoc, startLocation;
  private boolean isRandom, isMaster;
  private Random random = new Random();
  private boolean wallCollision = false;
  private boolean zombieCollision = false;
  private double angle;
  public boolean inRange;
  //Smoothing count is used to smooth out the movement of a zombie
  //when it is moving between two tiles during path finding. 
  public int smoothingCount = 0;
  //PathIndex represents the index inside of the path ArrayList for
  //the zombie. 
  public int pathIndex = 0;
  //Data Structures used in path finding, they both hold the path
  //but he ArrayList has it formatted correctly for how it is 
  //read inside of the House class. 
  private ArrayList<GraphNode> path;
  private static HashMap<GraphNode,GraphNode>cameFrom;
  //Boolean flag if the zombie is path finding or not. 
  public boolean isPathFinding = false;
  //Sound Clip objects for both the right and left sound channels
  //for the zombie sounds. 
  private Clip zombieSoundsRightChannel;
  private Clip zombieSoundsLeftChannel;
  //Flag if the sound channel is playing. 
  public boolean isZombieSoundsRightChannelPlaying = false;
  public boolean isZombieSoundsLeftChannelPlaying = false;
  //FloatControl objects that allow us manipulate the sound file
  //while they are running. 
  FloatControl zombieSoundsRightChannelVolumeControl;
  FloatControl zombieSoundsLeftChannelVolumeControl;
  
  
  private Point3D newLoc;
	private ObjModelImporter objModelImporter;
	public List<MeshView[]> movingZombie = new ArrayList<>();
	private MeshView zombie3D;
  
  
  /**
   * 
   * @param zombieSpeed
   *    The zombie's speed in tiles per second.
   * @param zombieDecRate
   *    The zombie's decision rate in seconds.
   * @param isRandom
   *    A boolean value determining whether or not the zombie is a random zombie
   *    or a linewalk zombie.
   * @param loc
   *    The zombie's start location in x,y coordinates.
   *    
   * This is a zombie constructor, that creates a zombie object and defines its starting attributes
   * and location.
   */
  public Zombie(double zombieSpeed, double decRate, boolean isRandom, boolean isMaster, Point startLocation)
  {
    this.zombieSpeed = zombieSpeed;
    zombieDecRate = decRate;
    this.isRandom = isRandom;
    //Initialize data structures. 
    this.path = new ArrayList<>();
    cameFrom = new HashMap<>();
    this.startLocation = startLocation;
    
    addZombie();
    modifiedZombie();
    
  }
  
  /**
   * 
   * @return If the zombie is the master zombie. 
   * gets whether or not this zombie is the master
   */
  public boolean getIsMaster()
  {
  	return isMaster;
  }

  /**
   * skin for each type of zombie and set direction for each zombie
   * */
  private void modifiedZombie()
  {
		final Image lightZombie = new Image("file:"+PATH+"/Resources/Images/zomby_light.png");
		final Image darkZombie = new Image("file:"+PATH+"/Resources/Images/zomby_dark.png");
		final Image masterZombie = new Image("file:"+PATH+"/Resources/Images/zomby_master.png");

    final PhongMaterial lightZombieSkin = new PhongMaterial();
    final PhongMaterial darkZombieSkin = new PhongMaterial();
    final PhongMaterial masterZombieSkin = new PhongMaterial();
    
    lightZombieSkin.setDiffuseMap(lightZombie);
    darkZombieSkin.setDiffuseMap(darkZombie);
    masterZombieSkin.setDiffuseMap(masterZombie);
    
    currentLoc = startLocation;
    zombie3D.setTranslateX(startLocation.getX() * 2);
    zombie3D.setTranslateZ(startLocation.getY() * 2);
    if(isRandom)
    {
      zombie3D.setMaterial(lightZombieSkin);
    }
    else if(isMaster)
    {
      zombie3D.setMaterial(masterZombieSkin);
    }
    else
    {
    	zombie3D.setMaterial(darkZombieSkin);
    }
    angle = random.nextDouble() * 360;
    zombie3D.setRotationAxis(Rotate.Y_AXIS);
    zombie3D.setRotate(angle);
  }
  
  /**
   * reading the zombie movement models from the source.
   * initialize zombie models with meshview and setup the zombie to fit the map
   * */
  private void addZombie()
  {
		MeshView[] mesh;
		Mesh triMesh;

		objModelImporter = new ObjModelImporter();
		objModelImporter.read(new File(PATH + "/Resources/ZombieAnimation/zombie0.obj"));
		mesh = objModelImporter.getImport();
		
		triMesh = mesh[0].getMesh();
		zombie3D = new MeshView(triMesh);
		
		zombie3D.setScaleX(2);
		zombie3D.setScaleY(2);
		zombie3D.setScaleZ(2);
		
		zombie3D.setTranslateY(1.4);
		
  }
  
  /**
   * 
   * @return MeshView of zombie. 
   */
  public MeshView getZombie3D()
  {
  	return zombie3D;
  }
  
  /**
   * @author Christian Seely
   * @param left Sound channel file. 
   * @param right Sound channel file. 
   * This method initializes the sound clips for the zombie object,
   * this is done once for every zombie object when it is created by
   * calling this method. 
   */
  public void initializeZombieSounds(File left, File right)
  {
    //Open the right channel sounds as a Clip Object. 
    try
    {
      AudioInputStream ais1 = AudioSystem.getAudioInputStream(right);
      DataLine.Info info1 = new DataLine.Info(Clip.class, ais1.getFormat());
      zombieSoundsRightChannel = (Clip) AudioSystem.getLine(info1);
      zombieSoundsRightChannel.open(ais1);
    } catch (UnsupportedAudioFileException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (LineUnavailableException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
   //Open the left channel sounds as a clip object. 
    try
    {
      AudioInputStream ais2 = AudioSystem.getAudioInputStream(left);
      DataLine.Info info2 = new DataLine.Info(Clip.class, ais2.getFormat());
      zombieSoundsLeftChannel = (Clip) AudioSystem.getLine(info2);
      zombieSoundsLeftChannel.open(ais2);
    } catch (UnsupportedAudioFileException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (LineUnavailableException e1)
    {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
   
  }
  /**
   * Close the clips for the zombies left and right channel. 
   */
  public void closeSounds()
  {
    zombieSoundsLeftChannel.close();
    zombieSoundsRightChannel.close();
  }
  /**
   * 
   * @param newX Location
   * @param newZ Location
   * Given the x and z value create a new point object to represent
   * the zombies current location. 
   */
  public void updateCurrentLoc(int newX, int newZ)
  {
    this.currentLoc = new Point(newX,newZ);
  }
  /**
   * 
   * @return The start location of the zombie. 
   */
  public Point getStartLocation()
  {
    return this.startLocation;
  }
  /**
   * 
   * @return Point current awt point of zombie. 
   */
  public Point getCurrentLoc()
  {
    return currentLoc;
  }
  /**
   * @author Christian Seely
   * Start the sounds of the zombie object, this is called
   * when the zombie is in range, e.g 15 euclidian distance. 
   */
  public void startSounds()
  {
    //Start the clips.
    this.zombieSoundsRightChannel.start();
    this.zombieSoundsLeftChannel.start();
    //Toggle their boolean flags.
    this.isZombieSoundsRightChannelPlaying = true;
    this.isZombieSoundsLeftChannelPlaying = true;
    //Make sure they continuously loop. 
    this.zombieSoundsRightChannel.loop(Clip.LOOP_CONTINUOUSLY);
    this.zombieSoundsLeftChannel.loop(Clip.LOOP_CONTINUOUSLY);
    
    //Get control of the sound files to allow us to change their DB levels. 
    this.zombieSoundsRightChannelVolumeControl = (FloatControl) this.zombieSoundsRightChannel.getControl(FloatControl.Type.MASTER_GAIN);          
    this.zombieSoundsLeftChannelVolumeControl = (FloatControl) this.zombieSoundsLeftChannel.getControl(FloatControl.Type.MASTER_GAIN);
    //Start the sounds initially as -15 DB's 
    this.zombieSoundsRightChannelVolumeControl.setValue(-15);
    this.zombieSoundsLeftChannelVolumeControl.setValue(-15);
  }
  /**
   * Stop the sound clips and untoggle their boolean values. 
   */
  public void stopSounds()
  {
    this.zombieSoundsRightChannel.stop();
    this.zombieSoundsLeftChannel.stop();
    this.isZombieSoundsRightChannelPlaying = false;
    this.isZombieSoundsLeftChannelPlaying = false;
  }
  /**
   * @author Christian Seely
   * @param betweenDistance Distance between the zombie and the player
   * @param left channel ratio 
   * @param right channel ratio
   * Given two left and right channel ratios between 0 and 100 which sum to
   * 100 calculate the new DB levels of the two sound channels to simulate
   * directional sounds. 
   */
  public void updateSounds(double betweenDistance,double left, double right)
  {
    //For these sound clips the range of DB's is -15 to 5 DB's so the channels
    //Range from -15 to 5 by multiplying the left/right ratios (0-100) by 
    //0.2 which allows for a minimum new volume of -15 DB's and a max DB value
    //of 5 DB. 
    float newRightChannelVolume = (float) (-15 + (0.2)*right); 
    float newLeftChannelVolume = (float) (-15 + (0.2)*left); 
    //If the sound channels are in range then get control of the sound clips so we can
    //change their DB levels. 
    if(newRightChannelVolume <=5 && newLeftChannelVolume <=5 && newRightChannelVolume >-80&&newLeftChannelVolume>-80)
    {
      this.zombieSoundsRightChannelVolumeControl = (FloatControl) this.zombieSoundsRightChannel.getControl(FloatControl.Type.MASTER_GAIN);          
      this.zombieSoundsLeftChannelVolumeControl = (FloatControl) this.zombieSoundsLeftChannel.getControl(FloatControl.Type.MASTER_GAIN);
    if(left>=0&&left<=100&&right>=0&&right<=100)
    {
      //Set the new DB levels of the two sound clips. 
    this.zombieSoundsRightChannelVolumeControl.setValue(newRightChannelVolume);
    this.zombieSoundsLeftChannelVolumeControl.setValue(newLeftChannelVolume);
    }
    }
  }
  
  /**
   * 
   * @param elapsedSeconds number of seconds since the last more
   * @return new Zombie location
   * 
   * moves the zombie forward, given its angle.
   */
  public Point3D move(double elapsedSeconds)
  {
    double xVelocity, zVelocity, deltaX, deltaZ, newX, newZ, oldX, oldZ;
    xVelocity = zombieSpeed * Math.sin(angle * (Math.PI / 180));
    zVelocity = zombieSpeed * Math.cos(angle * (Math.PI / 180));
    deltaX = xVelocity * elapsedSeconds;
    deltaZ = zVelocity * elapsedSeconds;
    oldX = zombie3D.getTranslateX();
    oldZ = zombie3D.getTranslateZ();
    newX = oldX + deltaX;
    newZ = oldZ + deltaZ;
    
    //Update the newLoc.
    newLoc = new Point3D(newX, 0, newZ);
    return newLoc;
  }
  /**
   * 
   * @return New 3DPoint location. 
   * gets the new zombie location.
   */
  public Point3D getNewLoc()
  {
    return this.newLoc;
  }
  /**
   * 
   * @param Set the new 3D Point location. 
   * sets the new zombie location
   */
  public void setNewLoc(Point3D newLoc)
  {
    this.newLoc = newLoc;
  }
  /**
   * 
   * @return If the zombie is a random zombie. 
   * gets whether or not the zombie is a random walk zombie
   */
  public boolean getIsRandom()
  {
    return this.isRandom;
  }
  /**
   * 
   * @return The zombies decision rate. 
   * returns the zombie's decision rate.
   */
  public static double getZombieDecRate()
  {
    return zombieDecRate;
  }
  
  /**
   * 
   * @param angle The angle that the zombie is moving in.
   * sets the zombies movement angle
   */
  public void setAngle(double angle)
  {
    this.angle = angle;
  }
  
  /**
   * 
   * @return zombie movement angle
   * gets the zombie's current movement angle
   */
  public double getAngle()
  {
    return this.angle;
  }
  /**
   * 
   * @return If the zombie had a wall collision. 
   * returns whether or not this zombie is colliding with a wall
   */
  public boolean getWallCollision()
  {
    return this.wallCollision;
  }
  /**
   * 
   * @param wallCollision Set if the zombie had a wall collision. 
   * sets whether or not this zombie is colliding with a wall.
   */
  public void setWallCollision(boolean wallCollision)
  {
    this.wallCollision = wallCollision;
  }
  /**
   * 
   * @return Get if the zombie had a collision. 
   * gets whether or not this zombie is colliding with another zombie
   */
  public boolean getZombieCollision()
  {
    return this.zombieCollision;
  }
  
  /**
   * 
   * @param zombieCollision Set if the zombie had a zombie on 
   * zombie collision. 
   * sets whether or not this zombie is colliding with another zombie
   */
  public void setZombieCollision(boolean zombieCollision)
  {
    this.zombieCollision = zombieCollision;
  }
  
  
  
  /**
   * 
   * @param zombieSpeed
   *    The zombie's speed in tiles per second.
   * 
   * This method sets this zombie's speed to the passed zombieSpeed parameter.
   */
  public void setZombieSpeed(double zombieSpeed)
  {
    this.zombieSpeed = zombieSpeed;
  }
  /**
   * 
   * @param node1 Current Node
   * @param node2 Next Node 
   * Put a key pair value which represents the current node and
   * the next node inside of the hashmap camefrom which is the
   * shortest path. 
   */
  public void putKeyValuePair(GraphNode node1, GraphNode node2)
  {
    cameFrom.put(node1,node2);
  }
  
  /**
   * 
   * @return Return the hashmap containing the path. 
   */
  public HashMap<GraphNode, GraphNode> getCameFromMap()
  {
    return cameFrom;
  }
  /**
   * 
   * @param isPathFinding If the zombie is path finding or now. 
   */
  public void setIsPathFinding(boolean isPathFinding)
  {
    this.isPathFinding = isPathFinding;
  }
  /**
   * 
   * @param path the shortest path from the zombie to the player
   * as an arrayList. 
   */
  public void setPath(ArrayList<GraphNode> path)
  {
    this.path=path;
  }
  /**
   * 
   * @return The shortest path from the zombie to the player as
   * an arrayList. 
   */
  public ArrayList<GraphNode> getPath()
  {
    return path;
  }
  /**
   * Initialize the path. 
   */
  public void initializePath() 
  {
    this.path = new ArrayList<>();
  }
  /**
   * Initialize the hashmap. 
   */
  public void initializeMap()
  {
    cameFrom = new HashMap<>();
  }



  /**
   * 
   * @param zombieDecRate
   *    The zombie's decision rate in seconds.
   *    
   * This method sets the zombie's decision rate to the passed zombieDecRate
   * parameter.
   */
  public static void setZombieDecRate(double rate)
  {
    zombieDecRate = rate;
  }
}