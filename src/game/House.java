package game;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import game.House.MainGameLoop;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author Christian Seely
 * @author Duong Nguyen
 * @author John Clark
 *
 */
public class House extends Application
{
  int translate = 0;

  private final String PATH = System.getProperty("user.dir") + "/src";
  // private double newX, oldX, deltaX;
  private double maxWidth = Screen.getPrimary().getBounds().getWidth();
  private double maxHeight = Screen.getPrimary().getBounds().getHeight();
  private int SCREEN_CENTER_WIDTH = 0;
  private int SCREEN_CENTER_HEIGHT = 0;
  private final int MAP_WIDTH = 1000;
  private final int MAP_HEIGHT = 1000;
  private final int MAP_TREE_HEIGHT = 2;
  private double width, height;

  private final DoubleProperty xVelocity = new SimpleDoubleProperty();
  private final DoubleProperty zVelocity = new SimpleDoubleProperty();
  private final LongProperty lastUpdateTime = new SimpleLongProperty();
  private final LongProperty zombieTimer = new SimpleLongProperty();
  private final BooleanProperty aPressed = new SimpleBooleanProperty(false);
  private final BooleanProperty sPressed = new SimpleBooleanProperty(false);
  private final BooleanProperty wPressed = new SimpleBooleanProperty(false);
  private final BooleanProperty dPressed = new SimpleBooleanProperty(false);
  
  //Array to hold the Graph nodes objects which represent nodes in a 
  //navigational type mesh used for path finding. 
  private GraphNode graphNodeArr[][] = new GraphNode[100][100]; 
  private double maxPlayerStamina = 5.0;
  private double playerStamina = maxPlayerStamina;  
  private double staminaRegen = .2;
  
  private Stage levelUpStage;
  private Stage stage;
  //Data structure of bounds objects, the bounds objects represents the
  //bounds of a wall block which is 2x2. 
  private Bounds wallAndObstacleBounds[][] = new Bounds[100][100];
  //Hash map which links a key which is a point on the map to an arrayList of
  //bounds objects which are the bounds objects of wall blocks in a close
  //proximity. 
  private HashMap<Point, ArrayList<Bounds>> nearPossibleCollisions = new HashMap<>();
  private List<Zombie> zombieList = new ArrayList<>();
  //Holds point of the start locations of all the zombies. 
  private static List<Point> randZombieLocs = new ArrayList<>();
  private static List<Point> lineZombieLocs = new ArrayList<>();
  private static Point masterLoc;
  //Points of the exit door (two tiles)
  private static Point[] exit = new Point[2];
  private static boolean isVerticalExit;
  private final boolean SHOWING_BOARD = false;
  private double xSpeed;
  private double zSpeed;
  //Trigger if the master zombie is path finding. 
  private boolean triggerMaster;
  private double walkSpeed = 4;
  private double runSpeed;
  private double walkSpeedHolder = walkSpeed;
  private GraphicsContext gfx;
  private Group root;
  private final Group cameraGroup = new Group();
  private Map map;
  //Paths for all of the sounds and images. 
  private Image wallMap = new Image("file:"+PATH + "/Resources/Textures/newbricks.png");
  private Image specWall = new Image("file:"+PATH + "/Resources/Textures/specularMap.png");
  private Image ceilingMap = new Image("file:"+PATH + "/Resources/Textures/celtpat.png");
  private Image floor1 = new Image("file:"+PATH + "/Resources/Textures/graywood.jpg");
  private Image floor2 = new Image("file:"+PATH + "/Resources/Textures/stone2.jpg");
  private Image floor3 = new Image("file:"+PATH + "/Resources/Textures/lightwood.jpg");
  private Image floor4 = new Image("file:"+PATH + "/Resources/Textures/stone.jpg");
  private Image door = new Image("file:"+PATH + "/Resources/Textures/stone3.jpg");
  private File right = new File(PATH + "/Resources/Sounds/zombiesoundsright.wav");
  private File left = new File(PATH + "/Resources/Sounds/zombiesoundsleft.wav");
  private boolean isRunning;
  private int zombieSmell = 15;
  //We need to be able to access the same GraphNode objects multiple times
  //as we only initialize their neighbors once so we need to store them
  //in a hash map so we can access them later based off the current 
  //Point (awt)
  private HashMap<Point,GraphNode> pointToGraphNode = new HashMap<>();
  private static int startX, startZ;
  private AnimationTimer gameLoop;
  //Audio clips or walking and running sounds. 
  private AudioClip walkingClip;
  private AudioClip runningClip;
  private boolean isWalkingPlaying = false;
  private boolean isRunningPlaying = false;
  private Random random = new Random();
  private Scene scene; 
  private PointLight light;
  private Image deathScreen;
  private BackgroundImage deathBG;
  
  private int levelNum = 0;
  private List<BackgroundImage> endLevelImages = new ArrayList<>();
  private char board[][];
  GraphNode startNode;
  GraphNode endNode;
  private Robot bot;
  private Node rootNode;
  private int zombieMovementSize;
  private List<MeshView[]> zombieMovementList;
  private Color[] ballColors = {Color.YELLOW, Color.BLUE, Color.LIME, Color.BLACK, Color.RED};

  PerspectiveCamera camera;

  @Override
  public void start(Stage primaryStage)
  {
    
    xSpeed = zSpeed = walkSpeed;
    runSpeed = walkSpeed * 2;
    zombieTimer.set(System.nanoTime());
    
    

    // Get center of screen width/height;
    width = Screen.getPrimary().getBounds().getWidth();
    height = Screen.getPrimary().getBounds().getHeight();
    fillImageList();
    deathScreen = new Image("file:"+PATH + "/Resources/Images/DeathScreen.png", width, height, false, true);
    deathBG  = new BackgroundImage(deathScreen, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, 
        BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    SCREEN_CENTER_WIDTH = (int) (width / 2);
    SCREEN_CENTER_HEIGHT = (int) (height / 2);
    // Center mouse in center of screen
    try
    {
      bot = new Robot();
      bot.mouseMove(SCREEN_CENTER_WIDTH, SCREEN_CENTER_HEIGHT);
    } catch (AWTException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Read in the audio files
    File f = new File(PATH + "/Resources/Sounds/walkingProjectBothChannel16bit.wav");
    try
    {
      URL resource = f.toURI().toURL();
      walkingClip = new AudioClip(resource.toString());

    } catch (MalformedURLException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    File f2 = new File(PATH + "/Resources/Sounds/runningSoundProjectbothChannels16bits.wav");
    try
    {
      URL resource = f2.toURI().toURL();
      runningClip = new AudioClip(resource.toString());

    } catch (MalformedURLException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
    
    primaryStage.setScene(buildScene(primaryStage));
    primaryStage.setTitle("Zombie House");
    primaryStage.setFullScreen(true); 
    loadStartScreen();   

    ZombieMovement zombieMovement = new ZombieMovement();
    zombieMovementSize = zombieMovement.getSize();
    zombieMovementList = zombieMovement.getMovement();
  }
  
  /**
   * @author John Clark
   * Loads the game startup screen.
   */
  private void loadStartScreen()
  {
    Image startImage = new Image("file:"+PATH + "/Resources/Images/StartScreen.png", width, height, false, true);
    BackgroundImage bgImage = new BackgroundImage(startImage,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);
    BorderPane startRoot = new BorderPane();
    Stage startStage = new Stage();
    Scene startScene = new Scene(startRoot, width ,height, true, SceneAntialiasing.BALANCED);
    
    //Handles Mouse Clicks
    startScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {
      
      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .312 * width && e.getSceneX() <= .681 * width && e.getSceneY() >= .764 * height && e.getSceneY() <= .818 * height)
        {
          stage.toFront();
          startStage.toBack();
          startStage.close();
          stage.show();
          gameLoop = new MainGameLoop();
          gameLoop.start();
          startZombies();
          
        }
             
      }     
    }); 
    //turns cursor into a hand when hovering over a clickable area
    startScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .312 * width && e.getSceneX() <= .681 * width && 
            e.getSceneY() >= .764 * height && e.getSceneY() <= .818 * height)
        {
          startScene.setCursor(Cursor.HAND);
        }
        else startScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    
    
    startRoot.setBackground(new Background(bgImage));
    startStage.toFront();
    stage.toBack();
    
    startStage.setScene(startScene);
    startStage.setFullScreen(true);
    startStage.show();
  }
  
/**
 * Reinitialize costs associated with each graph node object. 
 * Since we do not want to interfere with future path finding. 
 */
  private void reinitializeCosts()
  {
    for(int i = 0; i < 100; i++)
    {
      for(int j = 0; j<100; j++)
      {
        if(graphNodeArr[i][j]!=null)
        {
        graphNodeArr[i][j].setCost(0);
        }
      }
    }
  }

  /**
   * @author John Clark
   * fills a list of images that are used in between each level
   */
  private void fillImageList()
  {
    Image i1 = new Image("file:"+PATH + "/Resources/Images/EndLevel1.png", width, height, false, true);
    Image i2 = new Image("file:"+PATH + "/Resources/Images/EndLevel2.png", width, height, false, true);
    Image i3 = new Image("file:"+PATH + "/Resources/Images/EndLevel3.png", width, height, false, true);
    Image i4 = new Image("file:"+PATH + "/Resources/Images/EndLevel4.png", width, height, false, true);
   
    
    endLevelImages.add(new BackgroundImage(i1,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT));
    endLevelImages.add(new BackgroundImage(i2,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT));
    endLevelImages.add(new BackgroundImage(i3,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT));
    endLevelImages.add(new BackgroundImage(i4,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT));
  }
  
  /**
   * @author John Clark
   * @author Christian Seely
   * @param stage the main game Stage 
   * @return The main game scene
   * This method builds the main 3d game scene
   */
  private Scene buildScene(Stage stage)
  {
    this.stage = stage;
    root = new Group();
    scene = new Scene(root, maxWidth, maxHeight, true, SceneAntialiasing.BALANCED);
    camera = new PerspectiveCamera(true);
    cameraGroup.getChildren().add(camera);
    camera.setNearClip(0.1);
    camera.setFarClip(5000.0);

    scene.setFill(Color.GRAY); 
    scene.setCursor(Cursor.NONE); 
    scene.setCamera(camera); 
    light = new PointLight(Color.rgb(200, 200, 200));
    root.getChildren().add(cameraGroup);
    cameraGroup.getChildren().add(light);
    light.setTranslateX(-.5);
    light.setTranslateY(-4);
    
    cameraGroup.setRotationAxis(Rotate.Y_AXIS); 

    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent e)
      {

        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        double x = b.getX();
        //Rotate the camera with the ratio of .375 with guarantees 
        //two full 360 rotations when going from the far left side
        //of ones screen to the far right side of ones screen with
        //their mouse. 
        cameraGroup.setRotate((x - SCREEN_CENTER_WIDTH) * .375); 
    
        // At edge of screen put mouse at center of screen.
        //Since there is the end of a 360 rotation at the end of
        //each screen when the program follows the mouses movement
        //from the robot it wont make a difference as its a complete 
        //360 rotaiton. 
        if (x > width - 20 || x < 20)
        {
          
          try
          {
            Robot bot = new Robot();
            bot.mouseMove(SCREEN_CENTER_WIDTH, SCREEN_CENTER_HEIGHT);
          } catch (AWTException de)
          {
            // TODO Auto-generated catch block
            de.printStackTrace();
          }
        }
      }
    });
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      /**
       * Sets the left and right velocity based on the camera's current angle.
       * also handles the shift key and the space bar to pause.
       */
      @Override
      public void handle(KeyEvent e)
      {
        //If the sound clip is not walking play it. 
        if (!isWalkingPlaying)
        {
          walkingClip.play();
          isWalkingPlaying = true;
        }

        if (e.getCode() == KeyCode.SHIFT)
        {
          
          if(playerStamina > 0)
          {
            isRunning = true;            
          }
        }
        if(e.getCode() == KeyCode.SPACE)
        {
          gameLoop.stop();
          loadPauseScreen();
          
        }
        if (e.getCode() == KeyCode.A)
        {
          aPressed.set(true);
        } else if (e.getCode() == KeyCode.D)
        {
          dPressed.set(true);
        } else if (e.getCode() == KeyCode.W)
        {
          wPressed.set(true);
        } else if (e.getCode() == KeyCode.S)
        {
          sPressed.set(true);
        }

      }
    });
    /**
     * Handles releasing the keys. A boolean for each key is set to false upon release.
     */
    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      /**
       * Sets the velocity back to zero as soon as a key is released. Otherwise,
       * the camera would just keep moving until another direction is pressed.
       */
      @Override
      public void handle(KeyEvent e)
      {
        if (e.getCode() == KeyCode.SHIFT)
        {
          isRunning = false;
          

        }
        if (e.getCode() == KeyCode.A)
        {
          aPressed.set(false);
          xVelocity.set(0);
          zVelocity.set(0);
        } else if (e.getCode() == KeyCode.S)
        {
          sPressed.set(false);
          xVelocity.set(0);
          zVelocity.set(0);
        } else if (e.getCode() == KeyCode.D)
        {
          dPressed.set(false);
          xVelocity.set(0);
          zVelocity.set(0);
        } else if (e.getCode() == KeyCode.W)
        {
          wPressed.set(false);
          xVelocity.set(0);
          zVelocity.set(0);
        }
        //If none of the keys are pressed no walking or running sounds should be playing. 
        if (!aPressed.get() && !sPressed.get() && !dPressed.get() && !wPressed.get())
        {
          walkingClip.stop();
          isWalkingPlaying = false;
          runningClip.stop();
          isRunningPlaying = false;
        }
      }
    });
    //If you want to see the 2d representation of the map, set this boolean to true
    if (SHOWING_BOARD)
    {
      Canvas boardCanvas = new Canvas(MAP_WIDTH, MAP_HEIGHT);
      gfx = boardCanvas.getGraphicsContext2D();
      Stage boardStage = new Stage();
      Group boardGroup = new Group();
      Scene boardScene = new Scene(boardGroup, MAP_WIDTH, MAP_HEIGHT);
      boardGroup.getChildren().add(boardCanvas);
      boardStage.setScene(boardScene);
      boardStage.show();
    }
    
    map = new Map();
 
    map.fillPossibleValues();
    rootNode = map.buildTree(new Rectangle(0, 0, MAP_WIDTH, MAP_HEIGHT), MAP_TREE_HEIGHT, false);
    map.fillRegionList(rootNode);
    map.createAsciiMap();
    map.createRooms(gfx);
    map.addObstaclesandDoors();
    board = map.asciiMap;

    if (SHOWING_BOARD)
    {
      map.displayBoard(gfx);
    }

    buildHouse();
    return scene;
  }
  
  
  /**
   * @author Christian Seely
   * @param startNode Start node in the map. 
   * @param endNode End node in the map. 
   * @param z zombie object
   * Find the shortest path from the start node to the end node for the 
   * zombie object z. 
   */
  private void findPath(GraphNode startNode, GraphNode endNode,Zombie z)
  {
    Comparator<GraphNode> comparator = new CostComparator();
    PriorityQueue<GraphNode> pQueue = new PriorityQueue<>(10, comparator);
    HashMap<GraphNode,Integer> costSoFar = new HashMap<>();
    GraphNode currentNode =  null;
    GraphNode neighborNode = null;
    int costFromStart = 0;
    ArrayList<GraphNode> neighbors;
    //Add first node to queue
    pQueue.add(startNode);
    //Initialize hash maps
    z.putKeyValuePair(startNode, null);//Acts like edge between two nodes
    costSoFar.put(startNode,0);
    
    while(!pQueue.isEmpty())
    {    
      currentNode = pQueue.remove();
      //If the current node equals the end node then
      //we have found the path
      if(currentNode.equals(endNode))
    {
      setPath(startNode,currentNode,z); //Set the path to the zombie object z
      break;   //Break out as we are done and we do not want to continue the search.  
    }
      neighbors = currentNode.getNeighbors();
      int size = neighbors.size();
      int newCost = 0;
      int distance = 0;
      int xDiff;
      int zDiff;
      for(int i = 0; i < size; i++)
      {
        neighborNode = neighbors.get(i);
        xDiff = currentNode.getXLoc()-neighborNode.getXLoc();
        zDiff = currentNode.getZLoc()-neighborNode.getZLoc();
        //Diagonal movements cost more, so to check if the movement was diagonal
        //multiple the two x and z directional differences together and if the
        //sum is non zero that implies that the direction was diagonal. 
        if(xDiff*zDiff!=0)
        {
          newCost = costSoFar.get(currentNode)+ 3; //Cost thus far update
       }
      else
        {
        newCost = costSoFar.get(currentNode) +2; //Cost thus far update
        }
        if(!costSoFar.containsKey(neighborNode)||
            newCost < costSoFar.get(neighborNode))
        {
          costSoFar.put(neighborNode,newCost);
          //Find the euclidian distance between the two nodes this will
          //serve as the other cost factored in for the graphNodes
          //This applies the Dikstras component of the A* algorithm. 
          distance = findDistanceToEndGoal(neighborNode,endNode);
          neighborNode.setCost(distance);
          costFromStart = costSoFar.get(neighborNode);
          neighborNode.setCost(neighborNode.getCost()+costFromStart);
          pQueue.add(neighborNode);
          z.putKeyValuePair(neighborNode, currentNode);
        }
      }
    }
  }

  /**
   * 
   * @param neighborNode Node in Graph
   * @param endNode Node in Graph
   * @return Distance between the two nodes in the graph. 
   */
  private int findDistanceToEndGoal(GraphNode neighborNode, GraphNode endNode)
  {
    
    return (int)Math.sqrt(Math.pow((neighborNode.getXLoc() - endNode.getXLoc()), 2) 
        + Math.pow((neighborNode.getZLoc() - endNode.getXLoc()), 2));
  }
  
  
  //Hold the path shortest path for the zombie object. 
  ArrayList<GraphNode> path = new ArrayList<>();
  /**
   * @author Christian Seely
   * @param startNode Start node in path 
   * @param endNode end node in path
   * @param z object. 
   * Put the path in an ArrayList
   */
  private void setPath(GraphNode startNode, GraphNode endNode,Zombie z) 
  {   
    //Convert the path from a hashMap to an arrayList quickly and
    //add it to the zombie object.
     path = new ArrayList<>();
    GraphNode current = endNode;
    //Grab the hashmap of the path from the zombie object. 
    HashMap<GraphNode, GraphNode> cameFrom= z.getCameFromMap();
    path.add(current);
    while (!current.equals(startNode)) 
    {
      current = cameFrom.remove(current);
      path.add(current);
    }
    //Set the path in the zombie object. 
    z.setPath(path);

  }

  
/**
 * @author Christian Seely
 * @param startNode Player location in map 
 * @param endNode Zombie location in map.
 * @param z Zombie object. 
 * @return If the zombie is within a distance of 15 using a flood fill, if so
 * then pathfinding is started for that zombie object. 
 */
  private boolean inRangeCheck(GraphNode startNode, GraphNode endNode,Zombie z)
  {
    Comparator<GraphNode> comparator = new CostComparator();
    PriorityQueue<GraphNode> pQueue = new PriorityQueue<>(10, comparator);
    HashMap<GraphNode,Integer> costSoFar = new HashMap<>();
    GraphNode currentNode =  null;
    GraphNode neighborNode = null;
    int costFromStart = 0;
    ArrayList<GraphNode> neighbors;
    pQueue.add(endNode);
    costSoFar.put(endNode,0);
    //This follows the exact same methodology as the findpath method except
    //the costs is only based of movement thus far. 
    while(!pQueue.isEmpty())
    {
      currentNode = pQueue.remove();
      if(currentNode.equals(startNode))
    {
        //In Range. 
         z.inRange = true;
         return true;  
    }
      neighbors = currentNode.getNeighbors();
      int size = neighbors.size();
      
      int newCost = 0;
      
      
      for(int i = 0; i < size; i++)
      {
        neighborNode = neighbors.get(i);
        newCost = costSoFar.get(currentNode) +1;
        
        if(!costSoFar.containsKey(neighborNode)||
            newCost < costSoFar.get(neighborNode))
        {
          costSoFar.put(neighborNode,newCost);
          costFromStart = costSoFar.get(neighborNode);
          neighborNode.setCost(neighborNode.getCost()+costFromStart);
          //Only flood fill till max range of 15
          if(neighborNode.getCost()<=zombieSmell)
          {
          pQueue.add(neighborNode);
          }
        }
      }
    }
    //Not in range. 
    z.inRange = false;
    return false;
  }
  
  /**
   * @author John Clark
   * @author Christian Seely
   * This method reads through the ascii map created in the map class to build the 3d house.
   * The house is built tile by tile. Also in this method a hashmap is filled with lists of
   * boxes that are surrounding each tile in the house. These lists are the possible collisions
   * for each tile that the player may be standing on. The player and all zombies are also placed
   * on the map in this method.
   */
  private void buildHouse()
  {
    // Create different colors to represent the four region floor tiles
    // for the actual game we just need to switch the materials to
    // some image we find e.g stone, dirt, grass etc.
       
    PhongMaterial wallMat = new PhongMaterial();
    wallMat.setDiffuseMap(wallMap);
    wallMat.setSpecularMap(specWall);
    wallMat.setSpecularPower(.15);
    wallMat.setSpecularColor(Color.rgb(115, 115, 115));
    wallMat.setDiffuseColor(Color.rgb(115, 115, 115));
    PhongMaterial ceilingMat = new PhongMaterial();
    ceilingMat.setDiffuseMap(ceilingMap);
    ceilingMat.setSpecularColor(Color.rgb(115, 115, 115));
    ceilingMat.setDiffuseColor(Color.rgb(115, 115, 115));
    PhongMaterial floor1Mat = new PhongMaterial();
    floor1Mat.setDiffuseMap(floor1);
    floor1Mat.setSpecularColor(Color.rgb(115, 115, 115));
    floor1Mat.setDiffuseColor(Color.rgb(115, 115, 115));
    PhongMaterial floor2Mat = new PhongMaterial();
    floor2Mat.setDiffuseMap(floor2);
    floor2Mat.setDiffuseColor(Color.rgb(115, 115, 115));
    floor2Mat.setSpecularColor(Color.rgb(115, 115, 115));
    PhongMaterial floor3Mat = new PhongMaterial();
    floor3Mat.setDiffuseMap(floor3);
    floor3Mat.setDiffuseColor(Color.rgb(115, 115, 115));
    floor3Mat.setSpecularColor(Color.rgb(115, 115, 115));
    PhongMaterial floor4Mat = new PhongMaterial();
    floor4Mat.setDiffuseMap(floor4);
    floor4Mat.setDiffuseColor(Color.rgb(115, 115, 115));
    floor4Mat.setSpecularColor(Color.rgb(115, 115, 115));
    PhongMaterial doorMat = new PhongMaterial();
    doorMat.setDiffuseMap(door);
    doorMat.setDiffuseColor(Color.rgb(115, 115, 115));
    doorMat.setSpecularColor(Color.rgb(115, 115, 115));

    Box b;
    
    for(int i = 0; i <100; i++)
    {
      for(int j = 0; j < 100; j++)
      {
        if(board[i][j]!='w' &&board[i][j]!='X')
        {

         GraphNode newGraphNode = new GraphNode(i,j);
         graphNodeArr[i][j] = newGraphNode;
         pointToGraphNode.put(new Point(i,j), newGraphNode);
       
        }
      }
    }
    //place the player
    cameraGroup.setTranslateX(startZ * 2);
    cameraGroup.setTranslateZ(startX * 2);
    for (int i = 0; i < MAP_WIDTH / 10; i++)
    {
      for (int j = 0; j < MAP_HEIGHT / 10; j++)
      {
        // Walls and obstacles are drawn/treated the same currently, if we
        // want different textures for say pillars all we have to do is
        // add another conditional statement.

        if (board[i][j] == 'X' || board[i][j] == 'w')
        {
          //Set dimensions of a wall and obstacle block. 
          b = new Box(2, 6, 2);
          b.setTranslateX(i * 2);
          b.setTranslateZ(j * 2);
          b.setMaterial(wallMat);
          b.setOpacity(1);
          
          // Add the wall block to the correct location on 3D plane.
          root.getChildren().add(b);
          

          // Grabs the bounds off the box and put that in a 2D array
          // containing the bounds of each wall/obstacles tile, note
          // the indices of this array align with the indices of the
          // 2DMap array.
          wallAndObstacleBounds[i][j] = b.getBoundsInParent();
        } 
        else
        {
          Box bf = new Box(2, 1, 2);
          if (board[i][j] == '1' || board[i][j] == '5') 
          {
            bf.setMaterial(floor1Mat);
          } 
          else if (board[i][j] == '2' || board[i][j] == '6') 
          {
            bf.setMaterial(floor2Mat);
          } 
          else if (board[i][j] == '3' || board[i][j] == '7') 
          {
            bf.setMaterial(floor3Mat);
          } 
          else if (board[i][j] == '4' || board[i][j] == '8') 
          {
            bf.setMaterial(floor4Mat);
          } 
          else if (board[i][j] == 'D' || board[i][j] == 'E')
          {
            bf.setMaterial(doorMat);
          }
          //The floor and ceiling tiles are placed at the same locations. 
          // Floor
          bf.setTranslateY(2.5);
          bf.setTranslateX(i * 2);
          bf.setTranslateZ(j * 2);
          root.getChildren().add(bf);
          // Ceiling
          Box bc = new Box(2, 1, 2);
          bc.setMaterial(ceilingMat);
          bc.setTranslateX(i * 2);
          bc.setTranslateY(-2.5);
          bc.setTranslateZ(j * 2);
         
          root.getChildren().add(bc);
          
          
          
          //Create the Neighbors list for each graph node in the 
          //graph, check all 8 directions from the node and if
          //the node is not null e.g not a wall or obstacle location
          //when it is added as a neighbor node. 
          if(graphNodeArr[i][j]!=null)
          {
        
         
          if(inBounds(i+1,j)&& graphNodeArr[i+1][j]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i+1][j]);
          }
          if(inBounds(i-1,j)&& graphNodeArr[i-1][j]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i-1][j]);
          }
          if(inBounds(i,j+1)&& graphNodeArr[i][j+1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i][j+1]);
          }
          if(inBounds(i,j-1)&& graphNodeArr[i][j-1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i][j-1]);
          }
          if(inBounds(i+1,j-1)&& graphNodeArr[i+1][j-1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i+1][j-1]);
          }
          if(inBounds(i-1,j-1)&& graphNodeArr[i-1][j-1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i-1][j-1]);
          }
          if(inBounds(i+1,j+1)&& graphNodeArr[i+1][j+1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i+1][j+1]);
          }
          if(inBounds(i-1,j+1)&& graphNodeArr[i-1][j+1]!= null)
          {
            graphNodeArr[i][j].addNeighbor(graphNodeArr[i-1][j+1]);
          }

          }
    
          
         
        }
      }

    }


   
    
    for (int i = 0; i < 100; i++)
    {
      for (int j = 0; j < 100; j++)
      {
        
        ArrayList<Bounds> boundsList = new ArrayList<>();
        // Iterate through the twoDMap array and check the surroundings of i,j
        // with a span of one, if there is a wall or obstacles grab the bounds
        // from the wallAndObstacleBounds array (since the indices are aligned)
        // and add that to the list.

        if (inBounds(i + 1, j - 1) && (board[i + 1][j - 1] == 'X' || board[i + 1][j - 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i + 1][j - 1]);
        }

        if (inBounds(i + 1, j) && (board[i + 1][j] == 'X' || board[i + 1][j] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i + 1][j]);
        }

        if (inBounds(i + 1, j + 1) && (board[i + 1][j + 1] == 'X' || board[i + 1][j + 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i + 1][j + 1]);
        }

   
        if (inBounds(i, j - 1) && (board[i][j - 1] == 'X' || board[i][j - 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i][j - 1]);
        }



        if (inBounds(i, j + 1) && (board[i][j + 1] == 'X' || board[i][j + 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i][j + 1]);
        }


        if (inBounds(i - 1, j - 1) && (board[i - 1][j - 1] == 'X' || board[i - 1][j - 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i - 1][j - 1]);
        }

        if (inBounds(i - 1, j) && (board[i - 1][j] == 'X' || board[i - 1][j] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i - 1][j]);
        }

        if (inBounds(i - 1, j + 1) && (board[i - 1][j + 1] == 'X' || board[i - 1][j + 1] == 'w'))
        {
          boundsList.add(wallAndObstacleBounds[i - 1][j + 1]);
        }

    
        // Once we have filled the list with the the possible surrounding bounds
        // then create a new entry in our hashmap mapping the point on the board
        // with the close walls/obstacles which we will check for collision
        // when the player goes the corresponding point (key)
        nearPossibleCollisions.put(new Point(i, j), boundsList);

      }
    }
    
    placeZombies();
    for (Zombie z : zombieList)
    {
      z.initializeZombieSounds(left, right);
    }
    placeBall();
  }
  /**
   * 
   * @param one first exit point
   * @param two second exit point
   * @param isVertical whether or not the door is on a vertical wall.
   * Sets the exit points.
   */
  public static void setExit(Point one, Point two, boolean isVertical)
  {
    exit[0] = one;
    exit[1] = two;
    isVerticalExit = isVertical;
  }
  
  /*
   * places colored balls in the exits of each level.
   */
  private void placeBall()
  {
    Sphere ball = new Sphere(1);
    ball.setMaterial(new PhongMaterial(ballColors[levelNum]));
    if(isVerticalExit)
    {
      ball.setTranslateX(exit[0].getX() * 2 + 1);
      ball.setTranslateZ(exit[0].getY() * 2);
    }
    else
    {
      ball.setTranslateX(exit[0].getX() * 2);
      ball.setTranslateZ(exit[0].getY() * 2 + 1);
    }
    root.getChildren().add(ball);
  }
  
  /**
   * @author John Clark
   * places the zombies in the house.
   */
  private void placeZombies()
  {    
    Zombie zombie;
    Zombie master = new Zombie(0.0, 2.0, false, true, masterLoc);
    
    root.getChildren().add(master.getZombie3D());
    for (Point loc : lineZombieLocs)
    {
    
      zombie = new Zombie(0.0, 2.0, false, false, loc);
      zombieList.add(zombie);
      root.getChildren().add(zombie.getZombie3D());
    }
    for (Point loc : randZombieLocs)
    {    
      zombie = new Zombie(0.0, 2.0, true, false, loc);
      zombieList.add(zombie);
      root.getChildren().add(zombie.getZombie3D());
    }
    zombieList.add(master);
    System.out.println(zombieList.size());
  }
  
  /**
   * starts zombie movement
   */
  private void startZombies()
  {
    for(Zombie z: zombieList)
    {
      if(z.getIsMaster())
      {
        z.setZombieSpeed(3.0);
      }
      else z.setZombieSpeed(1.0);
    }
  }
  
  /**
   * 
   * @param i index in map
   * @param j index in map 
   * @return Make sure indices are in bounds of map. 
   */
  private boolean inBounds(int i, int j)
  {
    return (i < 100 && j < 100 && i >= 0 && j >= 0);
  }
  
  /**
   * 
   * @param x starting player x location
   * @param z starting player z location
   * sets the player start location.
   */
  public static void setStartPos(int x, int z)
  {
    startX = x;
    startZ = z;
  }
  
  /**
   * 
   * @param x starting zombie x location
   * @param z starting zombie z location
   */
  public static void setMasterLoc(int x, int z)
  {
    masterLoc = new Point(x, z);
  }
  
  /**
   * 
   * @param x starting random zombie x location
   * @param z starting random zombie z location
   * adds a random zombie starting location to the list of random zombie locations.
   */
  public static void addRandZombieLoc(int x, int z)
  {
    randZombieLocs.add(new Point(x, z));
  }
  
  /**
   * 
   * @param x starting line walk zombie x location
   * @param z starting line walk zombie z location
   * adds a line walk zombie starting location to the list of line walk zombie locations.
   */
  public static void addLineZombieLoc(int x, int z)
  {
    lineZombieLocs.add(new Point(x, z));
  } 

 
  ArrayList<Bounds> possibleCollisions;
  ArrayList<Bounds> possibleZombieCollisions;
  ArrayList<GraphNode> pathTemp;
  List<Box> textures;
  boolean collision = false;
  boolean zombieCollision = false;
  double xPlayerLoc = 0;
  double zPlayerLoc = 0;
  double xZombieLoc = 0;
  double zZombieLoc = 0;
  double updateZombies = 0;
  double betweenDistance = 0;
  double pathCount = 0;
  double updateSoundsCount;
  
  /**
   * 
   * @author John Clark
   * @author Christian Seely
   * main animation loop
   *
   */
  class MainGameLoop extends AnimationTimer
  {
    double cameraAngle;
    Point playerLoc;   
    double elapsedSeconds;
    double deltaTime;
    int i;
    
    @Override
    public void handle(long now)
    {
      
      playerLoc = new Point((int) cameraGroup.getTranslateX() / 2, (int) cameraGroup.getTranslateZ() / 2);
      //player has reached the end of the level.
      if(playerLoc.equals(exit[0]) || playerLoc.equals(exit[1]))
      {
        if (levelNum <= 3) 
        {          
          walkSpeed = 0;
          goToNewLevel(stage);
        }
        else endGame();
      }
      
      
      cameraAngle = cameraGroup.getRotate();
      // sets the translation of x and z each tick of the camera, based on keys
      // pressed.
      if (lastUpdateTime.get() > 0)
      {
        elapsedSeconds = (now - lastUpdateTime.get()) / 1_000_000_000.0;
        if(playerStamina <= 0)
        {
          isRunning = false;
        }
        if(isRunning && playerStamina > 0)
        {
          playerStamina -= elapsedSeconds;
          if(playerStamina <= 0)
          {
            playerStamina = 0;
            runningClip.stop();
            isRunningPlaying = false;
            walkingClip.play();
            isWalkingPlaying = true;
          }
          else
          {
            walkingClip.stop();

            isWalkingPlaying = false;
            if (!isRunningPlaying && (wPressed.get() || aPressed.get() || sPressed.get() || dPressed.get()))
            {
              runningClip.play();
              isRunningPlaying = true;
            }
            xSpeed = runSpeed;
            zSpeed = runSpeed;
          }
        }
        else
        {          
          xSpeed = walkSpeed;
          zSpeed = walkSpeed;
          if(playerStamina < maxPlayerStamina)
          {
            playerStamina += staminaRegen * elapsedSeconds;
          }
          if(isRunningPlaying)
          {
            runningClip.stop();
            isRunningPlaying = false;
            walkingClip.play();
            isWalkingPlaying = true;
          }          
        }        
        if (aPressed.get())
        {
          // diagonal movement
          if (wPressed.get())
          {
            cameraAngle -= 45;
          }
          if (sPressed.get())
          {
            cameraAngle += 45;
          }
          xVelocity.set(-xSpeed * Math.cos(cameraAngle * (Math.PI / 180)));
          zVelocity.set(zSpeed * Math.sin(cameraAngle * (Math.PI / 180)));
        }
        if (dPressed.get())
        {
          // diagonal movement
          if (wPressed.get())
          {
            cameraAngle += 45;
          }
          if (sPressed.get())
          {
            cameraAngle -= 45;
          }
          xVelocity.set(xSpeed * Math.cos(cameraAngle * (Math.PI / 180)));
          zVelocity.set(-zSpeed * Math.sin(cameraAngle * (Math.PI / 180)));
        }
        if (wPressed.get())
        {
          xVelocity.set(xSpeed * Math.sin(cameraAngle * (Math.PI / 180)));
          zVelocity.set(zSpeed * Math.cos(cameraAngle * (Math.PI / 180)));
        }
        if (sPressed.get())
        {

          xVelocity.set(-xSpeed * Math.sin(cameraAngle * (Math.PI / 180)));
          zVelocity.set(-zSpeed * Math.cos(cameraAngle * (Math.PI / 180)));
        }

        double deltaX = elapsedSeconds * xVelocity.get();
        double deltaZ = elapsedSeconds * zVelocity.get();
        double oldX = cameraGroup.getTranslateX();
        double oldZ = cameraGroup.getTranslateZ();
        double newX = oldX + deltaX;
        double newZ = oldZ + deltaZ;
        updateSoundsCount += elapsedSeconds;

        //To limit the number of calculations done the update of the sounds is done
        //every quarter second. 
       if (updateSoundsCount > .25)
        {
          // In the game logic the board is 100x100 in in actuality it is
          // 200x200 so divide by 2.
          xPlayerLoc = oldX / 2;
          zPlayerLoc = oldZ / 2;
          for (Zombie z : zombieList)
          {
            xZombieLoc = z.getCurrentLoc().getX();
            zZombieLoc = z.getCurrentLoc().getY();
            // Always positive.
            betweenDistance = Math.sqrt(Math.pow((xPlayerLoc - xZombieLoc), 2) + Math.pow((zPlayerLoc - zZombieLoc), 2));
            //Stop the sound of the zombie object if out of range. 
            if (betweenDistance > 20 && z.isZombieSoundsRightChannelPlaying && z.isZombieSoundsLeftChannelPlaying)
            {
              z.stopSounds();
            }
            // Sound not playing now in range start sound.
            if (betweenDistance < 20 && !z.isZombieSoundsRightChannelPlaying && !z.isZombieSoundsLeftChannelPlaying)
            {
              z.startSounds();
            }
            // In range and sound already playing update sound.
            if (betweenDistance < 20 && z.isZombieSoundsRightChannelPlaying && z.isZombieSoundsLeftChannelPlaying)
            {
              //Get the coordinates of a point projected forward from the cameraGroup point after using a rotational
              //matricie to transform the point to be where the point is projected so its 0 degrees in front of the camera.
              //Also the distance between this point and the point of the camera is the same distance as the distance
              //between the zombie and the player. 
              double forwardProjectedZ = (Math.cos(cameraGroup.getRotate() * (Math.PI / 180))*betweenDistance+cameraGroup.getTranslateZ());
              double forwardProjectedX = (Math.sin(cameraGroup.getRotate() * (Math.PI / 180))*betweenDistance+cameraGroup.getTranslateX());

              //Get points used for cross product. 
              double ax = forwardProjectedZ;
              double ay = forwardProjectedX;
              //Zombie location. 
              double bx = z.getZombie3D().getTranslateZ();
              double by = z.getZombie3D().getTranslateX();
              //Camera location
              double cx = oldZ;
              double cy = oldX;
              //Cross product using three poings. 
              double crossProduct = (((bx-ax)*(cy-ay))-((by-ay)*(cx-ax)));
              
              //Get distance between zombie point and the point of the forward
              //projected point. 
              double tempDist = Math.sqrt(Math.pow((ay-by), 2) +
                  Math.pow((ax-bx), 2));
              //Get the angle between between the 3 points where the camera/player
              //location is the center vertex. 
              double angle = Math.acos(((betweenDistance*betweenDistance)+(betweenDistance*betweenDistance) - (tempDist*tempDist))/(2*betweenDistance*betweenDistance));
              //Ratio used for scalling the ratios of the left and right channel
              //between 0 and 100 using the angle. 
              double ratio = 31.83699459;
              
              //If the angle is more that this number than mirror it over
              //the y axis, (this is passing 90 degrees) 
              if(angle>1.5705)
              {
                double temp2 = angle - 1.5705;
                angle = 1.5705 - temp2;
              }
              //Start channels at equal ratio. 
              double left = 50;
              double right = 50;
              //The cross project tells us if the zombie is on the right or
              //left depending on its sign, if it is more than 0 than its
              //on the right if its less than 0 then its on the left. 
              if(crossProduct > 0)
              {
               
                right += angle*ratio;
                left -= angle*ratio;
             
              }
              if(crossProduct < 0)
              {
               
                right -= angle*ratio;
                left+= angle*ratio;

              }
              //Update sound with the new channel ratios. 
              z.updateSounds(betweenDistance,left,right);
            }

          }
          updateSoundsCount = 0;
        }
        updateZombies+=elapsedSeconds;
       //Update zombie movement during pathfinding every second, each second
       //the zombie moves half a tile so it still has the correct speed during
       //path finding. 
       if(updateZombies > 1)
        {
         
            for(Zombie zombie: zombieList)
            {         
            if(zombie.isPathFinding) //Check if zombie is path finding. 
            {
            if(zombie.pathIndex<zombie.getPath().size()) //Iterate through its path. 
            {
              pathTemp = zombie.getPath();
              //The smoothing count allows a movement in the path to be broken
              //into two parts to make the animation more smooth. 
              if(zombie.smoothingCount==2) 
              {
                zombie.pathIndex++;
                zombie.smoothingCount=0;
              }
              //The movement differences need the +1 index in the path. 
              if(zombie.pathIndex<zombie.getPath().size()-1)
              {
              double xMovementDifference = pathTemp.get(zombie.pathIndex+1).getXLoc()-pathTemp.get(zombie.pathIndex).getXLoc();
              double zMovementDifference =  pathTemp.get(zombie.pathIndex+1).getZLoc()-pathTemp.get(zombie.pathIndex).getZLoc();
              //Based of the movement differences, find which direction the zombie should be facing when it
              //is moving in the path. 
              if(xMovementDifference == 1 && zMovementDifference == 0)
              {
                zombie.getZombie3D().setRotate(270);
              }
              if(xMovementDifference == 1 && zMovementDifference == 1)
              {
                zombie.getZombie3D().setRotate(225);
              }
              
              if(xMovementDifference == 0 && zMovementDifference == 1)
              {
                zombie.getZombie3D().setRotate(180);
              }
              
              if(xMovementDifference == -1 && zMovementDifference == 1)
              {
                zombie.getZombie3D().setRotate(135);
              }
              
              if(xMovementDifference == -1 && zMovementDifference == 0)
              {
                zombie.getZombie3D().setRotate(90);
              }
              
              if(xMovementDifference == -1 && zMovementDifference == -1)
              {
                zombie.getZombie3D().setRotate(45);
              }
              
              if(xMovementDifference == 0 && zMovementDifference == -1)
              {
                zombie.getZombie3D().setRotate(0);
              }
              
              if(xMovementDifference == 1 && zMovementDifference == -1)
              {
                zombie.getZombie3D().setRotate(315);
              }
              //Half tile movement during first run though.
              if(zombie.smoothingCount==0)
              {
          
              zombie.getZombie3D().setTranslateX(pathTemp.get(zombie.pathIndex).getXLoc()*2+(-1)*xMovementDifference);
              zombie.getZombie3D().setTranslateZ(pathTemp.get(zombie.pathIndex).getZLoc()*2+(-1)*zMovementDifference);
              } 

              }
              //Next run through finish the movement for a total of 1 tile per two movements
              //for a tile per 2 seconds or 0.5 tiles per second. 
              if(zombie.smoothingCount==1)
              {
         
              zombie.getZombie3D().setTranslateX(pathTemp.get(zombie.pathIndex).getXLoc()*2);
              zombie.getZombie3D().setTranslateZ(pathTemp.get(zombie.pathIndex).getZLoc()*2);
              }   
           
              
              
              zombie.smoothingCount++;
  
              
            }
    
            }
          
            }
            
      
          updateZombies = 0;
        }
        possibleCollisions = nearPossibleCollisions.get(playerLoc);
        if (possibleCollisions != null)
        {
          for(Zombie z: zombieList)
          {
            //check for player/zombie collision. 
            if(collidingWithZombie(newX, newZ, z.getZombie3D().getTranslateX(), z.getZombie3D().getTranslateZ()))
            {
              newX = startZ * 2;
              newZ = startX * 2;
              walkSpeed = 0;
              loadDeathScreen();
              resetLevel();
              
            }
          }

          // Make sure the players points are not in bounds.
          collision = collidingWithWall(possibleCollisions, newX, newZ, cameraGroup.getRotate());
          if (!collision)
          {
            cameraGroup.setTranslateX(newX);
            cameraGroup.setTranslateZ(newZ);
          } 
          else
          {

            collision = false;
          }
        } 
        else
        {
          cameraGroup.setTranslateX(newX);
          cameraGroup.setTranslateZ(newZ);
        }
      }
      

      for (int i = 0; i < zombieList.size(); i++)
      {
        Zombie zombie = zombieList.get(i);
        if(!zombie.isPathFinding)
        {
        zombie.setNewLoc(zombie.move(elapsedSeconds));
        possibleZombieCollisions = nearPossibleCollisions.get(new Point((int) zombie.getZombie3D().getTranslateX() / 2, (int) zombie.getZombie3D().getTranslateZ() / 2));
        
        //check for zombie wall collision.
        zombie.setWallCollision(collidingWithWall(possibleZombieCollisions, zombie.getNewLoc().getX(), zombie.getNewLoc().getZ(), zombie.getAngle()));        
        for(int j = 0; j < zombieList.size(); j++)
        {
          if (j == i) continue;
          Zombie otherZombie = zombieList.get(j);
          
          
          
          
        }
        if(!zombie.getWallCollision() && !zombie.getZombieCollision())
        {
          //as long as the zombie's not colliding, go ahead and move
          zombie.getZombie3D().setTranslateX(zombie.getNewLoc().getX());
          zombie.getZombie3D().setTranslateZ(zombie.getNewLoc().getZ());
          zombie.updateCurrentLoc((int) (zombie.getNewLoc().getX() / 2), (int) (zombie.getNewLoc().getZ() / 2));
        }
        }
      }
      //Every two seconds check if in range and if so find shortest path 
      pathCount += elapsedSeconds;
       if(pathCount >2)
      {
        for(Zombie zombie : zombieList)
        {
          //If any of the zombies are in range trigger the master
          //zombies path finding. 
          if(zombie.inRange)
          {
            triggerMaster = true;
          }
        }
        
        for(Zombie zombie : zombieList)
        {
          
          reinitializeCosts();
          Point startPoint2 = new Point((int)(cameraGroup.getTranslateX()/2),(int)(cameraGroup.getTranslateZ()/2));
          Point endPoint2 = new Point((int)(zombie.getZombie3D().getTranslateX()/2),(int)(zombie.getZombie3D().getTranslateZ()/2));
          //Get two graph node objects based on the location of the zombie
          //and the player on the map. 
          startNode = pointToGraphNode.get(startPoint2);
          endNode = pointToGraphNode.get(endPoint2);
          //Initialize the data structures. 
          zombie.initializeMap();
          zombie.initializePath();
          zombie.pathIndex=0;
          zombie.smoothingCount = 2;
          //If the nodes are null e.g they are off the map (not actual map the
          //navigational map) then find the closest node in the map to their
          //location. 
          if(endNode==null)
          {
          endNode = findClosestNode(endPoint2,zombie);
          }
          if(startNode == null)
          {
            startNode = findClosestNode(startPoint2,zombie);
          }
          //Flood fill check if in range. 
          inRangeCheck(startNode, endNode, zombie);
          //If in range then toggle path finding mode, otherwise
          //untoggle it. 
          if(zombie.inRange)
          {
            zombie.isPathFinding = true;
          }
          else
          {
            zombie.isPathFinding= false;
          }
          //Master zombie is triggered if any of the
          //other zombies are. 
          if(zombie.getIsMaster()&&triggerMaster)
          {
            zombie.isPathFinding = true;
            triggerMaster = false;
          } 
          
          
          if(zombie.isPathFinding)
          {
            //Find nodes on the map for the player and zombie and then find the path. 
            //If the zombie is in range we do the same thing find the point that the zombie and player are at
            //and find the closest node on the navigational mesh. 
            Point startPoint = new Point((int)(cameraGroup.getTranslateX()/2),(int)(cameraGroup.getTranslateZ()/2));
            Point endPoint = new Point((int)(zombie.getZombie3D().getTranslateX()/2),(int)(zombie.getZombie3D().getTranslateZ()/2));
            startNode = pointToGraphNode.get(startPoint);
            endNode = pointToGraphNode.get(endPoint);
            zombie.initializeMap();
            zombie.initializePath();
            zombie.pathIndex=0;
            if(endNode==null)
            {
            endNode = findClosestNode(endPoint,zombie);
            }
            if(startNode == null)
            {
              startNode = findClosestNode(startPoint,zombie);
            }
            reinitializeCosts();
            findPath(startNode,endNode,zombie); 
          }
        }
        pathCount = 0;
      }
      
       //decision timer
      if (now - zombieTimer.get() >= Zombie.getZombieDecRate() * 1_000_000_000)
      {
        
        for (Zombie zombie : zombieList)
        {
          if(!zombie.isPathFinding)
          {
         
          if (zombie.getWallCollision() || zombie.getZombieCollision())
          {
            //try turning around first, semi randomly
            zombie.setAngle(zombie.getAngle() + (random.nextInt(210 - 120) + 120));
            zombie.setNewLoc(zombie.move(elapsedSeconds));
            
            zombie.setWallCollision(false);
            zombie.setZombieCollision(false);
            
            //go ahead and move the zombie
            zombie.getZombie3D().setTranslateX(zombie.getNewLoc().getX());
            zombie.getZombie3D().setTranslateZ(zombie.getNewLoc().getZ());
            zombie.updateCurrentLoc((int) (zombie.getNewLoc().getX() / 2), (int) (zombie.getNewLoc().getZ() / 2));
          } 
          else if (zombie.getIsRandom())
          {
            zombie.setAngle(random.nextDouble() * 360);
          }
          //reset the timer.
          }
  
          
          zombieTimer.set(now);
        }
      }

      deltaTime = (now - lastUpdateTime.get()) / 1_000_000_000.0;
      /*body moving depending on deltaTimer, if deltaTimer smaller, it will be moving faster*/
      if(deltaTime >= 0.1)
      {
      	for (Zombie zombie : zombieList)
      	{					
      		zombie.getZombie3D().setMesh(zombieMovementList.get(i)[0].getMesh());
      	}
      	if(i+1 == zombieMovementSize) i = -1;
      	i++;
//      	lastUpdateTime.set(now);
      }
      
      
      lastUpdateTime.set(now);
      
      
      
      
      
      

    }
  }
  /**
   * 
   * @param Point on the map. 
   * @param z object. 
   * @return The Graph node closest to the point on the navigational mesh. 
   */
  private GraphNode findClosestNode(Point point,Zombie z)
  {
    
    for(int i = 0;;i++)
    {
     
      if(pointToGraphNode.get(new Point(point.x+i,point.y))!=null)
      {
        return pointToGraphNode.get(new Point(point.x+i,point.y));
      }
      if(pointToGraphNode.get(new Point(point.x-i,point.y))!=null)
      {
        return pointToGraphNode.get(new Point(point.x-i,point.y));
      }
      if(pointToGraphNode.get(new Point(point.x,point.y+i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x,point.y+i));
      }
      if(pointToGraphNode.get(new Point(point.x,point.y-i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x,point.y-i));
      }
      if(pointToGraphNode.get(new Point(point.x+i,point.y+i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x+i,point.y+i));
      }
      if(pointToGraphNode.get(new Point(point.x-i,point.y-i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x-i,point.y-i));
      }
      if(pointToGraphNode.get(new Point(point.x-i,point.y+i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x-i,point.y+i));
      }
      if(pointToGraphNode.get(new Point(point.x+i,point.y-i))!=null)
      {
        return pointToGraphNode.get(new Point(point.x+i,point.y-i));
      }
      //Emergency error break, should not reach this point. 
      if(i==200)
      {
        return null;
      }
    }
  }
  
  /**
   * resets the level to its original state after death
   */
  private void resetLevel()
  {    
    gameLoop.stop();
    for(Zombie zombie: zombieList)
    {
      zombie.stopSounds();
      zombie.setAngle(random.nextInt(360));
      zombie.getZombie3D().setTranslateX(zombie.getStartLocation().getX() * 2);
      zombie.getZombie3D().setTranslateZ(zombie.getStartLocation().getY() * 2);
    }    
    cameraGroup.setTranslateX(startZ * 2);
    cameraGroup.setTranslateZ(startX * 2);
    cameraGroup.setRotate(0);
    playerStamina = maxPlayerStamina;
    
  }
  
  /**
   * loads the pause screen when the space bar is pressed
   */
  private void loadPauseScreen()
  {
    Image endScreen = new Image("file:"+PATH + "/Resources/Images/PauseScreen.png", width, height, false, true);
    BackgroundImage bgImage = new BackgroundImage(endScreen,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);
    BorderPane pauseRoot = new BorderPane();
    Stage pauseStage = new Stage();
    Scene pauseScene = new Scene(pauseRoot, width ,height, true, SceneAntialiasing.BALANCED);
    pauseScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .359 * width && e.getSceneX() <= .635 * width && 
            e.getSceneY() >= .368 * height && e.getSceneY() <= .423 * height)
        {
          pauseStage.close();
          gameLoop.start();
        }
        if((e.getSceneX() >= .435 * width && e.getSceneX() <= .559 * width && 
            e.getSceneY() >= .566 * height && e.getSceneY() <= .622 * height))
        {
          System.exit(0);
        }        
      }     
    }); 
    pauseScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if((e.getSceneX() >= .359 * width && e.getSceneX() <= .635 * width && 
            e.getSceneY() >= .368 * height && e.getSceneY() <= .423 * height) || 
            (e.getSceneX() >= .435 * width && e.getSceneX() <= .559 * width && 
            e.getSceneY() >= .566 * height && e.getSceneY() <= .622 * height))
        {
          pauseScene.setCursor(Cursor.HAND);
        }
        else pauseScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    pauseRoot.setBackground(new Background(bgImage));
    pauseStage.toFront();
    stage.toBack();
    
    pauseStage.setScene(pauseScene);
    pauseStage.setFullScreen(true);
    pauseStage.show();
  }
  
  /**
   * loads the death screen when the player dies.
   */
  private void loadDeathScreen()
  {
    gameLoop.stop();
    BorderPane deathRoot = new BorderPane();
    Stage deathStage = new Stage();
    Scene deathScene = new Scene(deathRoot, width ,height, true, SceneAntialiasing.BALANCED);
    deathScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .286 * width && e.getSceneX() <= .703 * width && 
            e.getSceneY() >= .380 * height && e.getSceneY() <= .450 * height)
        {
          stage.setFullScreen(true);
          stage.toFront();
          gameLoop.start();
          deathStage.close();
          
          //any keys that were pressed at death remain pressed, so this code fixes that.
          bot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyPress(java.awt.event.KeyEvent.VK_W);
          bot.keyPress(java.awt.event.KeyEvent.VK_A);
          bot.keyPress(java.awt.event.KeyEvent.VK_S);
          bot.keyPress(java.awt.event.KeyEvent.VK_D);
          bot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyRelease(java.awt.event.KeyEvent.VK_W);
          bot.keyRelease(java.awt.event.KeyEvent.VK_A);
          bot.keyRelease(java.awt.event.KeyEvent.VK_S);
          bot.keyRelease(java.awt.event.KeyEvent.VK_D);
          walkSpeed = walkSpeedHolder;
        }
        if((e.getSceneX() >= .393 * width && e.getSceneX() <= .596 * width && 
            e.getSceneY() >= .639 * height && e.getSceneY() <= .709 * height))
        {
          System.exit(0);
        }        
      }     
    }); 
    deathScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if((e.getSceneX() >= .286 * width && e.getSceneX() <= .703 * width && 
            e.getSceneY() >= .380 * height && e.getSceneY() <= .450 * height) || 
            (e.getSceneX() >= .393 * width && e.getSceneX() <= .596 * width && 
            e.getSceneY() >= .639 * height && e.getSceneY() <= .709 * height))
        {
          deathScene.setCursor(Cursor.HAND);
        }
        else deathScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    deathRoot.setBackground(new Background(deathBG));
    deathStage.toFront();
    stage.toBack();
    
    deathStage.setScene(deathScene);
    deathStage.setFullScreen(true);
    deathStage.show();
    
  }
  /**
   * 
   * @param currentStage main game stage
   * closes the current stage and starts a new one on a new level.
   */
  private void goToNewLevel(Stage currentStage)
  {
    walkingClip.stop();
    runningClip.stop();
    gameLoop.stop();
    for(Zombie z: zombieList)
    {
      z.closeSounds();
    }
   
    zombieList.clear();
    randZombieLocs.clear();
    lineZombieLocs.clear();
   
    gameLoop = new MainGameLoop();
    
    rootNode.setLeftChild(null);
    rootNode.setRightChild(null);
    levelUp();
    currentStage.close();
    root.getChildren().clear();
    
    
    nearPossibleCollisions.clear();
    
    stage = new Stage();
    stage.toBack();
    stage.setScene(buildScene(stage));
    stage.setTitle("Zombie House");
    stage.setFullScreen(true);
    
  }
  
  /**
   * loads the level up screen and handles which attribute the player wants to level up.
   */
  private void levelUp()
  {
    BorderPane levelUpRoot = new BorderPane();
    levelUpStage = new Stage();
    Scene levelUpScene = new Scene(levelUpRoot, width ,height, true, SceneAntialiasing.BALANCED);
    
    levelUpScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .291 * width && e.getSceneX() <= .392 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .798 * height)
        {
          
          walkSpeedHolder += 1;
             
          stage.toFront();
          levelUpStage.toBack();  
          stage.show();
          gameLoop.start();
          startZombies();
          walkSpeed = walkSpeedHolder;
          cameraGroup.setTranslateX(startZ * 2);
          cameraGroup.setTranslateZ(startX * 2);
          //any keys that were pressed at death remain pressed, so this code fixes that.
          bot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyPress(java.awt.event.KeyEvent.VK_W);
          bot.keyPress(java.awt.event.KeyEvent.VK_A);
          bot.keyPress(java.awt.event.KeyEvent.VK_S);
          bot.keyPress(java.awt.event.KeyEvent.VK_D);
          bot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyRelease(java.awt.event.KeyEvent.VK_W);
          bot.keyRelease(java.awt.event.KeyEvent.VK_A);
          bot.keyRelease(java.awt.event.KeyEvent.VK_S);
          bot.keyRelease(java.awt.event.KeyEvent.VK_D);
          levelUpStage.close();

        }
        if((e.getSceneX() >= .426 * width && e.getSceneX() <= .569 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .798 * height))
        {
          playerStamina += 1;
           
          stage.toFront();
          levelUpStage.toBack();
          
          stage.show();  
          gameLoop.start();
          startZombies();
          walkSpeed = walkSpeedHolder;
          cameraGroup.setTranslateX(startZ * 2);
          cameraGroup.setTranslateZ(startX * 2);
          //any keys that were pressed at death remain pressed, so this code fixes that.
          bot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyPress(java.awt.event.KeyEvent.VK_W);
          bot.keyPress(java.awt.event.KeyEvent.VK_A);
          bot.keyPress(java.awt.event.KeyEvent.VK_S);
          bot.keyPress(java.awt.event.KeyEvent.VK_D);
          bot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyRelease(java.awt.event.KeyEvent.VK_W);
          bot.keyRelease(java.awt.event.KeyEvent.VK_A);
          bot.keyRelease(java.awt.event.KeyEvent.VK_S);
          bot.keyRelease(java.awt.event.KeyEvent.VK_D);
          levelUpStage.close();
          
          
          
        }        
        if((e.getSceneX() >= .603 * width && e.getSceneX() <= .705 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .789 * height))
        {
          staminaRegen += 0.1;
         
          stage.toFront();
          levelUpStage.toBack();
          stage.show();   
          gameLoop.start();
          startZombies();
          walkSpeed = walkSpeedHolder;
          cameraGroup.setTranslateX(startZ * 2);
          cameraGroup.setTranslateZ(startX * 2);
          //any keys that were pressed at death remain pressed, so this code fixes that.
          bot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyPress(java.awt.event.KeyEvent.VK_W);
          bot.keyPress(java.awt.event.KeyEvent.VK_A);
          bot.keyPress(java.awt.event.KeyEvent.VK_S);
          bot.keyPress(java.awt.event.KeyEvent.VK_D);
          bot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
          bot.keyRelease(java.awt.event.KeyEvent.VK_W);
          bot.keyRelease(java.awt.event.KeyEvent.VK_A);
          bot.keyRelease(java.awt.event.KeyEvent.VK_S);
          bot.keyRelease(java.awt.event.KeyEvent.VK_D);
          levelUpStage.close();
        }        
      }     
    }); 
    levelUpScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if((e.getSceneX() >= .291 * width && e.getSceneX() <= .392 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .798 * height) || 
            (e.getSceneX() >= .426 * width && e.getSceneX() <= .569 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .798 * height) ||
            (e.getSceneX() >= .603 * width && e.getSceneX() <= .705 * width && e.getSceneY() >= .766 * height && e.getSceneY() <= .798 * height))
        {
          levelUpScene.setCursor(Cursor.HAND);
        }
        else levelUpScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    levelUpRoot.setBackground(new Background(endLevelImages.get(levelNum)));
    levelNum++;
    levelUpStage.toFront();
    levelUpStage.setScene(levelUpScene);
    levelUpStage.setFullScreen(true);
    levelUpStage.show();
  }
  
  /**
   * loads the end game screen.
   */
  private void endGame()
  {
    Image endScreen = new Image("file:"+PATH + "/Resources/Images/EndGame.png", width, height, false, true);
    BackgroundImage bgImage = new BackgroundImage(endScreen,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);
    BorderPane endRoot = new BorderPane();
    Stage endStage = new Stage();
    Scene endScene = new Scene(endRoot, width ,height, true, SceneAntialiasing.BALANCED);
    
    endScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .294 * width && e.getSceneX() <= .700 * width && e.getSceneY() >= .760 * height && e.getSceneY() <= .815 * height)
        {
          rollCredits(endStage);
          
        }
            
      }     
    }); 
    endScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .294 * width && e.getSceneX() <= .700 * width && e.getSceneY() >= .760 * height && e.getSceneY() <= .815 * height) 
        {
          endScene.setCursor(Cursor.HAND);
        }
        else endScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    
    endRoot.setBackground(new Background(bgImage));
    endStage.toFront();
    stage.toBack();    
    endStage.setScene(endScene);
    endStage.setFullScreen(true);
    endStage.show();
    for(Zombie z: zombieList)
    {
      z.closeSounds();
    }
    stage.close();
  }
  
  /**
   * 
   * @param endStage the previous stage
   * loads the credits
   */
  private void rollCredits(Stage endStage)
  {
    Image creditScreen = new Image("file:"+PATH + "/Resources/Images/Credits.png", width, height, false, true);
    BackgroundImage bgImage = new BackgroundImage(creditScreen,
        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);
    BorderPane creditRoot = new BorderPane();
    Stage creditStage = new Stage();
    Scene creditScene = new Scene(creditRoot, width ,height, true, SceneAntialiasing.BALANCED);
    creditScene.setOnMouseClicked(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .339 * width && e.getSceneX() <= .654 * width && e.getSceneY() >= .690 * height && e.getSceneY() <= .748 * height)
        {
          System.exit(0);
          
        }
            
      }     
    }); 
    creditScene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {

      @Override
      public void handle(MouseEvent e)
      {
        if(e.getSceneX() >= .339 * width && e.getSceneX() <= .654 * width && e.getSceneY() >= .690 * height && e.getSceneY() <= .748 * height) 
        {
          creditScene.setCursor(Cursor.HAND);
        }
        else creditScene.setCursor(Cursor.DEFAULT);
      }
      
    });
    
    
    creditRoot.setBackground(new Background(bgImage));
    
    creditStage.toFront();
    endStage.toBack();
    endStage.close();
    creditStage.setScene(creditScene);
    creditStage.setFullScreen(true);
    creditStage.show();
  }
  
  /**
   * 
   * @param possibleCollisions list of wall objects surrounding the player
   * @param newX the x position that the player/zombie is moving to
   * @param newZ the z position that the player/zombie is moving to
   * @param angle camera angle
   * @return whether or not the player/zombie will collide with this movement
   */
  private boolean collidingWithWall(ArrayList<Bounds> possibleCollisions, double newX, double newZ, double angle)
  {
    boolean collidingWithWall = false;
    if(possibleCollisions == null) return true;
    List<Double> xPoints = new ArrayList<>();
    List<Double> zPoints = new ArrayList<>();
    
    //creates a "circle" of points around the player
    for(int i = 0; i <= 360; i+=10)
    {
      xPoints.add(Math.cos((angle + i) * (Math.PI / 180)));
      zPoints.add((Math.sin((angle + i) * (Math.PI) / 180)));
    }
    
    //if any of those points are contained within a surrounding wall, collision is detected
    for (Bounds b : possibleCollisions)
    {
      for(int i = 0; i < xPoints.size(); i++)
      {
        if(b.contains(newX + xPoints.get(i), 0, newZ + zPoints.get(i)))
        {
          collidingWithWall = true;
          break;
        }        
      }
      if(collidingWithWall) break;



    }
    return collidingWithWall;
  }
  
  /**
   * 
   * @param x1 entity one's x position
   * @param z1 entity one's z position
   * @param x2 entity two's x position
   * @param z2 entity two's z position
   * @return
   */
  private boolean collidingWithZombie(double x1, double z1, double x2, double z2)
  {
    double x = x2 - x1;
    double z = z2 - z1;
    //if the distance squared is less or equal to the two radii squared, collision is detected
    return (x * x) + (z * z) <= 4;
  }

  /**
   * 
   * @param args command line arguments. None in this case
   * launches the application
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}

     

