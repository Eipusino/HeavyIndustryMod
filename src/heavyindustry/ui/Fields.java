package heavyindustry.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import heavyindustry.ui.defaults.*;
import heavyindustry.util.Ref.*;

/** Custom fields */
public final class Fields {
    public static String tooBigNumberKey = "too-big-number";
    public static String numberOutOfRange = "number-out-of-range";

    private Fields() {}

    /**
     * If value duplicated field will change color
     *
     * @param duplicateKey key in {@link Core#bundle}
     * @param validator    Checks is value duplicated
     */
    public static TextField uniqueField(String def, String duplicateKey, Cons<String> listener, Boolf<String> validator) {
        return uniqueField(def, duplicateKey, listener, validator, new ObjectRef<>());
    }

    /**
     * If value duplicated field will change color
     *
     * @param duplicateKey key in {@link Core#bundle}
     * @param validator    Checks is value duplicated
     * @param tooltipText  if values is duplicated, tooltipText.element value will be <br>{@code
     *                     Core.bundle.get(duplicateKey)
     *                     }
     */
    public static TextField uniqueField(String def, String duplicateKey, Cons<String> listener, Boolf<String> validator, ObjectRef<String> tooltipText) {
        String duplicateMessage = Core.bundle.get(duplicateKey);
        TextField textField = Elem.newField(def, listener);
        textField.setValidator(t -> tooltipText.element == null);
        textField.update(() -> {

            tooltipText.element = validator.get(textField.getText()) ? null : duplicateMessage;
        });
        return textField;
    }

    //region ranged number fields

    public static Cell<TextField> rangedNumberField(Table table, @Nullable String name, int defaultValue, int minValue, int maxValue, Intc consumer) {
        return rangedNumberField(table, name == null ? null : () -> Core.bundle.get(name), defaultValue, minValue, maxValue, consumer);
    }

    public static Cell<TextField> rangedNumberField(Table table, @Nullable Prov<String> nameProv, int defaultValue, int minValue, int maxValue, Intc consumer) {
        ObjectRef<TextField> fieldRef = new ObjectRef<>();
        ObjectRef<String> tooltipText = new ObjectRef<>();
        if (nameProv != null) {
            table.label(nameProv::get);
        }
        return table.field(String.valueOf(defaultValue), TextFieldFilter.digitsOnly, text -> {
            if (!Strings.canParseInt(text)) {
                fieldRef.element.color.set(Color.scarlet);
                tooltipText.element = Core.bundle.get(tooBigNumberKey);
                return;
            }
            int number = Strings.parseInt(text);
            if (number > maxValue || number < minValue) {
                fieldRef.element.color.set(Color.scarlet);
                tooltipText.element = Core.bundle.format(numberOutOfRange, minValue, maxValue);
                return;
            }
            fieldRef.element.color.set(Color.white);
            tooltipText.element = null;
            consumer.get(number);
        }).with(i -> fieldRef.element = i).tooltip(t -> {
            t.visible(() -> tooltipText.element != null);
            t.background(DefaultBackground.black6()).margin(4f).label(() -> tooltipText.element);
        });
    }

    public static Cell<TextField> rangedFloatNumberField(Table table, @Nullable String name, float defaultValue, float minValue, float maxValue, Floatc consumer) {
        return rangedFloatNumberField(table, name == null ? null : () -> Core.bundle.get(name), defaultValue, minValue, maxValue, consumer);
    }

    public static Cell<TextField> rangedFloatNumberField(Table table, @Nullable Prov<String> nameProv, float defaultValue, float minValue, float maxValue, Floatc consumer) {
        ObjectRef<TextField> fieldRef = new ObjectRef<>();
        ObjectRef<String> tooltipText = new ObjectRef<>();
        if (nameProv != null) {
            table.label(nameProv::get);
        }
        return table.field(defaultValue % 1f == 0 ? String.valueOf((int) defaultValue) : String.valueOf(defaultValue), TextFieldFilter.floatsOnly, text -> {
            if (!Strings.canParseFloat(text)) {
                fieldRef.element.color.set(Color.scarlet);
                tooltipText.element = Core.bundle.get(tooBigNumberKey);
                return;
            }
            float number = Strings.parseFloat(text);
            if (number > maxValue || number < minValue) {
                fieldRef.element.color.set(Color.scarlet);
                tooltipText.element = Core.bundle.format(numberOutOfRange, minValue, maxValue);
                return;
            }
            fieldRef.element.color.set(Color.white);
            tooltipText.element = null;
            consumer.get(number);
        }).with(i -> fieldRef.element = i).tooltip(t -> {
            t.visible(() -> tooltipText.element != null);
            t.background(DefaultBackground.black6()).margin(4f).label(() -> tooltipText.element);
        });
    }
}
