package heavyindustry.ui.dialogs;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Scaling;
import heavyindustry.util.Utils;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class DDItemsList extends BaseDialog {
	public DDItemsList() {
		super("Donor And Developer");
	}

	public void toShow() {
		cont.clear();

		cont.pane(p -> {
			//donor
			p.add(Core.bundle.get("hi-donor-item")).pad(40).left();

			p.row();
			p.image(Tex.whiteui, Pal.accent).left().width(Core.graphics.getWidth() / 2f).height(3f).row();
			for (int i = 0; i < Utils.donors.length; i++) {
				int j = i;
				if (Utils.donorMap.get(i).size <= 0) continue;
				p.table(d -> {
					d.left().defaults().left();
					d.add(Utils.donors[j]).pad(10).padRight(15).left();

					d.image(Tex.whiteui, Pal.accent).growY().width(3).left();
					d.table(item -> {
						for (int c = 0; c < Utils.donorMap.get(j).size; c++) {
							UnlockableContent content = Utils.donorMap.get(j).get(c);
							item.table(inner -> {
								inner.add(Utils.selfStyleImageButton(new TextureRegionDrawable(content.uiIcon), Styles.emptyi, () -> ui.content.show(content))).size(40).pad(10).left().scaling(Scaling.fit).tooltip(content.localizedName);
								//inner.add(content.localizedName).left().pad(5);
							});
							if ((c + 1) % 3 == 0) item.row();
						}
					}).growX();
				}).pad(2).width(Core.graphics.getWidth() / 2f);

				p.row();
				p.image(Tex.whiteui, Pal.accent).width(Core.graphics.getWidth() / 2f).height(3).pad(4).margin(5).left().row();
			}

			//developer
			p.add(Core.bundle.get("hi-developer-item")).pad(40).left();

			p.row();
			p.image(Tex.whiteui, Pal.accent).left().width(Core.graphics.getWidth() / 2f).height(3f).row();
			for (int i = 0; i < Utils.developers.length; i++) {
				int j = i;
				if (Utils.developerMap.get(i).size <= 0) continue;
				p.table(d -> {
					d.left().defaults().left();
					d.add(Utils.developers[j]).pad(10).padRight(15).left();

					d.image(Tex.whiteui, Pal.accent).growY().width(3).left();
					d.table(item -> {
						for (int c = 0; c < Utils.developerMap.get(j).size; c++) {
							UnlockableContent content = Utils.developerMap.get(j).get(c);
							item.table(inner -> {
								inner.add(Utils.selfStyleImageButton(new TextureRegionDrawable(content.uiIcon), Styles.emptyi, () -> ui.content.show(content))).size(40).pad(10).left().scaling(Scaling.fit).tooltip(content.localizedName);
								//inner.add(content.localizedName).left().pad(5);
							});
							if ((c + 1) % 3 == 0) item.row();
						}
					}).growX();
				}).pad(2).width(Core.graphics.getWidth() / 2f);

				p.row();
				p.image(Tex.whiteui, Pal.accent).width(Core.graphics.getWidth() / 2f).height(3).pad(4).margin(5).left().row();
			}
		});


		buttons.clearChildren();
		addCloseButton();
		addCloseListener();

		show();
	}
}
