package endfield.entities;

public interface GameObject extends Transform {
	int getID();//entityID

	void setID(int index);//entityID

	void update();
}
