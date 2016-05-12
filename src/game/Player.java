package game;

import javafx.geometry.Point2D;

/**
 * 
 * @author John Clark
 * This class defines a player character, assigning attributes to the player
 * and allowing those attributes to be set to different values during the game.
 *
 */
public class Player
{
  double playerSpeed, playerSight, playerHearing, playerStamina, playerStaminaRegen;
  Point2D startLocation;
  
  /**
   * 
   * @param playerSpeed
   *    The player's speed in tiles per second.
   * @param playerSight
   *    The player's sight in tiles.
   * @param playerHearing
   *    The player's hearing in tiles.
   * @param playerStamina
   *    The player's stamina, which decreases when running.
   * @param startLocation
   *    The player's start location on the map in x, y coordinates.
   * 
   * This is a player constructor, that creates a player object and defines its starting attributes.
   */
  public Player(double playerSpeed, double playerSight, double playerHearing, double playerStamina, Point2D startLocation)
  {
    this.playerSpeed = playerSpeed;
    this.playerSight = playerSight;
    this.playerHearing = playerHearing;
    this.playerStamina = playerStamina; 
    this.startLocation = startLocation;
    playerStaminaRegen = 0.2;
    //create the camera and pointlight here as well?
  }
  
  /**
   * 
   * @param playerSpeed
   *    The player's speed in tiles per second.
   *
   *This method set's this player's speed to the value of the passed 
   *playerSpeed parameter.
   */
  public void setPlayerSpeed(double playerSpeed)
  {
    this.playerSpeed = playerSpeed;
  }
  
  /**
   * 
   * @param playerSight
   *    The player's sight in tiles.
   *
   *This method set's this player's sight to the value of the passed 
   *playerSight parameter.
   */
  public void setPlayerSight(double playerSight)
  {
    this.playerSight = playerSight;
  }
 
  /**
   * 
   * @param playerHearing
   *    The player's hearing in tiles.
   *
   *This method set's this player's hearing to the value of the passed 
   *playerHearing parameter.
   */
  public void setPlayerHearing(double playerHearing)
  {
    this.playerHearing = playerHearing;
  }
  
  /**
   * 
   * @param playerStamina
   *    The player's stamina, which decreases when running.
   *
   *This method set's this player's stamina to the value of the passed 
   *playerStamina parameter.
   */
  public void setPlayerStamina(double playerStamina)
  {
    this.playerStamina = playerStamina;
  }
  
  /**
   * 
   * @param playerStaminaRegen
   *    The player's stamina regeneration factor when not running.
   *    
   *This method set's this player's stamina regen to the value of the passed 
   *playerStaminaRegen parameter.
   */
  public void setPlayerStaminaRegen(double playerStaminaRegen)
  {
    this.playerStaminaRegen = playerStaminaRegen;
  }
}
