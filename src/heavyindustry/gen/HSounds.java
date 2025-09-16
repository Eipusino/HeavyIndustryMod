package heavyindustry.gen;

import arc.audio.Sound;
import arc.files.Fi;
import heavyindustry.HVars;
import mindustry.Vars;

public final class HSounds {
	public static Sound
			ct1 = new Sound(),
			dbz1 = new Sound(),
			dd1 = new Sound(),
			fj = new Sound(),
			jg1 = new Sound(),
			flak = new Sound(),
			flak2 = new Sound(),
			shock = new Sound(),
			jumpIn = new Sound(),
			launch = new Sound(),
			gauss = new Sound(),
			radar = new Sound(),
			fissure = new Sound(),
			blaster = new Sound(),
			hugeShoot = new Sound(),
			hugeBlast = new Sound(),
			largeBeam = new Sound(),
			metalPipe = new Sound(),
			metalWalk = new Sound(),
			alert2 = new Sound(),
			hammer = new Sound(),
			coolingFan = new Sound(),
			hailRain = new Sound(),
			bigHailstoneHit = new Sound(),
			giantHailstoneHit = new Sound(),
			railGunBlast = new Sound(),
			railGunCharge = new Sound(),
			shootAltHeavy = new Sound(),
			shootAltLight = new Sound(),
			flowrateAbosrb = new Sound(),
			eviscerationCharge = new Sound(),
			eviscerationBlast = new Sound(),
			extinctionShoot = new Sound(),
			beamIntenseHighpitchTone = new Sound();

	/** Don't let anyone instantiate this class. */
	private HSounds() {}

	public static void load() {
		ct1 = new Sound(ogg("ct1"));
		dbz1 = new Sound(ogg("dbz1"));
		dd1 = new Sound(ogg("dd1"));
		fj = new Sound(ogg("fj"));
		jg1 = new Sound(ogg("jg1"));
		flak = new Sound(mp3("flak"));
		flak2 = new Sound(ogg("flak2"));
		launch = new Sound(ogg("launch"));
		shock = new Sound(ogg("shock"));
		jumpIn = new Sound(ogg("jumpIn"));
		gauss = new Sound(ogg("gauss"));
		radar = new Sound(mp3("radar"));
		fissure = new Sound(ogg("fissure"));
		blaster = new Sound(ogg("blaster"));
		hugeShoot = new Sound(ogg("hugeShoot"));
		hugeBlast = new Sound(ogg("hugeBlast"));
		largeBeam = new Sound(ogg("largeBeam"));
		metalPipe = new Sound(ogg("metalPipe"));
		metalWalk = new Sound(ogg("metalWalk"));
		alert2 = new Sound(ogg("alert2"));
		hammer = new Sound(ogg("hammer"));
		coolingFan = new Sound(ogg("coolingFan"));
		hailRain = new Sound(ogg("hailRain"));
		bigHailstoneHit = new Sound(ogg("bigHailstoneHit"));
		giantHailstoneHit = new Sound(ogg("giantHailstoneHit"));
		railGunBlast = new Sound(ogg("railGunBlast"));
		railGunCharge = new Sound(ogg("railGunCharge"));
		shootAltHeavy = new Sound(ogg("shootAltHeavy"));
		shootAltLight = new Sound(ogg("shootAltLight"));
		flowrateAbosrb = new Sound(ogg("flowrateAbsorb"));
		eviscerationCharge = new Sound(ogg("eviscerationCharge"));
		eviscerationBlast = new Sound(ogg("eviscerationBlast"));
		extinctionShoot = new Sound(ogg("extinctionShoot"));
		beamIntenseHighpitchTone = new Sound(ogg("beamIntenseHighpitchTone"));
	}

	public static void alertLoop() {
		if (!Vars.headless) {
			Vars.control.sound.loop(alert2, 2f);
		}
	}

	public static Fi ogg(String name) {
		return HVars.internalTree.child("sounds/" + name + ".ogg");
	}

	public static Fi mp3(String name) {
		return HVars.internalTree.child("sounds/" + name + ".mp3");
	}
}
