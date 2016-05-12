package game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.interactivemesh.jfx.importer.obj.*;


import javafx.scene.shape.MeshView;

/**
 * This class creates the list of zombie's movements
 * */
public class ZombieMovement
{
	private ObjModelImporter objModelImporter;
	private List<MeshView[]> movingZombie = new ArrayList<>();
  private final String PATH = System.getProperty("user.dir") + "/src";

	
	public ZombieMovement()
	{
		addZombie();
	}
	
	/**
	 * adding zombie model with animation to a list of meshview
	 * */
	private void addZombie()
	{
		MeshView[] mesh;

		for (int i = 0; i < 4; i++)
		{
			objModelImporter = new ObjModelImporter();
			objModelImporter.read(new File(PATH + "/Resources/ZombieAnimation/zombie"+i+".obj"));
			
			mesh = objModelImporter.getImport();
			objModelImporter.close();
			
			movingZombie.add(mesh);
		}

	}
	
	/**
	 * 
	 * @return a list the list of meshview that contain a list of movements
	 */
	public List<MeshView[]> getMovement()
	{
		return movingZombie;
	}
	
	/**
	 * 
	 * @return the size of the zombie movements list
	 */
	public int getSize()
	{
		return movingZombie.size();
	}

}
