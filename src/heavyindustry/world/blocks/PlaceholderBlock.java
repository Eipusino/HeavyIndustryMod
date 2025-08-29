package heavyindustry.world.blocks;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.meta.BuildVisibility;

public class PlaceholderBlock extends Block {
    public PlaceholderBlock(String name) {
        super(name);

        update = true;
        squareSprite = false;

        destructible = true;
        breakable = false;
        solid = true;
        rebuildable = false;

        inEditor = false;

        buildVisibility = BuildVisibility.hidden;

	    localizedName = Core.bundle.get(getContentType() + ".heavy-industry-inner-block.name", this.name);
	    description = Core.bundle.getOrNull(getContentType() + ".heavy-industry-inner-block.description");
	    details = Core.bundle.getOrNull(getContentType() + ".heavy-industry-inner-block.details");
    }

    public boolean canBreak(Tile tile) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find("status-blasted");
    }

    @Override
    public void loadIcon() {
        fullIcon = uiIcon = Core.atlas.find("status-blasted");
    }

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PlaceholderBuild::new;
	}

	public class PlaceholderBuild extends Building {
        //check for next tick
        public boolean checkTile = false;
        public Tile linkTile;
        public ConstructBlock.ConstructBuild linkBuild;

        public void updateLink(Tile tile) {
            linkTile = tile;
            if (tile.build instanceof ConstructBlock.ConstructBuild) {
                linkBuild = (ConstructBlock.ConstructBuild) tile.build;
            }
        }

        @Override
        public void draw() {
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (!checkTile && linkBuild == null) {
                if (linkTile != null) {
                    updateLink(linkTile);
                    checkTile = true;
                }
            }
            if (linkBuild == null || !linkBuild.isAdded()) {
                tile.removeNet();
            }
        }

        @Override
        public TextureRegion getDisplayIcon() {
            return linkBuild == null ? block.uiIcon : linkBuild.getDisplayIcon();
        }

        @Override
        public String getDisplayName() {
            String name = linkBuild == null ? block.localizedName : linkBuild.block.localizedName;
            return team == Team.derelict ? name + "\n" + Core.bundle.get("block.derelict") : name + (team != Vars.player.team() && !team.emoji.isEmpty() ? " " + team.emoji : "");
        }

        @Override
        public float handleDamage(float amount) {
            if (linkBuild != null) {
                return linkBuild.handleDamage(amount);
            } else {
                return 0;
            }
        }

	    @Override
	    public boolean canPickup() {
		    return canPickup;
	    }

	    @Override
	    public boolean canResupply() {
		    return allowResupply;
	    }

	    @Override
	    public boolean canUnload() {
		    return unloadable;
	    }
    }
}
