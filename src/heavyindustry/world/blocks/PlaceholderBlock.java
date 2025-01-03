package heavyindustry.world.blocks;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.meta.*;

public class PlaceholderBlock extends Block {
    public PlaceholderBlock(String name) {
        super(name);

        update = true;
        squareSprite = false;

        destructible = true;
        breakable = false;
        solid = true;
        rebuildable = false;

        buildVisibility = BuildVisibility.hidden;
    }

    @Override
    public boolean canBreak(Tile tile) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    public class PlaceholderBuild extends Building {
        //check for next tick
        public boolean checkTile = false;
        public Tile linkTile;
        public ConstructBuild linkBuild;

        public void updateLink(Tile tile) {
            linkTile = tile;
            if (tile.build instanceof ConstructBuild con) {
                linkBuild = con;
            }
        }

        @Override
        public void draw() {}

        @Override
        public void updateTile() {
            super.updateTile();
            if (!checkTile && linkBuild == null) {
                updateLink(linkTile);
                checkTile = true;
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
            return linkBuild != null ? linkBuild.handleDamage(amount) : 0f;
        }
    }
}
