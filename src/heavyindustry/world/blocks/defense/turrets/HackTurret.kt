package heavyindustry.world.blocks.defense.turrets

import arc.*
import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import arc.struct.*
import arc.util.*
import arc.util.io.*
import heavyindustry.*
import mindustry.*
import mindustry.entities.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.*
import mindustry.world.blocks.defense.turrets.*
import mindustry.world.meta.*

open class HackTurret(name: String) : BaseTurret(name) {
	@JvmField val targets = Seq<Unit>()

	@JvmField var baseRegion = HVars.whiteRegion
	@JvmField var laser = HVars.whiteRegion
	@JvmField var laserEnd = HVars.whiteRegion

	@JvmField var shootCone = 6f
	@JvmField var shootLength = 5f
	@JvmField var laserWidth = 0.6f
	@JvmField var damage = 0.5f
	@JvmField var targetAir = true
	@JvmField var targetGround = true
	@JvmField var laserColor = Color.white
	@JvmField var shootSound = Sounds.tractorbeam
	@JvmField var shootSoundVolume = 0.9f

	@JvmField var unitSort = Units.Sortf { obj: Unit, x: Float, y: Float -> obj.dst2(x, y) }

	init {
		rotateSpeed = 10f
		canOverdrive = true

		Timer.schedule({ targets.removeAll { it.dead() } }, 0f, 1f)
	}

	override fun load() {
		super.load()
		baseRegion = Core.atlas.find("block-$size")
		laser = Core.atlas.find("$name-laser")
		laserEnd = Core.atlas.find("$name-laser-end")
	}

	override fun setStats() {
		super.setStats()
		stats.add(Stat.targetsAir, targetAir)
		stats.add(Stat.targetsGround, targetGround)
		stats.add(Stat.damage, 60f * damage, StatUnit.perSecond)
	}

	override fun icons(): Array<TextureRegion> = arrayOf(baseRegion, region)

	open inner class HackTurretBuild : BaseTurretBuild() {
		@JvmField var target: Unit? = null
		@JvmField var lastX = 0f
		@JvmField var lastY = 0f
		@JvmField var progress = 0f
		@JvmField var normalProgress = 0f

		override fun updateTile() {
			if (target != null && validateTarget()) {
				if (!Vars.headless) {
					Vars.control.sound.loop(shootSound, this, shootSoundVolume)
				}

				val dest = angleTo(target)
				rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta())

				lastX = target!!.x
				lastY = target!!.y

				if (Angles.within(rotation, dest, shootCone)) {
					progress += edelta() * damage
					normalProgress = progress / target!!.maxHealth()
					if (progress > target!!.maxHealth()) {
						target!!.team(team())
						reset()
					}
				} else {
					reset()
				}
			} else {
				reset()
				findTarget()
			}
		}

		open fun findTarget() {
			target = Units.bestEnemy(
				team, x, y, range,
				{ e: Unit -> !e.dead() && (e.isGrounded || targetAir) && (!e.isGrounded || targetGround) && !e.spawnedByCore && e !in targets },
				unitSort
			)
			target?.let {
				targets.add(it)
				lastX = target!!.x
				lastY = target!!.y
			}
		}

		open fun reset() {
			progress = 0f
			targets.remove(target)
			target = null
		}

		open fun validateTarget(): Boolean {
			return !Units.invalidateTarget(target, team, x, y, range) && efficiency() > 0.02f
		}

		override fun onRemoved() {
			targets.remove(target)
			super.onRemoved()
		}

		override fun draw() {
			drawTurret()
			if (target != null) {
				drawLaser()
				drawProgress()
			}
		}

		open fun drawTurret() {
			Draw.rect(baseRegion, x, y)
			Drawf.shadow(region, x - size / 2f, y - size / 2f, rotation - 90)
			Draw.rect(region, x, y, rotation - 90)
		}

		open fun drawLaser() {
			Draw.z(Layer.bullet)
			val ang = angleTo(lastX, lastY)
			Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f))
			Drawf.laser(
				laser, laserEnd,
				x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
				lastX, lastY, efficiency() * laserWidth
			)
			Draw.mixcol()
		}

		open fun drawProgress() {
			Drawf.target(lastX, lastY, target!!.hitSize, normalProgress, team.color)
		}

		override fun write(write: Writes) {
			super.write(write)
			write.f(rotation)
		}

		override fun read(read: Reads, revision: Byte) {
			super.read(read, revision)
			rotation = read.f()
		}

		override fun shouldConsume() = target != null
	}
}