//$Header: /oftp2/de/mendelson/util/DateChooserUI.java 5     25.10.19 16:51 Heller $
package de.mendelson.util;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JDayChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * UI setup for used date choosers in mendelson software Call
 * jDateChooser.setUI(new DateChooserUI());
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class DateChooserUI extends PanelUI {

    private static final MendelsonMultiResolutionImage IMAGE_CALENDAR
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/calendar.svg", 14, 28);

    public static ComponentUI createUI(JComponent c) {
        return new DateChooserUI();
    }

    @Override
    public void installUI(JComponent component) {
        if (!(component instanceof JDateChooser)) {
            throw new IllegalArgumentException("setUI: JDateChooser expected - please pass this UI to JDateChoosers only");
        }
        Color panelBackgroundColor = UIManager.getColor("Panel.background");
        JDateChooser datechooser = (JDateChooser) component;
        JButton selectionButton = datechooser.getCalendarButton();
        selectionButton.setIcon(new ImageIcon(IMAGE_CALENDAR.toMinResolution(14)));
        JCalendar calendar = datechooser.getJCalendar();
        Color decorationBackgroundColor = panelBackgroundColor.darker();
        calendar.setDecorationBackgroundColor(decorationBackgroundColor);
        Color sundayForegroundColor = calendar.getSundayForeground();
        sundayForegroundColor = ColorUtil.getBestContrastColorAroundForeground(
                panelBackgroundColor, sundayForegroundColor);
        calendar.setSundayForeground(sundayForegroundColor);
        Color weekdayForegroundColor = calendar.getWeekdayForeground();
        weekdayForegroundColor = ColorUtil.getBestContrastColorAroundForeground(
                panelBackgroundColor, weekdayForegroundColor);
        calendar.setWeekdayForeground(weekdayForegroundColor);
        calendar.setWeekOfYearVisible(false);
        JDayChooser dayChooser = calendar.getDayChooser();
        for (Component subComponent : dayChooser.getComponents()) {
            if (subComponent instanceof JComponent) {
                JComponent subsubJComponent = (JComponent)subComponent;
                for (Component subsubComponent : subsubJComponent.getComponents()) {
                    if (subsubComponent instanceof JButton) {
                        JButton button = (JButton)subsubComponent;
                        Dimension dimension = new Dimension(30, 30);
                        button.setMaximumSize(dimension);
                        button.setPreferredSize(dimension);
                        button.setMinimumSize(dimension);
                    }
                }
            }
        }
    }

}
