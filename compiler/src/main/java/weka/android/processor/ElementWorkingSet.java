package weka.android.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

/**
 * Manages the list of annotated elements.
 * Uses generics to avoid unnecessary type casting.
 *
 * @author Jin, Heonkyu <heonkyu.jin@gmail.com>
 */
abstract class ElementWorkingSet<Item extends Element, SubItem extends Element> {
    /**
     * A {@link Map} to store annotated elements.
     */
    private Map<Item, Set<SubItem>> set = new HashMap<>();

    /**
     * Adds a combination of {@link Item} and {@link SubItem} to the working set.
     *
     * @param item An {@link Item}
     * @param subItem A {@link SubItem}
     */
    void add(Item item, SubItem subItem) {
        if (!set.containsKey(item)) {
            set.put(item, new HashSet<SubItem>());
        }
        set.get(item).add(subItem);
    }

    /**
     * Returns {@link Set} of {@link Item}.
     *
     * @return
     */
    Set<Item> getItems() {
        return set.keySet();
    }

    /**
     * Returns {@link Set} of {@link SubItem} whose enclosing element is {@link Item}.
     *
     * @param item An {@link Item}
     * @return Set of subitems which belong to the given item.
     */
    Set<SubItem> getSubItems(Item item) {
        return set.get(item);
    }
}
