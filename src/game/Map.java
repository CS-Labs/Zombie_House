package game;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * @author Christian Seely
 * @author John Clark
 * @author Duong Nguyen
 * 
 * This class currently procedurally generates four regions and with 
 * those four region three rooms in each regions. As the rooms are created
 * the boundaries are pulled inwards to create the hallways. From there, a
 * pillar or inner wall is added to each room as well, doors are added, and
 * zombie/player locations set. This is all represented on a 2D floor plan.
 * 
 *
 */
public class Map
{
  private final int WINDOW_WIDTH  = 1000;
  private final int WINDOW_HEIGHT = 1000;  
  private int numRooms = 1;
  private int goalEndRooms = 4;
  private LinkedList<Rectangle> regions = new LinkedList<>();
  private List<Rectangle> regionOneRooms = new ArrayList<>();
  private List<Rectangle> regionTwoRooms = new ArrayList<>();
  private List<Rectangle> regionThreeRooms = new ArrayList<>();
  private List<Rectangle> regionFourRooms = new ArrayList<>();
  private LinkedList<Integer> possibleValues = new LinkedList<>();
  //The ascii array is 100 by 100 the reason that this an many things in the
  //class are divided by 10 is because the dimensions of a jframe used for displaying
  //the procedurally generated house was 1000 by 1000 in width and height and when
  //finishing the procedural generation many things had some logic based around
  //the size of the jframe but in the end everything is calculated for a 100x100
  //char array and that is what is sent to the house class to generate the house
  //in 3D. 
  public char asciiMap[][] = new char[WINDOW_WIDTH/10][WINDOW_HEIGHT/10];
  private Random random = new Random(); 
  private List<List<Rectangle>> listRegions = new ArrayList<>();
  private int start, exit;

  /**
   * @author John Clark
   * Adds obstacles and doors to each room in the house.
   */
  public void addObstaclesandDoors()
  {
    int minX, minY, maxX, maxY, topY, leftX, roomHeight, roomWidth, bottomY, rightX;
    int startY, startX, endY, endX, x, y;
    for(Rectangle room: regionOneRooms)
    {

      topY = room.getY() / 10;
      leftX = room.getX() / 10;
      roomHeight = room.getHeight() / 10;
      roomWidth = room.getWidth() / 10;
      bottomY = topY + roomHeight;
      rightX = leftX + roomWidth;

      if(random.nextInt(2) == 0) // pillar
      {
        minX = leftX + 3;
        maxX = rightX - 4;
        minY = topY + 3; 
        maxY = bottomY - 4;
        createPillar(minX, maxX, minY, maxY);

      }
      else //inner-room wall
      {
        int randomLoc = random.nextInt(4);
        if(random.nextInt(2) == 0) //vertical wall
        {

          if(randomLoc == 0) //wall starts at top wall
          {
            startY = topY + 1;

            //end the wall at least two tiles up from the bottom.
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2); 

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3); 
          }

          else if(randomLoc == 1) //wall ends at bottom wall
          {
            //start the wall at least two tiles from the top wall.
            startY = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3);
            endY = bottomY - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 down from the top to 5 up from the bottom.
            startY = random.nextInt((bottomY - 5) - (topY + 3)) + (topY + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the bottom
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          createVerticalWall(startY, endY, x); 

        }
        else //horizontal wall
        {
          if(randomLoc == 0) //wall starts at left wall
          {
            startX = leftX + 1;

            //end the wall at least 2 tiles away from the right side
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }

          else if(randomLoc == 1) //wall ends at right wall
          {
            //start the wall at least two tiles from the left wall.
            startX = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
            endX = rightX - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 over from the left to 5 in from the right.
            startX = random.nextInt((rightX - 5) - (leftX + 3)) + (leftX + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the right
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          createHorizontalWall(startX, endX, y);

        }
      }
      addDoors(room);
    }
    for(Rectangle room: regionTwoRooms)
    {
      topY = room.getY() / 10;
      leftX = room.getX() / 10;
      roomHeight = room.getHeight() / 10;
      roomWidth = room.getWidth() / 10;
      bottomY = topY + roomHeight;
      rightX = leftX + roomWidth;

      if(random.nextInt(2) == 0) // pillar
      {

        minX = leftX + 3;
        maxX = rightX - 4;
        minY = topY + 3; 
        maxY = bottomY - 4;

        createPillar(minX, maxX, minY, maxY);
      }
      else //inner-room wall
      {
        int randomLoc = random.nextInt(4);
        if(random.nextInt(2) == 0) //vertical wall
        {

          if(randomLoc == 0) //wall starts at top wall
          {
            startY = topY + 1;

            //end the wall at least two tiles up from the bottom.
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2); 

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3); 
          }

          else if(randomLoc == 1) //wall ends at bottom wall
          {
            //start the wall at least two tiles from the top wall.
            startY = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3);
            endY = bottomY - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 down from the top to 5 up from the bottom.
            startY = random.nextInt((bottomY - 5) - (topY + 3)) + (topY + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the bottom
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          createVerticalWall(startY, endY, x); 

        }
        else //horizontal wall
        {
          if(randomLoc == 0) //wall starts at left wall
          {
            startX = leftX + 1;

            //end the wall at least 2 tiles away from the right side
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }

          else if(randomLoc == 1) //wall ends at right wall
          {
            //start the wall at least two tiles from the left wall.
            startX = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
            endX = rightX - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 over from the left to 5 in from the right.
            startX = random.nextInt((rightX - 5) - (leftX + 3)) + (leftX + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the right
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          createHorizontalWall(startX, endX, y);

        }
      }
      addDoors(room);

    }
    for(Rectangle room: regionThreeRooms)
    {

      topY = room.getY() / 10;
      leftX = room.getX() / 10;
      roomHeight = room.getHeight() / 10;
      roomWidth = room.getWidth() / 10;
      bottomY = topY + roomHeight;
      rightX = leftX + roomWidth;

      if(random.nextInt(2) == 0) // pillar
      {
        minX = leftX + 3;
        maxX = rightX - 4;
        minY = topY + 3; 
        maxY = bottomY - 4;

        createPillar(minX, maxX, minY, maxY);
      }
      else //inner-room wall
      {
        int randomLoc = random.nextInt(4);
        if(random.nextInt(2) == 0) //vertical wall
        {

          if(randomLoc == 0) //wall starts at top wall
          {
            startY = topY + 1;

            //end the wall at least two tiles up from the bottom.
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2); 

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3); 
          }

          else if(randomLoc == 1) //wall ends at bottom wall
          {
            //start the wall at least two tiles from the top wall.
            startY = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3);
            endY = bottomY - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 down from the top to 5 up from the bottom.
            startY = random.nextInt((bottomY - 5) - (topY + 3)) + (topY + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the bottom
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          createVerticalWall(startY, endY, x); 

        }
        else //horizontal wall
        {
          if(randomLoc == 0) //wall starts at left wall
          {
            startX = leftX + 1;

            //end the wall at least 2 tiles away from the right side
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }

          else if(randomLoc == 1) //wall ends at right wall
          {
            //start the wall at least two tiles from the left wall.
            startX = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
            endX = rightX - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 over from the left to 5 in from the right.
            startX = random.nextInt((rightX - 5) - (leftX + 3)) + (leftX + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the right
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          createHorizontalWall(startX, endX, y);

        }
      }
      addDoors(room);

    }
    for(Rectangle room: regionFourRooms)
    {

      topY = room.getY() / 10;
      leftX = room.getX() / 10;
      roomHeight = room.getHeight() / 10;
      roomWidth = room.getWidth() / 10;
      bottomY = topY + roomHeight;
      rightX = leftX + roomWidth;

      if(random.nextInt(2) == 0) // pillar
      {
        minX = leftX + 3;
        maxX = rightX - 4;
        minY = topY + 3; 
        maxY = bottomY - 4;

        createPillar(minX, maxX, minY, maxY);
      }
      else //inner-room wall
      {
        int randomLoc = random.nextInt(4);
        if(random.nextInt(2) == 0) //vertical wall
        {

          if(randomLoc == 0) //wall starts at top wall
          {
            startY = topY + 1;

            //end the wall at least two tiles up from the bottom.
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2); 

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3); 
          }

          else if(randomLoc == 1) //wall ends at bottom wall
          {
            //start the wall at least two tiles from the top wall.
            startY = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3);
            endY = bottomY - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 down from the top to 5 up from the bottom.
            startY = random.nextInt((bottomY - 5) - (topY + 3)) + (topY + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the bottom
            endY = random.nextInt((bottomY - 3) - (startY + 2)) + (startY + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            x = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
          }
          createVerticalWall(startY, endY, x); 

        }
        else //horizontal wall
        {
          if(randomLoc == 0) //wall starts at left wall
          {
            startX = leftX + 1;

            //end the wall at least 2 tiles away from the right side
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }

          else if(randomLoc == 1) //wall ends at right wall
          {
            //start the wall at least two tiles from the left wall.
            startX = random.nextInt((rightX - 3) - (leftX + 3)) + (leftX + 3);
            endX = rightX - 1;

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          else //added wall doesn't touch either room wall.
          {
            //start the wall anywhere from 2 over from the left to 5 in from the right.
            startX = random.nextInt((rightX - 5) - (leftX + 3)) + (leftX + 3);

            //end the wall at least 2 tiles from the start point, and leave 2 tiles at the right
            endX = random.nextInt((rightX - 3) - (startX + 2)) + (startX + 2);

            //makes sure the sides of the wall are at least two tiles from the side of the room.
            y = random.nextInt((bottomY - 3) - (topY + 3)) + (topY + 3); 
          }
          createHorizontalWall(startX, endX, y);

        }
      }
      addDoors(room);
      borderRegions();
    }

    addConnection();
    addCharacters();
  }
  /**
   * @author John Clark
   *Adds doors to each wall of the passed room.
   */
  private void addDoors(Rectangle room)
  {
    int leftLoc, rightLoc, topLoc, bottomLoc, left, right, top, bottom;

    left = room.getX() / 10;
    right = (room.getX() + room.getWidth()) / 10;
    top = room.getY() / 10;
    bottom = (room.getY() + room.getHeight()) / 10;
    
    leftLoc = random.nextInt((bottom - 2) - (top + 1)) + (top + 1); 
    //door is added. If there is a wall on the other side of the door, pick another random location
    while(asciiMap[leftLoc][left+1] == 'w' || asciiMap[leftLoc+1][left+1] == 'w')
    {
      leftLoc = random.nextInt((bottom - 2) - (top + 1)) + (top + 1); 
    }

    topLoc = random.nextInt((right - 2) - (left + 1)) + (left + 1);
   //door is added. If there is a wall on the other side of the door, pick another random location
    while(asciiMap[top+1][topLoc] == 'w' || asciiMap[top+1][topLoc+1] == 'w')
    {
      topLoc = random.nextInt((right - 2) - (left + 1)) + (left + 1);
    }

    rightLoc = random.nextInt((bottom - 2) - (top + 1)) + (top + 1);

    /*doorways that were not on region boundaries were ending up in the hallways. 
    this corrects that.*/
    if(asciiMap[rightLoc][right] == '5' || asciiMap[rightLoc][right] == '6' || 
        asciiMap[rightLoc][right] == '7' || asciiMap[rightLoc][right] == '8' )
    {
      right--;
    }

    /*Make sure that a doorway is not placed with an inner wall obstacle blocking it*/
    while(asciiMap[rightLoc][right-1] == 'w' || asciiMap[rightLoc+1][right-1] == 'w')
    {
      if(asciiMap[rightLoc][right] == '5' || asciiMap[rightLoc][right] == '6' || 
          asciiMap[rightLoc][right] == '7' || asciiMap[rightLoc][right] == '8' )
      {
        right--;
      }
      rightLoc = random.nextInt((bottom - 2) - (top + 1)) + (top + 1);
    }


    bottomLoc = random.nextInt((right - 2) - (left + 1)) + (left + 1); 

    /*doorways that were not on region boundaries were ending up in the hallways. 
    this corrects that.*/
    if(asciiMap[bottom][bottomLoc] == '5' || asciiMap[bottom][bottomLoc] == '6' || 
        asciiMap[bottom][bottomLoc] == '7' || asciiMap[bottom][bottomLoc] == '8' )
    {
      bottom--;
    }

    /*Make sure that a doorway is not placed with an inner wall obstacle blocking it*/
    while(asciiMap[bottom-1][bottomLoc] == 'w' || asciiMap[bottom-1][bottomLoc+1] == 'w')
    {
      if(asciiMap[bottom][bottomLoc] == '5' || asciiMap[bottom][bottomLoc] == '6' || 
          asciiMap[bottom][bottomLoc] == '7' || asciiMap[bottom][bottomLoc] == '8' )
      {
        bottom--;
      }
      bottomLoc = random.nextInt((right - 2) - (left + 1)) + (left + 1); 
    }    

    //add all 4 doors to the ascii map
    asciiMap[leftLoc][left] = 'D';
    asciiMap[leftLoc+1][left] = 'D';

    asciiMap[top][topLoc] = 'D';
    asciiMap[top][topLoc+1] = 'D';   

    asciiMap[rightLoc][right] = 'D';
    asciiMap[rightLoc+1][right] = 'D';    

    asciiMap[bottom][bottomLoc] = 'D';
    asciiMap[bottom][bottomLoc + 1] = 'D';    
  }

  /**
   * @author John Clark
   * @param minX
   *    minimum X value for random location generation
   * @param maxX
   *    maximum X value for random location generation
   * @param minY
   *    minimum Y value for random location generation
   * @param maxY
   *    maximum Y value for random location generation
   *    
   * randomly generates a location for, and places a pillar onto the map
   */
  private void createPillar(int minX, int maxX, int minY, int maxY)
  {   
    int startX = random.nextInt(maxX - minX) + minX;
    int startY = random.nextInt(maxY - minY) + minY;

    asciiMap[startY][startX] = 'X';
    asciiMap[startY+1][startX] = 'X';
    asciiMap[startY][startX+1] = 'X';
    asciiMap[startY+1][startX+1] = 'X';
  }

  /**
   * @author John Clark
   * @param startY
   *    starting Y coordinate for the inner vertical wall
   * @param endY
   *    ending Y coordinate for the inner vertical wall
   * @param x
   *    X coordinate for the inner vertical wall
   *    
   * adds a vertical inner wall to the map.
   */
  private void createVerticalWall(int startY, int endY, int x)
  {
    for(int y = startY; y <= endY; y++)
    {
      asciiMap[y][x] = 'w';
    }
  }

  /**
   * @author John Clark
   * @param startX
   *    starting X coordinate for the inner vertical wall
   * @param endX
   *    ending X coordinate for the inner vertical wall
   * @param y
   *    Y coordinate for the inner vertical wall
   *    
   * adds a horizontal inner wall to the map.
   */
  private void createHorizontalWall(int startX, int endX, int y)
  {
    for(int x = startX; x <= endX; x++)
    {
      asciiMap[y][x] = 'w';
    }
  }


  /**
   * @author Christian Seely
   * @param gfx graphics Context
   * creates 3 rooms in each region
   * Note gfx context is only used if displaying the jframe
   * for demoing/testing. 
   */
  public void createRooms(GraphicsContext gfx)
  {
    goalEndRooms = 3; //We want 3 rooms per region. 
    //For each region build a tree of depth 2 where each leaf node
    //will represent a room (we are going to ignore one of of leaf nodes
    //as we want 3 rooms not 4 per region) 
    for(int i = 0; i < regions.size(); i++)
    {
      numRooms=1;
      Node rootNode = buildTree(regions.get(i),2, true);//Depth 2 tree
      //Add the rooms to each regions room list. 
      fillRoomsList(rootNode,i);
      //Update the rooms borders. 
      updateAsciiMapWithRoomBorders(i, gfx);
    }
    //Update map with hallways. 
    updateAsciiMapWithHallways();
  }
  
  /**
   * @author John Clark
   * adds hallways to the ascii map
   */
  private void updateAsciiMapWithHallways()
  {
    Rectangle region;
    for(int i = 1; i <= 4; i++)
    {
      region = regions.get(i-1);      
      for(int j = region.getX() / 10; j < (region.getX() + region.getWidth()) / 10; j++)
      {

        for(int k = region.getY() / 10; k < (region.getY() + region.getHeight()) / 10; k++)
        {
          if(asciiMap[k][j] == ' ')
          {
            asciiMap[k][j] = (char) ((i + 4) + 48);
          }
        } 
      }
    }
  }


  /*
   * Fill the regions list with regions objects. 
   */
  public void fillRegionList(Node node)
  {
    if(node.getLeftChild() != null) 
    {
      fillRegionList(node.getLeftChild());
    }
    if(node.getRightChild() != null) 
    {
      fillRegionList(node.getRightChild());
    }
    if(node.getLeftChild() == null && node.getRightChild() == null) 
    {	    	
      regions.add(node.getRoom());		    	
    }
  }

  /*
   * Fill each regions list with the rooms within that region.
   */
  private void fillRoomsList(Node node, int listNum)
  {
    if(listNum==0)
    {
      if(node.getLeftChild() != null) 
      {
        fillRoomsList(node.getLeftChild(),listNum);
      }
      if(node.getRightChild() != null) 
      {
        fillRoomsList(node.getRightChild(), listNum);
      }
      if(node.getLeftChild() == null && node.getRightChild() == null) 
      {	    	
        regionOneRooms.add(node.getRoom());		    	
      }
    }
    else if (listNum==1)
    {
      if(node.getLeftChild() != null) 
      {
        fillRoomsList(node.getLeftChild(),listNum);
      }
      if(node.getRightChild() != null) 
      {
        fillRoomsList(node.getRightChild(), listNum);
      }
      if(node.getLeftChild() == null && node.getRightChild() == null) 
      {	    	
        regionTwoRooms.add(node.getRoom());		    	
      }
    }
    else if (listNum == 2)
    {
      if(node.getLeftChild() != null) 
      {
        fillRoomsList(node.getLeftChild(),listNum);
      }
      if(node.getRightChild() != null) 
      {
        fillRoomsList(node.getRightChild(), listNum);
      }
      if(node.getLeftChild() == null && node.getRightChild() == null) 
      {	    	
        regionThreeRooms.add(node.getRoom());		    	
      }
    }
    else
    {
      if(node.getLeftChild() != null) 
      {
        fillRoomsList(node.getLeftChild(),listNum);
      }
      if(node.getRightChild() != null) 
      {
        fillRoomsList(node.getRightChild(), listNum);
      }
      if(node.getLeftChild() == null && node.getRightChild() == null) 
      {	    	
        regionFourRooms.add(node.getRoom());		    	
      }
    }
  }

  /**
   * @author Christian Seely
   * @author John Clark
   * @param listNum region index
   * @param gfx graphics context
   * adds the representation of the rooms to the ascii map based on the dimensions
   * of each room held within the rectangle object. 
   */
  public void updateAsciiMapWithRoomBorders(int listNum, GraphicsContext gfx)
  {
    if(listNum==0)
    {
      int width,height,x,y;
      for(Rectangle room: regionOneRooms )
      {
        width = room.getWidth()/10;
        height = room.getHeight()/10;
        x = room.getX()/10;
        y = room.getY()/10;
        for (int i = y; i < y+height; i++)
        {
          for(int j = x; j < x+width; j++)
          {
            if(i==y||i==(y+height-1) && asciiMap[i+1][j] != 'w'
                || j==x || j==(x+width - 1) && asciiMap[i][j+1] != 'w')
            {
              asciiMap[i][j] = 'w';

            }
            else if(asciiMap[i][j] != 'w')
            {
              asciiMap[i][j] = '1';
            }
          }
        }
      }
    }
    else if (listNum==1)
    {
      int width,height,x,y;
      for(Rectangle room: regionTwoRooms )
      {
        width = room.getWidth()/10;
        height = room.getHeight()/10;
        x = room.getX()/10;
        y = room.getY()/10;

        for (int i = y; i < y+height; i++)
        {
          for(int j = x; j < x+width; j++)
          {
            if(i==y||i==(y+height-1) && asciiMap[i+1][j] != 'w'
                || j==x || j==(x+width-1) && asciiMap[i][j+1] != 'w')
            {
              asciiMap[i][j] = 'w';

            }
            else if(asciiMap[i][j] != 'w')
            {
              asciiMap[i][j] = '2';
            }
          }
        }


      }
    }
    else if (listNum == 2)
    {
      int width,height,x,y;
      for(Rectangle room: regionThreeRooms )
      {
        width = room.getWidth()/10;
        height = room.getHeight()/10;
        x = room.getX()/10;
        y = room.getY()/10;

        for (int i = y; i < y+height; i++)
        {
          for(int j = x; j < x+width; j++)
          {
            if(i==y||i==(y+height-1) && asciiMap[i+1][j] != 'w'
                || j==x || j==(x+width-1) && asciiMap[i][j+1] != 'w')
            {
              asciiMap[i][j] = 'w';
            }
            else if(asciiMap[i][j] != 'w')
            {
              asciiMap[i][j] = '3';
            }
          }
        }




      }
    }
    else
    {
      int width,height,x,y;
      for(Rectangle room: regionFourRooms )
      {
        width = room.getWidth()/10;
        height = room.getHeight()/10;
        x = room.getX()/10;
        y = room.getY()/10;

        for (int i = y; i < y+height; i++)
        {
          for(int j = x; j < x+width; j++)
          {
            if(i==y||i==(y+height-1) && asciiMap[i+1][j] != 'w'
                || j==x || j==(x+width-1) && asciiMap[i][j+1] != 'w')
            {
              asciiMap[i][j] = 'w';

            }
            else if(asciiMap[i][j] != 'w')
            {
              asciiMap[i][j] = '4';
            }
          }
        }



      }
    }
     

  }

/**
 * Since the jframe was 1000x1000 the possible values
 * had to be values of 10, which if divided by 10
 * represent the indices in the ascii array. 
 */
  public void fillPossibleValues()
  {
    for(int i = 1; i <=1000; i++)
    {
      if(i%10==0)
      {
        possibleValues.push(i);
      }
    }
  }

  /**
   * Initializes the ascii map
   */
  public void createAsciiMap()
  {

    //Initialize.
    for(int j = 0; j < WINDOW_WIDTH/10;j++)
    {
      for(int k = 0; k < WINDOW_HEIGHT/10;k++)
      {
        asciiMap[j][k] = ' ';
      }
    }
    //Account for regions borders in map.
    borderRegions();

  }

  /**
   * re-borders the regions to cover up doors that were placed on region boundaries
   * since we just placed a door on each wall.
   */
  private void borderRegions()
  {
    int width,height,x,y;
    for(Rectangle room: regions)
    {
      width = room.getWidth()/10;
      height = room.getHeight()/10;
      x = room.getX()/10;
      y = room.getY()/10;
      for (int i = y; i < y+height; i++)
      {
        for(int j = x; j < x+width; j++)
        {
          if(i==y||i==(y+height)
              || j==x || j==(x+width))
          {
            asciiMap[i][j] = 'w';
          }
        }
      }
    }
    for(int j = 0; j < WINDOW_WIDTH/10;j++)
    {
      asciiMap[0][j]='w';
      asciiMap[WINDOW_WIDTH/10-1][j] = 'w';
    }
    for(int j = 0; j < WINDOW_HEIGHT/10;j++)
    {
      asciiMap[j][0]='w';
      asciiMap[j][WINDOW_HEIGHT/10-1] = 'w';
    }
  }

  /**
   * Prints out the ascii map to the console
   */
  public void printAsciiMap()
  {
    for(int i = 0; i < WINDOW_WIDTH/10;i++)
    {
      for(int j = 0; j < WINDOW_HEIGHT/10;j++)
      {
        System.out.print("[" + asciiMap[i][j] + "]");
      }
      System.out.println();
    }
  }


  /**
   * @author Christian Seely
   * @author John Clark
   * @param gfx graphics context
   * creates a visual representation of the floor plan
   */
  public void displayBoard(GraphicsContext gfx)
  {
    for(int i = 0; i < WINDOW_WIDTH/10;i++)
    {
      for(int j = 0; j < WINDOW_HEIGHT/10;j++)
      {
        if(asciiMap[i][j] == ('w'))
        {
          gfx.setFill(Color.LIGHTGREY);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);
        }
        else if(asciiMap[i][j] == '1' || asciiMap[i][j] == '5')
        {
          gfx.setFill(Color.RED);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);

        }
        else if(asciiMap[i][j] == '2' || asciiMap[i][j] == '6')
        {
          gfx.setFill(Color.GREEN);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);

        }
        else if(asciiMap[i][j] == '3' || asciiMap[i][j] == '7')
        {
          gfx.setFill(Color.BLUE);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);

        }
        else if(asciiMap[i][j] == '4' || asciiMap[i][j] == '8')
        {
          gfx.setFill(Color.ORANGE);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);

        }
        else if(asciiMap[i][j] == 'X')
        {
          gfx.setFill(Color.INDIGO);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);
          
        }
        //Uncomment if you want to see the player for testing or demos. 
        //        else if(asciiMap[i][j] == 'P')
        //        {
        //          gfx.setFill(Color.WHITE);
        //          gfx.fillRect(j*10, i*10, 10, 10);
        //          gfx.setFill(Color.BLACK);
        //          gfx.strokeRect(j*10, i*10, 10, 10);
        //        }
        else
        {
          gfx.setFill(Color.BLACK);
          gfx.fillRect(j*10, i*10, 10, 10);
          gfx.setFill(Color.BLACK);
          gfx.strokeRect(j*10, i*10, 10, 10);
        }

      }
    }

  }




  /**
   * @author Christian Seely
   * @param room for start node
   * @param level depth of tree
   * @param creatingRooms if creating rooms. 
   * @return Root node of the tree
   * Recursively build the tree, each node in the tree represents a room
   * or rectangle object, the two children of every node represent the two
   * rooms that are created by splitting their parent nodes horizontally 
   * or vertically at some location. By doing this the leaf nodes of the 
   * tree contain the final rooms we want, except we don't need every one
   * because we have a goal number of rooms per region (3) so we can ignore
   * the four leaf node (depth 2 tree if you consider the root node the 
   * 0th depth)
   */
  public Node buildTree(Rectangle room,  int level, boolean creatingRooms)
  {

    Node node = new Node(room);
    if(level != 0 && numRooms < goalEndRooms)
    {
      //Get two rectangle objects randomly created from a node, these 
      //will be used in the creation of the nodes left and right child. 
      Rectangle[] rooms = randomRoomSplitter(node.getRoom(), creatingRooms);
      node.setLeftChild(buildTree(rooms[0],  level-1, creatingRooms));
      node.setRightChild(buildTree(rooms[1], level-1, creatingRooms));			
    }		

    return node;
  }
  /**
   * 
   * @param min
   * @param max
   * @return A random number between min and max. 
   */
  public int getRandomNumber(int min, int max)
  {
    return possibleValues.get(random.nextInt(max - 1));
  }
  /*
   * @author John Clark
   * @author Christian Seely
   * 
   * @param room The room to be split
   * @param creatingRooms whether or not the actual rooms are being created
   * 
   * This method takes a room (parent) and splits it vertically or
   * horizontally (50/50 chance) and then returns a two element 
   * array containing the two new rooms (children). As the rooms are
   * split, they are reduced in size by three tiles to add the hallways
   * 
   
   */
  private  Rectangle[] randomRoomSplitter(Rectangle room, boolean creatingRooms)
  {
    int randomWidth;
    int randomHeight;
    double randomRange;
    Rectangle newRoomOne = null;
    Rectangle newRoomTwo = null;

    //if creating the rooms, there should be more variability in size
    if(creatingRooms) randomRange = .25;
    else randomRange = .1;
    
    if(room.getWidth() > room.getHeight() ) //Vertically split
    {
      randomWidth = getRandomNumber(1, WINDOW_WIDTH / 10);
      
      while(randomWidth < ((room.getWidth() / 2) - randomRange * room.getWidth()) || 
          randomWidth > ((room.getWidth() / 2) + randomRange * room.getWidth()))
      {
        randomWidth = getRandomNumber(1, WINDOW_WIDTH /10);
      }

      //adds hallways if the rooms are being created
      if(creatingRooms)
      {
        //if room one is bigger, reduce its size for the hallways. Otherwise, do the same to room two
        if(randomWidth > room.getWidth() / 2)
        {
          newRoomOne = new Rectangle(room.getX(), room.getY(), randomWidth - 30,
              room.getHeight());

          newRoomTwo = new Rectangle(room.getX() + randomWidth, room.getY(),
              room.getWidth() - randomWidth, room.getHeight());  
        }
        else
        {
          newRoomOne = new Rectangle(room.getX(), room.getY(), randomWidth,
              room.getHeight());

          newRoomTwo = new Rectangle(room.getX() + randomWidth + 30, room.getY(),
              room.getWidth() - randomWidth - 30, room.getHeight());  
        }
      }
      else
      {
        newRoomOne = new Rectangle(room.getX(), room.getY(), randomWidth,
            room.getHeight());

        newRoomTwo = new Rectangle(room.getX() + newRoomOne.getWidth(), room.getY(),
            room.getWidth() - newRoomOne.getWidth() - 10, room.getHeight());  
      }
      numRooms ++;
    }
    else //Horizontally split
    {
      randomHeight = getRandomNumber(1, WINDOW_HEIGHT/10);
      while(randomHeight < ((room.getHeight() / 2) - randomRange * room.getHeight()) || randomHeight > ((room.getHeight() / 2) + randomRange * room.getHeight())) 
      {       
        randomHeight = getRandomNumber(1, WINDOW_HEIGHT / 10);
      }
      
      //adds hallways if rooms are being created
      if(creatingRooms)
      {
      //if room one is bigger, reduce its size for the hallways. Otherwise, do the same to room two
        if(randomHeight > room.getHeight() / 2)
        {
          newRoomOne = new Rectangle(room.getX(), room.getY(), room.getWidth(), randomHeight - 30);

          newRoomTwo = new Rectangle(room.getX(), room.getY() + randomHeight, room.getWidth(),
              room.getHeight() - randomHeight);  
        }
        else
        {
          newRoomOne = new Rectangle(room.getX(), room.getY() + 40, room.getWidth(), randomHeight);

          newRoomTwo = new Rectangle(room.getX(), room.getY() + randomHeight + 70, room.getWidth(),
              room.getHeight() - randomHeight - 70);  
        }
      }
      else
      {
        newRoomOne = new Rectangle(room.getX(), room.getY(), room.getWidth(), randomHeight);

        newRoomTwo = new Rectangle(room.getX(), room.getY() + newRoomOne.getHeight(), room.getWidth(),
            room.getHeight() - newRoomOne.getHeight() - 10);  
      }


      numRooms++;
    }


    Rectangle[] splitRooms = {newRoomOne,newRoomTwo};
    return splitRooms;

  }


  /**
   * 
   * @return ascii representation of the floorplan
   * returns the ascii representation of the floorplan.
   */
  public char[][] getBoard()
  {
    return asciiMap;
  }


  /**
   * adding an exit door on a random room and random horizontal or vertical wall of that room
   * */
  private void addExitDoor(int index1, int index2)
  {
    int rand;
    int width, height, xLoc, yLoc, x, y, maxWidth, maxHeight;
    
    /*choose start and exit on the regions randomly*/

    if(Math.random() > 0.5)
    {
      start = index1;
      exit = index2;
    }else
    {
      start = index2;
      exit = index1;
    }

    width = regions.get(exit).getWidth()/10;
    height = regions.get(exit).getHeight()/10;
    maxWidth = regions.get(1).getX()/10 + regions.get(1).getWidth()/10;
    maxHeight = regions.get(2).getY()/10 + regions.get(2).getHeight()/10;
    xLoc = regions.get(exit).getX()/10;
    yLoc = regions.get(exit).getY()/10;

    

    /*random exit door on horizontal or vertical wall for a certain region*/
    if(Math.random() > 0.5)
    {/*random exit door on vertical wall*/
      rand = (int)(yLoc+1 + Math.random()*(height-3));
      x = (exit == 0 || exit == 2) ? 1 : maxWidth-1;
      while(!(asciiMap[rand][x] != 'w'&& asciiMap[rand+1][x] != 'w'))
      {
        rand = (int)(yLoc+1 + Math.random()*(height-3));
      }
      x = (exit == 0 || exit == 2) ? 0 : maxWidth;
      asciiMap[rand][x] = 'E';
      asciiMap[rand+1][x] = 'E';
      House.setExit(new Point(rand, x), new Point(rand + 1, x), true);
    }
    else
    {/*random exit door on horizontal door*/
      rand = (int)(xLoc+1 + Math.random()*(width-3));
      y = (exit == 0 || exit == 1) ? 1 : maxHeight-1;
      while(!(asciiMap[y][rand] != 'w'&& asciiMap[y][rand+1] != 'w'))
      {
        rand = (int)(xLoc+1 + Math.random()*(width-3));
      }
      y = (exit == 0 || exit == 1) ? 0 : maxHeight;
      asciiMap[y][rand] = 'E';
      asciiMap[y][rand+1] = 'E';
      House.setExit(new Point(y, rand), new Point(y, rand + 1), false);
    }
  }


  /**
   * adding connections to rooms with random location on the wall
   * */
  public void addConnection()
  {

    int rand, maxRange, len1, len2;
    int leftLen, rightLen, topLoc, botLoc;

    if(Math.random() > 0.5)
    {/*vertical walls with a random door on horizontal wall*/

      /*random connection on top wall*/
      len1 =  regions.get(0).getHeight()/10;
      topLoc = regions.get(0).getWidth()/10;
      rand = (int)(1 + Math.random()*(len1-3));
      while(!(asciiMap[rand][topLoc-1] != 'w' && asciiMap[rand][topLoc+1] != 'w' &&
          asciiMap[rand+1][topLoc-1] != 'w' && asciiMap[rand+1][topLoc+1] != 'w'))
      {
        rand = (int)(1 + Math.random()*(len1 - 3));
      }
      asciiMap[rand][topLoc] = 'D';
      asciiMap[rand+1][topLoc] = 'D';		

      /*random connection on bottom wall*/
      len1 = regions.get(2).getHeight()/10;
      botLoc = regions.get(2).getWidth()/10;
      rand = (int)(regions.get(2).getY()/10 + Math.random()*(len1 - 3));
      while(!(asciiMap[rand][botLoc-1] != 'w' && asciiMap[rand][botLoc+1] != 'w' &&
          asciiMap[rand+1][botLoc-1] != 'w' && asciiMap[rand+1][botLoc+1] != 'w'))
      {
        rand = (int)(regions.get(2).getY()/10 + Math.random()*(len1 - 3));
      }
      asciiMap[rand][botLoc] = 'D';
      asciiMap[rand+1][botLoc] = 'D';

      /*pick a random connection on a horizontal wall*/
      if(Math.random() > 0.5)
      {/*left horizontal wall*/
        /*random connection on the left wall*/
        len1 = regions.get(0).getWidth()/10;
        len2 = regions.get(2).getWidth()/10;
        leftLen = regions.get(0).getHeight()/10;
        maxRange =  (len1 > len2) ? len2 : len1;
        rand = (int)(1 + Math.random()*(maxRange - 3));
        /*avoiding inner wall blocks*/
        while(!(asciiMap[leftLen-1][rand] != 'w' && asciiMap[leftLen+1][rand] != 'w' &&
            asciiMap[leftLen-1][rand+1] != 'w' && asciiMap[leftLen+1][rand+1] != 'w'))
        {
          rand = (int)(1 + Math.random()*(maxRange - 3));
        }
        asciiMap[leftLen][rand] = 'D';
        asciiMap[leftLen][rand+1] = 'D';

        /*creating an exit door on region 1 or 3*/
        addExitDoor(1, 3);

      }else
      {/*right horizontal wall*/
        /*random connection on the right wall*/
        len1 = regions.get(1).getWidth()/10;
        len2 = regions.get(3).getWidth()/10;
        rightLen = regions.get(1).getHeight()/10;
        if(len1 > len2)
          rand = (int)(regions.get(3).getX()/10 + 1 + Math.random()*(len2 - 3));  		
        else
          rand = (int)(regions.get(1).getX()/10 + 1 + Math.random()*(len1 - 3));
        /*avoiding inner wall blocks*/
        while(!(asciiMap[rightLen-1][rand] != 'w' && asciiMap[rightLen+1][rand] != 'w' &&
            asciiMap[rightLen-1][rand+1] != 'w' && asciiMap[rightLen+1][rand+1] != 'w'))
        {
          if(len1 > len2)
            rand = (int)(regions.get(3).getX()/10 + 1 + Math.random()*(len2 - 3));  		
          else
            rand = (int)(regions.get(1).getX()/10 + 1 + Math.random()*(len1 - 3));
        }
        asciiMap[rightLen][rand] = 'D';
        asciiMap[rightLen][rand+1] = 'D';

        /*creating an exit door on region 0 or 2*/
        addExitDoor(0, 2);

      }
    }else
    {/*horizontal walls with a random door on vertical wall*/

      /*random connection on the left wall*/
      len1 = regions.get(0).getWidth()/10;
      len2 = regions.get(2).getWidth()/10;
      leftLen = regions.get(0).getHeight()/10;
      maxRange =  (len1 > len2) ? len2 : len1;
      rand = (int)(1 + Math.random()*(maxRange - 3));
      /*avoiding inner wall blocks*/
      while(!(asciiMap[leftLen-1][rand] != 'w' && asciiMap[leftLen+1][rand] != 'w' &&
          asciiMap[leftLen-1][rand+1] != 'w' && asciiMap[leftLen+1][rand+1] != 'w'))
      {
        rand = (int)(1 + Math.random()*(maxRange - 3));
      }
      asciiMap[leftLen][rand] = 'D';
      asciiMap[leftLen][rand+1] = 'D';


      /*random connection on the right wall*/
      len1 = regions.get(1).getWidth()/10;
      len2 = regions.get(3).getWidth()/10;
      rightLen = regions.get(1).getHeight()/10;
      if(len1 > len2)
        rand = (int)(regions.get(3).getX()/10 + 1 + Math.random()*(len2 - 3));  		
      else
        rand = (int)(regions.get(1).getX()/10 + 1 + Math.random()*(len1 - 3));
      /*avoiding inner wall blocks*/
      while(!(asciiMap[rightLen-1][rand] != 'w' && asciiMap[rightLen+1][rand] != 'w' &&
          asciiMap[rightLen-1][rand+1] != 'w' && asciiMap[rightLen+1][rand+1] != 'w'))
      {
        if(len1 > len2)
          rand = (int)(regions.get(3).getX()/10 + 1 + Math.random()*(len2 - 3));  		
        else
          rand = (int)(regions.get(1).getX()/10 + 1 + Math.random()*(len1 - 3));
      }
      asciiMap[rightLen][rand] = 'D';
      asciiMap[rightLen][rand+1] = 'D';

      /*pick a random connection on vertical wall*/
      if(Math.random() > 0.5)
      {/*top vertical wall*/
        /*random connection on top wall*/
        len1 =  regions.get(0).getHeight()/10;
        topLoc = regions.get(0).getWidth()/10;
        rand = (int)(1 + Math.random()*(len1-3));
        while(!(asciiMap[rand][topLoc-1] != 'w' && asciiMap[rand][topLoc+1] != 'w' &&
            asciiMap[rand+1][topLoc-1] != 'w' && asciiMap[rand+1][topLoc+1] != 'w'))
        {
          rand = (int)(1 + Math.random()*(len1 - 3));
        }
        asciiMap[rand][topLoc] = 'D';
        asciiMap[rand+1][topLoc] = 'D';

        /*creating an exit door on region 2 or 3*/
        addExitDoor(2, 3);

      }else
      {/*bottom vertical wall*/
        /*random connection on bottom wall*/
        len1 = regions.get(2).getHeight()/10;
        botLoc = regions.get(2).getWidth()/10;
        rand = (int)(regions.get(2).getY()/10 + Math.random()*(len1 - 3));
        while(!(asciiMap[rand][botLoc-1] != 'w' && asciiMap[rand][botLoc+1] != 'w' &&
            asciiMap[rand+1][botLoc-1] != 'w' && asciiMap[rand+1][botLoc+1] != 'w'))
        {
          rand = (int)(regions.get(2).getY()/10 + Math.random()*(len1 - 3));
        }
        asciiMap[rand][botLoc] = 'D';
        asciiMap[rand+1][botLoc] = 'D';

        /*creating an exit door on region 0 or 1*/
        addExitDoor(0, 1);

      }
    }

  } 
  
  /**
   * @author John Clark
   * @author Duong Nguyen
   * 
   * sets the locations for all of the zombies and the player. 
   */
  public void addCharacters()
  {
    //The higher the spawn rate the worse the lag gets exponentially (prior to
    //adding models), After adding models the correct spawn rate (0.01)
    //cause a heap overflow unless you manipulate your available heap space.
    //This is a known bug in our program. 
    double zombieSpawn = .005;
    
    int randX = 0, randY = 0, regionIndex = 0, roomIndex = 0, randRegion, randRoom;
    int roomWidth, roomHeight;   

    listRegions.add(regionOneRooms);
    listRegions.add(regionTwoRooms);
    listRegions.add(regionThreeRooms);
    listRegions.add(regionFourRooms);

    /*getting random region for Master Zombie but the start region*/
    randRegion = start;
    while(randRegion == start)
    {
      randRegion = (int)(Math.random()*4);
    }
    randRoom = (int)(Math.random()*3);

    /*getting a list of ranges of x and y*/
    /*every pair of items in the list is a range*/

    
    

        
    roomWidth = regions.get(start).getWidth()/10;
    roomHeight = regions.get(start).getHeight()/10;

    

    /*loop thru each region*/
    for (List<Rectangle> region : listRegions)
    {
      for (Rectangle room : region)
      {
        roomWidth = room.getWidth()/10;
        roomHeight = room.getHeight()/10;

        for(int x = room.getX() / 10 + 1; x < (room.getX() + room.getWidth()) / 10 - 1; x++)
        {
          for(int y = room.getY() / 10 + 1; y < (room.getY() + room.getHeight()) / 10 - 1; y++)
          {

            //changed this to search by tile, as specified, rather than by room.
            if(asciiMap[y][x] != 'w' && asciiMap[y][x] != 'X' && asciiMap[y][x] != 'D' && Math.random() < zombieSpawn)
            {

              if(Math.random() > 0.5)
              {

                //add the location for the line walk zombie to the list of line walk locations in House.
                House.addLineZombieLoc(y, x);
              }						

              else
              {

                //add the location for the random walk zombie to the list of random walk locations in House.
                House.addRandZombieLoc(y, x);
              }
            }
          }
        }

        /* put a Master Zombie on random position, room and region but the start region*/
        if(randRegion == regionIndex && randRoom == roomIndex)
        {
          randX = 0;
          randY = 0;
          while(asciiMap[randY][randX] == 'w')
          {
            randX = (int)(room.getX()/10 + 1 + Math.random()*(roomWidth-1));
            randY = (int)(room.getY()/10 + 1 + Math.random()*(roomHeight-1));						
          }

          //set the location of the Master zombie in the house class.
          House.setMasterLoc(randY, randX);
         //System.out.println("Master Zombie created");
        }

        roomIndex++;
      }     

      roomIndex = 0;
      regionIndex++;
    }
    Rectangle startRegion = regions.get(start); 

    //this will be the character for this region's hallway in the asciiMap.
    char hallChar = (char) (start + 5 + 48);
    //pick a random x and z
    int playerX = random.nextInt((startRegion.getX() + startRegion.getWidth()) / 10 - startRegion.getX() / 10) + 
        startRegion.getX() / 10;
    int playerZ = random.nextInt((startRegion.getY() + startRegion.getHeight()) / 10 - startRegion.getY() / 10) + 
        startRegion.getY() / 10;
    //make sure that x and z place the player in a hallway
    while(asciiMap[playerZ][playerX] != hallChar)
    {
      playerX = random.nextInt((startRegion.getX() + startRegion.getWidth()) / 10 - startRegion.getX() / 10) + 
          startRegion.getX() / 10;
      playerZ = random.nextInt((startRegion.getY() + startRegion.getHeight()) / 10 - startRegion.getY() / 10) + 
          startRegion.getY() / 10;
    }    
    House.setStartPos(playerX, playerZ);

  }




}