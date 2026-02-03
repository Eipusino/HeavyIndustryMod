"use strict";

const endfield = Packages.rhino.NativeJavaPackage("endfield", Vars.mods.mainLoader());
Packages.rhino.ScriptRuntime.setObjectProtoAndParent(endfield, Vars.mods.scripts.scope);

importPackage(endfield);
importPackage(endfield.ai);
importPackage(endfield.async);
importPackage(endfield.audio);
importPackage(endfield.content);
importPackage(endfield.core);
importPackage(endfield.editor);
importPackage(endfield.entities);
importPackage(endfield.entities.abilities);
importPackage(endfield.entities.bullet);
importPackage(endfield.entities.effect);
importPackage(endfield.entities.part);
importPackage(endfield.entities.pattern);
importPackage(endfield.files);
importPackage(endfield.func);
importPackage(endfield.game);
importPackage(endfield.gen);
importPackage(endfield.graphics);
importPackage(endfield.graphics.g2d);
importPackage(endfield.graphics.g3d);
importPackage(endfield.graphics.gl);
importPackage(endfield.input);
importPackage(endfield.io);
importPackage(endfield.maps);
importPackage(endfield.maps.planets);
importPackage(endfield.math);
importPackage(endfield.math.gravity);
importPackage(endfield.mod);
importPackage(endfield.net);
importPackage(endfield.type);
importPackage(endfield.type.ammo);
importPackage(endfield.type.lightnings);
importPackage(endfield.type.particles);
importPackage(endfield.type.shape);
importPackage(endfield.type.unit);
importPackage(endfield.type.weapons);
importPackage(endfield.type.weather);
importPackage(endfield.ui);
importPackage(endfield.ui.chart);
importPackage(endfield.ui.dialogs);
importPackage(endfield.ui.markdown);
importPackage(endfield.ui.markdown.elemdraw);
importPackage(endfield.ui.markdown.extensions);
importPackage(endfield.ui.markdown.highlighter);
importPackage(endfield.ui.markdown.highlighter.defaults);
importPackage(endfield.util);
importPackage(endfield.util.concurrent);
importPackage(endfield.util.handler);
importPackage(endfield.util.misc);
importPackage(endfield.util.path);
importPackage(endfield.util.pooling);
importPackage(endfield.util.script);
importPackage(endfield.util.stream);
importPackage(endfield.world);
importPackage(endfield.world.blocks);
importPackage(endfield.world.blocks.campaign);
importPackage(endfield.world.blocks.defense);
importPackage(endfield.world.blocks.defense.turrets);
importPackage(endfield.world.blocks.distribution);
importPackage(endfield.world.blocks.environment);
importPackage(endfield.world.blocks.heat);
importPackage(endfield.world.blocks.liquid);
importPackage(endfield.world.blocks.logic);
importPackage(endfield.world.blocks.payload);
importPackage(endfield.world.blocks.power);
importPackage(endfield.world.blocks.production);
importPackage(endfield.world.blocks.sandbox);
importPackage(endfield.world.blocks.storage);
importPackage(endfield.world.blocks.units);
importPackage(endfield.world.consumers);
importPackage(endfield.world.draw);
importPackage(endfield.world.meta);
importPackage(endfield.world.patterns);

if (OS.isAndroid) {
	importPackage(endfield.android);
	importPackage(endfield.android.util.field);
} else if (!OS.isIos) {
	importPackage(endfield.desktop);
}

function apply() {
	let cx = Packages.rhino.Context.getContext();
	let lastLoader = cx.getApplicationClassLoader();
	cx.setApplicationClassLoader(Vars.mods.mainLoader());

	let base = arguments[0];
	let def = arguments[arguments.length - 1];
	let args = [base, def].concat(Array.from(arguments).splice(1, arguments.length - 2));

	let result = JavaAdapter.apply(null, args);
	for (var i in def) {
		if (typeof(def[i]) != "function") {
			result[i] = def[i];
		}
	}

	cx.setApplicationClassLoader(lastLoader);

	return result;
}

function getClass(name) {
	return Packages.java.lang.Class.forName(name, true, Vars.mods.mainLoader());
}

const jbyte = (value) => Packages.java.lang.Byte(value);
const jshort = (value) => Packages.java.lang.Short(value);
const jint = (value) => Packages.java.lang.Integer(value);
const jlong = (value) => Packages.java.lang.Long(value);
const jfloat = (value) => Packages.java.lang.Float(value);
const jdouble = (value) => Packages.java.lang.Double(value);
const jchar = (value) => Packages.java.lang.Character(value);
