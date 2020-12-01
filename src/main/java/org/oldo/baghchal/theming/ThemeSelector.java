package org.oldo.baghchal.theming;

/**
 * Theme selection
 */
public interface ThemeSelector {

    Iterable<String> getAvailableThemeNames();

    void selectTheme(String themeName);

    String getSelectedThemeName();
}
