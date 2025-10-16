package heavyindustry.audio;

import arc.Events;
import arc.audio.Sound;
import arc.files.Fi;
import arc.util.Log;
import heavyindustry.HVars;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Sounds;

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
			beamIntenseHighpitchTone = new Sound(),

			transform = new Sound(),
			largeTransform = new Sound(),
			idle = new Sound(),
			clang = new Sound(),
			bigCharge = new Sound(),
			laserCharge = new Sound(),
			laserSmall = new Sound(),
			laserBig = new Sound(),
			desSpearHit = new Sound(),
			desSpearCry = new Sound(),
			desLaser = new Sound(),
			desLaserShoot = new Sound(),
			desRailgun = new Sound(),
			desRailHit = new Sound(),
			desNukeShoot =  new Sound(),
			desNukeHit = new Sound(),
			desNukeHitFar = new Sound(),
			expElectric = new Sound(),
			expExotic = new Sound(),
			expOrganic = new Sound();

	/** Don't let anyone instantiate this class. */
	private HSounds() {}

	public static void load() {
		try {
			ct1 = load("ct1.ogg");
			dbz1 = load("dbz1.ogg");
			dd1 = load("dd1.ogg");
			fj = load("fj.ogg");
			jg1 = load("jg1.ogg");
			flak = load("flak.mp3");
			flak2 = load("flak2.ogg");
			launch = load("launch.ogg");
			shock = load("shock.ogg");
			jumpIn = load("jumpIn.ogg");
			gauss = load("gauss.ogg");
			radar = load("radar.mp3");
			fissure = load("fissure.ogg");
			blaster = load("blaster.ogg");
			hugeShoot = load("hugeShoot.ogg");
			hugeBlast = load("hugeBlast.ogg");
			largeBeam = load("largeBeam.ogg");
			metalPipe = load("metalPipe.ogg");
			metalWalk = load("metalWalk.ogg");
			alert2 = load("alert2.ogg");
			hammer = load("hammer.ogg");
			coolingFan = load("coolingFan.ogg");
			hailRain = load("hailRain.ogg");
			bigHailstoneHit = load("bigHailstoneHit.ogg");
			giantHailstoneHit = load("giantHailstoneHit.ogg");
			railGunBlast = load("railGunBlast.ogg");
			railGunCharge = load("railGunCharge.ogg");
			shootAltHeavy = load("shootAltHeavy.ogg");
			shootAltLight = load("shootAltLight.ogg");
			flowrateAbosrb = load("flowrateAbsorb.ogg");
			eviscerationCharge = load("eviscerationCharge.ogg");
			eviscerationBlast = load("eviscerationBlast.ogg");
			beamIntenseHighpitchTone = load("beamIntenseHighpitchTone.ogg");

			transform = load("transform.ogg");
			largeTransform = load("transformLarge.ogg");
			idle = load("idle.ogg");
			clang = load("clang.ogg");
			bigCharge = load("bigCharge.ogg");
			laserCharge = load("laserCharge.ogg");
			laserSmall = load("laserSmall.ogg");
			laserBig = load("laserBig.ogg");
			desSpearHit = load("desSpearHit.ogg");
			desSpearCry = load("desSpearCry.ogg");
			desLaser = load("desLaser.ogg");
			desLaserShoot = load("desLaserShoot.ogg");
			desRailgun = load("desRailgun.ogg");
			desRailHit = load("desRailHit.ogg");
			desNukeShoot = load("desNukeShoot.ogg");
			desNukeHit = load("desNukeHit.ogg");
			desNukeHitFar = load("desNukeHitFar.ogg");
			expElectric = load("expElectric.ogg");
			expExotic = load("expExotic.ogg");
			expOrganic = load("expOrganic.ogg");
		} catch (Exception e) {
			Log.err(e);

			Events.on(ClientLoadEvent.class, event -> Vars.ui.showException(e));
		}
	}

	public static void alertLoop() {
		if (!Vars.headless) {
			Vars.control.sound.loop(alert2, 2f);
		}
	}

	public static Sound load(String name) {
		Fi file = HVars.internalTree.child("sounds/" + name);

		if (file.exists()) {
			return new Sound(file);
		} else {
			Log.warn("The path @ does not exist!", file.name());

			return Sounds.back;
		}
	}

	public static Fi ogg(String name) {
		return HVars.internalTree.child("sounds/" + name + ".ogg");
	}

	public static Fi mp3(String name) {
		return HVars.internalTree.child("sounds/" + name + ".mp3");
	}
}
