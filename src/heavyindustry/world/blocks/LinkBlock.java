package heavyindustry.world.blocks;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

/**
 * Inner building that are linked to a specific building.
 * Handle items, liquids, damage and so on a passed to the main building to handle.
 * <p>NEVER SUPPOSED TO USE OUTSIDE MULTI BLOCK!
 */
public class LinkBlock extends Block {
    public LinkBlock(String name) {
        super(name);

        update = false;
        squareSprite = false;

        destructible = true;
        breakable = false;
        solid = true;
        rebuildable = false;

        hasItems = true;
        hasLiquids = true;

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

    public class LinkBuild extends Building {
        /** Linked Build. Can't be null */
        public Building linkBuild;

        public void updateLink(Building link) {
            if (link instanceof MultiBuild) {
                linkBuild = link;
                items = link.items;
                liquids = link.liquids;
            }
        }

        @Override
        public void draw() {}

        @Override
        public void drawSelect() {
            if (linkBuild != null) {
                linkBuild.drawSelect();
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
        public void displayBars(Table table) {
            if (linkBuild != null) {
                for (Func<Building, Bar> buildingBarFunc : linkBuild.block.listBars()) {
                    Bar result = buildingBarFunc.get(linkBuild);
                    if (result != null) {
                        table.add(result).growX();
                        table.row();
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return linkBuild != null && linkBuild.acceptItem(source, item);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            return linkBuild != null ? linkBuild.acceptStack(item, amount, source) : 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return linkBuild != null && linkBuild.acceptLiquid(source, liquid);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            //todo
            return super.acceptPayload(source, payload);
        }

        @Override
        public float handleDamage(float amount) {
            return linkBuild != null ? linkBuild.handleDamage(amount) : 0f;
        }

        @Override
        public void damage(float damage) {
            if (linkBuild != null) {
                linkBuild.damage(damage);
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (linkBuild != null) {
                linkBuild.handleItem(source, item);
            }
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if (linkBuild != null) {
                linkBuild.handleStack(item, amount, source);
            }
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            if (linkBuild != null) {
                linkBuild.handleLiquid(source, liquid, amount);
            }
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            if (linkBuild != null) {
                linkBuild.handlePayload(source, payload);
            }
        }

        @Override
        public void handleString(Object value) {
            if (linkBuild != null) {
                linkBuild.handleString(value);
            }
        }

        @Override
        public void handleUnitPayload(Unit unit, Cons<Payload> grabber) {
            if (linkBuild != null) {
                linkBuild.handleUnitPayload(unit, grabber);
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            if (linkBuild instanceof MultiBuild other) {
                other.updateLinkProximity();
            }
        }

        @Override
        public void onProximityAdded() {
            super.onProximityAdded();
        }

        @Override
        public void onProximityRemoved() {
            super.onProximityRemoved();
        }

        @Override
        public void onDestroyed() {}
    }
}
