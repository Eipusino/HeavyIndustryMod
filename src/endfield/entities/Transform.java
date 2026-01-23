package endfield.entities;

import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import endfield.util.CollectionList;
import org.jetbrains.annotations.Nullable;

public interface Transform {
	CollectionList<Transform> tmpStack = new CollectionList<>(Transform.class);

	@Nullable Transform parent();

	Mat3D parentTrans();//tempTrans, new arc.math.geom.Mat3D()

	boolean parTransformed();//parTransformed

	void parTransformed(boolean transformed);//parTransformed

	default void updateParentTransform() {
		if (!parTransformed()) {
			tmpStack.clear();

			Transform curr = this;
			while (curr != null) {
				tmpStack.add(curr);

				curr = curr.parent();
			}

			for (int i = tmpStack.size - 1; i >= 0; i--) {
				Transform obj = tmpStack.items[i];
				Transform par = obj.parent();
				if (par == null) obj.parentTrans().idt();
				else {
					Mat3D objTrn = obj.parentTrans();
					Mat3D parTrn = par.parentTrans();

					par.getTransform(objTrn)
							.translate(obj.getX(), obj.getY(), obj.getZ())
							.mulLeft(parTrn);
				}
				obj.parTransformed(true);
			}
		}
	}

	Quat tmpQuat();//tmpQuat, new arc.math.geom.Quat()

	float getX();//x

	void setX(float x);//x

	float getY();//y

	void setY(float y);//y

	float getZ();//z

	void setZ(float z);//z

	default Vec3 getPos(Vec3 result) {
		return result.set(getX(), getY(), getZ());
	}

	default void setPosition(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	default void setPosition(Vec3 vec3) {
		setPosition(vec3.x, vec3.y, vec3.z);
	}

	default void transform(float x, float y, float z) {
		setPosition(getX() + x, getY() + y, getZ() + z);
	}

	default void transform(Vec3 vec3) {
		transform(vec3.x, vec3.y, vec3.z);
	}

	float getEulerX();//eulerX

	void setEulerX(float x);//eulerX

	float getEulerY();//eulerY

	void setEulerY(float y);//eulerY

	float getEulerZ();//eulerZ

	void setEulerZ(float z);//eulerZ

	default Quat getEuler(Quat result) {
		return getRotation(result);
	}

	default void setEuler(float x, float y, float z) {
		setEulerX(x);
		setEulerY(y);
		setEulerZ(z);
	}

	default void setEuler(Vec3 vec3) {
		setEuler(vec3.x, vec3.y, vec3.z);
	}

	default void rotate(float x, float y, float z) {
		setEuler(getEulerX() + x, getEulerY() + y, getEulerZ() + z);
	}

	default void rotate(Vec3 vec3) {
		rotate(vec3.x, vec3.y, vec3.z);
	}

	float getScaleX();//scaleX, 1f

	void setScaleX(float x);//scaleX

	float getScaleY();//scaleY, 1f

	void setScaleY(float y);//scaleY

	float getScaleZ();//scaleZ, 1f

	void setScaleZ(float z);//scaleZ

	default Vec3 getScale(Vec3 result) {
		return result.set(getScaleX(), getScaleY(), getScaleZ());
	}

	default void setScale(float x, float y, float z) {
		setScaleX(x);
		setScaleY(y);
		setScaleZ(z);
	}

	default void setScale(Vec3 vec3) {
		setScale(vec3.x, vec3.y, vec3.z);
	}

	default Quat getRotation(Quat result) {
		return result.setEulerAngles(getEulerY(), getEulerX(), getEulerZ());
	}

	default void setRotation(Quat quat) {
		setEuler(quat.getPitch(), quat.getYaw(), quat.getRoll());
	}

	default Mat3D getTransform(Mat3D result) {
		Quat q = getRotation(tmpQuat());

		return result.set(
				getX(), getY(), getZ(),
				q.x, q.y, q.z, q.w,
				getScaleX(), getScaleY(), getScaleZ()
		);
	}

	default Mat3D getAbsTransform(Mat3D result) {
		if (parent() == null) return getTransform(result);

		Quat q = getRotation(tmpQuat());

		return result.set(
				getX(), getY(), getZ(),
				q.x, q.y, q.z, q.w,
				getScaleX(), getScaleY(), getScaleZ()
		).mulLeft(parentTrans());
	}
}
