//This js is main used to check if the user downloaded the wrong version of mod, usually uncomplicated source.
Events.on(ClientLoadEvent, cons(ignored => {
	var loadFailed = false;

	let mod = Vars.mods.getMod("heavy-industry");
	if (mod == null || (mod.meta.name.equals("heavy-industry") && mod.loader == null)) {
		loadFailed = true;
	}

	if (mod != null && loadFailed) {
		Log.err("Load Mod <Heavy Industry> Failed::Mod ClassLoader Missing");

		let dialog = new BaseDialog("Missing ClassLoader");
		dialog.addCloseButton();
		dialog.cont.pane(cons(table => {
			table.center();
			table.margin(60);
			table.add("Failed to install [accent]<Heavy Industry>[] mod").pad(6).row();
			table.image().growX().height(4).pad(4).color(Color.lightGray).row();
			table.add("Please down load jar-packaged format mod file from GitHub or other places, or download this mod through [sky]Mod Browser[].");
		})).grow();
		dialog.show();
	}
}));
