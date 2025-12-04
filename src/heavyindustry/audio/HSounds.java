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
	public static Sound ct1 = new Sound();
	public static Sound dbz1 = new Sound();
	public static Sound dd1 = new Sound();
	public static Sound fj = new Sound();
	public static Sound jg1 = new Sound();
	public static Sound flak = new Sound(), flak2 = new Sound();
	public static Sound shock = new Sound();
	public static Sound jumpIn = new Sound();
	public static Sound launch = new Sound();
	public static Sound gauss = new Sound();
	public static Sound radar = new Sound();
	public static Sound fissure = new Sound();
	public static Sound blaster = new Sound();
	public static Sound hugeShoot = new Sound();
	public static Sound hugeBlast = new Sound();
	public static Sound largeBeam = new Sound();
	public static Sound metalPipe = new Sound();
	public static Sound metalWalk = new Sound();
	public static Sound alert2 = new Sound();
	public static Sound hammer = new Sound();
	public static Sound coolingFan = new Sound();
	public static Sound hailRain = new Sound();
	public static Sound bigHailstoneHit = new Sound(), giantHailstoneHit = new Sound();
	public static Sound railGunBlast = new Sound(), railGunCharge = new Sound();
	public static Sound shootAltHeavy = new Sound(), shootAltLight = new Sound();
	public static Sound flowrateAbosrb = new Sound();
	public static Sound eviscerationCharge = new Sound(), eviscerationBlast = new Sound();
	public static Sound beamIntenseHighpitchTone = new Sound();

	public static Sound transform = new Sound(), largeTransform = new Sound();
	public static Sound idle = new Sound();
	public static Sound clang = new Sound();
	public static Sound bigCharge = new Sound(), laserCharge = new Sound(), laserSmall = new Sound(), laserBig = new Sound();
	public static Sound desSpearHit = new Sound(), desSpearCry = new Sound();
	public static Sound desLaser = new Sound(), desLaserShoot = new Sound();
	public static Sound desRailgun = new Sound(), desRailHit = new Sound();
	public static Sound desNukeShoot =  new Sound(), desNukeHit = new Sound(), desNukeHitFar = new Sound();
	public static Sound expElectric = new Sound();
	public static Sound expExotic = new Sound();
	public static Sound expOrganic = new Sound();

	/// Don't let anyone instantiate this class.
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
		Fi file = HVars.internalTree.child("sounds").child(name);

		if (file.exists()) {
			return new Sound(file);
		}

		Log.warn("The path @ does not exist!", file.name());

		return Sounds.back;
	}
}
