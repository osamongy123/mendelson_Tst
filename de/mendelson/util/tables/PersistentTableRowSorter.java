//$Header: /as2/de/mendelson/util/tables/PersistentTableRowSorter.java 2     30.11.21 11:08 Heller $
package de.mendelson.util.tables;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * RowSorter that stores the last used sort keys and uses them once the
 * RowSorter is used again in the same VM instance. This will keep the sort keys
 * persistent if the user leaves a dialog etc and reopens it later.
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class PersistentTableRowSorter<M extends TableModel> extends TableRowSorter {

    private final static Map<String, List> KEY_MAP = new ConcurrentHashMap<String, List>();
    private String uniqueId;

    /**
     * Requires a unique id that will identify the row sorter for a new request
     * Sample to call:
     *
     * RowSorter<TableModel> sorter = new
     * PersistentTableRowSorter<TableModel>(this.jTableUser.getModel(),
     * JPanelUserTasks.class.getName());
     *
     * @param model
     * @param uniqueId
     */
    public PersistentTableRowSorter(M model, String uniqueId) {
        super(model);
        this.uniqueId = uniqueId;
    }

    @Override
    public void setSortKeys(List sortKeys) {
        if (this.uniqueId != null) {
            KEY_MAP.put(this.uniqueId, sortKeys);
        }
        super.setSortKeys(sortKeys);
    }

    @Override
    public List getSortKeys() {
        if (this.uniqueId != null && KEY_MAP.containsKey(this.uniqueId)) {
            return (KEY_MAP.get(this.uniqueId));
        }
        return (super.getSortKeys());
    }

}
